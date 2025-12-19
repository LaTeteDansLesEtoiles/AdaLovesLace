package org.alienlabs.adaloveslace.util;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.CompressionConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.Diagram;
import org.alienlabs.adaloveslace.domain.Knot;
import org.alienlabs.adaloveslace.domain.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import static org.alienlabs.adaloveslace.App.*;

public class FileUtil {

    public static final String JAVA_CLASS_PATH_PROPERTY       = System.getProperty("java.class.path", ".");
    public static final String PATH_SEPARATOR                 = File.pathSeparator;
    public static final String FILE_SEPARATOR                 = File.separator;
    public static final String APP_FOLDER_IN_USER_HOME        = System.getProperty(USER_HOME) + File.separator +
        PROJECT_NAME + File.separator;

    // For code under test:
    public static final String CLASSPATH_RESOURCES_PATH       = ".*org" + FILE_SEPARATOR + "alienlabs" + FILE_SEPARATOR + "adaloveslace" + FILE_SEPARATOR + ".*.jpg";
    public static final String HOME_DIRECTORY_RESOURCES_PATH  = ".+\\.(png|jpg|gif|bmp|jpeg|PNG|JPG|GIF|BMP|JPEG)$";

    public static final String XML_FILE_TO_SAVE_IN_LACE_FILE = "save.xml";

    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);
    private App app;
    private Diagram diagram;
    private static volatile File lastLoadedLaceFile;

    public FileUtil() {
        // Sometimes you don't need the Application
    }

    public FileUtil(App app) {
        this.app = app;
    }

    public void buildUiFromLaceFile(final App app, final File file) {
        lastLoadedLaceFile = file;
        final Dialog<Diagram> dialog = getDialog(app, LoadingLaceInProgress);

        Task<Diagram> loadTask = new Task<>() {
            @Override
            protected Diagram call() {
                return loadFromLaceFile(app, file);
            }
        };

        loadTask.setOnSucceeded(_ -> {
            Diagram value = loadTask.getValue();
            dialog.setResult(value);
            dialog.close();
            // Show the diagram immediately; normal rendering will draw patterns & texts
            new FileChooserUtil().restartGui(app, value);
            // Extract the remaining patterns in the background to keep UI smooth
            Set<String> needed = getNeededPatternFilenamesInViewport(app, value);
            copyRemainingPatternsAsync(file, needed);
        });
        loadTask.setOnFailed(_ -> {
            logger.error("Error loading task", loadTask.getException());
            dialog.close();
        });
        loadTask.setOnCancelled(_ -> dialog.close());

        Thread bg = new Thread(loadTask, "AdaLovesLace-LoaderThread");
        bg.setDaemon(true);
        bg.setPriority(Thread.MAX_PRIORITY);
        bg.start();

        dialog.showAndWait();
    }

    public Diagram loadFromLaceFile(App app, File file) {
        new ImageUtil(app).backupKnots();
        return readZip(app, file);
    }

    private Diagram readZip(App app, File file) {
        Diagram diagram = null;

        try (ZipFile zipFile = new ZipFile(file)) {
            // 1) Read only the XML to display quickly
            ZipEntry xmlEntry = zipFile.getEntry(XML_FILE_TO_SAVE_IN_LACE_FILE);
            if (xmlEntry != null) {
                diagram = buildDiagram(zipFile, xmlEntry);
            }

            if (diagram == null) {
                logger.warn("Built diagram is null, creating new one");
                diagram = new Diagram(app);
            }

            // Initialize typedText (OK in background)
            initializeTypedTextFromLoadedText(diagram);

            // 2) Extract with priority the patterns visible in the viewport (blocking but limited)
            Set<String> needed = getNeededPatternFilenamesInViewport(app, diagram);
            copyOnlyPatterns(zipFile, needed);

            // 3) Let the UI update immediately, then extract the rest in the background
            // (done after return, in setOnSucceeded)
            app.getOptionalDotGrid().setDiagram(diagram);
        } catch (JAXBException | IOException e) {
            logger.error("Error loading lace file: {}", file.getAbsolutePath(), e);
            // Return an empty diagram instead of null to avoid errors
            diagram = new Diagram(app);
        }
        
        // Ensure a valid diagram is always returned
        if (diagram == null) {
            logger.warn("Diagram is null after loading, creating new one");
            diagram = new Diagram(app);
        }
        
        return diagram;
    }

    private Set<String> getNeededPatternFilenames(Diagram diagram) {
        Set<String> needed = new HashSet<>();
        if (diagram == null || diagram.getAllSteps() == null) return needed;
        if (diagram.getCurrentStep() != null) {
            for (Knot k : diagram.getCurrentStep().getAllVisibleKnots()) {
                if (k.getPattern().isPresent()) {
                    needed.add(k.getPattern().get().getFilename());
                }
            }
        }
        return needed;
    }

    private Set<String> getNeededPatternFilenamesInViewport(App app, Diagram diagram) {
        Set<String> needed = new HashSet<>();
        if (diagram == null || diagram.getCurrentStep() == null) return needed;
        double vw = app.getResizes().getGridWidth();
        double vh = app.getResizes().getGridHeight();
        for (Knot k : diagram.getCurrentStep().getAllVisibleKnots()) {
            if (k.getPattern().isPresent()) {
                double w = k.getPattern().get().getWidth();
                double h = k.getPattern().get().getHeight();
                if (rectsIntersect(0, 0, vw, vh, k.getX(), k.getY(), w, h)) {
                    needed.add(k.getPattern().get().getFilename());
                }
            }
        }
        return needed;
    }

    private boolean rectsIntersect(double x1, double y1, double w1, double h1,
                                   double x2, double y2, double w2, double h2) {
        return x1 < x2 + w2 && x2 < x1 + w1 && y1 < y2 + h2 && y2 < y1 + h1;
    }

    private void copyOnlyPatterns(ZipFile zipFile, Set<String> filenames) throws IOException {
        for (String name : filenames) {
            ZipEntry entry = zipFile.getEntry(name);
            if (entry != null) {
                try (InputStream in = zipFile.getInputStream(entry)) {
                    copyTargetFile(entry, in);
                }
            }
        }
    }

    private void copyRemainingPatternsAsync(File file, Set<String> alreadyCopied) {
        Thread t = new Thread(() -> {
            try (ZipFile zip = new ZipFile(file)) {
                Enumeration<? extends ZipEntry> entries = zip.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    if (XML_FILE_TO_SAVE_IN_LACE_FILE.equals(entry.getName())) continue;
                    String name = entry.getName();
                    if (alreadyCopied.contains(name)) continue;
                    try (InputStream in = zip.getInputStream(entry)) {
                        copyTargetFile(entry, in);
                    } catch (IOException ioe) {
                        logger.warn("Background copy failed for {}", name, ioe);
                    }
                }
            } catch (IOException e) {
                logger.warn("Cannot open lace file for async copy", e);
            }
        }, "AdaLovesLace-PatternsCopier");
        t.setDaemon(true);
        t.setPriority(Thread.NORM_PRIORITY);
        t.start();
    }

    public void copyPatternFromZipAsyncByName(String name) {
        File file = lastLoadedLaceFile;
        if (file == null || name == null) return;
        Thread t = new Thread(() -> {
            try (ZipFile zip = new ZipFile(file)) {
                ZipEntry entry = zip.getEntry(name);
                if (entry != null) {
                    try (InputStream in = zip.getInputStream(entry)) {
                        copyTargetFile(entry, in);
                    }
                }
            } catch (IOException e) {
                logger.warn("Cannot copy on-demand pattern {} from {}", name, file.getAbsolutePath(), e);
            }
        }, "AdaLovesLace-Pattern-OnDemand");
        t.setDaemon(true);
        t.start();
    }

    /**
     * After loading the diagram from the .lace file, copy the content of the persisted text field
     * into the transient typedText for text nodes, to ensure they display.
     */
    private void initializeTypedTextFromLoadedText(Diagram diagram) {
        if (diagram == null || diagram.getAllSteps() == null) {
            return;
        }
        for (Step step : diagram.getAllSteps()) {
            for (Knot knot : step.getAllVisibleKnots()) {
                if (knot.getPattern().isEmpty()) {
                    String loaded = knot.getText().orElse("");
                    if (!loaded.trim().isEmpty()) {
                        knot.setTypedText(new StringBuilder(loaded));
                    }
                }
            }
        }
    }

    private void buildKnotsImageViews(App app, Diagram diagram) {
        for (Step step : diagram.getAllSteps()) {
            for (Knot knot : step.getDisplayedKnots()) {
                prepareImageViews(app, knot);
            }

            for (Knot knot : step.getSelectedKnots()) {
                preparePatterns(app, knot);
            }
        }
    }

    private void preparePatterns(App app, Knot knot) {
        if (knot.getPattern().isPresent()) {
            loadImageView(knot);
        } else {
            knot.setImageView(app.getOptionalDotGrid().drawTextImageView(knot, knot.getX(), knot.getY()));
        }
    }

    private void loadImageView(Knot knot) {
        try (FileInputStream fis = new FileInputStream(APP_FOLDER_IN_USER_HOME + PATTERNS_DIRECTORY_NAME + File.separator
                + knot.getPattern().get().getFilename())) {
            buildKnotImageView(knot, fis);
        } catch (IOException e) {
            logger.error("Problem with pattern resource file!", e);
        }
    }

    private void prepareImageViews(App app, Knot knot) {
        if (knot.getPattern().isPresent()) {
            loadPattern(knot);
        } else {
            knot.setImageView(app.getOptionalDotGrid().drawTextImageView(knot, knot.getX(), knot.getY()));
        }
    }

    private void loadPattern(Knot knot) {
        String filename = APP_FOLDER_IN_USER_HOME + PATTERNS_DIRECTORY_NAME + File.separator
                + knot.getPattern().get().getFilename();

        try (FileInputStream fis = new FileInputStream(filename)) {
            buildKnotImageView(knot, fis);
            knot.getPattern().get().setAbsoluteFilename(filename);
        } catch (IOException e) {
            logger.error("Problem with pattern resource file!", e);
        }
    }

    public void buildKnotImageView(Knot knot, FileInputStream fis) {
        if (knot.getPattern().isPresent()) {
            loadImageView(knot, fis, knot.getColor());
        }
    }

    private static void loadImageView(Knot knot, FileInputStream fis, Optional<Color> currentColor) {
        Image image;

        if (currentColor.isPresent()) {
            image = new NodeUtil().replaceColoredPixels(new Image(fis), currentColor.get());
        } else {
            image = new Image(fis);
        }
        ImageView iv = new ImageView(image);

        iv.setLayoutX(knot.getX());
        iv.setLayoutY(knot.getY());
        iv.setFitHeight(knot.getPattern().get().getHeight());
        iv.setFitWidth(knot.getPattern().get().getWidth());

        iv.setRotate(knot.getRotationAngle());

        knot.setImageView(iv);
    }

    private void deleteXmlFile() throws IOException {
        File xmlFile = new File(APP_FOLDER_IN_USER_HOME + PATTERNS_DIRECTORY_NAME + File.separator +
            XML_FILE_TO_SAVE_IN_LACE_FILE);

        if (xmlFile.exists() && xmlFile.canWrite()) {
            Files.delete(xmlFile.toPath());
        }
    }

    private void copyPattern(File file, ZipFile zipFile, ZipEntry entry) {
        try (InputStream initialStream = zipFile.getInputStream(entry)) {
            copyTargetFile(entry, initialStream);
        } catch (IOException e) {
            // Specific handling for missing images (like splashscreen.jpg)
            if (entry.getName().contains("splashscreen") || entry.getName().endsWith(".jpg") || entry.getName().endsWith(".png")) {
                logger.warn("Image file not found in lace file: {}, skipping...", entry.getName());
            } else {
                logger.error("Error copying pattern from loaded file: " + file.getAbsolutePath() + ", entry: " + entry.getName(), e);
            }
        }
    }

    private static void copyTargetFile(ZipEntry entry, InputStream initialStream) throws IOException {
        File targetFile = new File(APP_FOLDER_IN_USER_HOME + PATTERNS_DIRECTORY_NAME + File.separator + entry.getName());

        if (!targetFile.exists()) {
            Files.copy(
                    initialStream,
                targetFile.toPath(),
                StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private Diagram buildDiagram(ZipFile zipFile, ZipEntry entry) throws JAXBException, IOException {
        Diagram diagram = unmarshallXmlFile(zipFile, entry);
        buildAbsoluteFilenamesForPatternsAndKnots(diagram);
        diagram.setCurrentPattern(diagram.getPatterns().stream().findFirst().orElse(null));
        return diagram;
    }

    public Diagram unmarshallXmlFile(ZipFile zipFile, ZipEntry entry) throws JAXBException, IOException {
        JAXBContext context = JAXBContext.newInstance(Diagram.class);
        Unmarshaller jaxbUnmarshaller = context.createUnmarshaller();
        return (Diagram) jaxbUnmarshaller.unmarshal(zipFile.getInputStream(entry));
    }

    private void buildAbsoluteFilenamesForPatternsAndKnots(Diagram diagram) {
        for (org.alienlabs.adaloveslace.domain.Pattern p : diagram.getPatterns()) {
            p.setAbsoluteFilename(APP_FOLDER_IN_USER_HOME + PATTERNS_DIRECTORY_NAME + File.separator + p.getFilename());
        }

        for (Step s : diagram.getAllSteps()) {
            loadPatterns(s.getDisplayedKnots());
            loadPatterns(s.getSelectedKnots());
        }
    }

    private static void loadPatterns(List<Knot> s) {
        for (Knot k : s) {
            if (k.getPattern().isPresent()) {
                k.getPattern().get().setAbsoluteFilename(
                        APP_FOLDER_IN_USER_HOME + PATTERNS_DIRECTORY_NAME + File.separator +
                                k.getPattern().get().getFilename()
                );
            }
        }
    }

    public File saveFile(final File file, Diagram diagram, boolean layoutChildren) {
        if (this.app != null && app.getMainWindow() != null && this.app.getOptionalDotGrid() != null && layoutChildren) {
            this.app.getOptionalDotGrid().layoutChildren();
        }

        Dialog<Diagram> dialog = getDialog(app, SavingLaceInProgress);
        AtomicReference<File> output = new AtomicReference<>();

        Task<File> saveTask = new Task<>() {
            @Override
            protected File call() {
                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
                try {
                    diagram.getCurrentStep().getDisplayedKnots().addAll(new ArrayList<>(diagram.getCurrentStep().getSelectedKnots()));
                    diagram.getCurrentStep().getSelectedKnots().clear();
                    diagram.getCurrentStep().clearStepsGreaterThanPresentStepPlusLimit(diagram);
                    marshallLaceFile(
                            file,
                            diagram,
                            diagram.getAllSteps().size()
                    );
                } catch (JAXBException e) {
                    logger.error("Error marshalling save file: {}", file.getAbsolutePath(), e);
                } catch (CompletionException e) {
                    logger.error("Error uploading file: {}", file.getAbsolutePath(), e);
                } catch (IOException e) {
                    logger.error("Error deleting file to upload", e);
                }

                return file;
            }
        };

        saveTask.setOnSucceeded(_ -> {
            output.set(saveTask.getValue());
            dialog.close();

        });
        saveTask.setOnFailed(_ -> {
            dialog.close();
            Throwable err = saveTask.getException();
            logger.error("Error loading diagram!", err);
        });
        saveTask.setOnCancelled(_ -> dialog.close());

        Thread saveThread = new Thread(saveTask, "AdaLovesLace-saver-Thread");
        saveThread.setDaemon(true);
        saveThread.setPriority(Thread.MAX_PRIORITY);
        saveThread.start();

        dialog.showAndWait();
        return output.get();
    }

    public Dialog<Diagram> getDialog(App app, String operationInProgress) {
        ProgressIndicator pi = new ProgressIndicator();
        pi.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);

        Dialog<Diagram> dialog = new Dialog<>();
        dialog.initOwner(app.getPrimaryStage().getOwner());
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        VBox content = new VBox(
                10,
                new Label(resourceBundle.getString(operationInProgress)),
                pi
        );
        content.setAlignment(Pos.CENTER);
        dialog.getDialogPane().setContent(content);

        return dialog;
    }

    private void marshallLaceFile(File file, Diagram diagram, Integer currentStepIndex) throws JAXBException, IOException {
        JAXBContext context = JAXBContext.newInstance(Diagram.class);
        Marshaller jaxbMarshaller = context.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        writeLaceFile(file, jaxbMarshaller, diagram, currentStepIndex);
        deleteXmlFile();
    }

    private void writeLaceFile(File file, Marshaller jaxbMarshaller, Diagram toSave, Integer currentStepIndex) throws JAXBException {
        try (FileOutputStream fos = new FileOutputStream(file);
                ZipOutputStream zipOut = new ZipOutputStream(fos)) {
            writePatternsToLaceFile(toSave, zipOut);
            writeDiagramToLaceFile(jaxbMarshaller, toSave, zipOut, currentStepIndex);
            zipOut.closeEntry();
        } catch (IOException e) {
            logger.error("Error saving .lace file!", e);
        }
    }

    private void writeDiagramToLaceFile(Marshaller jaxbMarshaller, Diagram toSave, ZipOutputStream zipOut, Integer currentStepIndex) throws JAXBException, IOException {
        toSave.setCurrentStepIndex(currentStepIndex); // Not -1 because of the empty step at the beginning
        File xmlFile = new File(APP_FOLDER_IN_USER_HOME + PATTERNS_DIRECTORY_NAME + File.separator +
                XML_FILE_TO_SAVE_IN_LACE_FILE);
        // Ensure the text field contains the displayed content before saving
        normalizeTextBeforeSave(toSave);
        removeEmptyTexts(toSave);
        jaxbMarshaller.marshal(toSave, xmlFile);

        zipOut.putNextEntry(new ZipEntry(XML_FILE_TO_SAVE_IN_LACE_FILE));
        Files.copy(xmlFile.toPath(), zipOut);
    }

    /**
     * For each text node, synchronize the persisted text field with the transient typedText
     * so the content is correctly saved into the .lace file.
     */
    private static void normalizeTextBeforeSave(Diagram diagram) {
        if (diagram == null || diagram.getAllSteps() == null) {
            return;
        }
        for (Step step : diagram.getAllSteps()) {
            for (Knot knot : step.getAllVisibleKnots()) {
                if (knot.getPattern().isEmpty()) {
                    String typed = knot.getTypedText() != null ? knot.getTypedText().toString() : knot.getText().orElse("");
                    if (typed.trim().isEmpty()) {
                        knot.setText(Optional.of(""));
                    } else {
                        knot.setText(Optional.of(typed));
                    }
                }
            }
        }
    }

    private static void removeEmptyTexts(Diagram toSave) {
        for (Step s : toSave.getAllSteps()) {
            s.getDisplayedKnots().removeAll(s.getDisplayedKnots().stream().filter(knot ->
                    (knot.getText().isPresent()) && (knot.getText().get().isEmpty())).toList());

            s.getSelectedKnots().removeAll(s.getSelectedKnots().stream().filter(knot ->
                    (knot.getText().isPresent()) && (knot.getText().get().isEmpty())).toList());
        }
    }

    private void writePatternsToLaceFile(Diagram toSave, ZipOutputStream zipOut) throws IOException {
        for (org.alienlabs.adaloveslace.domain.Pattern pattern : new HashSet<>(toSave.getPatterns())) {
            File fileToZip = new File(APP_FOLDER_IN_USER_HOME + PATTERNS_DIRECTORY_NAME + File.separator
                + pattern.getFilename());
            try {
                zipOut.putNextEntry(new ZipEntry(pattern.getFilename()));
                Files.copy(fileToZip.toPath(), zipOut);
            } catch (ZipException e) {
                logger.error("Error saving pattern of .lace file!", e);
            }
        }
    }

    /**
     * For all elements of java classpath (starting from app class package if java.class.path system property is empty),
     * get a Collection of resources with a pattern.
     *
     * @param classpathBase production app or unit test class
     * @param pattern the pattern to match
     * @return the resources in the order they are found
     */
    public List<String> getResources(Object classpathBase, final Pattern pattern) {
        final List<String> retval = new ArrayList<>();
        final String classPath = JAVA_CLASS_PATH_PROPERTY;
        logger.info("classpath: {}", classPath);

        if (classPath != null && !classPath.trim().isEmpty()) {
            processClasspath(pattern, retval, classPath);
        } else {
            processLocationPath(classpathBase, pattern, retval);
        }

        return retval;
    }

    private void processLocationPath(Object classpathBase, Pattern pattern, List<String> retval) {
        File file = new File(classpathBase.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        String absolutePath = file.getAbsolutePath();
        logger.info("absolute path: {}", absolutePath);

        retval.addAll(getResources(absolutePath, pattern));
    }

    private void processClasspath(Pattern pattern, List<String> retval, String classPath) {
        final String[] classPathElements = classPath.split(PATH_SEPARATOR);
        for (final String element : classPathElements) {
            logger.info("element: {}, pattern: {}", element, pattern);

            retval.addAll(getResources(element, pattern));
        }
    }

    /**
     * For all elements of a folder, get a Collection of resources with a pattern.
     *
     * @param directory production folder or unit test folder
     * @param pattern the pattern to match
     * @return the resources in the order they are found
     */
    public List<String> getDirectoryResources(File directory, final Pattern pattern) {
        String absolutePath = directory.getAbsolutePath();
        logger.info("absolute path: {}", absolutePath);
        return new ArrayList<>(getResources(absolutePath, pattern));
    }

    public void generatePdf(String pdfFilename, String imageFilename) {
        PageSize pageSize = PageSize.A4.rotate();

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(pdfFilename)); Document doc = new Document(pdfDoc,pageSize)){
            com.itextpdf.layout.element.Image image = new com.itextpdf.layout.element.Image(ImageDataFactory.create(imageFilename));
            image.getXObject().getPdfObject().setCompressionLevel(CompressionConstants.DEFAULT_COMPRESSION);
            doc.add(image);

            logger.info("PDF file generated successfully!");
        } catch (IOException e){
            logger.error("Error generating PDF document!", e);
        }
    }

    private Collection<String> getResources(final String element, final Pattern pattern) {
        final List<String> retval = new ArrayList<>();
        final File file = new File(element);

        if(file.isDirectory()) {
            retval.addAll(getResourcesFromDirectory(file, pattern));
        } else{
            retval.addAll(getResourcesFromJarFile(file, pattern));
        }
        return retval;
    }

    private Collection<String> getResourcesFromJarFile(final File file, final Pattern pattern) {
        return prepareZipFile(file, pattern, new ArrayList<>());
    }

    private List<String> prepareZipFile(File file, Pattern pattern, List<String> retval) {
        ZipFile zf;
        try {
            zf = new ZipFile(file);
        } catch(final IOException e) {
            logger.info("Error reading classpath .jar file!", e);
            return retval;
        }
        return getStrings(pattern, zf, retval);
    }

    private List<String> getStrings(Pattern pattern, ZipFile zf, List<String> retval) {
        final Enumeration<? extends ZipEntry> e = zf.entries();

        while(e.hasMoreElements()) {
            addElementFromZipFile(pattern, retval, e);
        }

        return closeZipFile(retval, zf);
    }

    private void addElementFromZipFile(Pattern pattern, List<String> retval, Enumeration<? extends ZipEntry> e) {
        final ZipEntry ze = e.nextElement();
        final String fileName = ze.getName();

        if(pattern.matcher(fileName).matches()) {
            retval.add(fileName);
        }
    }

    private List<String> closeZipFile(List<String> retval, ZipFile zf) {
        try {
            zf.close();
        } catch (final IOException e1) {
            throw new IllegalStateException("Error closing .zip file: " + zf, e1);
        }
        return retval;
    }

    private Collection<String> getResourcesFromDirectory(final File directory, final Pattern pattern) {
        final List<String> retval = new ArrayList<>();
        final File[] fileList = directory.listFiles();

        logger.info("Directory: {}", directory.getAbsolutePath());

        if (null != fileList) {
            for (final File file : fileList) {
                getResourceFromFileOrDirectory(pattern, retval, file);
            }
        }

        return retval;
    }

    private void getResourceFromFileOrDirectory(Pattern pattern, List<String> retval, File file) {
        if (file.isDirectory()) {
            logger.info("loading from directory: {}", file.getAbsolutePath());
            retval.addAll(getResourcesFromDirectory(file, pattern));
        } else {
            getResourceFromFile(pattern, retval, file);
        }
    }

    private void getResourceFromFile(Pattern pattern, List<String> retval, File file) {
        logger.info("loading from file: {}", file.getAbsolutePath());

        try {
            final String fileName = file.getCanonicalPath();

            if (pattern.matcher(fileName).matches()) {
                logger.info("matches");
                retval.add(fileName);
            } else {
                logger.info("doesn't match");
            }
        } catch (final IOException e) {
            throw new IllegalStateException("Error reading file / directory from classpath: " + file, e);
        }
    }

}

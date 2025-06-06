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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Diagram;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.business.model.Step;
import org.alienlabs.adaloveslace.view.component.button.geometrywindow.DrawingButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.CompletionException;
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

    public FileUtil() {
        // Sometimes you don't need the Application
    }

    public FileUtil(App app) {
        this.app = app;
    }

    public void buildUiFromLaceFile(App app, File file) {
        Diagram diagram = loadFromLaceFile(app, file);

        preparePrimaryStage(app, diagram);
        prepareGeometryAndToolbox(app, diagram);
    }

    private static void prepareGeometryAndToolbox(App app, Diagram diagram) {
        app.getToolboxStage().close();
        app.showToolboxWindow(app, app, CLASSPATH_RESOURCES_PATH);
        diagram.setApp(app);
        app.getOptionalDotGrid().layoutChildren();
        DrawingButton.onSetDrawModeAction(app, app.getGeometryWindow());
    }

    private static void preparePrimaryStage(App app, Diagram diagram) {
        app.getPrimaryStage().close();
        app.showMainWindow(MAIN_WINDOW_WIDTH, MAIN_WINDOW_HEIGHT, GRID_WIDTH, GRID_HEIGHT,
            app.getPrimaryStage(), diagram);
        app.getOptionalDotGrid().setDiagram(diagram);
        new KeyboardUtil().initializeKeyboardShorcuts(app);
        app.onDoMainWindowResize();
    }

    public Diagram loadFromLaceFile(App app, File file) {
        Diagram diagram = null;
        diagram = buildZip(app, file, diagram);

        return diagram;
    }

    private Diagram buildZip(App app, File file, Diagram diagram) {
        try (ZipFile zipFile = new ZipFile(file)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while(entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();

                if (XML_FILE_TO_SAVE_IN_LACE_FILE.equals(entry.getName())) {
                    diagram = buildDiagram(zipFile, entry);
                    app.getOptionalDotGrid().setDiagram(diagram);
                } else {
                    copyPattern(file, zipFile, entry);
                }
            }

            if (null != diagram) {
                buildKnotsImageViews(app, diagram);
            }
        } catch (JAXBException | IOException e) {
            logger.error("Error unmarshalling loaded file: " + file.getAbsolutePath(), e);
        }
        return diagram;
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
            loadImageView(knot, fis);
        }
    }

    private static void loadImageView(Knot knot, FileInputStream fis) {
        Image image = new Image(fis);
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
            logger.error("Error unmarshalling loaded file: " + file.getAbsolutePath(), e);
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
        diagram.setCurrentPattern(diagram.getPatterns().stream().findFirst().get());
        return diagram;
    }

    public Diagram unmarshallXmlFile(ZipFile zipFile, ZipEntry entry) throws JAXBException, IOException {
        JAXBContext context = JAXBContext.newInstance(Diagram.class);
        Unmarshaller jaxbUnmarshaller = context.createUnmarshaller();
        return (Diagram) jaxbUnmarshaller.unmarshal(zipFile.getInputStream(entry));
    }

    private void buildAbsoluteFilenamesForPatternsAndKnots(Diagram diagram) {
        for (org.alienlabs.adaloveslace.business.model.Pattern p : diagram.getPatterns()) {
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

    public File saveFile(File file, Diagram diagram, Integer currentStepIndex) {
        if (this.app != null && app.getMainWindow() != null && this.app.getOptionalDotGrid() != null) {
            this.app.getOptionalDotGrid().layoutChildren();
        }

        try {
            marshallLaceFile(file, diagram, currentStepIndex);
        } catch (JAXBException e) {
            logger.error("Error marshalling save file: " + file.getAbsolutePath(), e);
        } catch (CompletionException e) {
            logger.error("Error uploading file: " + file.getAbsolutePath(), e);
        } catch (IOException e) {
            logger.error("Error deleting file to upload", e);
        }


        return file;
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
        removeEmptyTexts(toSave);
        jaxbMarshaller.marshal(toSave, xmlFile);

        zipOut.putNextEntry(new ZipEntry(XML_FILE_TO_SAVE_IN_LACE_FILE));
        Files.copy(xmlFile.toPath(), zipOut);
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
        for (org.alienlabs.adaloveslace.business.model.Pattern pattern : new HashSet<>(toSave.getPatterns())) {
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
        logger.debug("classpath: {}", classPath);

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
        logger.debug("absolute path: {}", absolutePath);

        retval.addAll(getResources(absolutePath, pattern));
    }

    private void processClasspath(Pattern pattern, List<String> retval, String classPath) {
        final String[] classPathElements = classPath.split(PATH_SEPARATOR);
        for (final String element : classPathElements) {
            logger.debug("element: {}, pattern: {}", element, pattern);

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
        logger.debug("absolute path: {}", absolutePath);
        return new ArrayList<>(getResources(absolutePath, pattern));
    }

    public void generatePdf(String pdfFilename, String imageFilename) {
        PageSize pageSize = PageSize.A4.rotate();

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(pdfFilename)); Document doc = new Document(pdfDoc,pageSize)){
            com.itextpdf.layout.element.Image image = new com.itextpdf.layout.element.Image(ImageDataFactory.create(imageFilename));
            image.getXObject().getPdfObject().setCompressionLevel(CompressionConstants.DEFAULT_COMPRESSION);
            doc.add(image);

            logger.debug("PDF file generated successfully!");
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
            logger.debug("Error reading classpath .jar file!", e);
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

        logger.debug("Directory: {}", directory.getAbsolutePath());

        if (null != fileList) {
            for (final File file : fileList) {
                getResourceFromFileOrDirectory(pattern, retval, file);
            }
        }

        return retval;
    }

    private void getResourceFromFileOrDirectory(Pattern pattern, List<String> retval, File file) {
        if (file.isDirectory()) {
            logger.debug("loading from directory: {}", file.getAbsolutePath());
            retval.addAll(getResourcesFromDirectory(file, pattern));
        } else {
            getResourceFromFile(pattern, retval, file);
        }
    }

    private void getResourceFromFile(Pattern pattern, List<String> retval, File file) {
        logger.debug("loading from file: {}", file.getAbsolutePath());

        try {
            final String fileName = file.getCanonicalPath();

            if (pattern.matcher(fileName).matches()) {
                logger.debug("matches");
                retval.add(fileName);
            } else {
                logger.debug("doesn't match");
            }
        } catch (final IOException e) {
            throw new IllegalStateException("Error reading file / directory from classpath: " + file, e);
        }
    }

}

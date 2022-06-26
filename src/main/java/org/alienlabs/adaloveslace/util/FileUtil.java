package org.alienlabs.adaloveslace.util;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Diagram;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import static org.alienlabs.adaloveslace.App.*;

public class FileUtil {

  public static final String JAVA_CLASS_PATH_PROPERTY       = System.getProperty("java.class.path", ".");
  public static final String PATH_SEPARATOR_PROPERTY        = System.getProperty("path.separator");
  public static final String PATH_SEPARATOR                 = File.separator;

  // For code under test:
  public static final String CLASSPATH_RESOURCES_PATH       = ".*org" + PATH_SEPARATOR + "alienlabs" + PATH_SEPARATOR + "adaloveslace" + PATH_SEPARATOR + ".*.jpg";
  public static final String HOME_DIRECTORY_RESOURCES_PATH  = ".+\\.(png|jpg|gif|bmp|jpeg|PNG|JPG|GIF|BMP|JPEG)$";

  public static final String XML_FILE_TO_SAVE_IN_LACE_FILE = "save.xml";

  public static final String USER_HOME = "user.home";

  private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

  public FileUtil() {
    // Nothing to do here, that's just to avoid an all-static class
  }

  public void loadFromLaceFile(App app, File file) {
    Diagram diagram = null;

    try (ZipFile zipFile = new ZipFile(file)) {
      Enumeration<? extends ZipEntry> entries = zipFile.entries();

      while(entries.hasMoreElements()) {
        ZipEntry entry = entries.nextElement();

        if (XML_FILE_TO_SAVE_IN_LACE_FILE.equals(entry.getName())) {
          diagram = buildDiagram(zipFile, entry);
        } else {
          copyPattern(file, zipFile, entry);
        }
      }

      buildKnotsImageViews(diagram);
      deleteXmlFile();
    } catch (JAXBException | IOException e) {
      logger.error("Error unmarshalling loaded file: " + file.getAbsolutePath(), e);
    }

    app.showToolboxWindow(app, app, CLASSPATH_RESOURCES_PATH);

    app.getOptionalDotGrid().getDiagramProperty().set(diagram);
    app.getOptionalDotGrid().layoutChildren();
  }

  private void buildKnotsImageViews(Diagram diagram) {
    for (Knot knot : diagram.getKnots()) {
      try (FileInputStream fis = new FileInputStream(knot.getPattern().getAbsoluteFilename())) {
        Image image = new Image(fis);
        ImageView iv = new ImageView(image);

        iv.setX(knot.getX());
        iv.setY(knot.getY());
        iv.setRotate(0d);
        iv.setOpacity(1.0d);
        knot.setImageView(iv);
      } catch (IOException e) {
        logger.error("Problem with pattern resource file!", e);
      }
    }
  }

  private void deleteXmlFile() throws IOException {
    File xmlFile = new File(System.getProperty(USER_HOME) + File.separator +
      PROJECT_NAME + File.separator + PATTERNS_DIRECTORY_NAME + File.separator +
      XML_FILE_TO_SAVE_IN_LACE_FILE);
    if (xmlFile.exists() && xmlFile.canWrite()) {
      Files.delete(xmlFile.toPath());
    }
  }

  private void copyPattern(File file, ZipFile zipFile, ZipEntry entry) {
    try (InputStream initialStream = zipFile.getInputStream(entry)) {
      File targetFile = new File(System.getProperty(USER_HOME) + File.separator +
        PROJECT_NAME + File.separator + PATTERNS_DIRECTORY_NAME + File.separator + entry.getName());

      if (!targetFile.exists()) {
        Files.copy(
          initialStream,
          targetFile.toPath(),
          StandardCopyOption.REPLACE_EXISTING);
      }
    } catch (IOException e) {
      logger.error("Error unmarshalling loaded file: " + file.getAbsolutePath(), e);
    }
  }

  private Diagram buildDiagram(ZipFile zipFile, ZipEntry entry) throws JAXBException, IOException {
    JAXBContext context = JAXBContext.newInstance(Diagram.class);
    Unmarshaller jaxbUnmarshaller = context.createUnmarshaller();
    Diagram diagram = (Diagram) jaxbUnmarshaller.unmarshal(zipFile.getInputStream(entry));

    for (org.alienlabs.adaloveslace.business.model.Pattern p : diagram.getPatterns()) {
      p.setAbsoluteFilename(System.getProperty(USER_HOME) + File.separator +
        PROJECT_NAME + File.separator + PATTERNS_DIRECTORY_NAME + File.separator + p.getFilename());
    }

    for (Knot k : diagram.getKnots()) {
      k.getPattern().setAbsoluteFilename(System.getProperty(USER_HOME) + File.separator +
        PROJECT_NAME + File.separator + PATTERNS_DIRECTORY_NAME + File.separator + k.getPattern().getFilename());
    }
    diagram.setCurrentKnotIndex(diagram.getKnots().size());
    diagram.setCurrentPattern(diagram.getPatterns().get(0));
    return diagram;
  }

  public void saveFile(App app, File file) {
    try {
      JAXBContext context = JAXBContext.newInstance(Diagram.class);
      Marshaller jaxbMarshaller = context.createMarshaller();
      jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

      // In order not to lose the undo / redo history
      Diagram toSave = buildDiagramToSave(app);
      File homeDirectoryResourcesPath = new File(System.getProperty(USER_HOME) + File.separator + PROJECT_NAME + File.separator + PATTERNS_DIRECTORY_NAME);

      if (!file.getName().endsWith(LACE_FILE_EXTENSION)) {
        file = new File(file.getParent() + File.separator + file.getName() + LACE_FILE_EXTENSION);
      }

      if (homeDirectoryResourcesPath.exists() && homeDirectoryResourcesPath.canRead()) {
        writeLaceFile(file, jaxbMarshaller, toSave, homeDirectoryResourcesPath);
      } else {
        throw new IllegalArgumentException("Home directory " + homeDirectoryResourcesPath.getAbsolutePath() + " not read accessible!");
      }

      deleteXmlFile();
    } catch (JAXBException | IOException e) {
      logger.error("Error marshalling save file: " + file.getAbsolutePath(), e);
    }
  }

  private void writeLaceFile(File file, Marshaller jaxbMarshaller, Diagram toSave, File homeDirectoryResourcesPath) throws JAXBException {
    try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(file))) {
      writePatternsToLaceFile(toSave, zipOut);
      writeDiagramToLaceFile(jaxbMarshaller, toSave, homeDirectoryResourcesPath, zipOut);
    } catch (IOException e) {
      logger.error("Error saving .lace file!", e);
    }
  }

  private void writeDiagramToLaceFile(Marshaller jaxbMarshaller, Diagram toSave, File homeDirectoryResourcesPath, ZipOutputStream zipOut) throws JAXBException, IOException {
    File xmlFile = new File(homeDirectoryResourcesPath + File.separator + XML_FILE_TO_SAVE_IN_LACE_FILE);
    jaxbMarshaller.marshal(toSave, xmlFile);

    zipOut.putNextEntry(new ZipEntry(xmlFile.getName()));
    Files.copy(xmlFile.toPath(), zipOut);
  }

  private void writePatternsToLaceFile(Diagram toSave, ZipOutputStream zipOut) throws IOException {
    for (org.alienlabs.adaloveslace.business.model.Pattern pattern : toSave.getPatterns()) {
      File fileToZip = new File(pattern.getAbsoluteFilename());
      zipOut.putNextEntry(new ZipEntry(pattern.getFilename()));
      Files.copy(fileToZip.toPath(), zipOut);
    }
  }

  private Diagram buildDiagramToSave(App app) {
    Diagram toSave = new Diagram(app.getOptionalDotGrid().getDiagram());
    toSave.setKnots(toSave.getKnots().subList(0, toSave.getCurrentKnotIndex()));

    return toSave;
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
    final ArrayList<String> retval = new ArrayList<>();
    final String classPath = JAVA_CLASS_PATH_PROPERTY;
    logger.info("classpath: {}", classPath);

    if (classPath != null && !"".equals(classPath.trim())) {
      final String[] classPathElements = classPath.split(PATH_SEPARATOR_PROPERTY);
      for (final String element : classPathElements) {
        logger.info("element: {}, pattern: {}", element, pattern);
        retval.addAll(getResources(element, pattern));
      }
    } else {
      File file = new File(classpathBase.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
      String absolutePath = file.getAbsolutePath();
      logger.info("absolute path: {}", absolutePath);
      retval.addAll(getResources(absolutePath, pattern));
    }
    return retval;
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
    return  new ArrayList<>(getResources(absolutePath, pattern));
  }

  private Collection<String> getResources(
    final String element,
    final Pattern pattern){
    final ArrayList<String> retval = new ArrayList<>();
    final File file = new File(element);
    if(file.isDirectory()) {
      retval.addAll(getResourcesFromDirectory(file, pattern));
    } else{
      retval.addAll(getResourcesFromJarFile(file, pattern));
    }
    return retval;
  }

  private Collection<String> getResourcesFromJarFile(
    final File file,
    final Pattern pattern){
    final ArrayList<String> retval = new ArrayList<>();
    ZipFile zf;
    try {
      zf = new ZipFile(file);
    } catch(final IOException e) {
      logger.error("Error reading classpath .jar file!", e);
      return retval;
    }
    final Enumeration<? extends ZipEntry> e = zf.entries();
    while(e.hasMoreElements()) {
      final ZipEntry ze = e.nextElement();
      final String fileName = ze.getName();
      final boolean accept = pattern.matcher(fileName).matches();
      if(accept) {
        retval.add(fileName);
      }
    }
    try {
      zf.close();
    } catch (final IOException e1) {
      throw new IllegalStateException("Error closing .zip file: " + zf, e1);
    }
    return retval;
  }

  private Collection<String> getResourcesFromDirectory(
    final File directory,
    final Pattern pattern){
    final ArrayList<String> retval = new ArrayList<>();
    final File[] fileList = directory.listFiles();

    logger.info("Directory: {}", directory.getAbsolutePath());

    if (null != fileList) {
      for (final File file : fileList) {
        if (file.isDirectory()) {
          logger.info("loading from directory: {}", file.getAbsolutePath());
          retval.addAll(getResourcesFromDirectory(file, pattern));
        } else {
          logger.info("loading from file: {}", file.getAbsolutePath());

          try {
            final String fileName = file.getCanonicalPath();
            final boolean accept = pattern.matcher(fileName).matches();
            if (accept) {
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
    }

    return retval;
  }

}

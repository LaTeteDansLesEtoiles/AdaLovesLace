package org.alienlabs.adaloveslace.util;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Diagram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileUtil {

  public static final String JAVA_CLASS_PATH_PROPERTY       = System.getProperty("java.class.path", ".");
  public static final String PATH_SEPARATOR_PROPERTY        = System.getProperty("path.separator");
  public static final String PATH_SEPARATOR                 = File.separator;

  // For code under test:
  public static final String CLASSPATH_RESOURCES_PATH       = ".*org" + PATH_SEPARATOR + "alienlabs" + PATH_SEPARATOR + "adaloveslace" + PATH_SEPARATOR + ".*.jpg";
  public static final String HOME_DIRECTORY_RESOURCES_PATH  = ".+\\.(png|jpg|gif|bmp|jpeg|PNG|JPG|GIF|BMP|JPEG)$";

  private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

  public FileUtil() {
    // Nothing to do here, that's just to avoid an all-static class
  }

  public void saveFile(App app, File file) {
    try {
      JAXBContext context = JAXBContext.newInstance(Diagram.class);
      Marshaller jaxbMarshaller = context.createMarshaller();
      jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

      // In order not to lose the undo / redo history
      Diagram toSave = new Diagram(app.getCanvasWithOptionalDotGrid().getDiagram());
      toSave.setKnots(toSave.getKnots().subList(0, toSave.getCurrentKnotIndex()));

      jaxbMarshaller.marshal(toSave, file);
    } catch (JAXBException e) {
      logger.error("Error marshalling save file: " + file.getAbsolutePath(), e);
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

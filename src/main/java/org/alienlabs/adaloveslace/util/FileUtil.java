package org.alienlabs.adaloveslace.util;

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

  /**
   * For all elements of java classpath (starting from app class package if java.class.path system property is empty),
   * get a Collection of resources with a pattern.
   *
   * @param app production app or unit test class
   * @param pattern the pattern to match
   * @return the resources in the order they are found
   */
  public static List<String> getResources(
    Object app, final Pattern pattern){
    final ArrayList<String> retval = new ArrayList<>();
    final String classPath = System.getProperty("java.class.path", ".");
    System.out.println("### " + classPath);

    if (classPath != null && !"".equals(classPath.trim())) {
      final String[] classPathElements = classPath.split(System.getProperty("path.separator"));
      for (final String element : classPathElements) {
        System.out.println("### " + element + ", " + pattern);
        retval.addAll(getResources(element, pattern));
      }
    } else {
      File file = new File(app.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
      String absolutePath = file.getAbsolutePath();
      System.out.println("### " + absolutePath);
      retval.addAll(getResources(absolutePath, pattern));
    }
    return retval;
  }

  private static Collection<String> getResources(
    final String element,
    final Pattern pattern){
    final ArrayList<String> retval = new ArrayList<>();
    final File file = new File(element);
    if(file.isDirectory()){
      retval.addAll(getResourcesFromDirectory(file, pattern));
    } else{
      retval.addAll(getResourcesFromJarFile(file, pattern));
    }
    return retval;
  }

  private static Collection<String> getResourcesFromJarFile(
    final File file,
    final Pattern pattern){
    final ArrayList<String> retval = new ArrayList<>();
    ZipFile zf;
    try{
      zf = new ZipFile(file);
    } catch(final IOException e){
      e.printStackTrace();
      return retval;
    }
    final Enumeration<? extends ZipEntry> e = zf.entries();
    while(e.hasMoreElements()){
      final ZipEntry ze = e.nextElement();
      final String fileName = ze.getName();
      final boolean accept = pattern.matcher(fileName).matches();
      if(accept){
        retval.add(fileName);
      }
    }
    try{
      zf.close();
    } catch(final IOException e1){
      throw new Error(e1);
    }
    return retval;
  }

  private static Collection<String> getResourcesFromDirectory(
    final File directory,
    final Pattern pattern){
    final ArrayList<String> retval = new ArrayList<>();
    final File[] fileList = directory.listFiles();
    if (null != fileList) {
      for (final File file : fileList) {
        if (file.isDirectory()) {
          retval.addAll(getResourcesFromDirectory(file, pattern));
        } else {
          try {
            final String fileName = file.getCanonicalPath();
            final boolean accept = pattern.matcher(fileName).matches();
            if (accept) {
              retval.add(fileName);
            }
          } catch (final IOException e) {
            throw new Error(e);
          }
        }
      }
    }
    return retval;
  }

}

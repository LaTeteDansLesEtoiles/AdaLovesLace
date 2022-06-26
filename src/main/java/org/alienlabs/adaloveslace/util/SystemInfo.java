package org.alienlabs.adaloveslace.util;

public class SystemInfo {

  public static final String JAVA_VERSION = "java.version";
  public static final String JAVAFX_VERSION = "javafx.version";

  private SystemInfo() {
    // Nothing to do here as all is static
  }

  public static String javaVersion() {
    return System.getProperty(JAVA_VERSION);
  }

  public static String javafxVersion() {
    return System.getProperty(JAVAFX_VERSION);
  }

}

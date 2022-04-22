package org.alienlabs.adaloveslace.util;

import org.alienlabs.adaloveslace.App;

import java.io.File;


public class Preferences {

  public static final String XML_FILE_SAVE_PATH = "XML_FILE_SAVE_PATH";

  public Preferences() {
    // Nothing special to do here as it is just a utility class
  }

  public File getPathWithFileValue(String path) {
    java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userNodeForPackage(App.class);
    String filePath = prefs.get(path, null);

    if (filePath != null) {
      return new File(filePath);
    } else {
      return null;
    }
  }

  public File setPathWithFileValue(File file, String path) {
    java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userNodeForPackage(App.class);

    if (file != null) {
      prefs.put(path, file.getAbsolutePath());
    } else {
      prefs.remove(path);
    }

    return file;
  }

  public String getPathWithStringValue(String path) {
    java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userNodeForPackage(App.class);
    return prefs.get(path, null);
  }

  public void setPathWithStringValue(String value, String path) {
    java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userNodeForPackage(App.class);

    if (value != null) {
      prefs.put(path, value);
    } else {
      prefs.remove(path);
    }
  }

}

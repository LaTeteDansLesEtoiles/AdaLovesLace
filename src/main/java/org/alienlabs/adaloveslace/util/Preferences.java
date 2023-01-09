package org.alienlabs.adaloveslace.util;

import org.alienlabs.adaloveslace.App;

import java.io.File;


public class Preferences {

  /** In which folder is the .lace file saved? */
  public static final String LACE_FILE_FOLDER_SAVE_PATH = "LACE_FILE_FOLDER_SAVE_PATH";

  /** What is File path of the saved .lace file? */
  public static final String SAVED_LACE_FILE = "SAVED_LACE_FILE";

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

  public void setPathWithFileValue(File file, String path) {
    java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userNodeForPackage(App.class);

    if (file != null) {
      prefs.put(path, file.getAbsolutePath());
    } else {
      prefs.remove(path);
    }
  }

  public String getStringValue(String key) {
    java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userNodeForPackage(App.class);
    return prefs.get(key, "");
  }

  public void setStringValue(String key, String value) {
    java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userNodeForPackage(App.class);

    if (value != null) {
      prefs.put(key, value);
    } else {
      prefs.remove(key);
    }
  }

}

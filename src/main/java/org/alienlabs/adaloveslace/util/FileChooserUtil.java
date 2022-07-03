package org.alienlabs.adaloveslace.util;

import javafx.stage.FileChooser;

import java.io.File;

import static org.alienlabs.adaloveslace.App.USER_HOME;

public class FileChooserUtil {

  public FileChooserUtil() {
    // Nothing to do here, that's just to avoid an all-static class
  }

  public FileChooser getFileChooser(String dialogTitle, String file, String folderSavePath,
                                            String filteredFiles, String fileFilter) {
    FileChooser export = new FileChooser();
    export.setTitle(dialogTitle);

    Preferences preferences = new Preferences();
    File xmlFile      = preferences.getPathWithFileValue(file);
    File xmlFilePath  = preferences.getPathWithFileValue(folderSavePath);

    if (xmlFilePath == null || !xmlFilePath.canRead() || !xmlFile.canRead()) {
      // We don't know from where to export
      export.setInitialDirectory(new File(System.getProperty(USER_HOME)));
    } else {
      // We do know
      export.setInitialDirectory(xmlFilePath);
    }

    FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(filteredFiles, fileFilter);
    export.getExtensionFilters().add(filter);
    return export;
  }

}

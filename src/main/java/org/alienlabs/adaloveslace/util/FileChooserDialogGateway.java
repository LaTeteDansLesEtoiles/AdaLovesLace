package org.alienlabs.adaloveslace.util;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

/**
 * Indirection for {@link FileChooser} modal dialogs so headless functional tests can supply paths
 * without driving native file choosers.
 */
public interface FileChooserDialogGateway {

  File showOpenDialog(Window owner, FileChooser fileChooser);

  File showSaveDialog(Window owner, FileChooser fileChooser);

  static FileChooserDialogGateway javaFxDefault() {
    return new FileChooserDialogGateway() {
      @Override
      public File showOpenDialog(Window owner, FileChooser fileChooser) {
        return fileChooser.showOpenDialog(owner);
      }

      @Override
      public File showSaveDialog(Window owner, FileChooser fileChooser) {
        return fileChooser.showSaveDialog(owner);
      }
    };
  }
}

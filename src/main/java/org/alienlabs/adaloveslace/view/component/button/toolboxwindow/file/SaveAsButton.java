package org.alienlabs.adaloveslace.view.component.button.toolboxwindow.file;

import javafx.application.Platform;
import javafx.stage.FileChooser;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.util.FileUtil;
import org.alienlabs.adaloveslace.util.Preferences;
import org.alienlabs.adaloveslace.util.ToolboxFilePaths;
import org.alienlabs.adaloveslace.view.component.button.ImageButton;
import org.alienlabs.adaloveslace.view.window.FileAlreadyExistsWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.alienlabs.adaloveslace.App.*;
import static org.alienlabs.adaloveslace.util.Preferences.LACE_FILE_FOLDER_SAVE_PATH;
import static org.alienlabs.adaloveslace.util.Preferences.SAVED_LACE_FILE;

public class SaveAsButton extends ImageButton {

  private static App app;

  public static final String SAVE_FILE_AS_DIALOG_TITLE  = "SaveAs";
  public static final String DIAGRAM_FILES              = ".lace files (*.lace)";
  public static final String DIAGRAM_FILE_FILTER        = "*.lace";

  private static final Logger logger                  = LoggerFactory.getLogger(SaveAsButton.class);

  public SaveAsButton(App app, String buttonLabel) {
    super(buttonLabel);
    SaveAsButton.app = app;
    this.setOnMouseClicked(_ -> onSaveAsAction());
    buildButtonImage("save_as.png");
  }

  public static void onSaveAsAction() {
    saveAsDiagram(app, false);
  }

  public static boolean saveAsDiagram(App app, boolean quitAfterSave) {
    logger.info("Saving file as");
    FileChooser saveAs = new FileChooser();
    saveAs.setTitle(resourceBundle.getString(SAVE_FILE_AS_DIALOG_TITLE));

    Preferences preferences = new Preferences();
    setInitialDirectory(preferences, saveAs);
    saveAs.getExtensionFilters().add(new FileChooser.ExtensionFilter(DIAGRAM_FILES, DIAGRAM_FILE_FILTER));

    File file = app.getFileChooserDialogGateway().showSaveDialog(app.getScene().getWindow(), saveAs);
    if (file == null) {
      return false;
    }

    file = ToolboxFilePaths.ensureFileEndsWithExtension(file, LACE_FILE_EXTENSION);
    FileAlreadyExistsWindow alert = file.exists() ? new FileAlreadyExistsWindow(file) : null;
    preferences.setPathWithFileValue(file.getParentFile(), LACE_FILE_FOLDER_SAVE_PATH);
    if (alert != null && alert.isCancelled()) {
      return false;
    }

    preferences.setPathWithFileValue(file, SAVED_LACE_FILE);
    new FileUtil(app).saveFile(file, app.getOptionalDotGrid().getDiagram(), true);
    if (quitAfterSave) {
      Platform.exit();
    }
    return true;
  }

  private static void setInitialDirectory(Preferences preferences, FileChooser saveAs) {
    File laceFilePath = preferences.getPathWithFileValue(LACE_FILE_FOLDER_SAVE_PATH);
    if (laceFilePath == null || !laceFilePath.exists() || !laceFilePath.isDirectory() || !laceFilePath.canWrite()) {
      File userHome = new File(System.getProperty(USER_HOME));
      saveAs.setInitialDirectory(userHome);
      preferences.setPathWithFileValue(userHome, LACE_FILE_FOLDER_SAVE_PATH);
      return;
    }
    saveAs.setInitialDirectory(laceFilePath);
  }

}

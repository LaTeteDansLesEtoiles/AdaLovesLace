package org.alienlabs.adaloveslace.view.component.button.toolboxwindow;

import javafx.stage.FileChooser;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.util.FileUtil;
import org.alienlabs.adaloveslace.util.Preferences;
import org.alienlabs.adaloveslace.view.component.button.ImageButton;
import org.alienlabs.adaloveslace.view.window.FileAlreadyExistsWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.alienlabs.adaloveslace.App.LACE_FILE_EXTENSION;
import static org.alienlabs.adaloveslace.App.USER_HOME;
import static org.alienlabs.adaloveslace.util.Preferences.LACE_FILE_FOLDER_SAVE_PATH;
import static org.alienlabs.adaloveslace.util.Preferences.SAVED_LACE_FILE;

public class SaveAsButton extends ImageButton {

  public static final String SAVE_FILE_AS_DIALOG_TITLE  = "Save diagram as";
  public static final String DIAGRAM_FILES              = ".lace files (*.lace)";
  public static final String DIAGRAM_FILE_FILTER        = "*.lace";

  private static final Logger logger                  = LoggerFactory.getLogger(SaveAsButton.class);

  public SaveAsButton(App app, String buttonLabel) {
    super(buttonLabel);
    this.setOnMouseClicked(event -> onSaveAsAction(app));
    buildButtonImage("save_as.png");
  }

  public static void onSaveAsAction(App app) {
    logger.debug("Saving file as");

    FileChooser saveAs = new FileChooser();
    saveAs.setTitle(SAVE_FILE_AS_DIALOG_TITLE);

    Preferences preferences = new Preferences();
    File laceFilePath = preferences.getPathWithFileValue(LACE_FILE_FOLDER_SAVE_PATH);
    if (laceFilePath == null || !laceFilePath.exists() || !laceFilePath.isDirectory() || !laceFilePath.canWrite()) {
      File userHome = new File(System.getProperty(USER_HOME));
      saveAs.setInitialDirectory(userHome);
      preferences.setPathWithFileValue(userHome, LACE_FILE_FOLDER_SAVE_PATH);
    } else {
      saveAs.setInitialDirectory(laceFilePath);
    }

    FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(DIAGRAM_FILES, DIAGRAM_FILE_FILTER);
    saveAs.getExtensionFilters().add(filter);

    File file = saveAs.showSaveDialog(app.getScene().getWindow());

    if (file != null) {
      if (!file.getName().endsWith(LACE_FILE_EXTENSION)) {
        file = new File(file.getAbsolutePath() + LACE_FILE_EXTENSION);
      }

      FileAlreadyExistsWindow alert = null;

      if (file.exists()) {
        alert = new FileAlreadyExistsWindow(file);
      }

      preferences.setPathWithFileValue(file.getParentFile(), LACE_FILE_FOLDER_SAVE_PATH);

      if (null == alert || !alert.isCancelled()) {
        preferences.setPathWithFileValue(file, SAVED_LACE_FILE);

        new FileUtil(app).saveFile(
                file,
                app.getOptionalDotGrid().getDiagram(),
                app.getOptionalDotGrid().getDiagram().getCurrentStepIndex()
        );
      }
    }
  }

}

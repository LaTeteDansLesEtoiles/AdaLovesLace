package org.alienlabs.adaloveslace.view.component.button;

import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.util.FileUtil;
import org.alienlabs.adaloveslace.util.Preferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.alienlabs.adaloveslace.util.Preferences.LACE_FILE_FOLDER_SAVE_PATH;
import static org.alienlabs.adaloveslace.util.Preferences.SAVED_LACE_FILE;

public class SaveAsButton extends Button {

  public static final String SAVE_FILE_AS_BUTTON_NAME   = "Save as";
  public static final String SAVE_FILE_AS_DIALOG_TITLE  = "Save diagram as";
  public static final String DIAGRAM_FILES              = ".lace files (*.lace)";
  public static final String DIAGRAM_FILE_FILTER        = "*.lace";

  private static final Logger logger                  = LoggerFactory.getLogger(SaveAsButton.class);

  public SaveAsButton(App app, Pane root, String buttonLabel) {
    super(buttonLabel);
    this.setOnMouseClicked(event -> onSaveAsAction(app, root));
  }

  public static void onSaveAsAction(App app, Pane root) {
    logger.info("Saving file as");

    FileChooser saveAs = new FileChooser();
    saveAs.setTitle(SAVE_FILE_AS_DIALOG_TITLE);

    Preferences preferences = new Preferences();
    File xmlFilePath = preferences.getPathWithFileValue(LACE_FILE_FOLDER_SAVE_PATH);
    if (xmlFilePath == null) {
      File userHome = new File(System.getProperty("user.home"));
      saveAs.setInitialDirectory(userHome);
      preferences.setPathWithFileValue(userHome, LACE_FILE_FOLDER_SAVE_PATH);
    } else {
      saveAs.setInitialDirectory(xmlFilePath);
    }

    FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(DIAGRAM_FILES, DIAGRAM_FILE_FILTER);
    saveAs.getExtensionFilters().add(filter);

    File file = saveAs.showSaveDialog(root.getScene().getWindow());

    if (file != null) {
      preferences.setPathWithFileValue(file, SAVED_LACE_FILE);
      preferences.setPathWithFileValue(file.getParentFile(), LACE_FILE_FOLDER_SAVE_PATH);

      new FileUtil().saveFile(app, file);
    }
  }

}

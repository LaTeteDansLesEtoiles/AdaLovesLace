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

import static org.alienlabs.adaloveslace.util.Preferences.SAVED_XML_FILE;
import static org.alienlabs.adaloveslace.util.Preferences.XML_FILE_FOLDER_SAVE_PATH;

public class SaveButton extends Button {

  public static final String SAVE_FILE_BUTTON_NAME    = "Save";
  public static final String SAVE_FILE_DIALOG_TITLE   = "Save diagram as";
  public static final String DIAGRAM_FILES            = "XML files (*.xml)";
  public static final String DIAGRAM_FILE_FILTER      = "*.xml";

  private static final Logger logger                  = LoggerFactory.getLogger(SaveButton.class);

  public SaveButton(App app, Pane root, String buttonLabel) {
    super(buttonLabel);
    this.setOnMouseClicked(event -> onSaveAction(app, root));
  }

  public static void onSaveAction(App app, Pane root) {
    logger.info("Saving file");

    File file;

    Preferences preferences = new Preferences();
    File xmlFilePath = preferences.getPathWithFileValue(SAVED_XML_FILE);

    if (xmlFilePath == null || !xmlFilePath.canWrite()) {
      // Save as anyway, since we don't know where to save
      FileChooser saveAs = new FileChooser();
      saveAs.setTitle(SAVE_FILE_DIALOG_TITLE);

      File userHome = new File(System.getProperty("user.home"));
      saveAs.setInitialDirectory(userHome);
      preferences.setPathWithFileValue(userHome, XML_FILE_FOLDER_SAVE_PATH);

      FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(DIAGRAM_FILES, DIAGRAM_FILE_FILTER);
      saveAs.getExtensionFilters().add(filter);
      saveAs.setInitialDirectory(preferences.getPathWithFileValue(XML_FILE_FOLDER_SAVE_PATH));

      file = saveAs.showSaveDialog(root.getScene().getWindow());
    } else {
      // We know where to save
      file = xmlFilePath;
    }

    if (file != null) {
      // Save
      preferences.setPathWithFileValue(file,                  SAVED_XML_FILE);
      preferences.setPathWithFileValue(file.getParentFile(),  XML_FILE_FOLDER_SAVE_PATH);

      new FileUtil().saveFile(app, file);
    }
  }

}

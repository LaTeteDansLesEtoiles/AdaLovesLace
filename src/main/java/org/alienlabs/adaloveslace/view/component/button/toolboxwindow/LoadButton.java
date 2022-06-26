package org.alienlabs.adaloveslace.view.component.button.toolboxwindow;

import javafx.stage.FileChooser;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.util.FileUtil;
import org.alienlabs.adaloveslace.util.Preferences;
import org.alienlabs.adaloveslace.view.component.button.ImageButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.alienlabs.adaloveslace.util.Preferences.LACE_FILE_FOLDER_SAVE_PATH;
import static org.alienlabs.adaloveslace.util.Preferences.SAVED_LACE_FILE;

public class LoadButton extends ImageButton {

  public static final String LOAD_BUTTON_NAME         = "          Load          ";

  public static final String LOAD_FILE_DIALOG_TITLE   = "Load diagram";
  public static final String DIAGRAM_FILES            = ".lace files (*.lace)";
  public static final String DIAGRAM_FILE_FILTER      = "*.lace";

  private static final Logger logger                  = LoggerFactory.getLogger(LoadButton.class);

  public LoadButton(App app, String buttonLabel) {
    super(buttonLabel);
    this.setOnMouseClicked(event -> onLoadAction(app));
    buildButtonImage("load.png");
  }

  public static void onLoadAction(App app) {
    logger.info("Load file");

    FileChooser load = new FileChooser();
    load.setTitle(LOAD_FILE_DIALOG_TITLE);

    Preferences preferences = new Preferences();
    File xmlFile      = preferences.getPathWithFileValue(SAVED_LACE_FILE);
    File xmlFilePath  = preferences.getPathWithFileValue(LACE_FILE_FOLDER_SAVE_PATH);

    if (xmlFilePath == null || !xmlFilePath.canRead() || !xmlFile.canRead()) {
      // We don't know from where to load
      load.setInitialDirectory(new File(System.getProperty("user.home")));
    } else {
      // We do know
      load.setInitialDirectory(xmlFilePath);
    }

    FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(DIAGRAM_FILES, DIAGRAM_FILE_FILTER);
    load.getExtensionFilters().add(filter);

    File file = load.showOpenDialog(App.getScene().getWindow());

    if (file != null) {
      new FileUtil().loadFromLaceFile(app, file);
    }
  }

}

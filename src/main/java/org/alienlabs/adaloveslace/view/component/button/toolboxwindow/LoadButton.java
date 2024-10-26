package org.alienlabs.adaloveslace.view.component.button.toolboxwindow;

import javafx.stage.FileChooser;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.util.FileChooserUtil;
import org.alienlabs.adaloveslace.util.FileUtil;
import org.alienlabs.adaloveslace.view.component.button.ImageButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.alienlabs.adaloveslace.util.Preferences.LACE_FILE_FOLDER_SAVE_PATH;
import static org.alienlabs.adaloveslace.util.Preferences.SAVED_LACE_FILE;

public class LoadButton extends ImageButton {

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
    logger.debug("Load file");

    FileChooser load = new FileChooserUtil().getFileChooser(LOAD_FILE_DIALOG_TITLE, SAVED_LACE_FILE, LACE_FILE_FOLDER_SAVE_PATH, DIAGRAM_FILES, DIAGRAM_FILE_FILTER);
    File file = load.showOpenDialog(app.getScene().getWindow());

    if (file != null) {
      new FileUtil().buildUiFromLaceFile(app, file);
      app.getPrimaryStage().requestFocus();
    }
  }

}

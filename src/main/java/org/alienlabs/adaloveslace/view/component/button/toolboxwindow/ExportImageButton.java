package org.alienlabs.adaloveslace.view.component.button.toolboxwindow;

import javafx.stage.FileChooser;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.util.ImageUtil;
import org.alienlabs.adaloveslace.util.Preferences;
import org.alienlabs.adaloveslace.view.component.button.ImageButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.alienlabs.adaloveslace.App.EXPORT_FILE_TYPE;
import static org.alienlabs.adaloveslace.App.USER_HOME;
import static org.alienlabs.adaloveslace.util.Preferences.LACE_FILE_FOLDER_SAVE_PATH;
import static org.alienlabs.adaloveslace.util.Preferences.SAVED_LACE_FILE;

public class ExportImageButton extends ImageButton {

  public static final String EXPORT_IMAGE_BUTTON_NAME   = "        Export an image        ";

  public static final String EXPORT_IMAGE_DIALOG_TITLE  = "Export a diagram image";

  public static final String EXPORTED_FILES             = ".png files (*.png)";

  public static final String EXPORT_FILE_FILTER         = "*.png";

  private static final Logger logger                    = LoggerFactory.getLogger(ExportImageButton.class);

  public ExportImageButton(App app, String buttonLabel) {
    super(buttonLabel);
    this.setOnMouseClicked(event -> onExportAction(app));
    buildButtonImage("export.png");
  }

  public static void onExportAction(App app) {
    logger.info("Exporting image file");

    FileChooser export = new FileChooser();
    export.setTitle(EXPORT_IMAGE_DIALOG_TITLE);

    Preferences preferences = new Preferences();
    File xmlFile      = preferences.getPathWithFileValue(SAVED_LACE_FILE);
    File xmlFilePath  = preferences.getPathWithFileValue(LACE_FILE_FOLDER_SAVE_PATH);

    if (xmlFilePath == null || !xmlFilePath.canRead() || !xmlFile.canRead()) {
      // We don't know from where to export
      export.setInitialDirectory(new File(System.getProperty(USER_HOME)));
    } else {
      // We do know
      export.setInitialDirectory(xmlFilePath);
    }

    FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(EXPORTED_FILES, EXPORT_FILE_FILTER);
    export.getExtensionFilters().add(filter);

    File file = export.showSaveDialog(app.getScene().getWindow());

    if (file != null) {
      new ImageUtil(app).buildWritableImage(
        file.getAbsolutePath().endsWith(EXPORT_FILE_TYPE) ?
          file.getAbsolutePath() :
          file.getAbsolutePath() + EXPORT_FILE_TYPE
      );
    }
  }

}

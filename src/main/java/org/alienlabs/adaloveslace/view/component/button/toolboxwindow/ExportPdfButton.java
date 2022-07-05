package org.alienlabs.adaloveslace.view.component.button.toolboxwindow;

import javafx.stage.FileChooser;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.util.FileChooserUtil;
import org.alienlabs.adaloveslace.util.FileUtil;
import org.alienlabs.adaloveslace.util.ImageUtil;
import org.alienlabs.adaloveslace.view.component.button.ImageButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.alienlabs.adaloveslace.App.*;
import static org.alienlabs.adaloveslace.util.Preferences.LACE_FILE_FOLDER_SAVE_PATH;
import static org.alienlabs.adaloveslace.util.Preferences.SAVED_LACE_FILE;

public class ExportPdfButton extends ImageButton {

  public static final String EXPORT_PDF_BUTTON_NAME     = "   Export as PDF  ";

  public static final String EXPORT_PDF_DIALOG_TITLE    = "Export a diagram as PDF";

  public static final String EXPORTED_FILES             = ".pdf files (*.pdf)";

  public static final String EXPORT_PDF_FILE_FILTER     = "*.pdf";

  private static final Logger logger                    = LoggerFactory.getLogger(ExportPdfButton.class);
  public static final String OUTPUT_TEMPORARY_IMAGE = "out.png";

  public ExportPdfButton(App app, String buttonLabel) {
    super(buttonLabel);
    this.setOnMouseClicked(event -> onExportAction(app));
    buildButtonImage("export.png");
  }

  public static void onExportAction(App app) {
    logger.info("Exporting image file");

    FileChooser export = new FileChooserUtil().getFileChooser(EXPORT_PDF_DIALOG_TITLE, SAVED_LACE_FILE, LACE_FILE_FOLDER_SAVE_PATH, EXPORTED_FILES, EXPORT_PDF_FILE_FILTER);
    File file = export.showSaveDialog(app.getScene().getWindow());

    if (file != null) {
      String pdfFilename = file.getAbsolutePath().endsWith(EXPORT_PDF_FILE_TYPE) ?
        file.getAbsolutePath() :
        file.getAbsolutePath() + EXPORT_PDF_FILE_TYPE;

      String imageFilename = System.getProperty(USER_HOME) + File.separator + PROJECT_NAME + File.separator +
        PATTERNS_DIRECTORY_NAME + File.separator + OUTPUT_TEMPORARY_IMAGE;

      new ImageUtil(app).buildWritableImageWithoutTechnicalElements(imageFilename);
      new FileUtil().generatePdf(pdfFilename, imageFilename);
    }
  }

}

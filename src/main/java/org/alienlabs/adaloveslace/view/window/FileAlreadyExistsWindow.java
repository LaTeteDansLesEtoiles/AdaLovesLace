package org.alienlabs.adaloveslace.view.window;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

import static javafx.scene.control.Alert.AlertType.CONFIRMATION;
import static javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE;
import static org.alienlabs.adaloveslace.App.resourceBundle;

public class FileAlreadyExistsWindow {

  public static final double CREATE_PATTERN_WINDOW_WIDTH    = 550d;

  public static final String FILE_ALREADY_EXISTS_WINDOW_TITLE     = "FILE_ALREADY_EXISTS_WINDOW_TITLE";
  public static final String FILE_ALREADY_EXISTS_HEADER_TEXT      = "FILE_ALREADY_EXISTS_HEADER_TEXT";
  public static final String FILE_ALREADY_EXISTS_CONTENT_TEXT     = "FILE_ALREADY_EXISTS_CONTENT_TEXT";
  public static final String SAVE_BUTTON_TEXT                     = "SAVE_BUTTON_TEXT";
  public static final String CANCEL_BUTTON_TEXT                   = "CANCEL_BUTTON_TEXT";

  private static final Logger logger = LoggerFactory.getLogger(FileAlreadyExistsWindow.class);
  private final File fileToSave;
  private boolean cancelled;

  public FileAlreadyExistsWindow(File file) {
    this.fileToSave = file;
    Alert alert = new Alert(CONFIRMATION);

    ButtonType saveFileButton = buildAlertWindow(alert);
    Optional<ButtonType> result = alert.showAndWait();

    if (result.isPresent() && result.get() == saveFileButton) {
      logger.debug("Accepted file overwrite");

      try {
        Files.delete(fileToSave.toPath());
        this.cancelled = false;
      } catch (IOException e) {
        this.cancelled = true;
        logger.error("Error deleting file during pattern creation window!", e);
      }
    } else {
      this.cancelled = true;
      logger.debug("File overwrite cancelled");
    }

    alert.close();
  }

  private ButtonType buildAlertWindow(Alert alert) {
    alert.setTitle(resourceBundle.getString(FILE_ALREADY_EXISTS_WINDOW_TITLE));
    alert.setHeaderText(resourceBundle.getString(FILE_ALREADY_EXISTS_HEADER_TEXT));
    alert.setContentText(resourceBundle.getString(FILE_ALREADY_EXISTS_CONTENT_TEXT));

    ButtonType saveFileButton  = new ButtonType(resourceBundle.getString(SAVE_BUTTON_TEXT));
    ButtonType cancelButton = new ButtonType(resourceBundle.getString(CANCEL_BUTTON_TEXT), CANCEL_CLOSE);

    alert.getButtonTypes().setAll(saveFileButton, cancelButton);
    return saveFileButton;
  }

  public boolean isCancelled() {
    return this.cancelled;
  }

}

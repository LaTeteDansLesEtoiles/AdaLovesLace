package org.alienlabs.adaloveslace.view.component.button.toolboxwindow;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.util.FileUtil;
import org.alienlabs.adaloveslace.util.Preferences;
import org.alienlabs.adaloveslace.view.component.button.ImageButton;
import org.alienlabs.adaloveslace.view.window.FileAlreadyExistsWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Optional;

import static javafx.scene.control.Alert.AlertType.CONFIRMATION;
import static javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE;
import static org.alienlabs.adaloveslace.App.*;
import static org.alienlabs.adaloveslace.util.Preferences.LACE_FILE_FOLDER_SAVE_PATH;
import static org.alienlabs.adaloveslace.util.Preferences.SAVED_LACE_FILE;
import static org.alienlabs.adaloveslace.view.component.button.toolboxwindow.file.SaveAsButton.DIAGRAM_FILES;
import static org.alienlabs.adaloveslace.view.component.button.toolboxwindow.file.SaveAsButton.DIAGRAM_FILE_FILTER;
import static org.alienlabs.adaloveslace.view.window.MainWindow.SAVE_FILE_AS;

public class QuitButton extends ImageButton {

  private static final String QUIT_WINDOW_TITLE         = "QuitWindowTitle";
  private static final String QUIT_HEADER_TEXT          = "QuitHeaderText";
  private static final String QUIT_CONTENT_TEXT         = "QuitContentText";
  private static final String QUIT_BUTTON_TEXT          = "QuitButtonText";
  private static final String SAVE_AND_QUIT_BUTTON_TEXT = "SaveAndQuitButtonText";
  private static final String CANCEL_QUIT_BUTTON_TEXT   = "CancelQuitButtonText";

  private static final Logger logger  = LoggerFactory.getLogger(QuitButton.class);
    private final App app;

  public QuitButton(App app, String buttonLabel) {
    super(buttonLabel);
    this.app = app;
    this.setOnMouseClicked(this::onQuitAction);
    buildButtonImage("quit.png");
  }

  public void onQuitAction(Event event) {
    logger.debug("Exiting app?");

    Alert quitAlert = new Alert(CONFIRMATION);
    quitAlert.setTitle(resourceBundle.getString(QUIT_WINDOW_TITLE));
    quitAlert.setHeaderText(resourceBundle.getString(QUIT_HEADER_TEXT));
    quitAlert.setContentText(resourceBundle.getString(QUIT_CONTENT_TEXT));

    ButtonType saveAndQuitButton = new ButtonType(resourceBundle.getString(SAVE_AND_QUIT_BUTTON_TEXT));
    ButtonType quitButton = new ButtonType(resourceBundle.getString(QUIT_BUTTON_TEXT));
    ButtonType cancelButton = new ButtonType(resourceBundle.getString(CANCEL_QUIT_BUTTON_TEXT), CANCEL_CLOSE);

    quitAlert.getButtonTypes().setAll(saveAndQuitButton, quitButton, cancelButton);
    Optional<ButtonType> result = quitAlert.showAndWait();

    if (result.isPresent() && result.get() == quitButton) {
      logger.debug("Exiting app");
      Platform.exit();
    }

    if (result.isPresent() && result.get() == saveAndQuitButton) {
      saveAndQuit(app);
    }

    logger.debug("Cancelled exiting app");
    event.consume();
  }

  private void saveAndQuit(App app) {
    logger.debug("Saving diagram & exiting app");

    FileChooser saveAs = new FileChooser();
    saveAs.setTitle(resourceBundle.getString(SAVE_FILE_AS));

    Preferences preferences = new Preferences();
    File laceFilePath = preferences.getPathWithFileValue(LACE_FILE_FOLDER_SAVE_PATH);

    setInitialDirectory(laceFilePath, saveAs, preferences);

    FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(DIAGRAM_FILES, DIAGRAM_FILE_FILTER);
    saveAs.getExtensionFilters().add(filter);

    File file = saveAs.showSaveDialog(app.getScene().getWindow());
    treatFile(app, file, preferences);
  }

  private void treatFile(App app, File file, Preferences preferences) {
    if (file != null) {
      logger.debug("Saving file as");

      if (!file.getName().endsWith(LACE_FILE_EXTENSION)) {
        file = new File(file.getAbsolutePath() + LACE_FILE_EXTENSION);
      }

      FileAlreadyExistsWindow alert = null;

      if (file.exists()) {
        alert = new FileAlreadyExistsWindow(file);
      }

      preferences.setPathWithFileValue(file.getParentFile(), LACE_FILE_FOLDER_SAVE_PATH);
      saveAndQuit(app, alert, preferences, file);
    }
  }

  private void saveAndQuit(App app, FileAlreadyExistsWindow alert, Preferences preferences, File file) {
    if (null == alert || !alert.isCancelled()) {
      preferences.setPathWithFileValue(file, SAVED_LACE_FILE);

      new FileUtil(app).saveFile(
              file,
              app.getOptionalDotGrid().getDiagram(),
              true
      );

      Platform.exit();
    }
  }

  private void setInitialDirectory(File laceFilePath, FileChooser saveAs, Preferences preferences) {
    if (laceFilePath == null || !laceFilePath.exists() || !laceFilePath.isDirectory() || !laceFilePath.canWrite()) {
      File userHome = new File(System.getProperty(USER_HOME));
      saveAs.setInitialDirectory(userHome);
      preferences.setPathWithFileValue(userHome, LACE_FILE_FOLDER_SAVE_PATH);
    } else {
      saveAs.setInitialDirectory(laceFilePath);
    }
  }

}

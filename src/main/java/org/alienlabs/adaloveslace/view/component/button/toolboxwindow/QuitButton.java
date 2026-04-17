package org.alienlabs.adaloveslace.view.component.button.toolboxwindow;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.view.component.button.ImageButton;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.file.SaveAsButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static javafx.scene.control.Alert.AlertType.CONFIRMATION;
import static javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE;
import static org.alienlabs.adaloveslace.App.*;
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
    logger.info("Exiting app?");

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
      logger.info("Exiting app");
      Platform.exit();
    }

    if (result.isPresent() && result.get() == saveAndQuitButton) {
      saveAndQuit(app);
    }

    logger.info("Cancelled exiting app");
    event.consume();
  }

  private void saveAndQuit(App app) {
    logger.info("Saving diagram & exiting app");
    SaveAsButton.saveAsDiagram(app, true);
  }

}

package org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.view.component.button.ImageButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static javafx.scene.control.Alert.AlertType.CONFIRMATION;
import static javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE;
import static org.alienlabs.adaloveslace.App.resourceBundle;

public class ResetDiagramButton extends ImageButton {

  private static App app;

  private static final String RESET_DIAGRAM_WINDOW_TITLE        = "ResetDiagramWindowTitle";
  private static final String RESET_DIAGRAM_HEADER_TEXT         = "ResetDiagramHeaderText";
  private static final String RESET_DIAGRAM_CONTENT_TEXT        = "ResetDiagramContentText";
  private static final String RESET_DIAGRAM_BUTTON_TEXT         = "ResetDiagramButtonText";
  private static final String CANCEL_RESET_DIAGRAM_BUTTON_TEXT  = "CancelResetDiagramButtonText";

  private static final Logger logger = LoggerFactory.getLogger(ResetDiagramButton.class);

    public ResetDiagramButton(String buttonLabel, App app) {
    super(buttonLabel);
      ResetDiagramButton.app = app;
        this.setOnMouseClicked(_ -> resetDiagram());
    buildButtonImage("reset_diagram.png");
  }

  public static void resetDiagram() {
    logger.debug("Event reset diagram");

    Alert alert = new Alert(CONFIRMATION);
    alert.setTitle(resourceBundle.getString(RESET_DIAGRAM_WINDOW_TITLE));
    alert.setHeaderText(resourceBundle.getString(RESET_DIAGRAM_HEADER_TEXT));
    alert.setContentText(resourceBundle.getString(RESET_DIAGRAM_CONTENT_TEXT));

    ButtonType resetDiagramButton  = new ButtonType(resourceBundle.getString(RESET_DIAGRAM_BUTTON_TEXT));
    ButtonType cancelButton = new ButtonType(resourceBundle.getString(CANCEL_RESET_DIAGRAM_BUTTON_TEXT), CANCEL_CLOSE);

    alert.getButtonTypes().setAll(resetDiagramButton, cancelButton);
    Optional<ButtonType> result = alert.showAndWait();

    if (result.isPresent() && result.get() == resetDiagramButton) {
      logger.debug("Reset diagram");

      app.getOptionalDotGrid().getDiagram().resetDiagram(app);
      app.getOptionalDotGrid().layoutChildren();
    }

    logger.debug("Cancelled reset diagram");
  }

}

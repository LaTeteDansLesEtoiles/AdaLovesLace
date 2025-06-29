package org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid;

import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.util.FileChooserUtil;
import org.alienlabs.adaloveslace.util.ImageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static javafx.scene.control.Alert.AlertType.CONFIRMATION;
import static javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE;
import static org.alienlabs.adaloveslace.App.RESET_KNOTS_BUTTON_NAME;
import static org.alienlabs.adaloveslace.App.resourceBundle;
import static org.alienlabs.adaloveslace.view.window.ToolboxWindow.PATTERN_TEXT_AND_COLOR_BUTTON;

public class ResetKnotsButton extends ToggleButton {

  private static App app;

  private static final String RESET_KNOTS_WINDOW_TITLE        = "ResetKnotsWindowTitle";
  private static final String RESET_KNOTS_HEADER_TEXT         = "ResetKnotsHeaderText";
  private static final String RESET_KNOTS_CONTENT_TEXT        = "ResetKnotsContentText";
  private static final String RESET_KNOTS_BUTTON_TEXT         = "ResetKnotsButtonText";
  private static final String CANCEL_RESET_KNOTS_BUTTON_TEXT  = "CancelResetKnots";

  private static final Logger logger                  = LoggerFactory.getLogger(ResetKnotsButton.class);

  public ResetKnotsButton(App app) {
    super(resourceBundle.getString(RESET_KNOTS_BUTTON_NAME));

    ResetKnotsButton.app = app;

    this.getStyleClass().add(PATTERN_TEXT_AND_COLOR_BUTTON);
    this.setSelected(false);

    this.setOnMouseClicked(onResetKnotsButtonClicked);
  }

  private final EventHandler<MouseEvent> onResetKnotsButtonClicked = _ -> {
    Alert alert = new Alert(CONFIRMATION);
    alert.setTitle(resourceBundle.getString(RESET_KNOTS_WINDOW_TITLE));
    alert.setHeaderText(resourceBundle.getString(RESET_KNOTS_HEADER_TEXT));
    alert.setContentText(resourceBundle.getString(RESET_KNOTS_CONTENT_TEXT));

    ButtonType resetKnotsButton  = new ButtonType(resourceBundle.getString(RESET_KNOTS_BUTTON_TEXT));
    ButtonType cancelButton = new ButtonType(resourceBundle.getString(CANCEL_RESET_KNOTS_BUTTON_TEXT), CANCEL_CLOSE);

    alert.getButtonTypes().setAll(resetKnotsButton, cancelButton);
    Optional<ButtonType> result = alert.showAndWait();

    if (result.isPresent() && result.get() == resetKnotsButton) {
      app.unselectPatternsAndTextButtons();
      app.getMovablePane().setOnKeyPressed(null);

      new ImageUtil(app).backupKnots();
      app.getOptionalDotGrid().getDiagram().getPatterns().clear();
      app.getOptionalDotGrid().getDiagram().resetDiagram(app);
      new FileChooserUtil().restartGui(app);
    }
  };

}

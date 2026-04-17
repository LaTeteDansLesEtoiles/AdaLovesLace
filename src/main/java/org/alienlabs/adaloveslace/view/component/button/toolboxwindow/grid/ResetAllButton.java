package org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid;

import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.util.FileChooserUtil;
import org.alienlabs.adaloveslace.util.ImageUtil;
import java.util.Optional;

import static javafx.scene.control.Alert.AlertType.CONFIRMATION;
import static javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE;
import static org.alienlabs.adaloveslace.App.RESET_ALL_BUTTON_NAME;
import static org.alienlabs.adaloveslace.App.resourceBundle;
import static org.alienlabs.adaloveslace.view.window.ToolboxWindow.PATTERN_TEXT_AND_COLOR_BUTTON;

public class ResetAllButton extends ToggleButton {

  private static App app;

  private static final String RESET_ALL_WINDOW_TITLE        = "ResetAllWindowTitle";
  private static final String RESET_ALL_HEADER_TEXT         = "ResetAllHeaderText";
  private static final String RESET_ALL_CONTENT_TEXT        = "ResetAllContentText";
  private static final String RESET_ALL_BUTTON_TEXT         = "ResetAllButtonText";
  private static final String CANCEL_RESET_ALL_BUTTON_TEXT  = "CancelResetAll";

  public ResetAllButton(App app) {
    super(resourceBundle.getString(RESET_ALL_BUTTON_NAME));

    ResetAllButton.app = app;

    this.getStyleClass().add(PATTERN_TEXT_AND_COLOR_BUTTON);
    this.setSelected(false);

    this.setOnMouseClicked(onResetAllButtonClicked);
  }

  private final EventHandler<MouseEvent> onResetAllButtonClicked = _ -> {
    Alert alert = new Alert(CONFIRMATION);
    alert.setTitle(resourceBundle.getString(RESET_ALL_WINDOW_TITLE));
    alert.setHeaderText(resourceBundle.getString(RESET_ALL_HEADER_TEXT));
    alert.setContentText(resourceBundle.getString(RESET_ALL_CONTENT_TEXT));

    ButtonType resetKnotsButton  = new ButtonType(resourceBundle.getString(RESET_ALL_BUTTON_TEXT));
    ButtonType cancelButton = new ButtonType(resourceBundle.getString(CANCEL_RESET_ALL_BUTTON_TEXT), CANCEL_CLOSE);

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

package org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.view.window.event.GridEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static org.alienlabs.adaloveslace.App.COLOR_BUTTON_NAME;
import static org.alienlabs.adaloveslace.App.resourceBundle;
import static org.alienlabs.adaloveslace.view.window.ToolboxWindow.*;

public class ColorButton extends ToggleButton {

  private static App app;
  private static final String CHOOSE_KNOT_COLOR = "ChooseKnotColor";
  private static final int CHOOSE_COLOR_DIALOG_WIDTH = 300;

  private static final Logger logger = LoggerFactory.getLogger(ColorButton.class);

  public ColorButton(App app) {
    super(resourceBundle.getString(COLOR_BUTTON_NAME));

    ColorButton.app = app;

    this.getStyleClass().add(PATTERN_TEXT_AND_COLOR_BUTTON);
    this.setSelected(false);

    this.setOnMouseClicked(onColorButtonClicked);
  }

  private final EventHandler<MouseEvent> onColorButtonClicked = _ -> {
    this.setSelected(true);
    this.getStyleClass().add(BUTTON_WAITING_SELECTION);

    PauseTransition pause = new PauseTransition(Duration.millis(250d));
    pause.setOnFinished(_ -> {
      this.getStyleClass().remove(BUTTON_WAITING_SELECTION);
      app.getToolboxWindow().getBackInBlackButton().getStyleClass().remove(BUTTON_SELECTED);
      this.getStyleClass().add(BUTTON_SELECTED);

      ColorPicker colorPicker = new ColorPicker(Color.BLACK);
      Dialog<Color> dialog = new Dialog<>();
      dialog.setTitle(resourceBundle.getString(CHOOSE_KNOT_COLOR));
      dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
      dialog.getDialogPane().setContent(new VBox(10, colorPicker));
      dialog.getDialogPane().setMinWidth(CHOOSE_COLOR_DIALOG_WIDTH);

      dialog.setResultConverter(dialogButton -> {
        if (dialogButton == ButtonType.OK) {
          return colorPicker.getValue();
        }
        return null;
      });

      Platform.runLater(() -> {
        Optional<Color> result = dialog.showAndWait();
        result.ifPresent(color -> logger.debug("Chosen color: {}", color));
        app.getOptionalDotGrid().getDiagram().setCurrentColor(result.orElse(null));
        GridEvents.setCurrentImageView(null);
        
        // Synchroniser l'?tat du bouton "Back to Black" avec la couleur
        if (result.isPresent()) {
          // Une couleur a ?t? choisie : d?s?lectionner "Back to Black"
          app.getToolboxWindow().getBackInBlackButton().setSelected(false);
          app.getToolboxWindow().getBackInBlackButton().getStyleClass().remove(BUTTON_SELECTED);
        } else {
          // Aucune couleur (annulation) : s?lectionner "Back to Black"
          app.getToolboxWindow().getBackInBlackButton().setSelected(true);
          app.getToolboxWindow().getBackInBlackButton().getStyleClass().add(BUTTON_SELECTED);
        }
      });
    });

    pause.playFromStart();
  };

}

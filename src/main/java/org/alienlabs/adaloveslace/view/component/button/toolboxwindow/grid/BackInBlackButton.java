package org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid;

import javafx.animation.PauseTransition;
import javafx.event.EventHandler;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.enumeration.MouseMode;
import org.alienlabs.adaloveslace.view.component.button.geometrywindow.SelectionButton;
import org.alienlabs.adaloveslace.view.window.event.GridEvents;

import static org.alienlabs.adaloveslace.App.resourceBundle;
import static org.alienlabs.adaloveslace.view.window.ToolboxWindow.*;
import static org.alienlabs.adaloveslace.view.window.event.GridEvents.keyHandler;

// 🤘
public class BackInBlackButton extends ToggleButton {

  private static App app;
  private static final String BACK_IN_BLACK_BUTTON_NAME = "BackInBlack";

  public BackInBlackButton(App app) {
    super(resourceBundle.getString(BACK_IN_BLACK_BUTTON_NAME));

    BackInBlackButton.app = app;

    this.getStyleClass().add(PATTERN_TEXT_AND_COLOR_BUTTON);
    // Synchroniser l'état initial avec la couleur actuelle
    // Si une couleur est déjà définie, ne pas sélectionner "Back in Black"
    if (app.getOptionalDotGrid().getDiagram().getCurrentColor() == null) {
      this.getStyleClass().add(BUTTON_SELECTED);
      this.setSelected(true);
    } else {
      this.setSelected(false);
    }

    this.setOnMouseClicked(onBackInBlackButtonClicked);
  }

  private final EventHandler<MouseEvent> onBackInBlackButtonClicked = _ -> {
    this.setSelected(true);
    this.getStyleClass().add(BUTTON_WAITING_SELECTION);

    PauseTransition pause = new PauseTransition(Duration.millis(250d));
    pause.setOnFinished(e -> {
      this.getStyleClass().remove(BUTTON_WAITING_SELECTION);
      this.getStyleClass().add(BUTTON_SELECTED);
      app.getToolboxWindow().getColorButton().getStyleClass().remove(BUTTON_SELECTED);
      app.getOptionalDotGrid().getDiagram().setCurrentColor(null);
      GridEvents.setCurrentImageView(null);
      
      // Mettre en mode sélection quand on active "Back in Black"
      app.getOptionalDotGrid().getDiagram().setCurrentMode(MouseMode.SELECTION);
      app.getScene().addEventHandler(KeyEvent.KEY_PRESSED, keyHandler);
      
      SelectionButton.refreshSelectionHandlers(app);
      SelectionButton.setSelectionModeButtonState(app);
    });

    pause.playFromStart();
  };

}

package org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid;

import javafx.animation.PauseTransition;
import javafx.event.EventHandler;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import org.alienlabs.adaloveslace.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.alienlabs.adaloveslace.App.resourceBundle;
import static org.alienlabs.adaloveslace.view.window.ToolboxWindow.*;

// 🤘
public class BackInBlackButton extends ToggleButton {

  private static App app;
  private static final String BACK_TO_BLACK_BUTTON_NAME = "BackToBlack";

  private static final Logger logger = LoggerFactory.getLogger(BackInBlackButton.class);

  public BackInBlackButton(App app) {
    super(resourceBundle.getString(BACK_TO_BLACK_BUTTON_NAME));

    BackInBlackButton.app = app;

    this.getStyleClass().add(PATTERN_TEXT_AND_COLOR_BUTTON);
    this.getStyleClass().add(BUTTON_SELECTED);
    this.setSelected(true);

    this.setOnMouseClicked(onBackInBlackButtonClicked);
  }

  private final EventHandler<MouseEvent> onBackInBlackButtonClicked = _ -> {
    this.setSelected(true);
    this.getStyleClass().add(BUTTON_WAITING_SELECTION);

    PauseTransition pause = new PauseTransition(Duration.millis(500));
    pause.setOnFinished(e -> {
      this.getStyleClass().remove(BUTTON_WAITING_SELECTION);
      this.getStyleClass().add(BUTTON_SELECTED);
      app.getToolboxWindow().getColorButton().getStyleClass().remove(BUTTON_SELECTED);
      app.getOptionalDotGrid().getDiagram().setCurrentColor(null);
    });

    pause.playFromStart();
  };

}

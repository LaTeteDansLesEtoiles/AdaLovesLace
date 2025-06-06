package org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid;

import javafx.animation.PauseTransition;
import javafx.event.EventHandler;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.enumeration.PatternOrTextMode;
import org.alienlabs.adaloveslace.util.Events;

import static org.alienlabs.adaloveslace.App.TEXT_BUTTON_NAME;
import static org.alienlabs.adaloveslace.App.resourceBundle;
import static org.alienlabs.adaloveslace.view.window.ToolboxWindow.*;

public class TextButton extends ToggleButton {

  private static App app;

  public TextButton(App app) {
    super(resourceBundle.getString(TEXT_BUTTON_NAME));

    TextButton.app = app;

    this.getStyleClass().add(PATTERN_AND_TEXT_BUTTON);
    this.setSelected(false);

    this.setOnMouseClicked(onTextButtonClicked);
  }

  private final EventHandler<MouseEvent> onTextButtonClicked = event -> {
    app.getOptionalDotGrid().getCurrentPatternOrTextModeProperty().set(PatternOrTextMode.TEXT);
    app.unselectPatternsAndTextButtons();
    app.getMovablePane().setOnKeyPressed(null);
    app.getMovablePane().addEventHandler(MouseEvent.MOUSE_MOVED, Events.getGridHoverEventHandler(app));
    this.setSelected(true);
    this.getStyleClass().add(PATTERN_AND_TEXT_BUTTON_WAITING_SELECTION);

    PauseTransition pause = new PauseTransition(Duration.seconds(1));
    pause.setOnFinished(e -> {
      this.getStyleClass().remove(PATTERN_AND_TEXT_BUTTON_WAITING_SELECTION);
      this.getStyleClass().add(PATTERN_AND_TEXT_BUTTON_SELECTED);
    });
    pause.play();
  };

}

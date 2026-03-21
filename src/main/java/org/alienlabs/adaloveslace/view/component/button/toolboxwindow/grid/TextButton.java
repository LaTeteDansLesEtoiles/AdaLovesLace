package org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid;

import javafx.animation.PauseTransition;
import javafx.event.EventHandler;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.enumeration.MouseMode;
import org.alienlabs.adaloveslace.domain.enumeration.PatternOrTextMode;
import org.alienlabs.adaloveslace.view.window.event.GridEvents;

import static org.alienlabs.adaloveslace.App.TEXT_BUTTON_NAME;
import static org.alienlabs.adaloveslace.App.resourceBundle;
import static org.alienlabs.adaloveslace.view.window.ToolboxWindow.*;
import static org.alienlabs.adaloveslace.view.window.event.GridEvents.keyHandler;

public class TextButton extends ToggleButton {

  private static App app;

  public TextButton(App app) {
    super(resourceBundle.getString(TEXT_BUTTON_NAME));

    TextButton.app = app;

    this.getStyleClass().add(PATTERN_TEXT_AND_COLOR_BUTTON);
    this.setSelected(false);

    this.setOnMouseClicked(onTextButtonClicked);
  }

  private final EventHandler<MouseEvent> onTextButtonClicked = event -> {
    app.getOptionalDotGrid().getCurrentPatternOrTextModeProperty().set(PatternOrTextMode.TEXT);
    app.getOptionalDotGrid().getDiagram().setCurrentMode(MouseMode.DRAWING);
    app.unselectPatternsAndTextButtons();
    app.getMovablePane().setOnKeyPressed(null);
    app.getScene().addEventHandler(KeyEvent.KEY_PRESSED, keyHandler);
    EventHandler<MouseEvent> hover = GridEvents.getGridHoverEventHandler(app);
    EventHandler<MouseEvent> click = GridEvents.getMouseClickEventHandler(app);
    EventHandler<MouseEvent> exit = GridEvents.getGridHoverExitEventHandler(app);
    app.getMovablePane().removeEventHandler(MouseEvent.MOUSE_MOVED, hover);
    app.getMovablePane().removeEventHandler(MouseEvent.MOUSE_CLICKED, click);
    app.getMovablePane().addEventHandler(MouseEvent.MOUSE_MOVED, hover);
    app.getMovablePane().addEventHandler(MouseEvent.MOUSE_CLICKED, click);
    app.getMovablePane().setOnMouseExited(exit);
    this.setSelected(true);
    this.getStyleClass().add(BUTTON_WAITING_SELECTION);

    PauseTransition pause = new PauseTransition(Duration.seconds(1));
    pause.setOnFinished(_ -> {
      this.getStyleClass().remove(BUTTON_WAITING_SELECTION);
      this.getStyleClass().add(BUTTON_SELECTED);
    });
    pause.play();
  };

}

package org.alienlabs.adaloveslace.view.component.button.toolboxwindow;

import javafx.animation.PauseTransition;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.PatternOrTextMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.alienlabs.adaloveslace.App.TEXT_BUTTON_NAME;
import static org.alienlabs.adaloveslace.App.resourceBundle;
import static org.alienlabs.adaloveslace.business.model.Diagram.*;
import static org.alienlabs.adaloveslace.view.window.ToolboxWindow.*;

public class TextButton extends ToggleButton {

  private static final Logger logger = LoggerFactory.getLogger(TextButton.class);

  public TextButton(App app) {
    super(resourceBundle.getString(TEXT_BUTTON_NAME));

    this.getStyleClass().add(PATTERN_AND_TEXT_BUTTON);
    this.setSelected(false);

    this.setOnMouseClicked(event -> {
      app.unselectPatternsAndTextButtons();
      app.getOptionalDotGrid().getDiagram().resetText();
      app.getOptionalDotGrid().getRoot().setOnKeyPressed(null);
      this.setSelected(true);
      this.getStyleClass().add(PATTERN_AND_TEXT_BUTTON_WAITING_SELECTION);

      PauseTransition pause = new PauseTransition(Duration.seconds(1));
      pause.setOnFinished(e -> {
        this.getStyleClass().remove(PATTERN_AND_TEXT_BUTTON_WAITING_SELECTION);
        this.getStyleClass().add(PATTERN_AND_TEXT_BUTTON_SELECTED);
      });
      pause.play();

      app.getOptionalDotGrid().getCurrentPatternOrTextModeProperty().set(PatternOrTextMode.TEXT);
      typedText = new StringBuilder();

      if (keyHandler == null) {
        keyHandler =evt -> {
          logger.info("key pressed -> {}", evt.getCode());

          switch (evt.getCode()) {
            case BACK_SPACE:
              shouldUpdate = true;
              if (!typedText.isEmpty())
                typedText.deleteCharAt(typedText.length() - 1);
              break;
            case ENTER:
              shouldUpdate = true;
              typedText.append("\n");
              break;
            default:
              if (!evt.isControlDown() && !evt.getText().isEmpty()) {
                typedText.append(evt.getText());
                shouldUpdate = true;
              } else if (evt.isControlDown()) {
                shouldUpdate = false;
              }
          }

          if (shouldUpdate) {
            updateImage.run();
          }
        };

        app.getScene().addEventHandler(KeyEvent.KEY_PRESSED, keyHandler);
      }
    });
  }

}

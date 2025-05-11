package org.alienlabs.adaloveslace.view.component.button.toolboxwindow;

import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyEvent;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.PatternOrTextMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.alienlabs.adaloveslace.App.TEXT_BUTTON_NAME;
import static org.alienlabs.adaloveslace.App.resourceBundle;
import static org.alienlabs.adaloveslace.business.model.Diagram.*;
import static org.alienlabs.adaloveslace.view.window.ToolboxWindow.PATTERN_AND_TEXT_BUTTON;
import static org.alienlabs.adaloveslace.view.window.ToolboxWindow.PATTERN_AND_TEXT_BUTTON_SELECTED;

public class TextButton extends ToggleButton {

  private static final Logger logger = LoggerFactory.getLogger(TextButton.class);

  public TextButton(App app) {
    super(resourceBundle.getString(TEXT_BUTTON_NAME));

    this.getStyleClass().add(PATTERN_AND_TEXT_BUTTON); // 👈 relie au style CSS
    this.setSelected(false);

    this.setOnMouseClicked(event -> {
      app.unselectPatternsAndTextButtons();
      app.getOptionalDotGrid().getDiagram().resetText();
      app.getOptionalDotGrid().getRoot().setOnKeyPressed(null);
      this.setSelected(true);
      this.getStyleClass().add(PATTERN_AND_TEXT_BUTTON_SELECTED);
      app.getOptionalDotGrid().getCurrentPatternOrTextModeProperty().set(PatternOrTextMode.TEXT);

      if (keyHandler == null) {
        keyHandler =evt -> {
          logger.info("key pressed -> {}", evt.getCode());

          switch (evt.getCode()) {
            case BACK_SPACE:
              if (!typedText.isEmpty())
                typedText.deleteCharAt(typedText.length() - 1);
              break;
            case ENTER:
              typedText.append("\n");
              break;
            default:
              if (!evt.isControlDown() && evt.getText().length() > 0) {
                typedText.append(evt.getText());
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

package org.alienlabs.adaloveslace.view.component.button.toolboxwindow;

import javafx.scene.control.ToggleButton;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.PatternOrTextMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.alienlabs.adaloveslace.App.TEXT_BUTTON_NAME;
import static org.alienlabs.adaloveslace.App.resourceBundle;
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
    });
  }

}

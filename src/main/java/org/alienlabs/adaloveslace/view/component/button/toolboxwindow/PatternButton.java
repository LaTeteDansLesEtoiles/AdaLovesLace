package org.alienlabs.adaloveslace.view.component.button.toolboxwindow;

import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Pattern;
import org.alienlabs.adaloveslace.business.model.PatternOrTextMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.alienlabs.adaloveslace.util.Events.keyHandler;
import static org.alienlabs.adaloveslace.view.window.ToolboxWindow.PATTERN_AND_TEXT_BUTTON;
import static org.alienlabs.adaloveslace.view.window.ToolboxWindow.PATTERN_AND_TEXT_BUTTON_SELECTED;

public class PatternButton extends ToggleButton {

  private final Pattern pattern;
  private static final double BUTTONS_PREF_WIDTH = 24d;
  private static final double BUTTONS_PREF_HEIGHT = 24d;

  private static final Logger logger = LoggerFactory.getLogger(PatternButton.class);

  public PatternButton(App app, String buttonLabel, Image image, Pattern pattern) {
    super(cleanButtonLabel(buttonLabel));
    this.pattern = pattern;

    ImageView imageView = new ImageView(image);
    imageView.setFitWidth(BUTTONS_PREF_WIDTH);
    imageView.setFitHeight(BUTTONS_PREF_HEIGHT);
    this.setGraphic(imageView);
    this.setGraphicTextGap(10d);

    this.getStyleClass().add(PATTERN_AND_TEXT_BUTTON); // 👈 relie au style CSS
    this.setSelected(false);
    app.getScene().removeEventHandler(KeyEvent.KEY_PRESSED, keyHandler);

    this.setOnMouseClicked(event -> {
      app.unselectPatternsAndTextButtons();

      this.setSelected(true);
      this.getStyleClass().add(PATTERN_AND_TEXT_BUTTON_SELECTED);

      Pattern newCurrentPattern = ((PatternButton) event.getSource()).getPattern();
      logger.debug("Event type -> {}, new current Pattern -> {}", event.getEventType(), newCurrentPattern);

      app.getOptionalDotGrid().getCurrentPatternProperty().set(newCurrentPattern);
      app.getOptionalDotGrid().getCurrentPatternOrTextModeProperty().set(PatternOrTextMode.PATTERN);
      app.getOptionalDotGrid().getDiagram().resetKnotsText();
    });
  }

  private static String cleanButtonLabel(String buttonLabel) {
    return buttonLabel.replace(".png", "").replace(".jpg", "")
      .replace(".gif", "")  .replace(".bmp", "")
      .replace(".jpeg", "") .replace(".PNG", "")
      .replace(".JPG", "")  .replace(".GIF", "")
      .replace(".BMP","")   .replace(".JPEG", "");
  }

  public Pattern getPattern() {
    return this.pattern;
  }

}

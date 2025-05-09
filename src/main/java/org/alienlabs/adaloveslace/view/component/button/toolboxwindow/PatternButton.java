package org.alienlabs.adaloveslace.view.component.button.toolboxwindow;

import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    this.getStyleClass().add("pattern-button"); // 👈 relie au style CSS
    this.setSelected(false);

    this.setOnMouseClicked(event -> {
      app.getToolboxWindow().getAllPatterns().forEach(toggleButton -> {
        toggleButton.setSelected(false);
        toggleButton.getStyleClass().remove("pattern-button-selected");
      });

      this.setSelected(true);
      this.getStyleClass().add("pattern-button-selected");

      Pattern newCurrentPattern = ((PatternButton) event.getSource()).getPattern();
      logger.debug("Event type -> {}, new current Pattern -> {}", event.getEventType(), newCurrentPattern);

      app.getOptionalDotGrid().getCurrentPatternProperty().set(newCurrentPattern);
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

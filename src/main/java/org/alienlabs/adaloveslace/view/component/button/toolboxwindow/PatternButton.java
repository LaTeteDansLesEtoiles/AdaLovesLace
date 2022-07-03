package org.alienlabs.adaloveslace.view.component.button.toolboxwindow;

import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PatternButton extends ToggleButton {

  private final Pattern pattern;

  private static final Logger logger = LoggerFactory.getLogger(PatternButton.class);

  public PatternButton(App app, String buttonLabel, Node node, Pattern pattern) {
    super(cleanButtonLabel(buttonLabel), node);
    this.pattern = pattern;
    this.setSelected(false);

    this.setOnMouseClicked(event -> {
      String eType = event.getEventType().toString();
      Pattern newCurrentPattern = ((PatternButton) event.getSource()).getPattern();
      PatternButton.this.setSelected(true);

      logger.info("Event type -> {}, new current Pattern -> {}", eType, newCurrentPattern);

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

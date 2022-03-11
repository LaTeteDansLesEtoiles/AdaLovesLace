package org.alienlabs.adaloveslace.view;

import javafx.scene.Node;
import javafx.scene.control.Button;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PatternButton extends Button {

  private final Pattern pattern;

  private static final Logger logger = LoggerFactory.getLogger(PatternButton.class);

  public PatternButton(App app, String s, Node node, Pattern pattern) {
    super(s, node);
    this.pattern = pattern;

    this.setOnMouseClicked(event -> {
      String eType = event.getEventType().toString();
      Pattern newCurrentPattern = ((PatternButton) event.getSource()).getPattern();
      logger.info("Event type -> {}, new current Pattern -> {}", eType, newCurrentPattern);

      app.getCanvasWithOptionalDotGrid().getCurrentPatternProperty().set(newCurrentPattern);
    });
  }

  public Pattern getPattern() {
    return this.pattern;
  }

}

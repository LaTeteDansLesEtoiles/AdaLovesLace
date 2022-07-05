package org.alienlabs.adaloveslace.view.component.button.toolboxwindow;

import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PatternButton extends ToggleButton {

  private final Pattern pattern;
  private static final double BUTTONS_PREF_WIDTH = 200d;
  private static final double BUTTONS_PREF_HEIGHT = 60d;

  private static final Logger logger = LoggerFactory.getLogger(PatternButton.class);

  public PatternButton(App app, String buttonLabel, Image image, Pattern pattern) {
    super(cleanButtonLabel(buttonLabel));
    this.pattern = pattern;

    BackgroundImage backgroundImage = new BackgroundImage(image,
      BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
      new BackgroundSize(90d, 90d, true, true, true, false));
    Background background = new Background(backgroundImage);
    this.setPrefSize(BUTTONS_PREF_WIDTH, BUTTONS_PREF_HEIGHT);
    this.setBackground(background);

    this.setStyle("-fx-border-color: black;");
    this.setSelected(false);

    this.setOnMouseClicked(event -> {
      // Unselect all Pattern Buttons
      app.getToolboxWindow().getAllPatterns().forEach(toggleButton -> {
        toggleButton.setSelected(false);
        toggleButton.setStyle("-fx-border-color: black;");
      });

      // Treat click on the Pattern Button: set it as current Pattern of the Diagram
      // (So it is selected)
      String eType = event.getEventType().toString();
      Pattern newCurrentPattern = ((PatternButton) event.getSource()).getPattern();
      PatternButton.this.setSelected(true);
      PatternButton.this.setStyle("-fx-border-color: blue;");

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

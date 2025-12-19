package org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid;

import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.Pattern;
import org.alienlabs.adaloveslace.domain.enumeration.PatternOrTextMode;
import org.alienlabs.adaloveslace.view.window.event.GridEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.alienlabs.adaloveslace.view.window.ToolboxWindow.BUTTON_SELECTED;
import static org.alienlabs.adaloveslace.view.window.ToolboxWindow.PATTERN_TEXT_AND_COLOR_BUTTON;
import static org.alienlabs.adaloveslace.view.window.event.GridEvents.keyHandler;

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
    
    // Create a Region with a white background for the image
    Region backgroundRegion = new Region();
    backgroundRegion.setPrefWidth(BUTTONS_PREF_WIDTH + 4);
    backgroundRegion.setPrefHeight(BUTTONS_PREF_HEIGHT + 4);
    backgroundRegion.setStyle("-fx-background-color: white; -fx-background-radius: 2;");
    
    // Create a StackPane to contain the background and the image
    StackPane imageContainer = new StackPane();
    imageContainer.getChildren().addAll(backgroundRegion, imageView);
    
    this.setGraphic(imageContainer);
    this.setGraphicTextGap(10d);

    this.getStyleClass().add(PATTERN_TEXT_AND_COLOR_BUTTON);
    this.setSelected(false);
    app.getScene().removeEventHandler(KeyEvent.KEY_PRESSED, keyHandler);

    this.setOnMouseClicked(event -> {
      app.unselectPatternsAndTextButtons();

      this.setSelected(true);
      this.getStyleClass().add(BUTTON_SELECTED);

      Pattern newCurrentPattern = ((PatternButton) event.getSource()).getPattern();
      logger.info("Event type -> {}, new current Pattern -> {}", event.getEventType(), newCurrentPattern);

      app.getOptionalDotGrid().getCurrentPatternProperty().set(newCurrentPattern);
      GridEvents.setCurrentImageView(imageView);
      app.getOptionalDotGrid().getCurrentPatternOrTextModeProperty().set(PatternOrTextMode.PATTERN);
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

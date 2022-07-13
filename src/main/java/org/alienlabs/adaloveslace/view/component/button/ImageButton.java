package org.alienlabs.adaloveslace.view.component.button;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;

import static org.alienlabs.adaloveslace.App.ASSETS_DIRECTORY;
import static org.alienlabs.adaloveslace.App.SMALL_ICON_SIZE;

public class ImageButton extends Button {

  private static final Logger logger = LoggerFactory.getLogger(ImageButton.class);

  public ImageButton(String buttonLabel) {
    super(buttonLabel);
  }

  public void buildButtonImage(String pathname) {
    try {
      Image buttonImage = new Image(new File(ASSETS_DIRECTORY + pathname).toURI().toURL().toExternalForm());

      ImageView buttonImageView  = new ImageView(buttonImage);
      buttonImageView.setFitHeight(SMALL_ICON_SIZE);
      buttonImageView.setPreserveRatio(true);
      this.setGraphic(buttonImageView);
    } catch (MalformedURLException e) {
      logger.error("Error loading button image!", e);
    }
  }

}

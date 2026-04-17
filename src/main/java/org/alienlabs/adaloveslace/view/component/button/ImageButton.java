package org.alienlabs.adaloveslace.view.component.button;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import static org.alienlabs.adaloveslace.App.ASSETS_DIRECTORY;
import static org.alienlabs.adaloveslace.App.SMALL_ICON_SIZE;

public class ImageButton extends Button {

  public ImageButton(String buttonLabel) {
    super(buttonLabel);
  }

  public void buildButtonImage(String pathname) {
      Image buttonImage = new Image(getClass()
              .getResource(ASSETS_DIRECTORY + pathname).toExternalForm());

      ImageView buttonImageView  = new ImageView(buttonImage);
      buttonImageView.setFitHeight(SMALL_ICON_SIZE);
      buttonImageView.setPreserveRatio(true);
      this.setGraphic(buttonImageView);
  }

}

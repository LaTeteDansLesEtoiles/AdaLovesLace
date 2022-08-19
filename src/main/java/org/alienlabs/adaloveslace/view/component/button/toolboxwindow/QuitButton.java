package org.alienlabs.adaloveslace.view.component.button.toolboxwindow;

import javafx.application.Platform;
import org.alienlabs.adaloveslace.view.component.button.ImageButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuitButton extends ImageButton {

  private static final Logger logger  = LoggerFactory.getLogger(QuitButton.class);

  public QuitButton(String buttonLabel) {
    super(buttonLabel);
    this.setOnMouseClicked(event -> onQuitAction());
    buildButtonImage("quit.png");
  }

  public static void onQuitAction() {
    logger.info("Exiting app");
    Platform.exit();
  }

}

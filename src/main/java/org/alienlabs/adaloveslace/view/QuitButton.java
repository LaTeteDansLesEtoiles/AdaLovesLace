package org.alienlabs.adaloveslace.view;

import javafx.scene.control.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuitButton extends Button {

  public static final String QUIT_APP = "Quit";

  private static final Logger logger = LoggerFactory.getLogger(QuitButton.class);

  public QuitButton(String buttonLabel) {
    super(buttonLabel);
    this.setOnMouseClicked(event -> onQuitAction());
  }

  @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
    value = "DM_EXIT",
    justification = "We shall exit when we have to, since we are not in a lib")
  public static void onQuitAction() {
    logger.info("Exiting app");
    System.exit(0);
  }

}

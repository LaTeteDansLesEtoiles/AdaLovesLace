package org.alienlabs.adaloveslace.view.component.button;

import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import org.alienlabs.adaloveslace.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DrawingButton extends Button {

  public static final String DRAWING_BUTTON_NAME    = "Draw";

  private static final Logger logger                  = LoggerFactory.getLogger(DrawingButton.class);

  public DrawingButton(App app, Pane root, String buttonLabel) {
    super(buttonLabel);
    this.setOnMouseClicked(event -> onSetDrawModeAction());
  }

  public static void onSetDrawModeAction() {
    logger.info("Setting draw mode");
  }

}

package org.alienlabs.adaloveslace.view.component.button.toolboxwindow;

import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.view.component.button.ImageButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedoKnotButton extends ImageButton {

  private static App app;

  private static final Logger logger = LoggerFactory.getLogger(RedoKnotButton.class);

  public RedoKnotButton(String buttonLabel, App app) {
    super(buttonLabel);
    RedoKnotButton.app = app;
    this.setOnMouseClicked(_ -> redoKnot());
    buildButtonImage("redo.png");
  }

  public static void redoKnot() {
    app.getOptionalDotGrid().getDiagram().redoLastStep(app, true);
    logger.debug("Redo knot event");
  }

}

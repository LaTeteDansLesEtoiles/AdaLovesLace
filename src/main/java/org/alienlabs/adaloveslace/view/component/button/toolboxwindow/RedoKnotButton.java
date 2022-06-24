package org.alienlabs.adaloveslace.view.component.button.toolboxwindow;

import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.view.component.button.ImageButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedoKnotButton extends ImageButton {

  public static final String REDO_KNOT_BUTTON_NAME = "      Redo knot     ";

  private static final Logger logger = LoggerFactory.getLogger(RedoKnotButton.class);

  public RedoKnotButton(String buttonLabel, App app) {
    super(buttonLabel);
    this.setOnMouseClicked(event -> redoKnot(app));
    buildButtonImage("redo.png");
  }

  public static void redoKnot(App app) {
    app.getCanvasWithOptionalDotGrid().getDiagram().redoLastKnot();
    app.getCanvasWithOptionalDotGrid().layoutChildren();
    logger.info("Event redo knot: {}", app);
  }

}

package org.alienlabs.adaloveslace.view.component.button.toolboxwindow;

import javafx.scene.control.Button;
import org.alienlabs.adaloveslace.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedoKnotButton extends Button {

  private static final Logger logger = LoggerFactory.getLogger(RedoKnotButton.class);

  public RedoKnotButton(String buttonLabel, App app) {
    super(buttonLabel);
    this.setOnMouseClicked(event -> redoKnot(app));
  }

  public static void redoKnot(App app) {
    app.getCanvasWithOptionalDotGrid().getDiagram().redoLastKnot();
    app.getCanvasWithOptionalDotGrid().layoutChildren();
    logger.info("Event redo knot: {}", app);
  }

}

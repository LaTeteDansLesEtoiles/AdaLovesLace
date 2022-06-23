package org.alienlabs.adaloveslace.view.component.button;

import javafx.scene.control.Button;
import org.alienlabs.adaloveslace.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UndoKnotButton extends Button {

  private static final Logger logger = LoggerFactory.getLogger(UndoKnotButton.class);

  public UndoKnotButton(String buttonLabel, App app) {
    super(buttonLabel);
    this.setOnMouseClicked(event -> undoKnot(app));
  }

  public static void undoKnot(App app) {
    app.getCanvasWithOptionalDotGrid().getDiagram().undoLastKnot();
    app.getCanvasWithOptionalDotGrid().layoutChildren();
    logger.info("Event undo knot: {}", app);
  }

}

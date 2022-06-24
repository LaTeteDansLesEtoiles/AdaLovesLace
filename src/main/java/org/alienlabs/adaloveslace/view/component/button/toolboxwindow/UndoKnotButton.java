package org.alienlabs.adaloveslace.view.component.button.toolboxwindow;

import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.view.component.button.ImageButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UndoKnotButton extends ImageButton {

  public static final String UNDO_KNOT_BUTTON_NAME = "     Undo knot     ";

  private static final Logger logger = LoggerFactory.getLogger(UndoKnotButton.class);

  public UndoKnotButton(String buttonLabel, App app) {
    super(buttonLabel);
    this.setOnMouseClicked(event -> undoKnot(app));
    buildButtonImage("undo.png");
  }

  public static void undoKnot(App app) {
    app.getCanvasWithOptionalDotGrid().getDiagram().undoLastKnot();
    app.getCanvasWithOptionalDotGrid().layoutChildren();
    logger.info("Event undo knot: {}", app);
  }

}

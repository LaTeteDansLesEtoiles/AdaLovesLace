package org.alienlabs.adaloveslace.view.component.button.toolboxwindow;

import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.view.component.button.ImageButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UndoKnotButton extends ImageButton {

  private static App app;

  private static final Logger logger = LoggerFactory.getLogger(UndoKnotButton.class);

    public UndoKnotButton(String buttonLabel, App app) {
    super(buttonLabel);
      UndoKnotButton.app = app;
        this.setOnMouseClicked(_ -> undoKnot());
    buildButtonImage("undo.png");
  }

  public static void undoKnot() {
    app.getOptionalDotGrid().getDiagram().undoLastStep(app, true);
    logger.debug("Undo knot event");
  }

}

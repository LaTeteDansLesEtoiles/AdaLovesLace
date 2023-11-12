package org.alienlabs.adaloveslace.view.component.button.toolboxwindow;

import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.view.component.button.ImageButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UndoKnotButton extends ImageButton {

  private static final Logger logger = LoggerFactory.getLogger(UndoKnotButton.class);

  public UndoKnotButton(String buttonLabel, App app) {
    super(buttonLabel);
    this.setOnMouseClicked(event -> undoKnot(app));
    buildButtonImage("undo.png");
  }

  public static void undoKnot(App app) {
    app.getOptionalDotGrid().getDiagram().undoLastStep(app);
    logger.debug("Undo knot event");
  }

}

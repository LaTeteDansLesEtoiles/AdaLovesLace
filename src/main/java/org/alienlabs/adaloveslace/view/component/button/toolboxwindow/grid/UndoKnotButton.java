package org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid;

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
        // Use only onAction: JavaFX Button click triggers both mouse and action handlers,
        // which would make undo execute twice for a single click.
        this.setOnAction(_ -> undoKnot());
    buildButtonImage("undo.png");
    setId("undoButton");
  }

  public static void undoKnot() {
    app.getOptionalDotGrid().getDiagram().undoLastStep(app, true);
    logger.info("Undo knot event");
  }

}

package org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid;

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
    // Use only onAction: JavaFX Button click triggers both mouse and action handlers,
    // which would make redo execute twice for a single click.
    this.setOnAction(_ -> redoKnot());
    buildButtonImage("redo.png");
    setId("redoButton");
  }

  public static void redoKnot() {
    app.getOptionalDotGrid().getDiagram().redoLastStep(app, true);
    logger.info("Redo knot event");
  }

}

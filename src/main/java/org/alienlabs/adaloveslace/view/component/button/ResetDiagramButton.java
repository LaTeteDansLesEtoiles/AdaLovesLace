package org.alienlabs.adaloveslace.view.component.button;

import javafx.scene.control.Button;
import org.alienlabs.adaloveslace.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResetDiagramButton extends Button {

  public static final String RESET_DIAGRAM_BUTTON_NAME = "Reset diagram";

  private static final Logger logger = LoggerFactory.getLogger(ResetDiagramButton.class);

  public ResetDiagramButton(String buttonLabel, App app) {
    super(buttonLabel);
    this.setOnMouseClicked(event -> resetDiagram(app));
  }

  public static void resetDiagram(App app) {
    app.getCanvasWithOptionalDotGrid().getDiagram().resetDiagram();
    app.getCanvasWithOptionalDotGrid().layoutChildren();
    logger.info("Event reset diagram: {}", app);
  }

}

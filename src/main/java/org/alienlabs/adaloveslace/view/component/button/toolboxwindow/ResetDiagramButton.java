package org.alienlabs.adaloveslace.view.component.button.toolboxwindow;

import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.view.component.button.ImageButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResetDiagramButton extends ImageButton {

  public static final String RESET_DIAGRAM_BUTTON_NAME = "  Reset diagram  ";

  private static final Logger logger = LoggerFactory.getLogger(ResetDiagramButton.class);

  public ResetDiagramButton(String buttonLabel, App app) {
    super(buttonLabel);
    this.setOnMouseClicked(event -> resetDiagram(app));
    buildButtonImage("reset_diagram.png");
  }

  public static void resetDiagram(App app) {
    app.getOptionalDotGrid().getDiagram().resetDiagram();
    app.getOptionalDotGrid().layoutChildren();
    logger.info("Event reset diagram: {}", app);
  }

}

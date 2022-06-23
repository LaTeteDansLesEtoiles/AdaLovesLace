package org.alienlabs.adaloveslace.view.component.button;

import javafx.scene.control.Button;
import org.alienlabs.adaloveslace.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClearDiagramButton extends Button {

  public static final String CLEAR_DIAGRAM_BUTTON_NAME = "Clear diagram";

  private static final Logger logger = LoggerFactory.getLogger(ClearDiagramButton.class);

  public ClearDiagramButton(String buttonLabel, App app) {
    super(buttonLabel);
    this.setOnMouseClicked(event -> clearDiagram(app));
  }

  public static void clearDiagram(App app) {
    logger.info("Event clear diagram: {}", app);
  }

}

package org.alienlabs.adaloveslace.view.component.button;

import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import org.alienlabs.adaloveslace.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelectionButton extends Button {

  public static final String SELECTION_BUTTON_NAME    = "Select";

  private static final Logger logger                  = LoggerFactory.getLogger(SelectionButton.class);

  public SelectionButton(App app, Pane root, String buttonLabel) {
    super(buttonLabel);
    this.setOnMouseClicked(event -> onSetSelectionModeAction(app, root));
  }

  public static void onSetSelectionModeAction(App app, Pane root) {
    logger.info("Setting selection mode");
  }

}

package org.alienlabs.adaloveslace.view.component.button.geometrywindow;

import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.MouseMode;
import org.alienlabs.adaloveslace.view.window.GeometryWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelectionButton extends ToggleButton {

  public static final String SELECTION_BUTTON_NAME    = "Select ";

  private static final Logger logger                  = LoggerFactory.getLogger(SelectionButton.class);

  public SelectionButton(App app, GeometryWindow window, Pane root, String buttonLabel) {
    super(buttonLabel);
    this.setOnMouseClicked(event -> onSetSelectionModeAction(app, window));
  }

  public static void onSetSelectionModeAction(App app, GeometryWindow window) {
    logger.info("Setting selection mode");
    app.getCanvasWithOptionalDotGrid().getDiagram().setCurrentMode(MouseMode.SELECTION);

    window.getDrawingButton().setSelected(false);
    window.getSelectionButton().setSelected(true);
    window.getRotationButton().setSelected(false);
    window.getZoomButton().setSelected(false);
  }

}

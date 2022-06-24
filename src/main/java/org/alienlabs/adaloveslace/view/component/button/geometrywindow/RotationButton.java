package org.alienlabs.adaloveslace.view.component.button.geometrywindow;

import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.MouseMode;
import org.alienlabs.adaloveslace.view.window.GeometryWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RotationButton extends ToggleButton {

  public static final String ROTATION_BUTTON_NAME    = "Rotate";

  private static final Logger logger                  = LoggerFactory.getLogger(RotationButton.class);

  public RotationButton(App app, GeometryWindow window, Pane root, String buttonLabel) {
    super(buttonLabel);
    this.setOnMouseClicked(event -> onSetRotationModeAction(app, window));
  }

  public static void onSetRotationModeAction(App app, GeometryWindow window) {
    logger.info("Setting rotation mode");
    app.getCanvasWithOptionalDotGrid().getDiagram().setCurrentMode(MouseMode.ROTATION);

    window.getDrawingButton().setSelected(false);
    window.getSelectionButton().setSelected(false);
    window.getRotationButton().setSelected(true);
    window.getZoomButton().setSelected(false);
  }

}

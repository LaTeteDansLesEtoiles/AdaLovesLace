package org.alienlabs.adaloveslace.view.component.button.geometrywindow;

import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.MouseMode;
import org.alienlabs.adaloveslace.view.window.GeometryWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DrawingButton extends ToggleButton {

  public static final String DRAWING_BUTTON_NAME    = " Draw ";

  private static final Logger logger                  = LoggerFactory.getLogger(DrawingButton.class);

  public DrawingButton(App app, GeometryWindow window, Pane root, String buttonLabel) {
    super(buttonLabel);
    this.setOnMouseClicked(event -> onSetDrawModeAction(app, window));
  }

  public static void onSetDrawModeAction(App app, GeometryWindow window) {
    logger.info("Setting draw mode");
    app.getCanvasWithOptionalDotGrid().getDiagram().setCurrentMode(MouseMode.DRAWING);

    window.getDrawingButton().setSelected(true);
    window.getSelectionButton().setSelected(false);
    window.getRotationButton().setSelected(false);
    window.getZoomButton().setSelected(false);
  }

}

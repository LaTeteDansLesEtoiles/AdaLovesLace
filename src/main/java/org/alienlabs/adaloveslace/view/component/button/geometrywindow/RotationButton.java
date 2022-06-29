package org.alienlabs.adaloveslace.view.component.button.geometrywindow;

import javafx.scene.control.ToggleButton;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.MouseMode;
import org.alienlabs.adaloveslace.view.window.GeometryWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.alienlabs.adaloveslace.view.window.GeometryWindow.GEOMETRY_BUTTONS_HEIGHT;

public class RotationButton extends ToggleButton {

  public static final String ROTATION_BUTTON_NAME    = "Rotate";

  private static final Logger logger                  = LoggerFactory.getLogger(RotationButton.class);

  public RotationButton(App app, GeometryWindow window, String buttonLabel) {
    super(buttonLabel);
    this.setOnMouseClicked(event -> onSetRotationModeAction(app, window));
    this.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);
    this.setMaxHeight(GEOMETRY_BUTTONS_HEIGHT);
  }

  public static void onSetRotationModeAction(App app, GeometryWindow window) {
    logger.info("Setting rotation mode");
    app.getOptionalDotGrid().getDiagram().setCurrentMode(MouseMode.ROTATION);

    window.getDrawingButton().setSelected(false);
    window.getSelectionButton().setSelected(false);
    window.getRotationButton().setSelected(true);
    window.getZoomButton().setSelected(false);
  }

}

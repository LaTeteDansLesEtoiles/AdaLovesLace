package org.alienlabs.adaloveslace.view.component.button.geometrywindow.move;

import javafx.scene.control.ToggleButton;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.view.window.GeometryWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.alienlabs.adaloveslace.view.window.GeometryWindow.GEOMETRY_BUTTONS_HEIGHT;

public class UpButton extends ToggleButton {

  public static final String UpButton     = "";

  private static final Logger logger      = LoggerFactory.getLogger(UpButton.class);

  public UpButton(App app, GeometryWindow window, String buttonLabel) {
    super(buttonLabel);
    this.setOnMouseClicked(event -> onMoveKnotUpAction(app, window));
    this.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);
    this.setMaxHeight(GEOMETRY_BUTTONS_HEIGHT);
  }

  public static void onMoveKnotUpAction(App app, GeometryWindow window) {
    logger.info("Moving knot {} up", app.getOptionalDotGrid().getDiagram().getCurrentKnot());
  }

}

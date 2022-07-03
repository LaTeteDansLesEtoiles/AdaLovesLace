package org.alienlabs.adaloveslace.view.component.button.geometrywindow.move;

import javafx.scene.control.ToggleButton;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.view.window.GeometryWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.alienlabs.adaloveslace.view.window.GeometryWindow.GEOMETRY_BUTTONS_HEIGHT;

public class LeftButton extends ToggleButton {

  public static final String LeftButton     = "";

  private static final Logger logger        = LoggerFactory.getLogger(LeftButton.class);

  public LeftButton(App app, GeometryWindow window, String buttonLabel) {
    super(buttonLabel);
    this.setOnMouseClicked(event -> onMoveKnotLeftAction(app, window));
    this.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);
    this.setMaxHeight(GEOMETRY_BUTTONS_HEIGHT);
  }

  public static void onMoveKnotLeftAction(App app, GeometryWindow window) {
    logger.info("Moving knot {} left", app.getOptionalDotGrid().getDiagram().getCurrentKnot());
  }

}

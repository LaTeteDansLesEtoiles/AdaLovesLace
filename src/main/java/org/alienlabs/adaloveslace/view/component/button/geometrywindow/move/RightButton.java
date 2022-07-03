package org.alienlabs.adaloveslace.view.component.button.geometrywindow.move;

import javafx.scene.control.ToggleButton;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.view.window.GeometryWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.alienlabs.adaloveslace.view.window.GeometryWindow.GEOMETRY_BUTTONS_HEIGHT;

public class RightButton extends ToggleButton {

  private static final Logger logger          = LoggerFactory.getLogger(RightButton.class);

  public RightButton(App app, GeometryWindow window) {
    this.setOnMouseClicked(event -> onMoveKnotRightAction(app, window));
    this.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);
    this.setMaxHeight(GEOMETRY_BUTTONS_HEIGHT);
  }

  public static void onMoveKnotRightAction(App app, GeometryWindow window) {
    Knot currentKnot = app.getOptionalDotGrid().getDiagram().getCurrentKnot();
    logger.info("Moving right knot {}", currentKnot);

    currentKnot.setX(currentKnot.getX() + 5d);
    app.getOptionalDotGrid().layoutChildren();
  }

}

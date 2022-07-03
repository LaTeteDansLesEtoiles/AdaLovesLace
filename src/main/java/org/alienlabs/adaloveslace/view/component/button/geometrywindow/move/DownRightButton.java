package org.alienlabs.adaloveslace.view.component.button.geometrywindow.move;

import javafx.scene.control.ToggleButton;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.view.window.GeometryWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.alienlabs.adaloveslace.view.window.GeometryWindow.GEOMETRY_BUTTONS_HEIGHT;

public class DownRightButton extends ToggleButton {

  private static final Logger logger        = LoggerFactory.getLogger(DownRightButton.class);

  public DownRightButton(App app, GeometryWindow window) {
    this.setOnMouseClicked(event -> onMoveKnotDownRightAction(app, window));
    this.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);
    this.setMaxHeight(GEOMETRY_BUTTONS_HEIGHT);
  }

  public static void onMoveKnotDownRightAction(App app, GeometryWindow window) {
    Knot currentKnot = app.getOptionalDotGrid().getDiagram().getCurrentKnot();
    logger.debug("Moving down knot {}", currentKnot);

    currentKnot.setX(currentKnot.getX() + FastMoveModeButton.getMoveSpeed());
    currentKnot.setY(currentKnot.getY() + FastMoveModeButton.getMoveSpeed());
    app.getOptionalDotGrid().layoutChildren();
  }

}

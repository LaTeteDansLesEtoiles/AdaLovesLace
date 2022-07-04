package org.alienlabs.adaloveslace.view.component.button.geometrywindow.move;

import javafx.scene.control.Button;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.view.window.GeometryWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.alienlabs.adaloveslace.view.window.GeometryWindow.GEOMETRY_BUTTONS_HEIGHT;

public class DownLeftButton extends Button {

  private static final Logger logger        = LoggerFactory.getLogger(DownLeftButton.class);

  public DownLeftButton(App app, GeometryWindow window) {
    this.setOnMouseClicked(event -> onMoveKnotDownLeftAction(app, window));
    this.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);
    this.setMaxHeight(GEOMETRY_BUTTONS_HEIGHT);
  }

  public static void onMoveKnotDownLeftAction(App app, GeometryWindow window) {
    Knot currentKnot = app.getOptionalDotGrid().getDiagram().getCurrentKnot();
    logger.debug("Moving down knot {}", currentKnot);

    currentKnot.setX(currentKnot.getX() - FastMoveModeButton.getMoveSpeed());
    currentKnot.setY(currentKnot.getY() + FastMoveModeButton.getMoveSpeed());
    app.getOptionalDotGrid().layoutChildren();
  }

}

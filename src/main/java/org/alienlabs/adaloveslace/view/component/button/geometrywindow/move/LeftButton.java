package org.alienlabs.adaloveslace.view.component.button.geometrywindow.move;

import javafx.scene.control.Button;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.view.window.GeometryWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.alienlabs.adaloveslace.view.window.GeometryWindow.GEOMETRY_BUTTONS_HEIGHT;

public class LeftButton extends Button {

  private static final Logger logger        = LoggerFactory.getLogger(LeftButton.class);

  public LeftButton(App app, GeometryWindow window) {
    this.setOnMouseClicked(event -> onMoveKnotLeftAction(app, window));
    this.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);
  }

  public static void onMoveKnotLeftAction(App app, GeometryWindow window) {
    Knot currentKnot = app.getOptionalDotGrid().getDiagram().getCurrentKnot();
    logger.debug("Moving left knot {}", currentKnot);

    for (Knot knot : app.getOptionalDotGrid().getAllSelectedKnots()) {
      knot.setX(knot.getX() - FastMoveModeButton.getMoveSpeed());
      app.getOptionalDotGrid().circleSelectedKnot(knot);
    }
    app.getOptionalDotGrid().layoutChildren();
  }

}

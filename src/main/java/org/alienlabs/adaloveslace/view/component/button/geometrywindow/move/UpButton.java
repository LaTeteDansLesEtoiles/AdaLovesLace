package org.alienlabs.adaloveslace.view.component.button.geometrywindow.move;

import javafx.scene.control.Button;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.view.window.GeometryWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.alienlabs.adaloveslace.view.window.GeometryWindow.GEOMETRY_BUTTONS_HEIGHT;

public class UpButton extends Button {

  private static final Logger logger      = LoggerFactory.getLogger(UpButton.class);

  public UpButton(App app, GeometryWindow window) {
    this.setOnMouseClicked(event -> onMoveKnotUpAction(app, window));
    this.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);
  }

  public static void onMoveKnotUpAction(App app, GeometryWindow window) {
    Knot currentKnot = app.getOptionalDotGrid().getDiagram().getCurrentKnot();
    logger.debug("Moving up knot {}", currentKnot);

    for (Knot knot : app.getOptionalDotGrid().getAllSelectedKnots()) {
      knot.setY(knot.getY() - FastMoveModeButton.getMoveSpeed());
      app.getOptionalDotGrid().circleSelectedKnot(knot);
      app.getOptionalDotGrid().drawGuideLines(knot);
    }
    app.getOptionalDotGrid().layoutChildren();
  }

}

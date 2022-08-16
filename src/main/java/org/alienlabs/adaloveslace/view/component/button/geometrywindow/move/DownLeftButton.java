package org.alienlabs.adaloveslace.view.component.button.geometrywindow.move;

import javafx.scene.control.Button;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.business.model.MouseMode;
import org.alienlabs.adaloveslace.view.window.GeometryWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.alienlabs.adaloveslace.view.window.GeometryWindow.GEOMETRY_BUTTONS_HEIGHT;

public class DownLeftButton extends Button {

  private static final Logger logger        = LoggerFactory.getLogger(DownLeftButton.class);

  public DownLeftButton(App app, GeometryWindow window) {
    this.setOnMouseClicked(event -> onMoveKnotDownLeftAction(app, window));
    this.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);
  }

  public static void onMoveKnotDownLeftAction(App app, GeometryWindow window) {
    app.getOptionalDotGrid().getDiagram().setCurrentMode(MouseMode.MOVE);

    for (Knot knot : app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots()) {
      knot.setX(knot.getX() - FastMoveModeButton.getMoveSpeed());
      knot.setY(knot.getY() + FastMoveModeButton.getMoveSpeed());
      logger.debug("Moving down left knot {}", knot);
    }

    app.getDiagram().addKnotsWithStep(app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots(),
      app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots());

    app.getOptionalDotGrid().layoutChildren();
  }

}

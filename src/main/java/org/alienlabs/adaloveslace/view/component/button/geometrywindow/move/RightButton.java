package org.alienlabs.adaloveslace.view.component.button.geometrywindow.move;

import javafx.scene.control.Button;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.business.model.MouseMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.alienlabs.adaloveslace.view.window.GeometryWindow.GEOMETRY_BUTTONS_HEIGHT;

public class RightButton extends Button {

  private static final Logger logger          = LoggerFactory.getLogger(RightButton.class);

  public RightButton(App app) {
    this.setOnMouseClicked(event -> onMoveKnotRightAction(app));
    this.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);
  }

  public static void onMoveKnotRightAction(App app) {
    app.getOptionalDotGrid().getDiagram().setCurrentMode(MouseMode.MOVE);

    for (Knot knot : app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots()) {
      knot.setX(knot.getX() + FastMoveModeButton.getMoveSpeed());
      app.getOptionalDotGrid().getDiagram().addKnotWithStep(knot, true);
      logger.debug("Moving right knot {}", knot);
    }

    app.getOptionalDotGrid().layoutChildren();
  }

}

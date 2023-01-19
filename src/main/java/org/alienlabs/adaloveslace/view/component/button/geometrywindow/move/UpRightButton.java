package org.alienlabs.adaloveslace.view.component.button.geometrywindow.move;

import javafx.scene.control.Button;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.business.model.MouseMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.alienlabs.adaloveslace.view.window.GeometryWindow.GEOMETRY_BUTTONS_HEIGHT;

public class UpRightButton extends Button {

  private static final Logger logger = LoggerFactory.getLogger(UpRightButton.class);

  public UpRightButton(App app) {
    this.setOnMouseClicked(event -> onMoveKnotUpRightAction(app));
    this.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);
  }

  public static void onMoveKnotUpRightAction(App app) {
    app.getOptionalDotGrid().getDiagram().setCurrentMode(MouseMode.MOVE);

    for (Knot knot : app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots()) {
      knot.setX(knot.getX() + FastMoveModeButton.getMoveSpeed());
      knot.setY(knot.getY() - FastMoveModeButton.getMoveSpeed());
      logger.debug("Moving up right knot {}", knot);
    }

    app.getOptionalDotGrid().getDiagram().addKnotsWithStep(app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots(),
      app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots());

    app.getOptionalDotGrid().layoutChildren();
  }

}

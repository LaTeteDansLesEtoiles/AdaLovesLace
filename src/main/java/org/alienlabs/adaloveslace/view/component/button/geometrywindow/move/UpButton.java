package org.alienlabs.adaloveslace.view.component.button.geometrywindow.move;

import javafx.scene.control.Button;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.business.model.MouseMode;
import org.alienlabs.adaloveslace.util.NodeUtil;
import org.alienlabs.adaloveslace.view.window.GeometryWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

import static org.alienlabs.adaloveslace.view.window.GeometryWindow.GEOMETRY_BUTTONS_HEIGHT;

public class UpButton extends Button {

  private static final Logger logger      = LoggerFactory.getLogger(UpButton.class);

  public UpButton(App app, GeometryWindow window) {
    this.setOnMouseClicked(event -> onMoveKnotUpAction(app, window));
    this.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);
  }

  public static void onMoveKnotUpAction(App app, GeometryWindow window) {
    app.getOptionalDotGrid().getDiagram().setCurrentMode(MouseMode.MOVE);
    Set<Knot> selectedKnots = new HashSet<>();

    for (Knot knot : app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots()) {
      Knot copiedKnot = new NodeUtil().copyKnot(knot);
      copiedKnot.setY(knot.getY() - FastMoveModeButton.getMoveSpeed());
      selectedKnots.add(copiedKnot);

      logger.debug("Moving up knot {}", knot);
    }

    app.getDiagram().addKnotsToStep(app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots(),
      selectedKnots);

    app.getOptionalDotGrid().layoutChildren();
  }

}

package org.alienlabs.adaloveslace.view.component.button.geometrywindow.move;

import javafx.scene.control.Button;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.business.model.MouseMode;
import org.alienlabs.adaloveslace.util.NodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.alienlabs.adaloveslace.business.model.Diagram.newStep;
import static org.alienlabs.adaloveslace.view.window.GeometryWindow.GEOMETRY_BUTTONS_HEIGHT;

public class UpRightButton extends Button {

  private static final Logger logger = LoggerFactory.getLogger(UpRightButton.class);

  public UpRightButton(App app) {
    this.setOnMouseClicked(event -> onMoveKnotUpRightAction(app));
    this.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);
  }

  public static void onMoveKnotUpRightAction(App app) {
    app.getOptionalDotGrid().getDiagram().setCurrentMode(MouseMode.MOVE);

    List<Knot> displayedKnots = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots());
    List<Knot> selectedKnots = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots());
    List<Knot> copiedKnots = new ArrayList<>();

    for (Knot knot : selectedKnots) {
      knot.setX(knot.getX() + FastMoveModeButton.getMoveSpeed());
      knot.setY(knot.getY() - FastMoveModeButton.getMoveSpeed());
      Knot copiedKnot = new NodeUtil().copyKnot(knot);

      displayedKnots.remove(knot);
      copiedKnots.add(copiedKnot);

      logger.debug("Moving up right knot {}", copiedKnot);
    }

    newStep(displayedKnots, copiedKnots, true);
  }

}

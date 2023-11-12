package org.alienlabs.adaloveslace.view.component.button.geometrywindow.move;

import javafx.scene.control.Button;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.business.model.MouseMode;
import org.alienlabs.adaloveslace.util.NodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.TreeSet;

import static org.alienlabs.adaloveslace.business.model.Diagram.newStep;
import static org.alienlabs.adaloveslace.view.window.GeometryWindow.GEOMETRY_BUTTONS_HEIGHT;

public class UpLeftButton extends Button {

  private static final Logger logger      = LoggerFactory.getLogger(UpLeftButton.class);

  public UpLeftButton(App app) {
    this.setOnMouseClicked(event -> onMoveKnotUpLeftAction(app));
    this.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);
  }

  public static void onMoveKnotUpLeftAction(App app) {
    app.getOptionalDotGrid().getDiagram().setCurrentMode(MouseMode.MOVE);

    Set<Knot> displayedKnots = new TreeSet<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots());
    Set<Knot> selectedKnots = new TreeSet<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots());
    Set<Knot> copiedKnots = new TreeSet<>();

    for (Knot knot : selectedKnots) {
      knot.setX(knot.getX() - FastMoveModeButton.getMoveSpeed());
      knot.setY(knot.getY() - FastMoveModeButton.getMoveSpeed());
      Knot copiedKnot = new NodeUtil().copyKnot(knot);

      displayedKnots.remove(knot);
      copiedKnots.add(copiedKnot);

      logger.debug("Moving up left knot {}", knot);

    }

    newStep(displayedKnots, copiedKnots, true);
  }

}

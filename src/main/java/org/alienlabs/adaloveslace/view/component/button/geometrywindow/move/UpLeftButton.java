package org.alienlabs.adaloveslace.view.component.button.geometrywindow.move;

import javafx.scene.control.Button;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.business.model.enumeration.MouseMode;
import org.alienlabs.adaloveslace.util.NodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.alienlabs.adaloveslace.business.model.Diagram.newStep;
import static org.alienlabs.adaloveslace.view.component.OptionalDotGrid.moveKnotPause;
import static org.alienlabs.adaloveslace.view.window.GeometryWindow.GEOMETRY_BUTTONS_HEIGHT;

public class UpLeftButton extends Button {

  private static final Logger logger      = LoggerFactory.getLogger(UpLeftButton.class);

  public UpLeftButton(App app) {
    this.setOnMouseClicked(event -> onMoveKnotUpLeftAction(app));
    this.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);
  }

  public static void onMoveKnotUpLeftAction(App app) {
    if (app.getOptionalDotGrid().getDiagram().getCurrentMode() != MouseMode.MOVE) {
      app.getOptionalDotGrid().getDiagram().setOldMode(app.getOptionalDotGrid().getDiagram().getCurrentMode());
    }

    List<Knot> displayedKnots = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots());
    List<Knot> selectedKnots = app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots();
    List<Knot> copiedKnots = new ArrayList<>();
    List<Knot> toRemoveKnots = new ArrayList<>();

    for (Knot knot : selectedKnots) {
      knot.setX(knot.getX() - FastMoveModeButton.getMoveSpeed());
      knot.setY(knot.getY() - FastMoveModeButton.getMoveSpeed());
      Knot copiedKnot = new NodeUtil().copyKnot(knot);

      toRemoveKnots.add(knot);
      copiedKnots.add(copiedKnot);
      moveKnotPause.playFromStart();

      logger.debug("Moving up left knot {}", knot);

    }

    displayedKnots.removeAll(toRemoveKnots);

    if (app.getOptionalDotGrid().getDiagram().getCurrentMode() != MouseMode.MOVE) {
      app.getOptionalDotGrid().getDiagram().setCurrentMode(MouseMode.MOVE);
      newStep(displayedKnots, copiedKnots, true);
    } else {
      app.getOptionalDotGrid().getDiagram().getCurrentStep().setDisplayedKnots(displayedKnots);
      app.getOptionalDotGrid().getDiagram().getCurrentStep().setSelectedKnots(copiedKnots);
      app.getOptionalDotGrid().layoutChildren();
    }
  }

}

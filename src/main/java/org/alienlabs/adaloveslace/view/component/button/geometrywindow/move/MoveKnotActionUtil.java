package org.alienlabs.adaloveslace.view.component.button.geometrywindow.move;

import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.Knot;
import org.alienlabs.adaloveslace.domain.enumeration.MouseMode;
import org.alienlabs.adaloveslace.util.NodeUtil;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static org.alienlabs.adaloveslace.domain.Diagram.newStep;
import static org.alienlabs.adaloveslace.view.component.grid.OptionalDotGrid.moveKnotPause;

final class MoveKnotActionUtil {

  private MoveKnotActionUtil() {
    // Utility class
  }

  static void moveSelectedKnots(App app, double deltaX, double deltaY, String directionLabel, Logger logger) {
    if (app.getOptionalDotGrid().getDiagram().getCurrentMode() != MouseMode.MOVE) {
      app.getOptionalDotGrid().getDiagram().setOldMode(app.getOptionalDotGrid().getDiagram().getCurrentMode());
    }

    List<Knot> displayedKnots = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots());
    List<Knot> selectedKnots = app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots();
    List<Knot> copiedKnots = new ArrayList<>();
    List<Knot> toRemoveKnots = new ArrayList<>();

    for (Knot knot : selectedKnots) {
      knot.setX(knot.getX() + deltaX);
      knot.setY(knot.getY() + deltaY);
      Knot copiedKnot = new NodeUtil().copyKnot(knot);

      toRemoveKnots.add(knot);
      copiedKnots.add(copiedKnot);
      moveKnotPause.playFromStart();

      logger.info("Moving {} knot {}", directionLabel, knot);
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

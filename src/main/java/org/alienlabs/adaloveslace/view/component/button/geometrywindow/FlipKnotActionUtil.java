package org.alienlabs.adaloveslace.view.component.button.geometrywindow;

import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.Knot;
import org.alienlabs.adaloveslace.domain.enumeration.MouseMode;
import org.alienlabs.adaloveslace.util.NodeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.alienlabs.adaloveslace.domain.Diagram.newStep;

final class FlipKnotActionUtil {

  private FlipKnotActionUtil() {
    // Utility class
  }

  static void flipSelectedKnots(App app, Consumer<Knot> flipOperation) {
    app.getOptionalDotGrid().getDiagram().setCurrentMode(MouseMode.MIRROR);

    List<Knot> displayedKnots = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots());
    List<Knot> selectedKnots = app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots();
    List<Knot> selectedKnotsCopy = new ArrayList<>();

    for (Knot knot : selectedKnots) {
      Knot copy = new NodeUtil().copyKnot(knot);
      flipOperation.accept(copy);
      selectedKnotsCopy.add(copy);
    }

    displayedKnots.removeAll(selectedKnots);
    newStep(displayedKnots, selectedKnotsCopy, true);
  }
}

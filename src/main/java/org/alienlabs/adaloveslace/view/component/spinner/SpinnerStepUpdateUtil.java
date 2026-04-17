package org.alienlabs.adaloveslace.view.component.spinner;

import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.Knot;
import org.alienlabs.adaloveslace.util.NodeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

final class SpinnerStepUpdateUtil {

  private SpinnerStepUpdateUtil() {
    // Utility class
  }

  static void applyToSelectedKnots(App app, int newValue, BiConsumer<Knot, Integer> knotUpdater) {
    List<Knot> selectedKnots = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots());
    List<Knot> copiedKnots = new ArrayList<>(selectedKnots.size());
    NodeUtil nodeUtil = new NodeUtil();

    for (Knot knot : selectedKnots) {
      Knot copiedKnot = nodeUtil.copyKnot(knot);
      knotUpdater.accept(copiedKnot, newValue);
      copiedKnots.add(copiedKnot);
    }

    app.getOptionalDotGrid().getDiagram().setCurrentKnot(selectedKnots.isEmpty() ? null : selectedKnots.getLast());
    NodeUtil.duplicateSelectedKnotsAsNewStep(app, copiedKnots, true);
  }
}

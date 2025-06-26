package org.alienlabs.adaloveslace.view.component.spinner;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tooltip;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.Knot;
import org.alienlabs.adaloveslace.util.NodeUtil;

import java.util.ArrayList;
import java.util.List;

import static org.alienlabs.adaloveslace.App.TOOLTIPS_DURATION;
import static org.alienlabs.adaloveslace.App.resourceBundle;
import static org.alienlabs.adaloveslace.domain.Diagram.newStep;
import static org.alienlabs.adaloveslace.view.window.GeometryWindow.GEOMETRY_BUTTONS_HEIGHT;

public class RotationSpinner {

  private static int numberOfUpdates = 0;

  public void buildRotationSpinner(App app, Spinner<Integer> spinner,
                                   SpinnerValueFactory<Integer> spinnerToReflect1,
                                   SpinnerValueFactory<Integer> spinnerToReflect2) {
    SpinnerValueFactory<Integer> valueFactory = spinner.getValueFactory();

    ChangeListener<Integer> valueChangeListener = (observableValue, oldValue, newValue) -> {
      valueFactory.setValue(newValue);
      spinnerToReflect1.setValue(newValue);
      spinnerToReflect2.setValue(newValue);

      if (++numberOfUpdates == 1) {
        List<Knot> displayedKnots = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots());
        List<Knot> selectedKnots = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots());
        List<Knot> copiedKnots = new ArrayList<>();

        for (Knot knot : selectedKnots) {
          Knot copiedKnot = new NodeUtil().copyKnot(knot);
          copiedKnot.setRotationAngle(newValue);
          copiedKnots.add(copiedKnot);
        }

        app.getOptionalDotGrid().getDiagram().setCurrentKnot(selectedKnots.isEmpty() ? null : selectedKnots.getLast());
        newStep(displayedKnots, copiedKnots, true);
      }

      if (numberOfUpdates > 2) {
        numberOfUpdates = 0;
      }
    };

    valueFactory.valueProperty().addListener(valueChangeListener);

    spinner.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_VERTICAL);
    spinner.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);

    final Tooltip tooltip = new Tooltip();
    tooltip.setText(resourceBundle.getString("ROTATION_SPINNER_BUTTON_TOOLTIP"));
    tooltip.setShowDuration(TOOLTIPS_DURATION);
    spinner.setTooltip(tooltip);
  }

}

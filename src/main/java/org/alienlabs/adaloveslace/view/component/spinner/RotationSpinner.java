package org.alienlabs.adaloveslace.view.component.spinner;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tooltip;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.util.NodeUtil;

import java.util.HashSet;
import java.util.Set;

import static org.alienlabs.adaloveslace.view.window.GeometryWindow.GEOMETRY_BUTTONS_HEIGHT;

public class RotationSpinner {

  public static final String BUTTON_TOOLTIP = "Use these fields to rotate\nthe currently selected knot\n";

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
        Set<Knot> displayedCopy = new HashSet<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots());
        Set<Knot> selectedCopy = new HashSet<>();

        if (app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots() != null && !app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().isEmpty()) {
          for (Knot currentKnot : app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots()) {
            Knot knotCopy = new NodeUtil().copyKnot(currentKnot);
            knotCopy.setRotationAngle(newValue);
            selectedCopy.add(knotCopy);

            if (app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots() != null && !app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots().isEmpty()) {
              displayedCopy.remove(currentKnot);
              app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots().remove(currentKnot);
            }
          }
        }

        app.getOptionalDotGrid().getDiagram().addKnotsWithStep(displayedCopy, selectedCopy);
        app.getOptionalDotGrid().layoutChildren();
      }

      if (numberOfUpdates >= 3) {
        numberOfUpdates = 0;
      }
    };

    valueFactory.valueProperty().addListener(valueChangeListener);

    spinner.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_VERTICAL);
    spinner.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);

    final Tooltip tooltip = new Tooltip();
    tooltip.setText(BUTTON_TOOLTIP);
    spinner.setTooltip(tooltip);
  }

}

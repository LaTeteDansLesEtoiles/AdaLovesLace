package org.alienlabs.adaloveslace.view.component.spinner;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tooltip;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Knot;

import static org.alienlabs.adaloveslace.view.window.GeometryWindow.GEOMETRY_BUTTONS_HEIGHT;

public class ZoomSpinner {

  public static final String BUTTON_TOOLTIP = "Use these fields to zoom in or out\nthe currently selected knot\n";

  private static int numberOfUpdates = 0;

  public void buildZoomSpinner(App app, Spinner<Integer> spinner,
                               SpinnerValueFactory<Integer> spinnerToReflect1,
                               SpinnerValueFactory<Integer> spinnerToReflect2) {
    SpinnerValueFactory<Integer> valueFactory = spinner.getValueFactory();

    ChangeListener<Integer> valueChangeListener = (observableValue, oldValue, newValue) -> {
      valueFactory.setValue(newValue);
      spinnerToReflect1.setValue(newValue);
      spinnerToReflect2.setValue(newValue);

      if (++numberOfUpdates == 1) {
        if (app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots() != null && !app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().isEmpty()) {
          for (Knot currentKnot : app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots()) {
            currentKnot.setZoomFactor(newValue);
            app.getOptionalDotGrid().getDiagram().addKnotWithStep(currentKnot, true);
          }
        }

        app.getOptionalDotGrid().layoutChildren();
      }

      if (numberOfUpdates > 2) {
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

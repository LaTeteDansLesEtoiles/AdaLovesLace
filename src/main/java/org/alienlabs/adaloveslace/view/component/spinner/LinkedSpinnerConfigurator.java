package org.alienlabs.adaloveslace.view.component.spinner;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tooltip;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.Knot;

import java.util.function.BiConsumer;

import static org.alienlabs.adaloveslace.App.TOOLTIPS_DURATION;
import static org.alienlabs.adaloveslace.view.window.ToolboxWindow.GEOMETRY_BUTTONS_HEIGHT;

final class LinkedSpinnerConfigurator {

  private LinkedSpinnerConfigurator() {
    // Utility class
  }

  static void configure(
      App app,
      Spinner<Integer> spinner,
      SpinnerValueFactory<Integer> spinnerToReflect1,
      SpinnerValueFactory<Integer> spinnerToReflect2,
      String tooltipText,
      Runnable beginInternalUpdate,
      Runnable endInternalUpdate,
      java.util.function.BooleanSupplier isInternalUpdate,
      BiConsumer<Knot, Integer> knotUpdater
  ) {
    SpinnerValueFactory<Integer> valueFactory = spinner.getValueFactory();
    ChangeListener<Integer> valueChangeListener = (_, __, newValue) -> {
      valueFactory.setValue(newValue);
      spinnerToReflect1.setValue(newValue);
      spinnerToReflect2.setValue(newValue);

      if (isInternalUpdate.getAsBoolean()) {
        return;
      }

      beginInternalUpdate.run();
      try {
        SpinnerStepUpdateUtil.applyToSelectedKnots(app, newValue, knotUpdater);
      } finally {
        endInternalUpdate.run();
      }
    };

    valueFactory.valueProperty().addListener(valueChangeListener);
    spinner.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_VERTICAL);
    spinner.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);

    Tooltip tooltip = new Tooltip();
    tooltip.setText(tooltipText);
    tooltip.setShowDuration(TOOLTIPS_DURATION);
    spinner.setTooltip(tooltip);
  }
}

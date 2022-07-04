package org.alienlabs.adaloveslace.view.component.spinner;

import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tooltip;
import org.alienlabs.adaloveslace.App;

import static org.alienlabs.adaloveslace.view.window.GeometryWindow.GEOMETRY_BUTTONS_HEIGHT;

public class ZoomSpinner {

  public static final String BUTTON_TOOLTIP = "Use these fields to zoom in or out\nthe currently selected knot\n";

  public void buildZoomSpinner(App app, Spinner<Integer> spinner,
                               SpinnerValueFactory<Integer> spinnerToReflect1,
                               SpinnerValueFactory<Integer> spinnerToReflect2) {
    spinner.getValueFactory().valueProperty().addListener(
      (observableValue, oldValue, newValue) -> {
        spinnerToReflect1.setValue(newValue);
        spinnerToReflect2.setValue(newValue);

        app.getOptionalDotGrid().getDiagram().getCurrentKnot()
          .setZoomFactor(spinner.getValueFactory().getValue());
        app.getOptionalDotGrid().layoutChildren();
      });

    spinner.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_VERTICAL);
    spinner.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);

    final Tooltip tooltip = new Tooltip();
    tooltip.setText(BUTTON_TOOLTIP);
    spinner.setTooltip(tooltip);
  }

}

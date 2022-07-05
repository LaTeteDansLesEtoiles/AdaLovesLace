package org.alienlabs.adaloveslace.view.component.spinner;

import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tooltip;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Knot;

import static org.alienlabs.adaloveslace.view.window.GeometryWindow.GEOMETRY_BUTTONS_HEIGHT;

public class RotationSpinner {

  public static final String BUTTON_TOOLTIP = "Use these fields to rotate\nthe currently selected knot\n";
  private static SpinnerValueFactory<Integer> valueFactory;

  public void buildRotationSpinner(App app, Spinner<Integer> spinner,
                                   SpinnerValueFactory<Integer> spinnerToReflect1,
                                   SpinnerValueFactory<Integer> spinnerToReflect2) {
    valueFactory = spinner.getValueFactory();
    valueFactory.valueProperty().addListener(
      (observableValue, oldValue, newValue) -> {
        spinnerToReflect1.setValue(newValue);
        spinnerToReflect2.setValue(newValue);

        app.getOptionalDotGrid().getDiagram().getCurrentKnot()
          .setRotationAngle(valueFactory.getValue());
        app.getOptionalDotGrid().layoutChildren();
      });

    spinner.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_VERTICAL);
    spinner.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);

    final Tooltip tooltip = new Tooltip();
    tooltip.setText(BUTTON_TOOLTIP);
    spinner.setTooltip(tooltip);
  }

  public static void restoreRotationSpinnersState(final Knot knot) {
    valueFactory.valueProperty().setValue(knot.getRotationAngle());
  }

}

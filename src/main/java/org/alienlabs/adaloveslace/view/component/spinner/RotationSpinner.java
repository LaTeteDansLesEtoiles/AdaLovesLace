package org.alienlabs.adaloveslace.view.component.spinner;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tooltip;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Knot;

import static org.alienlabs.adaloveslace.view.window.GeometryWindow.GEOMETRY_BUTTONS_HEIGHT;

public class RotationSpinner {

  public static final String BUTTON_TOOLTIP = "Use these fields to rotate\nthe currently selected knot\n";
  private SpinnerValueFactory<Integer> valueFactory;

  public void buildRotationSpinner(App app, Spinner<Integer> spinner,
                                   SpinnerValueFactory<Integer> spinnerToReflect1,
                                   SpinnerValueFactory<Integer> spinnerToReflect2, int increment) {
    this.valueFactory = spinner.getValueFactory();
    ChangeListener<Integer> valueChangeListener = (observableValue, oldValue, newValue) -> {
      spinnerToReflect1.setValue(newValue);
      spinnerToReflect2.setValue(newValue);

      if (app.getOptionalDotGrid().getAllSelectedKnots() != null && !app.getOptionalDotGrid().getAllSelectedKnots().isEmpty()) {
        for (Knot currentKnot : app.getOptionalDotGrid().getAllSelectedKnots()) {
          if (newValue - oldValue == increment || newValue - oldValue == -increment) {
            currentKnot
              .setRotationAngle(currentKnot.getRotationAngle() + (newValue > oldValue ? increment : -increment));
            app.getOptionalDotGrid().circleSelectedKnot(currentKnot);
          }
        }
      } else {
        app.getOptionalDotGrid().getDiagram().getCurrentKnot()
          .setRotationAngle(valueFactory.getValue());
      }

      app.getOptionalDotGrid().layoutChildren();
    };
    this.valueFactory.valueProperty().addListener(valueChangeListener);

    spinner.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_VERTICAL);
    spinner.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);

    final Tooltip tooltip = new Tooltip();
    tooltip.setText(BUTTON_TOOLTIP);
    spinner.setTooltip(tooltip);
  }

  public void restoreRotationSpinnersState(final Knot knot) {
    this.valueFactory.valueProperty().setValue(knot.getRotationAngle());
  }

}

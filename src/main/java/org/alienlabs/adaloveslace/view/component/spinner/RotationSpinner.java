package org.alienlabs.adaloveslace.view.component.spinner;

import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import org.alienlabs.adaloveslace.App;

public class RotationSpinner {

  public void buildRotationSpinner(App app, Spinner<Integer> spinner,
                                    SpinnerValueFactory<Integer> spinnerToReflect1,
                                    SpinnerValueFactory<Integer> spinnerToReflect2) {
    spinner.setOnMouseClicked(event -> {
      app.getCanvasWithOptionalDotGrid().getDiagram().getCurrentKnot()
        .setRotationAngle(spinner.getValueFactory().getValue());
      app.getCanvasWithOptionalDotGrid().layoutChildren();
    });

    spinner.getValueFactory().valueProperty().addListener(
      (observableValue, oldValue, newValue) -> {
        spinnerToReflect1.setValue(newValue);
        spinnerToReflect2.setValue(newValue);
      });

    spinner.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_VERTICAL);
  }

}

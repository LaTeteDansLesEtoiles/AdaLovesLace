package org.alienlabs.adaloveslace.view.component.spinner;

import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import org.alienlabs.adaloveslace.App;

public class RotationSpinner {

  public void buildRotationSpinner(App app, Spinner<Integer> spinner,
                                   SpinnerValueFactory<Integer> spinnerToReflect1,
                                   SpinnerValueFactory<Integer> spinnerToReflect2) {
    spinner.getValueFactory().valueProperty().addListener(
      (observableValue, oldValue, newValue) -> {
        spinnerToReflect1.setValue(newValue);
        spinnerToReflect2.setValue(newValue);

        app.getOptionalDotGrid().getDiagram().getCurrentKnot()
          .setRotationAngle(spinner.getValueFactory().getValue());
        app.getOptionalDotGrid().layoutChildren();
      });

    spinner.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_VERTICAL);
  }

}

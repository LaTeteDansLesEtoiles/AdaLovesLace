package org.alienlabs.adaloveslace.view.component.spinner;

import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.Knot;

import static org.alienlabs.adaloveslace.App.resourceBundle;

public class ZoomSpinner {
  // When spinners are linked (1/2/3) they update each other by setting values,
  // which triggers multiple ChangeListeners. We only want to apply the expensive
  // diagram step once per external/user update, not for internal synchronization.
  private static boolean internalUpdateInProgress = false;

  /**
   * Reset internal static state between functional tests.
   */
  public static void resetNumberOfUpdates() {
    internalUpdateInProgress = false;
  }

  public void buildZoomSpinner(App app, Spinner<Integer> spinner,
                               SpinnerValueFactory<Integer> spinnerToReflect1,
                               SpinnerValueFactory<Integer> spinnerToReflect2) {
    LinkedSpinnerConfigurator.configure(
            app,
            spinner,
            spinnerToReflect1,
            spinnerToReflect2,
            resourceBundle.getString("ZOOM_SPINNER_BUTTON_TOOLTIP"),
            () -> internalUpdateInProgress = true,
            () -> internalUpdateInProgress = false,
            () -> internalUpdateInProgress,
            Knot::setZoomFactor
    );
  }

}

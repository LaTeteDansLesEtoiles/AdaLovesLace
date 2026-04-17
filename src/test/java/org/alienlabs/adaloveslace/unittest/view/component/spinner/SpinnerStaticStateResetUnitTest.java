package org.alienlabs.adaloveslace.unittest.view.component.spinner;

import org.alienlabs.adaloveslace.view.component.spinner.RotationSpinner;
import org.alienlabs.adaloveslace.view.component.spinner.ZoomSpinner;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@Tag("unit")
class SpinnerStaticStateResetUnitTest {

  @Test
  void zoom_spinner_reset_clears_internal_guard() {
    assertDoesNotThrow(ZoomSpinner::resetNumberOfUpdates);
  }

  @Test
  void rotation_spinner_reset_clears_internal_guard() {
    assertDoesNotThrow(RotationSpinner::resetNumberOfUpdates);
  }
}

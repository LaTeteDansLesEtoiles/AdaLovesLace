package org.alienlabs.adaloveslace.unittest.view.component.button.geometrywindow.move;

import org.alienlabs.adaloveslace.view.component.button.geometrywindow.move.FastMoveModeButton;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("unit")
class FastMoveModeButtonConstantsTest {

  @Test
  void fast_and_slow_speed_constants_are_stable() {
    assertEquals(5d, FastMoveModeButton.FAST_MODE_SPEED);
    assertEquals(1d, FastMoveModeButton.SLOW_MODE_SPEED);
  }

}

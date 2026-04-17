package org.alienlabs.adaloveslace.unittest.util;

import org.alienlabs.adaloveslace.util.MoveKnotVectors;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("unit")
class MoveKnotVectorsTest {

  @ParameterizedTest
  @CsvSource({
      "LEFT, 1, -1, 0",
      "RIGHT, 1, 1, 0",
      "UP, 1, 0, -1",
      "DOWN, 1, 0, 1",
      "UP_LEFT, 2, -2, -2",
      "UP_RIGHT, 2, 2, -2",
      "DOWN_LEFT, 2, -2, 2",
      "DOWN_RIGHT, 2, 2, 2",
      "LEFT, 5, -5, 0",
      "UP_RIGHT, 5, 5, -5"
  })
  void deltas_scale_with_speed(String directionName, double speed, double expectedDx, double expectedDy) {
    MoveKnotVectors.Direction d = MoveKnotVectors.Direction.valueOf(directionName);
    assertEquals(expectedDx, d.deltaX(speed), 1e-9);
    assertEquals(expectedDy, d.deltaY(speed), 1e-9);
  }
}

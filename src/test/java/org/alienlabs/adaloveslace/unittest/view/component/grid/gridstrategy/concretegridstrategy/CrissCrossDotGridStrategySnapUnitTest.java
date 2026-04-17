package org.alienlabs.adaloveslace.unittest.view.component.grid.gridstrategy.concretegridstrategy;

import javafx.geometry.Point2D;
import org.alienlabs.adaloveslace.domain.Diagram;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.concretegridstrategy.CrissCrossDotGridStrategy;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("unit")
class CrissCrossDotGridStrategySnapUnitTest {

  @Test
  void snap_clamps_x_to_first_column_when_left_of_offset() {
    CrissCrossDotGridStrategy s = new CrissCrossDotGridStrategy();
    s.setViewPort(400, 300, 0, 0);
    Point2D p = s.getSnapToGridDrawCoordinates(5, 12);
    assertEquals(Diagram.SPACING_X_FOR_CRISS_CROSS, p.getX(), 1e-9);
  }

  @Test
  void snap_snaps_y_to_nearest_row_index() {
    CrissCrossDotGridStrategy s = new CrissCrossDotGridStrategy();
    s.setViewPort(500, 500, 0, 0);
    Point2D p = s.getSnapToGridDrawCoordinates(30, 37);
    double expectedY = Math.floor(37 / Diagram.SPACING_Y_FOR_CRISS_CROSS) * Diagram.SPACING_Y_FOR_CRISS_CROSS;
    assertEquals(expectedY, p.getY(), 1e-9);
  }
}

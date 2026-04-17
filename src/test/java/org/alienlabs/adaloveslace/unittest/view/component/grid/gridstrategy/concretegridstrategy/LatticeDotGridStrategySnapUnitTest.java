package org.alienlabs.adaloveslace.unittest.view.component.grid.gridstrategy.concretegridstrategy;

import javafx.geometry.Point2D;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.concretegridstrategy.LatticeDotGridStrategy;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("unit")
class LatticeDotGridStrategySnapUnitTest {

  @Test
  void snap_returns_hex_lattice_point_near_input() {
    LatticeDotGridStrategy s = new LatticeDotGridStrategy();
    s.setViewPort(800, 600, 0, 0);
    Point2D p = s.getSnapToGridDrawCoordinates(42, 55);
    assertTrue(Double.isFinite(p.getX()));
    assertTrue(Double.isFinite(p.getY()));
    double a = 25d;
    double h = a * Math.sqrt(3) / 2;
    double v = 55 / h;
    double u = (42 - (a / 2) * v) / a;
    long ui = Math.round(u);
    long vi = Math.round(v);
    assertEquals(ui * a + vi * (a / 2), p.getX(), 1e-6);
    assertEquals(vi * h, p.getY(), 1e-6);
  }
}

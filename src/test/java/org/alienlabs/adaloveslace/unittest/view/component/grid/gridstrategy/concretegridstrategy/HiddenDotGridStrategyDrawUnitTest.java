package org.alienlabs.adaloveslace.unittest.view.component.grid.gridstrategy.concretegridstrategy;

import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.ParentGridStrategy;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.concretegridstrategy.HiddenDotGridStrategy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("unit")
class HiddenDotGridStrategyDrawUnitTest {

  @AfterEach
  void tearDown() {
    if (ParentGridStrategy.gridPane != null) {
      ParentGridStrategy.hideGrid();
    }
    ParentGridStrategy.gridPane = null;
    ParentGridStrategy.app = null;
  }

  @Test
  void drawGrid_clears_static_grid_collections() {
    Pane pane = new Pane();
    pane.getChildren().add(new Line(0, 0, 10, 10));
    ParentGridStrategy.gridPane = pane;
    ParentGridStrategy.grid.add(new Line(1, 1, 2, 2));

    new HiddenDotGridStrategy().drawGrid();

    assertTrue(ParentGridStrategy.getGrid().isEmpty());
    assertTrue(pane.getChildren().isEmpty());
  }

  @Test
  void snap_returns_identity_point() {
    HiddenDotGridStrategy s = new HiddenDotGridStrategy();
    s.setViewPort(100, 80, 5, 5);
    Point2D p = s.getSnapToGridDrawCoordinates(3.25, -7.5);
    assertEquals(3.25, p.getX(), 1e-9);
    assertEquals(-7.5, p.getY(), 1e-9);
  }
}

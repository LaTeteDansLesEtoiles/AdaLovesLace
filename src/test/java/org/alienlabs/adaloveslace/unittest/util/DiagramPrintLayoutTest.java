package org.alienlabs.adaloveslace.unittest.util;

import javafx.geometry.Rectangle2D;
import org.alienlabs.adaloveslace.util.DiagramPrintLayout;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("unit")
class DiagramPrintLayoutTest {

  @Test
  void expandAndClampToPane_addsPaddingAndClamps() {
    Rectangle2D r = DiagramPrintLayout.expandAndClampToPane(100, 80, 200, 160, 1000, 800, 20);
    assertEquals(80, r.getMinX());
    assertEquals(60, r.getMinY());
    assertEquals(140, r.getWidth());
    assertEquals(120, r.getHeight());
  }

  @Test
  void expandAndClampToPane_clampsAtPaneEdges() {
    Rectangle2D r = DiagramPrintLayout.expandAndClampToPane(5, 5, 15, 15, 20, 20, 50);
    assertEquals(0, r.getMinX());
    assertEquals(0, r.getMinY());
    assertEquals(20, r.getWidth());
    assertEquals(20, r.getHeight());
  }

  @Test
  void expandAndClampToPane_returnsFullPaneWhenDegenerateAfterClamp() {
    Rectangle2D r = DiagramPrintLayout.expandAndClampToPane(500, 500, 500, 500, 400, 300, 0);
    assertEquals(0, r.getMinX());
    assertEquals(0, r.getMinY());
    assertEquals(400, r.getWidth());
    assertEquals(300, r.getHeight());
  }
}

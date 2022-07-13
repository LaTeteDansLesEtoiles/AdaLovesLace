package org.alienlabs.adaloveslace.test;

import org.alienlabs.adaloveslace.business.model.Diagram;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.business.model.Pattern;
import org.alienlabs.adaloveslace.view.component.OptionalDotGrid;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OptionalDotGridTest {

  @Test
  void zoom_knot_factor_0() {
    // Init
    Knot knot = new Knot(0d, 0d, new Pattern(), null);
    knot.setZoomFactor(0);

    // Run
    double zoomFactor = new OptionalDotGrid(new Diagram(), null).zoomKnot(knot, null);

    // Verify
    assertEquals(1d, zoomFactor);
  }

  @Test
  void zoom_knot_with_zoom_factor_1() {
    // Init
    Knot knot = new Knot(0d, 0d, new Pattern(), null);
    knot.setZoomFactor(1);

    // Run
    double zoomFactor = new OptionalDotGrid(new Diagram(), null).zoomKnot(knot, null);

    // Verify
    assertTrue(zoomFactor > 1d);
    assertTrue(zoomFactor < 1.5d);
  }


  @Test
  void zoom_knot_with_zoom_factor_minus_1() {
    // Init
    Knot knot = new Knot(0d, 0d, new Pattern(), null);
    knot.setZoomFactor(-1);

    // Run
    double zoomFactor = new OptionalDotGrid(new Diagram(), null).zoomKnot(knot, null);

    // Verify
    assertTrue(zoomFactor < 1d);
    assertTrue(zoomFactor > 0.6d);
  }

  @Test
  void zoom_knot_factor_7() {
    // Init
    Knot knot = new Knot(0d, 0d, new Pattern(), null);
    knot.setZoomFactor(7);

    // Run
    double zoomFactor = new OptionalDotGrid(new Diagram(), null).zoomKnot(knot, null);

    // Verify
    assertEquals(2.05d, zoomFactor);
  }

  @Test
  void zoom_knot_factor_20() {
    // Init
    Knot knot = new Knot(0d, 0d, new Pattern(), null);
    knot.setZoomFactor(20);

    // Run
    double zoomFactor = new OptionalDotGrid(new Diagram(), null).zoomKnot(knot, null);

    // Verify
    assertEquals(4d, zoomFactor);
  }

  @Test
  void zoom_knot_factor_minus_5() {
    // Init
    Knot knot = new Knot(0d, 0d, new Pattern(), null);
    knot.setZoomFactor(-5);

    // Run
    double zoomFactor = new OptionalDotGrid(new Diagram(), null).zoomKnot(knot, null);

    // Verify
    assertTrue(zoomFactor < 0.8d);
    assertTrue(zoomFactor > 0.6);
  }

  @Test
  void zoom_knot_factor_minus_15() {
    // Init
    Knot knot = new Knot(0d, 0d, new Pattern(), null);
    knot.setZoomFactor(-15);

    // Run
    double zoomFactor = new OptionalDotGrid(new Diagram(), null).zoomKnot(knot, null);

    // Verify
    assertTrue(zoomFactor < 0.7d);
    assertTrue(zoomFactor > 0.5d);
  }

}

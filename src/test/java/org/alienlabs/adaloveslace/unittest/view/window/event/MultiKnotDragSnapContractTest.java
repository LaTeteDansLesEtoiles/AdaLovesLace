package org.alienlabs.adaloveslace.unittest.view.window.event;

import javafx.geometry.Point2D;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.concretegridstrategy.CrissCrossDotGridStrategy;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.concretegridstrategy.HiddenDotGridStrategy;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.concretegridstrategy.LatticeDotGridStrategy;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.concretegridstrategy.PolarDotGridStrategy;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Documents and guards the multi-knot drag follower rule used in
 * {@link org.alienlabs.adaloveslace.view.window.event.GridEvents#dragOverHandleWithSelectionMode}:
 * each follower snaps to {@code getSnapToGridDrawCoordinates(start + displacement)} where
 * {@code displacement} is pointer motion in movablePane space since press (not leader snap deltas).
 */
@Tag("unit")
class MultiKnotDragSnapContractTest {

  private static final double VIEW_W = 800;
  private static final double VIEW_H = 600;

  @Test
  void hiddenGrid_snapIsIdentity_soFollowerEqualsStartPlusDisplacement() {
    HiddenDotGridStrategy strategy = new HiddenDotGridStrategy();
    strategy.setViewPort(VIEW_W, VIEW_H, 0, 0);

    Point2D start = new Point2D(12.5, 40);
    Point2D displacement = new Point2D(3, -7);
    Point2D candidate = start.add(displacement);

    Point2D snapped = strategy.getSnapToGridDrawCoordinates(candidate.getX(), candidate.getY());

    assertEquals(candidate.getX(), snapped.getX(), 1e-9);
    assertEquals(candidate.getY(), snapped.getY(), 1e-9);
  }

  @Test
  void crissCross_twoFollowersSameDisplacement_preservesSeparationAfterSnap() {
    CrissCrossDotGridStrategy strategy = new CrissCrossDotGridStrategy();
    strategy.setViewPort(VIEW_W, VIEW_H, 0, 0);

    Point2D startA = new Point2D(25, 0);
    Point2D startB = new Point2D(75, 0);
    Point2D displacement = new Point2D(25, 0);

    Point2D snapA = strategy.getSnapToGridDrawCoordinates(
        startA.getX() + displacement.getX(), startA.getY() + displacement.getY());
    Point2D snapB = strategy.getSnapToGridDrawCoordinates(
        startB.getX() + displacement.getX(), startB.getY() + displacement.getY());

    assertEquals(50, snapA.getX(), 1e-9);
    assertEquals(100, snapB.getX(), 1e-9);
    assertEquals(startB.getX() - startA.getX(), snapB.getX() - snapA.getX(), 1e-9);
  }

  /**
   * When the leader's snapped position does not change between frames (snap-to-snap delta 0),
   * followers must still react to pointer displacement in pane space. Using leader snap deltas
   * would wrongly keep followers fixed in this situation.
   */
  @Test
  void crissCross_pointerDisplacementCanMoveFollowerWhileLeaderSnapDeltaWouldBeZero() {
    CrissCrossDotGridStrategy strategy = new CrissCrossDotGridStrategy();
    strategy.setViewPort(VIEW_W, VIEW_H, 0, 0);

    Point2D followerStart = new Point2D(93, 100);
    Point2D displacement = new Point2D(20, 0);

    Point2D wrongIfLeaderDeltaZero = strategy.getSnapToGridDrawCoordinates(
        followerStart.getX(), followerStart.getY());
    Point2D correctWithPointerDisplacement = strategy.getSnapToGridDrawCoordinates(
        followerStart.getX() + displacement.getX(), followerStart.getY() + displacement.getY());

    assertEquals(75, wrongIfLeaderDeltaZero.getX(), 1e-9);
    assertEquals(100, correctWithPointerDisplacement.getX(), 1e-9);
    assertEquals(100, correctWithPointerDisplacement.getY(), 1e-9);
  }

  @Test
  void lattice_twoNeighborsSameDisplacement_preservesSeparationAfterSnap() {
    LatticeDotGridStrategy strategy = new LatticeDotGridStrategy();
    strategy.setViewPort(VIEW_W, VIEW_H, 0, 0);

    Point2D startA = new Point2D(0, 0);
    Point2D startB = new Point2D(25, 0);
    Point2D displacement = new Point2D(50, 0);

    Point2D snapA = strategy.getSnapToGridDrawCoordinates(
        startA.getX() + displacement.getX(), startA.getY() + displacement.getY());
    Point2D snapB = strategy.getSnapToGridDrawCoordinates(
        startB.getX() + displacement.getX(), startB.getY() + displacement.getY());

    assertEquals(50, snapA.getX(), 1e-9);
    assertEquals(75, snapB.getX(), 1e-9);
    assertEquals(25, snapB.getX() - snapA.getX(), 1e-9);
  }

  @Test
  void polar_followerSnapsToGridAlongRadialMotion() {
    PolarDotGridStrategy strategy = new PolarDotGridStrategy();
    strategy.setViewPort(VIEW_W, VIEW_H, 0, 0);

    double cx = VIEW_W / 2.0;
    double cy = VIEW_H / 2.0;

    Point2D start = new Point2D(cx + 25, cy);
    Point2D displacement = new Point2D(25, 0);
    Point2D snapped = strategy.getSnapToGridDrawCoordinates(
        start.getX() + displacement.getX(), start.getY() + displacement.getY());

    assertNotNull(snapped);
    double r = Math.hypot(snapped.getX() - cx, snapped.getY() - cy);
    assertTrue(r > 40 && r < 60, "expected second ring (~50) from center, got r=" + r);
  }
}

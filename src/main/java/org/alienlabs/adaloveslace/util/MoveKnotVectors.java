package org.alienlabs.adaloveslace.util;

/**
 * Unit-direction vectors for geometry-window knot nudges, scaled by {@link
 * org.alienlabs.adaloveslace.view.component.button.geometrywindow.move.FastMoveModeButton#getMoveSpeed()}.
 */
public final class MoveKnotVectors {

  public enum Direction {
    LEFT(-1d, 0d),
    RIGHT(1d, 0d),
    UP(0d, -1d),
    DOWN(0d, 1d),
    UP_LEFT(-1d, -1d),
    UP_RIGHT(1d, -1d),
    DOWN_LEFT(-1d, 1d),
    DOWN_RIGHT(1d, 1d);

    private final double unitX;
    private final double unitY;

    Direction(double unitX, double unitY) {
      this.unitX = unitX;
      this.unitY = unitY;
    }

    public double deltaX(double speed) {
      return unitX * speed;
    }

    public double deltaY(double speed) {
      return unitY * speed;
    }
  }

  private MoveKnotVectors() {
  }
}

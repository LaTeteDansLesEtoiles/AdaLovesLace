package org.alienlabs.adaloveslace.business.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Something drawn on a Canvas, like with a brush (which is a lace Pattern), at certain x & y coordinates.
 *
 * @see Pattern
 * @see javafx.scene.canvas.Canvas
 *
  */
public class Knot {

  // Two coinciding Knots can be different
  private final UUID uuid;

  private double x;
  private double y;
  private Pattern pattern;

  public Knot(final double x, final double y, final Pattern pattern) {
    this.uuid = UUID.randomUUID();
    this.x = x;
    this.y = y;
    this.pattern = pattern;
  }

  public UUID getUuid() {
    return this.uuid;
  }

  public double getX() {
    return this.x;
  }

  public double getY() {
    return this.y;
  }

  public Pattern getPattern() {
    return this.pattern;
  }

  public void setX(double x) {
    this.x = x;
  }

  public void setY(double y) {
    this.y = y;
  }

  public void setPattern(Pattern pattern) {
    this.pattern = pattern;
  }

  public boolean coincide(Knot other) {
    return this.x == other.x && this.y == other.y && this.pattern.filename().equals(other.getPattern().filename());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Knot knot)) return false;
    return uuid.equals(knot.uuid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid);
  }

}

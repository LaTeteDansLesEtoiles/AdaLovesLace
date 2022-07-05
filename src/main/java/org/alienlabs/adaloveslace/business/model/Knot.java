package org.alienlabs.adaloveslace.business.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;
import javafx.scene.image.ImageView;

import java.util.Objects;
import java.util.UUID;

/**
 * Something drawn on a Canvas, like with a brush (which is a lace Pattern), at certain x & y coordinates.
 *
 * @see Pattern
 * @see javafx.scene.canvas.Canvas
 *
  */
@XmlType(name = "Knot")
@XmlAccessorType(XmlAccessType.FIELD)
public class Knot {

  // Two coinciding Knots can be different
  private final UUID uuid;

  private double x;
  private double y;

  private int rotationAngle;

  private int zoomFactor;

  private Pattern pattern;

  private boolean visible;

  @XmlTransient
  private ImageView imageView;

  public Knot() {
    this.uuid = UUID.randomUUID();
    this.visible = true;
  }

  public Knot(final double x, final double y, final Pattern pattern, final ImageView imageView) {
    this.x              = x;
    this.y              = y;
    this.pattern        = pattern;
    this.imageView      = imageView;

    this.uuid           = UUID.randomUUID();
    this.rotationAngle  = 0;
    this.zoomFactor     = 0;
    this.visible        = true;
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

  public int getRotationAngle() {
    return rotationAngle;
  }

  public void setRotationAngle(int rotationAngle) {
    this.rotationAngle = rotationAngle;
  }

  public int getZoomFactor() {
    return zoomFactor;
  }

  public void setZoomFactor(int zoomFactor) {
    this.zoomFactor = zoomFactor;
  }

  public void setPattern(Pattern pattern) {
    this.pattern = pattern;
  }

  public ImageView getImageView() {
    return imageView;
  }

  public void setImageView(ImageView imageView) {
    this.imageView = imageView;
  }

  public boolean isVisible() {
    return visible;
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  public boolean coincide(Knot other) {
    return this.x == other.x && this.y == other.y &&
      this.pattern.getAbsoluteFilename().equals(other.getPattern().getAbsoluteFilename());
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

  @Override
  public String toString() {
    return "Knot{" +
      "uuid=" + uuid +
      ", x=" + x +
      ", y=" + y +
      ", rotationAngle=" + rotationAngle +
      ", zoomFactor=" + zoomFactor +
      ", pattern=" + pattern +
      ", visible=" + visible +
      '}';
  }

}

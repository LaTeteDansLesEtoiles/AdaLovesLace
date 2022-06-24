package org.alienlabs.adaloveslace.business.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import javafx.scene.image.Image;

import java.io.File;
import java.net.MalformedURLException;
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

  private double rotationAngle;

  private double zoomFactor;

  private Pattern pattern;

  public Knot() {
    this.uuid = UUID.randomUUID();
  }

  public Knot(final double x, final double y, final Pattern pattern) {
    this.uuid = UUID.randomUUID();
    this.x = x;
    this.y = y;
    this.rotationAngle = 0;
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

  public double getRotationAngle() {
    return rotationAngle;
  }

  public void setRotationAngle(double rotationAngle) {
    this.rotationAngle = rotationAngle;
  }

  public double getZoomFactor() {
    return zoomFactor;
  }

  public void setZoomFactor(double zoomFactor) {
    this.zoomFactor = zoomFactor;
  }

  public void setPattern(Pattern pattern) {
    this.pattern = pattern;
  }

  public boolean coincide(Knot other) {
    return this.x == other.x && this.y == other.y && this.pattern.getAbsoluteFilename().equals(other.getPattern().getAbsoluteFilename());
  }

  public boolean isClicked(double mouseX, double mouseY) throws MalformedURLException {
    Image img = new Image(new File(this.pattern.getAbsoluteFilename()).toURI().toURL().toExternalForm());

    return (this.x <= mouseX) && (this.x + img.getWidth() >= mouseX) &&
      (this.y <= mouseY) && (this.y + img.getHeight() >= mouseY);
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

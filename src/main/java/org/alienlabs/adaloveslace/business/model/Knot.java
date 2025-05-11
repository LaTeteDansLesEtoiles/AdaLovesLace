package org.alienlabs.adaloveslace.business.model;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javafx.scene.Node;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Something drawn on a Canvas, like with a brush (which is a lace Pattern), at given x & y coordinates.
 *
 * @see Pattern
 * @see javafx.scene.canvas.Canvas
 *
  */
@XmlType(name = "Knot")
@XmlAccessorType(XmlAccessType.FIELD)
public class Knot implements Comparable<Knot> {

  @XmlTransient
  public static final int DEFAULT_ROTATION  = 0;
  @XmlTransient
  public static final int DEFAULT_ZOOM      = 0;

  // Two coinciding Knots can be different
  private final UUID uuid;

  private double x;
  private double y;

  private int rotationAngle;

  private int zoomFactor;

  @XmlElement
  @XmlJavaTypeAdapter(OptionalPatternAdapter.class)
  private Optional<Pattern> pattern = Optional.empty();

  @XmlElement
  @XmlJavaTypeAdapter(OptionalStringAdapter.class)
  private Optional<String> text = Optional.of("");

  private boolean visible = true;

  private boolean selectable = true;

  private boolean flippedVertically = false;

  private boolean flippedHorizontally = false;


  @XmlTransient
  private ImageView imageView;

  @XmlTransient
  private Node selection;

  @XmlTransient
  private Node hovered;

  @XmlTransient
  private boolean hoveredKnot = false;

  @XmlTransient
  private Node handle;

  @XmlTransient
  private List<Node> guideLines = new ArrayList<>();

  public Knot() {
    this.uuid                 = UUID.randomUUID();
  }

  public Knot(
          final double x,
          final double y,
          final Optional<Pattern> pattern,
          final Optional<String> text,
          final ImageView imageView
  ) {
    this.x                    = x;
    this.y                    = y;
    this.pattern              = pattern;
    this.text                 = text;
    this.imageView            = imageView;

    this.uuid                 = UUID.randomUUID();
    this.rotationAngle        = DEFAULT_ROTATION;
    this.zoomFactor           = DEFAULT_ZOOM;
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

  public Optional<Pattern> getPattern() {
    return this.pattern;
  }

  public void setPattern(Optional<Pattern> pattern) {
    this.pattern = pattern;
  }

  public Optional<String> getText() {
    return this.text;
  }

  public void setText(Optional<String> text) {
    this.text = text;
  }

  public ImageView getImageView() {
    return imageView;
  }

  public void setImageView(ImageView imageView) {
    this.imageView = imageView;
  }

  public Node getSelection() {
    return selection;
  }

  public void setSelection(Node selection) {
    this.selection = selection;
  }

  public Node getHovered() {
    return hovered;
  }

  public void setHovered(Node hovered) {
    this.hovered = hovered;
  }

  public Node getHandle() {
    return handle;
  }

  public void setHandle(Node handle) {
    this.handle = handle;
  }

  public List<Node> getGuideLines() {
    return guideLines;
  }

  public boolean isVisible() {
    return visible;
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  public boolean isSelectable() {
    return this.selectable;
  }

  public void setSelectable(boolean selectable) {
    this.selectable = selectable;
  }

  public boolean isFlippedVertically() {
    return flippedVertically;
  }

  public void setFlippedVertically(boolean flippedVertically) {
    this.flippedVertically = flippedVertically;
  }

  public boolean isFlippedHorizontally() {
    return flippedHorizontally;
  }

  public void setFlippedHorizontally(boolean flippedHorizontally) {
    this.flippedHorizontally = flippedHorizontally;
  }

  public boolean isHoveredKnot() {
    return this.hoveredKnot;
  }

  public void setHoveredKnot(boolean hoveredKnot) {
    this.hoveredKnot = hoveredKnot;
  }

  @Override
  public final boolean equals(Object o) {
    if (!(o instanceof Knot knot)) return false;

    return uuid.equals(knot.uuid);
  }

  @Override
  public int hashCode() {
    return uuid.hashCode();
  }

  @Override
  public int compareTo(Knot knot) {
    return uuid.compareTo(knot.getUuid());
  }
}

package org.alienlabs.adaloveslace.business.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;
import org.alienlabs.adaloveslace.view.window.ToolboxWindow;

import java.io.File;
import java.util.Objects;

/**
 * An image, or at least the one represented by a Pattern filename, which can be put at will on a Canvas and chosen
 * in the ToolboxWindow.
 * <p>
 * Initially, the Patterns are loaded from a given package of the classpath, according to the expression:
 *
 * @see org.alienlabs.adaloveslace.util.FileUtil#CLASSPATH_RESOURCES_PATH
 * <p>
 * @see ToolboxWindow#loadPatternsResourcesFiles
 *
 * */
@XmlType(name = "Pattern")
@XmlAccessorType(XmlAccessType.FIELD)
public class Pattern implements Comparable<Pattern>{

  @XmlTransient
  private String absoluteFilename;

  private String filename;

  private double centerX;

  private double centerY;

  private double width;

  private double height;

  public Pattern(String absoluteFilename) {
    this.absoluteFilename = absoluteFilename;
    if (this.absoluteFilename == null) {
      this.filename = null;
    } else {
      this.filename = new File(absoluteFilename).getName();
    }
  }

  public Pattern() {
    // For JAXB
  }

  public String getAbsoluteFilename() {
    return this.absoluteFilename;
  }

  public void setAbsoluteFilename(String absoluteFilename) {
    this.absoluteFilename = absoluteFilename;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public double getCenterX() {
    return centerX;
  }

  public void setCenterX(double centerX) {
    this.centerX = centerX;
  }

  public double getCenterY() {
    return centerY;
  }

  public void setCenterY(double centerY) {
    this.centerY = centerY;
  }

  public double getWidth() {
    return width;
  }

  public void setWidth(double width) {
    this.width = width;
  }

  public double getHeight() {
    return height;
  }

  public void setHeight(double height) {
    this.height = height;
  }

  @Override
  public String toString() {
    return "Pattern{" +
      "absoluteFilename='" + absoluteFilename + '\'' +
      "width='" + width + '\'' +
      "height='" + height + '\'' +
      '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Pattern)) return false;
    Pattern pattern = (Pattern) o;
    return Objects.equals(filename, pattern.filename);
  }

  @Override
  public int hashCode() {
    return Objects.hash(filename);
  }

  @Override
  public int compareTo(Pattern p) {
    return this.filename.compareTo(p.filename);
  }
}

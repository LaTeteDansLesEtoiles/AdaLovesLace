package org.alienlabs.adaloveslace.business.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;

import java.util.ArrayList;
import java.util.List;

/**
 * What is drawn on a Canvas: the Diagram is the desired, final business object consisting of Knots drawn with Patterns.
 *
 * @see Pattern
 * @see Knot
 *
 */
@XmlRootElement(name = "Diagram")
@XmlAccessorType(XmlAccessType.FIELD)
public class Diagram {

  private final List<Pattern> patterns;
  private List<Knot>          knots;

  private int currentKnotIndex = 0;

  @XmlTransient
  private Pattern currentPattern;

  public Diagram() {
    this.patterns = new ArrayList<>();
    this.knots    = new ArrayList<>();
  }

  public Diagram(final Diagram diagram) {
    this.patterns         = new ArrayList<>(diagram.getPatterns());
    this.knots            = new ArrayList<>(diagram.getKnots());
    this.currentKnotIndex = diagram.getCurrentKnotIndex();
    this.setCurrentPattern(diagram.getCurrentPattern());
  }

  public List<Pattern> getPatterns() {
    return new ArrayList<>(this.patterns);
  }

  public List<Knot> getKnots() {
    return new ArrayList<>(this.knots);
  }

  public void setKnots(List<Knot> knots) {
    this.knots = knots;
  }

  public List<Pattern> addPattern(final Pattern pattern) {
    this.patterns.add(pattern);
    return new ArrayList<>(this.patterns);
  }

  public List<Knot> addKnot(final Knot knot) {
    // Add a knot to the end, deleting the knots after the current one
    if (this.currentKnotIndex < this.knots.size()) {
      this.knots = this.knots.subList(0, this.currentKnotIndex);
    }

    this.knots.add(knot);
    this.currentKnotIndex++;

    return new ArrayList<>(this.knots);
  }

  public List<Knot> undoLastKnot() {
    if (currentKnotIndex > 0) {
      this.currentKnotIndex--;
    }

    return new ArrayList<>(this.knots);
  }

  public List<Knot> redoLastKnot() {
    if (currentKnotIndex < this.knots.size()) {
      this.currentKnotIndex++;
    }

    return new ArrayList<>(this.knots);
  }

  public List<Knot> clearKnots() {
    this.knots.clear();
    this.currentKnotIndex = 0;
    return new ArrayList<>(this.knots);
  }

  public List<Pattern> clearPatterns() {
    this.patterns.clear();
    return new ArrayList<>(this.patterns);
  }

  public Pattern getCurrentPattern() {
    return this.currentPattern;
  }

  public void setCurrentPattern(Pattern currentPattern) {
    this.currentPattern = currentPattern;
  }

  public int getCurrentKnotIndex() {
    return this.currentKnotIndex;
  }

}

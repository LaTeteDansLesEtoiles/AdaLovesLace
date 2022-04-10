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
  private final List<Knot>    knots;

  @XmlTransient
  private Pattern currentPattern;

  public Diagram() {
    this.patterns = new ArrayList<>();
    this.knots    = new ArrayList<>();
  }

  public Diagram(final Diagram diagram) {
    this.patterns = new ArrayList<>(diagram.getPatterns());
    this.knots    = new ArrayList<>(diagram.getKnots());
    this.setCurrentPattern(diagram.getCurrentPattern());
  }

  public List<Pattern> getPatterns() {
    return new ArrayList<>(this.patterns);
  }

  public List<Knot> getKnots() {
    return new ArrayList<>(this.knots);
  }

  public List<Pattern> addPattern(final Pattern pattern) {
    this.patterns.add(pattern);
    return new ArrayList<>(this.patterns);
  }

  public List<Knot> addKnot(final Knot knot) {
    this.knots.add(knot);
    return new ArrayList<>(this.knots);
  }

  public List<Pattern> clearPatterns() {
    this.patterns.clear();
    return new ArrayList<>(this.patterns);
  }

  public List<Knot> clearKnots() {
    this.knots.clear();
    return new ArrayList<>(this.knots);
  }

  public Pattern getCurrentPattern() {
    return this.currentPattern;
  }

  public void setCurrentPattern(Pattern currentPattern) {
    this.currentPattern = currentPattern;
  }

}

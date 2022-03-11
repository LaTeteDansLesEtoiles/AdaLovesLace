package org.alienlabs.adaloveslace.business.model;

import java.util.ArrayList;
import java.util.List;

public class Diagram {

  private final List<Pattern> patterns;
  private final List<Knot>    knots;
  private Pattern currentPattern;

  public Diagram() {
    this.patterns = new ArrayList<>();
    this.knots    = new ArrayList<>();
  }

  public List<Pattern> getPatterns() {
    return this.patterns;
  }

  public List<Knot> getKnots() {
    return this.knots;
  }

  public List<Pattern> addPattern(final Pattern pattern) {
    this.patterns.add(pattern);
    return this.patterns;
  }

  public List<Knot> addKnot(final Knot knot) {
    this.knots.add(knot);
    return this.knots;
  }

  public List<Pattern> clearPatterns() {
    this.patterns.clear();
    return this.patterns;
  }

  public List<Knot> clearKnots() {
    this.knots.clear();
    return this.knots;
  }

  public Pattern getCurrentPattern() {
    return this.currentPattern;
  }

  public void setCurrentPattern(Pattern currentPattern) {
    this.currentPattern = currentPattern;
  }

}

package org.alienlabs.adaloveslace.business.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import org.alienlabs.adaloveslace.App;

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

  @XmlTransient
  private Knot currentKnot;

  @XmlTransient
  private boolean isKnotSelected;

  @XmlTransient
  private MouseMode currentMode;

  public Diagram() {
    this.patterns     = new ArrayList<>();
    this.knots        = new ArrayList<>();
    this.currentMode  = MouseMode.DRAWING;
  }

  public Diagram(final Diagram diagram) {
    this.patterns         = new ArrayList<>(diagram.getPatterns());
    this.knots            = new ArrayList<>(diagram.getKnots());
    this.currentKnotIndex = diagram.getCurrentKnotIndex();
    this.currentMode      = diagram.getCurrentMode();
    this.currentKnot      = diagram.getCurrentKnot();
    this.isKnotSelected   = diagram.isKnotSelected();
    this.setCurrentPattern(diagram.getCurrentPattern());
  }

  public List<Pattern> getPatterns() {
    return this.patterns;
  }

  public List<Knot> getKnots() {
    return this.knots;
  }

  public void setKnots(List<Knot> knots) {
    this.knots = knots;
  }

  public List<Pattern> addPattern(final Pattern pattern) {
    this.patterns.add(pattern);
    return this.patterns;
  }

  public List<Knot> addKnot(final Knot knot) {
    // Add a knot to the end, deleting the knots after the current one
    if (this.currentKnotIndex < this.knots.size()) {
      this.knots = this.knots.subList(0, this.currentKnotIndex);
    }

    this.knots.add(knot);
    this.currentKnotIndex++;

    return this.knots;
  }

  public List<Knot> undoLastKnot(App app) {
    if (currentKnotIndex > 0) {

      if (currentKnotIndex < knots.size()) {
        app.getOptionalDotGrid().clearSelection(knots.get(this.currentKnotIndex));
        app.getOptionalDotGrid().clearGuideLines(knots.get(this.currentKnotIndex));
      }

      this.currentKnotIndex--;

      if ((this.currentKnotIndex > 0) && (!knots.get(currentKnotIndex).isVisible())) {
        app.getOptionalDotGrid().clearSelection(knots.get(this.currentKnotIndex));
        app.getOptionalDotGrid().clearGuideLines(knots.get(this.currentKnotIndex));

        knots.get(currentKnotIndex).setVisible(true);
      }
    }

    app.getOptionalDotGrid().clearSelection(knots.get(this.currentKnotIndex));
    app.getOptionalDotGrid().clearGuideLines(knots.get(this.currentKnotIndex));

    return this.knots;
  }

  public List<Knot> redoLastKnot() {
    if (currentKnotIndex < this.knots.size()) {
      this.currentKnotIndex++;

      if ((this.currentKnotIndex < knots.size()) && (!knots.get(currentKnotIndex).isVisible())) {
        knots.get(currentKnotIndex).setVisible(true);
      }
    }

    return this.knots;
  }

  // We don't lose the undo / redo history
  public List<Knot> resetDiagram() {
    this.currentKnotIndex = 0;
    return this.knots;
  }

  public List<Knot> clearKnots() {
    this.knots.clear();
    this.currentKnotIndex = 0;
    return this.knots;
  }

  public List<Pattern> clearPatterns() {
    this.patterns.clear();
    return this.patterns;
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

  public Knot getCurrentKnot() {
    return currentKnot;
  }

  public void setCurrentKnot(Knot currentKnot) {
    this.currentKnot = currentKnot;
  }

  public boolean isKnotSelected() {
    return isKnotSelected;
  }

  public void setKnotSelected(boolean knotSelected) {
    isKnotSelected = knotSelected;
  }

  public MouseMode getCurrentMode() {
    return currentMode;
  }

  public void setCurrentMode(MouseMode currentMode) {
    this.currentMode = currentMode;
  }

  public void setCurrentKnotIndex(int currentKnotIndex) {
    this.currentKnotIndex = currentKnotIndex;
  }

}

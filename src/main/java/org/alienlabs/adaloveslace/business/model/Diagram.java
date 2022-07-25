package org.alienlabs.adaloveslace.business.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import org.alienlabs.adaloveslace.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  private int currentStepIndex = 0;

  private List<Step> allSteps = new ArrayList<>();

  @XmlTransient
  private Pattern currentPattern;

  @XmlTransient
  private Knot currentKnot;

  @XmlTransient
  private boolean isKnotSelected;

  @XmlTransient
  private MouseMode currentMode;

  @XmlTransient
  private static final Logger logger = LoggerFactory.getLogger(Diagram.class);

  public Diagram() {
    this.patterns     = new ArrayList<>();
    this.knots        = new ArrayList<>();
    this.currentMode  = MouseMode.DRAWING;
  }

  public Diagram(final Diagram diagram) {
    this.patterns         = new ArrayList<>(diagram.getPatterns());
    this.knots            = new ArrayList<>(diagram.getKnots());
    this.currentStepIndex = diagram.getCurrentStepIndex();
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

  public List<Knot> addKnotWithStep(App app, final Knot knot) {
    this.knots.add(knot);

    if (!this.getAllSteps().isEmpty()) {
      this.currentStepIndex++;
    }
    this.getAllSteps().add(new Step(this.knots, app.getOptionalDotGrid().getAllSelectedKnots()));

    return this.knots;
  }

  public List<Knot> addKnotWithoutStep(App app, final Knot knot) {
    this.knots.add(knot);
    return this.knots;
  }

  public void undoLastStep(App app) {
    logger.info("Undo step, current step={}", currentStepIndex);

    if (this.currentStepIndex >= 0) {
      this.knots.clear();
      this.knots.addAll(getCurrentStep().getDisplayedKnots());
      app.getOptionalDotGrid().deleteKnotsFromCanvas(); // Delete nodes from step before

      this.currentStepIndex--;

      if (this.currentStepIndex == 0) {
        this.knots.clear();
        this.knots.addAll(getCurrentStep().getDisplayedKnots());
        app.getOptionalDotGrid().layoutChildren(); // Display nodes from new state
      }

      logger.info("Undo step, new step={}", currentStepIndex);
    }

  }

  public void redoLastStep(App app) {
    logger.info("Redo step, current step={}", currentStepIndex);

    if (currentStepIndex < this.getAllSteps().size() - 1) {
      this.currentStepIndex++;

      this.knots.clear();
      this.knots.addAll(getCurrentStep().getDisplayedKnots());
      app.getOptionalDotGrid().layoutChildren(); // Display nodes from new state

      logger.info("Redo step, new step={}", currentStepIndex);
    }
  }

  // We don't lose the undo / redo history
  public List<Knot> resetDiagram() {
    this.currentStepIndex = 0;
    return this.knots;
  }

  public List<Knot> clearKnots() {
    this.knots.clear();
    this.currentStepIndex = 0;
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

  public int getCurrentStepIndex() {
    return this.currentStepIndex;
  }

  public void setCurrentStepIndex(int currentStepIndex) {
    this.currentStepIndex = currentStepIndex;
  }

  public void addStep(List<Knot> displayedKnots, List<Knot> selectedKnots) {
    logger.info("Adding step, current step={}", currentStepIndex);

    if (this.allSteps.isEmpty()) {
      this.allSteps.add(new Step(displayedKnots, selectedKnots));
    } else if (currentStepIndex < this.allSteps.size() - 1) {
      this.allSteps.subList(0, currentStepIndex).add(new Step(displayedKnots, selectedKnots));
      currentStepIndex++;
    } else if (currentStepIndex == this.allSteps.size() - 1) {
      this.allSteps.add(new Step(displayedKnots, selectedKnots));
      currentStepIndex++;
    }

    logger.info("Adding step, new step={}", currentStepIndex);
  }

  public Step getCurrentStep() {
    return allSteps.get(currentStepIndex);
  }

  public List<Step> getAllSteps() {
    return allSteps;
  }

}

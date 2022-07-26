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

  private int currentStepIndex;

  private List<Step> allSteps;

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
    this.allSteps     = new ArrayList<>();
    this.currentMode  = MouseMode.DRAWING;
    this.currentStepIndex = 0;
  }

  public Diagram(final Diagram diagram) {
    this.patterns         = new ArrayList<>(diagram.getPatterns());
    this.knots            = new ArrayList<>(diagram.getKnots());
    this.allSteps         = new ArrayList<>(diagram.getAllSteps());
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
    List<Knot> knots = new ArrayList<>(this.getCurrentStep().getDisplayedKnots());
    knots.add(knot);

    this.getAllSteps().add(new Step(knots, app.getOptionalDotGrid().getAllSelectedKnots()));
    this.currentStepIndex = this.getAllSteps().size() - 1;

    return this.knots;
  }

  public List<Knot> addKnotWithStep(final App app, final List<Knot> knots) {
    List<Knot> knotsToAdd = new ArrayList<>(this.getCurrentStep().getDisplayedKnots());
    knotsToAdd.addAll(knots);

    this.getAllSteps().add(new Step(knotsToAdd, app.getOptionalDotGrid().getAllSelectedKnots()));
    this.currentStepIndex = this.getAllSteps().size() - 1;

    return knotsToAdd;
  }

  public List<Knot> addKnotWithStepFiltering(final App app, final List<Knot> knotsToInclude, final List<Knot> knotsToFilterOut) {
    List<Knot> knotsToAdd = new ArrayList<>(this.getCurrentStep().getDisplayedKnots());
    knotsToAdd.addAll(knotsToInclude);
    knotsToAdd.removeAll(knotsToFilterOut);

    this.getAllSteps().add(new Step(knotsToAdd, app.getOptionalDotGrid().getAllSelectedKnots()));
    this.currentStepIndex = this.getAllSteps().size() - 1;

    return knotsToAdd;
  }

  public void undoLastStep(App app) {
    logger.info("Undo step, current step={}", currentStepIndex);

    if (this.currentStepIndex > 0) {
      deleteNodesFromCurrentStep(app);

      this.currentStepIndex--;
      app.getOptionalDotGrid().layoutChildren(); // Display nodes from new state
      logger.info("Undo step, new step={}", currentStepIndex);
    }
  }

  public void redoLastStep(App app) {
    logger.info("Redo step, current step={}", currentStepIndex);

    if (currentStepIndex < this.getAllSteps().size() -1) {
      deleteNodesFromCurrentStep(app);
      this.currentStepIndex++;
      app.getOptionalDotGrid().layoutChildren(); // Display nodes from new state

      logger.info("Redo step, new step={}", currentStepIndex);
    }
  }

  private void deleteNodesFromCurrentStep(App app) {
    getCurrentStep().getDisplayedKnots().stream().forEach(knot -> {
      app.getOptionalDotGrid().getRoot().getChildren().remove(knot.getImageView()); // Delete nodes from step before
      app.getOptionalDotGrid().getRoot().getChildren().remove(knot.getSelection());
      app.getOptionalDotGrid().getRoot().getChildren().remove(knot.getHovered());
      app.getOptionalDotGrid().getRoot().getChildren().remove(knot.getGuideLines());
    });
  }

  // We don't lose the undo / redo history
  public List<Knot> resetDiagram() {
    this.currentStepIndex = 0;
    return this.knots;
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

  public void addStep(App app, List<Knot> displayedKnots, List<Knot> selectedKnots) {
    logger.info("Adding step, current step={}", currentStepIndex);
    List<Knot> displayed = new ArrayList<>(app.getOptionalDotGrid().getAllVisibleKnots());
    displayed.addAll(displayedKnots);

    List<Knot> selected = new ArrayList<>(app.getOptionalDotGrid().getAllSelectedKnots());
    selected.addAll(selectedKnots);
    boolean increment = true;

    if (this.allSteps.isEmpty()) {
      increment = false;
    }

    if (currentStepIndex < this.allSteps.size() - 1) {
      this.allSteps = this.allSteps.subList(0, currentStepIndex);
      this.allSteps.add(new Step(displayed, selected));
    } else {
      this.allSteps.add(new Step(displayed, selected));
    }

    if (increment) {
      currentStepIndex++;
    }
    logger.info("Adding step, new step={}", currentStepIndex);
  }

  public void addStep(App app) {
    logger.info("Adding step, current step={}", currentStepIndex);

    if (currentStepIndex < this.allSteps.size() - 1) {
      this.allSteps = this.allSteps.subList(0, currentStepIndex);
      this.allSteps.add(new Step(app.getOptionalDotGrid().getAllVisibleKnots(), app.getOptionalDotGrid().getAllSelectedKnots()));
    } else {
      this.allSteps.add(new Step(app.getOptionalDotGrid().getAllVisibleKnots(), app.getOptionalDotGrid().getAllSelectedKnots()));
    }

    currentStepIndex++;
    logger.info("Added step, new step={}", currentStepIndex);
  }

  public Step getCurrentStep() {
    if (allSteps.isEmpty()) {
      return new Step();
    }
    return allSteps.get(currentStepIndex);
  }

  public List<Step> getAllSteps() {
    return allSteps;
  }

  public void setAllSteps(List<Step> allSteps) {
    this.allSteps = allSteps;
  }

}

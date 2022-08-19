package org.alienlabs.adaloveslace.business.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import javafx.scene.Group;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import org.alienlabs.adaloveslace.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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

  public Set<Knot> addKnotsWithStep(App app, final Knot knot) {
    Set<Knot> knots = new TreeSet<>(this.getCurrentStep().getDisplayedKnots());
    knots.add(knot);

    this.getAllSteps().add(new Step(knots, this.getCurrentStep().getSelectedKnots()));
    this.currentStepIndex = this.getAllSteps().size() - 1;

    return knots;
  }

  public Step addKnotsWithStep(final Set<Knot> displayedKnots, final Set<Knot> selectedKnots) {
    Step step = new Step(displayedKnots, selectedKnots);
    this.getAllSteps().add(step);
    this.currentStepIndex = this.getAllSteps().size() - 1;

    return step;
  }

  public Step addKnotsToStep(final Set<Knot> displayedKnots, final Set<Knot> selectedKnots) {
    Step step = Step.of(displayedKnots, selectedKnots);
    this.getAllSteps().add(step);
    this.currentStepIndex = this.getAllSteps().size() - 1;

    return step;
  }

  public Set<Knot> addKnotsToStep(final App app, final Set<Knot> knots) {
    Step step = Step.of(knots, app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots());
    this.getAllSteps().add(step);
    this.currentStepIndex = this.getAllSteps().size() - 1;

    return step.getDisplayedKnots();
  }

  public Set<Knot> addKnotWithStepFiltering(final App app, final Set<Knot> knotsToInclude, final Set<Knot> knotsToFilterOut) {
    Set<Knot> knotsToAdd = new HashSet<>(this.getCurrentStep().getDisplayedKnots());
    knotsToAdd.removeAll(knotsToFilterOut);
    knotsToAdd.addAll(knotsToInclude);

    this.getAllSteps().add(new Step(knotsToAdd, app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots()));
    this.currentStepIndex = this.getAllSteps().size() - 1;

    return knotsToAdd;
  }

  public void undoLastStep(App app) {
    logger.info("Undo step, current step={}", currentStepIndex);

    if (this.currentStepIndex >= 0) {
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
    getCurrentStep().getSelectedKnots().stream().forEach(knot -> {
      app.getOptionalDotGrid().getRoot().getChildren().remove(knot.getImageView()); // Delete nodes from step before
      app.getOptionalDotGrid().getRoot().getChildren().remove(knot.getSelection());
      app.getOptionalDotGrid().getRoot().getChildren().remove(knot.getHovered());
      app.getOptionalDotGrid().getRoot().getChildren().remove(knot.getGuideLines());
    });
  }

  public void deleteNodesFromCurrentStep(App app, Knot knot) {
    app.getOptionalDotGrid().getRoot().getChildren().remove(knot.getSelection());
    app.getOptionalDotGrid().getRoot().getChildren().remove(knot.getHovered());
    app.getOptionalDotGrid().getRoot().getChildren().removeAll(knot.getGuideLines());
    knot.getGuideLines().clear();
  }

  public void deleteNodesFromCurrentStep(Group root, Knot knot) {
    root.getChildren().remove(knot.getSelection());
    root.getChildren().remove(knot.getHovered());
    root.getChildren().removeAll(knot.getGuideLines());
    knot.getGuideLines().clear();

    root.getChildren().removeAll(root.getChildren().stream().filter(node -> (node instanceof Line || node instanceof Rectangle)).toList());
  }

  public void deleteNodesFromCurrentStep(Group root) {
    root.getChildren().removeAll(root.getChildren().stream().filter(node -> (node instanceof Line || node instanceof Rectangle)).toList());
  }

  // We don't lose the undo / redo history
  public void resetDiagram(App app) {
    app.getRoot().getChildren().removeAll(this.getCurrentStep().getDisplayedKnots().stream().
      map(knot -> knot.getImageView()).toList());
    app.getOptionalDotGrid().clearSelections();
    this.getAllSteps().clear();
    this.currentStepIndex = -1;
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

  public void drawHoveredOverOrSelectedKnots(final App app, final Set<Knot> knots) {
    knots.stream().forEach(knot -> app.getOptionalDotGrid().drawHoveredOverOrSelectedKnot(knot));

    knots.stream().forEach(knot ->
      app.getOptionalDotGrid().drawGuideLines(app.getDiagram().getCurrentStep(), knot));
  }

  public Step getCurrentStep() {
    if (allSteps.isEmpty() || currentStepIndex == -1) {
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

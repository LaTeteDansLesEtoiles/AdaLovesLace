package org.alienlabs.adaloveslace.business.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import javafx.scene.Group;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import org.alienlabs.adaloveslace.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

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

    private Integer             currentStepIndex;

    private List<Step>          allSteps = new ArrayList<>();

    @XmlTransient
    private Pattern             currentPattern;

    @XmlTransient
    private Knot                currentKnot;

    @XmlTransient
    private boolean             isKnotSelected;

    @XmlTransient
    private MouseMode           currentMode;

    @XmlTransient
    private static final Logger logger = LoggerFactory.getLogger(Diagram.class);

    // For JAXB
    public Diagram() {
        this.patterns           = new ArrayList<>();
        this.knots              = new ArrayList<>();
        this.currentMode        = MouseMode.DRAWING;
        this.currentStepIndex   = 1;
        this.allSteps.add(new Step());
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


    public void addPattern(final Pattern pattern) {
        this.patterns.add(pattern);
    }

    /**
     * Adds a single Knot to a new undo / redo Step
     * @param knot the Knot to add.
     */
    public void addKnotWithStep(final Knot knot) {
        Set<Knot> displayed = this.getCurrentStep().getDisplayedKnots();
        Set<Knot> selected = this.getCurrentStep().getSelectedKnots();

        Step newStep = new Step(this, this.getCurrentStepIndex() + 1);
        newStep.getDisplayedKnots().add(knot);
        newStep.getDisplayedKnots().addAll(displayed);
        newStep.getSelectedKnots().addAll(selected);

        Step.clearStepGreaterThan(this, this.getCurrentStepIndex() + 1);
        this.currentStepIndex = newStep.getStepIndex();
    }

    /**
     * Creates a step given the selected knot, plus the same displayed and selected knots than in the previous step.
     * A knot can only be one of those, not both.
     *
     * @param selectedKnot the knot to add to the new Step as selected or displayed
     * @return the new Step (for undo / redo)
     */
    public Step addKnotWithStep(final Knot selectedKnot, boolean shallSelect) {
        Step step = new Step(this, selectedKnot, shallSelect);
        this.setCurrentStepIndex(step.getStepIndex());

        return step;
    }

    public void addKnotsToStep(final Set<Knot> displayedKnots, final Set<Knot> selectedKnots) {
        Step step = Step.of(this, displayedKnots, selectedKnots);
        this.setCurrentStepIndex(step.getStepIndex());
    }

    public void undoLastStep(App app) {
        logger.info("Undo step, current step={}", currentStepIndex);

        if (this.allSteps == null || this.allSteps.isEmpty()) {
            return;
        }

        if (this.currentStepIndex > 1) {
            deleteNodesFromCurrentStep(app);
            this.currentStepIndex--;
            app.getOptionalDotGrid().layoutChildren(); // Display nodes from new state

            logger.info("Undo step, new step={}", currentStepIndex);
        } else {
            deleteNodesFromCurrentStep(app);
            this.currentStepIndex = 1;
            app.getOptionalDotGrid().layoutChildren(); // Display nodes from new state

            logger.info("Undo step, new step={}", currentStepIndex);
        }
    }

    public void redoLastStep(App app) {
        logger.info("Redo 0 step, current step={}", this.currentStepIndex);

        if (this.allSteps == null || this.allSteps.isEmpty()) {
            return;
        }

        if (this.currentStepIndex < this.allSteps.stream()
                .max(Comparator.comparing(Step::getStepIndex))
                .get()
                .getStepIndex()
                .intValue()) {
            deleteNodesFromCurrentStep(app);
            this.currentStepIndex++;
            app.getOptionalDotGrid().layoutChildren(); // Display nodes from new state

            logger.info("Redo 2 step, new step={}", this.currentStepIndex);
        } else {
            deleteNodesFromCurrentStep(app);
            app.getOptionalDotGrid().layoutChildren(); // Display nodes from new state
            logger.info("Redo 3 step, new index={}", this.currentStepIndex);
        }
    }

    private void deleteNodesFromCurrentStep(App app) {
        getCurrentStep().getDisplayedKnots().forEach(knot -> {
            app.getOptionalDotGrid().getRoot().getChildren().remove(knot.getImageView()); // Delete nodes from step before
            app.getOptionalDotGrid().getRoot().getChildren().remove(knot.getHovered());
            app.getOptionalDotGrid().getRoot().getChildren().removeAll(knot.getGuideLines());
        });
        getCurrentStep().getSelectedKnots().forEach(knot -> {
            app.getOptionalDotGrid().getRoot().getChildren().remove(knot.getImageView()); // Delete nodes from step before
            app.getOptionalDotGrid().getRoot().getChildren().remove(knot.getHovered());
            app.getOptionalDotGrid().getRoot().getChildren().removeAll(knot.getGuideLines());
        });
    }

    public void deleteNodesFromCurrentStep(App app, Knot knot) {
        app.getOptionalDotGrid().getRoot().getChildren().remove(knot.getSelection());
        app.getOptionalDotGrid().getRoot().getChildren().remove(knot.getHovered());
        app.getOptionalDotGrid().getRoot().getChildren().removeAll(knot.getGuideLines());
        knot.getGuideLines().clear();
    }

    public void deleteNodesFromCurrentStep(Group root) {
        root.getChildren().removeAll(root.getChildren().stream().filter(node ->
                (node instanceof Line ||
                        node instanceof Rectangle ||
                        node instanceof Circle)).toList());
    }

    public void deleteHandlesFromCurrentStep(Group root) {
        root.getChildren().removeAll(root.getChildren().stream().filter(Circle.class::isInstance).toList());
    }

    // We don't lose the undo / redo history
    public void resetDiagram(App app) {
        app.getRoot().getChildren().removeAll(this.getCurrentStep().getDisplayedKnots().stream().
            map(Knot::getImageView).toList());
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

    public MouseMode getCurrentMode() {
        return currentMode;
    }

    public void setCurrentMode(MouseMode currentMode) {
        this.currentMode = currentMode;
    }

    public Integer getCurrentStepIndex() {
        return this.currentStepIndex;
    }

    public void setCurrentStepIndex(Integer currentStepIndex) {
        this.currentStepIndex = currentStepIndex;
    }

    public Step getCurrentStep() {
        if (allSteps.isEmpty() || currentStepIndex <= 0) {
            Step newEmptyStep = new Step();
            this.getAllSteps().add(newEmptyStep);
            this.setCurrentStepIndex(1);
            return newEmptyStep;
        }

        return allSteps.stream().filter(step -> step.getStepIndex().equals(currentStepIndex)).findFirst().get();
    }

    public List<Step> getAllSteps() {
        return allSteps;
    }
}

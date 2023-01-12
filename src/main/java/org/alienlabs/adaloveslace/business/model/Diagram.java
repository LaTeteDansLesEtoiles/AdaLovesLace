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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.alienlabs.adaloveslace.business.model.Step.getTotalStepIndices;
import static org.alienlabs.adaloveslace.business.model.Step.incrementIndex;

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

    private final List<Step>    allSteps;

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


    public void addPattern(final Pattern pattern) {
        this.patterns.add(pattern);
    }

    public Set<Knot> addKnotsWithStep(final Knot knot) {
        Set<Knot> displayedKnots = this.getCurrentStep().getDisplayedKnots();
        Set<Knot> selectedKnots = this.getCurrentStep().getSelectedKnots();

        Step newStep;

        if (knot.getSelection() == null) {
            Set<Knot> newDisplayed = new HashSet<>(displayedKnots);
            newDisplayed.add(knot);
            newStep = new Step(newDisplayed, selectedKnots, incrementIndex());
        } else {
            Set<Knot> newSelected = new HashSet<>(selectedKnots);
            newSelected.add(knot);
            newStep = new Step(displayedKnots, newSelected, incrementIndex());
        }

        this.getAllSteps().add(newStep);
        this.currentStepIndex = getTotalStepIndices();
        return newStep.getAllVisibleKnots();
    }

    public Step addKnotsWithStep(final Set<Knot> displayedKnots, final Set<Knot> selectedKnots) {
        this.currentStepIndex = incrementIndex();
        Step step = new Step(displayedKnots, selectedKnots, this.currentStepIndex);
        this.getAllSteps().add(step);

        return step;
    }

    public void addKnotsToStep(final Set<Knot> displayedKnots, final Set<Knot> selectedKnots) {
        this.currentStepIndex = incrementIndex();
        Step step = Step.of(displayedKnots, selectedKnots, currentStepIndex);
        this.getAllSteps().add(step);
    }

    public void undoLastStep(App app) {
        logger.info("Undo step, current step={}", currentStepIndex);

        if (this.currentStepIndex >= 1) {
            deleteNodesFromCurrentStep(app);

            this.currentStepIndex--;
            app.getOptionalDotGrid().layoutChildren(); // Display nodes from new state
            logger.info("Undo step, new step={}", currentStepIndex);
        }
    }

    public void redoLastStep(App app) {
        logger.info("Redo step, current step={}", currentStepIndex);

        if (currentStepIndex < this.getAllSteps().size()) {
            deleteNodesFromCurrentStep(app);
            this.currentStepIndex++;
            app.getOptionalDotGrid().layoutChildren(); // Display nodes from new state

            logger.info("Redo step, new step={}", currentStepIndex);
        }
    }

    private void deleteNodesFromCurrentStep(App app) {
        getCurrentStep().getDisplayedKnots().forEach(knot -> {
            app.getOptionalDotGrid().getRoot().getChildren().remove(knot.getImageView()); // Delete nodes from step before
            app.getOptionalDotGrid().getRoot().getChildren().remove(knot.getSelection());
            app.getOptionalDotGrid().getRoot().getChildren().remove(knot.getHovered());
            app.getOptionalDotGrid().getRoot().getChildren().removeAll(knot.getGuideLines());
        });
        getCurrentStep().getSelectedKnots().forEach(knot -> {
            app.getOptionalDotGrid().getRoot().getChildren().remove(knot.getImageView()); // Delete nodes from step before
            app.getOptionalDotGrid().getRoot().getChildren().remove(knot.getSelection());
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
        root.getChildren().removeAll(root.getChildren().stream().filter(node -> (node instanceof Line || node instanceof Rectangle)).toList());
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
        if (allSteps.isEmpty() || currentStepIndex == -1) {
            return new Step(1);
        }

        if (allSteps.stream().anyMatch(step -> step.getStepIndex().equals(currentStepIndex))) {
            return allSteps.stream().filter(step -> step.getStepIndex().equals(currentStepIndex)).findFirst().get();
        }

        return new Step(1);
    }

    public List<Step> getAllSteps() {
        return allSteps;
    }
}

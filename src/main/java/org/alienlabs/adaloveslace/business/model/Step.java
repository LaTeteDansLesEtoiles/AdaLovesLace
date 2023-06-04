package org.alienlabs.adaloveslace.business.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;
import javafx.scene.shape.Circle;
import org.alienlabs.adaloveslace.App;

import java.util.*;

/**
 * What is drawn on a Canvas at any given time: we can move back and forward the Step list in order to undo / redo
 * the user's actions.
 *
 * @see Diagram
 * @see Knot
 *
 */
@XmlType(name = "Step")
@XmlAccessorType(XmlAccessType.FIELD)
public class Step implements Comparable<Step> {

    private Integer stepIndex;

    @XmlTransient
    public App app;

    private Set<Knot> displayedKnots = new HashSet<>();

    private Set<Knot> selectedKnots = new HashSet<>();

    @XmlTransient
    private Circle[] handle;

    // For JAXB
    public Step() {
        this.stepIndex = 0;
    }

    /**
     * Creates a step given the displayed knots and the selected knots. A knot can only be in one of those, not both.
     *
     * @param app the main App
     * @param diagram the diagram onto which to work
     * @param displayedKnots the knots to add to the new Step as displayed
     * @param selectedKnots the knots to add to the new Step as selected
     */

    public Step(App app,
                Diagram diagram,
                Set<Knot> displayedKnots,
                Set<Knot> selectedKnots,
                Circle... handle
    ) {
        this.app = app;

        this.displayedKnots = displayedKnots;
        this.selectedKnots = selectedKnots;
        this.handle = handle;

        this.displayedKnots.removeAll(this.selectedKnots);
        this.selectedKnots.removeAll(this.displayedKnots);

        this.stepIndex = diagram.getCurrentStepIndex() + 1;

        diagram.setCurrentStepIndex(this.stepIndex);
        diagram.getAllSteps().add(this);

        this.handle = handle;

        app.getOptionalDotGrid().layoutChildren();
    }

    public static void clearStepsGreaterThanPresentStep(Diagram diagram) {
        List<Step> stepsToRemove = new ArrayList<>(diagram.getAllSteps().stream()
                .filter(step1 -> (step1.stepIndex > diagram
                        .getCurrentStepIndex()))
                .toList());
        diagram.getAllSteps().removeAll(stepsToRemove);
    }

    public Set<Knot> getDisplayedKnots() {
        return displayedKnots;
    }

    public Set<Knot> getSelectedKnots() {
        return selectedKnots;
    }

    public Set<Knot> getAllVisibleKnots() {
        Set<Knot> all = new HashSet<>(selectedKnots);
        all.addAll(displayedKnots);

        return all;
    }

    public Integer getStepIndex() {
        return stepIndex;
    }

    public void setStepIndex(Integer stepIndex) {
        this.stepIndex = stepIndex;
    }

    @Override
    public int compareTo(Step o) {
        return this.stepIndex.compareTo(o.stepIndex);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Step step = (Step) o;
        return stepIndex.equals(step.stepIndex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stepIndex);
    }
}

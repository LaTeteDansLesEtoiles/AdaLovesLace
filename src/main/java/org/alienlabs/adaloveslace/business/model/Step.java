package org.alienlabs.adaloveslace.business.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;
import org.alienlabs.adaloveslace.util.NodeUtil;

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

    @XmlTransient
    private final Diagram diagram;

    private final Integer stepIndex;

    private final Set<Knot> displayedKnots;

    private final Set<Knot> selectedKnots;

    // For JAXB
    public Step() {
        this(new Diagram(), 1);
    }

    public Step(Diagram diagram, int stepIndex) {
        this.diagram = diagram;
        this.stepIndex = stepIndex;

        this.displayedKnots = new HashSet<>();
        this.selectedKnots = new HashSet<>();
    }

    /**
     * Creates a step given the displayed knots and the selected knots. A knot can only be one of those, not both.
     *
     * @param diagram the diagram onto which to work
     * @param displayedKnots the displayed but not selected knots to add to the new Step
     * @param selectedKnots the selected (hence displayed but not in the displayedKnots Set) to add to the new Step
     */
    public Step(Diagram diagram, Set<Knot> displayedKnots, Set<Knot> selectedKnots) {
        diagram.setCurrentStepIndex(diagram.getCurrentStepIndex() + 1);
        this.diagram = clearStepGreaterThan(diagram, diagram.getCurrentStepIndex());
        this.stepIndex = diagram.getCurrentStepIndex();

        this.displayedKnots = new HashSet<>(displayedKnots.stream().filter(knot -> !selectedKnots.contains(knot)).toList());
        this.selectedKnots =  new HashSet<>(selectedKnots.stream().map(knot ->
            new NodeUtil().copyKnot(knot)).toList());
    }

    public static Step of(Diagram diagram, Set<Knot> displayedKnots, Set<Knot> selectedKnots) {
        diagram.setCurrentStepIndex(diagram.getCurrentStepIndex() + 1);
        Step step = new Step(clearStepGreaterThan(diagram, diagram.getCurrentStepIndex()), diagram.getCurrentStepIndex());

        step.getDisplayedKnots().addAll(displayedKnots);
        step.getSelectedKnots().addAll(selectedKnots);

        return step;
    }

    private static Diagram clearStepGreaterThan(Diagram diagram, int stepIndex) {
        List<Step> toKeep = new ArrayList<>(diagram.getAllSteps().stream().
                filter(step1 -> step1.stepIndex < stepIndex).
                toList());
        diagram.getAllSteps().clear();
        diagram.getAllSteps().addAll(toKeep);

        return diagram;
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

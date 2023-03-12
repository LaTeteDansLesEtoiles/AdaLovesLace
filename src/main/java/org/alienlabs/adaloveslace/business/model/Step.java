package org.alienlabs.adaloveslace.business.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import org.alienlabs.adaloveslace.util.NodeUtil;

import java.util.*;
import java.util.stream.Collectors;

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

    private final Integer stepIndex;

    private Set<Knot> displayedKnots = new HashSet<>();

    private Set<Knot> selectedKnots = new HashSet<>();

    // For JAXB
    public Step() {
        this.stepIndex = 1;
    }

    public Step(Diagram diagram, int stepIndex) {
        diagram.setCurrentStepIndex(stepIndex);
        this.stepIndex = stepIndex;
        diagram.getAllSteps().add(this);
        diagram.setCurrentStepIndex(this.stepIndex);

        this.displayedKnots = new HashSet<>(diagram.getCurrentStep().displayedKnots);
        this.selectedKnots = new HashSet<>(diagram.getCurrentStep().selectedKnots);
    }

    /**
     * Creates a step given the displayed knots and the selected knots. A knot can only be one of those, not both.
     *
     * @param diagram the diagram onto which to work
     * @param selectedKnot the knot to add to the new Step as displayed or selected
     * @param shallSelect select the Knot if true, display it if false
     */
    public Step(final Diagram diagram, final Knot selectedKnot, final boolean shallSelect) {
        if (shallSelect) {
            this.displayedKnots = diagram.getCurrentStep().displayedKnots.stream().filter(knot -> !knot.equals(selectedKnot)).collect(Collectors.toSet());
            this.selectedKnots = new HashSet<>(diagram.getCurrentStep().selectedKnots.stream().filter(knot -> !knot.equals(selectedKnot)).toList());
            this.selectedKnots.add(selectedKnot);
        } else {
            this.displayedKnots = new HashSet<>(diagram.getCurrentStep().displayedKnots.stream().filter(knot -> !knot.equals(selectedKnot)).toList());
            this.displayedKnots.add(selectedKnot);
            this.selectedKnots = new HashSet<>(diagram.getCurrentStep().selectedKnots.stream().filter(knot -> !knot.equals(selectedKnot)).toList());
        }

        diagram.getAllSteps().add(this);
        this.stepIndex = diagram.getCurrentStepIndex() + 1;
        diagram.setCurrentStepIndex(this.stepIndex);
        clearStepGreaterThan(diagram, diagram.getCurrentStepIndex());
    }

    public static Step of(Diagram diagram, Set<Knot> displayedKnots, Set<Knot> selectedKnots) {
        Step step = new Step(diagram, diagram.getCurrentStepIndex() + 1);
        clearStepGreaterThan(diagram, step.getStepIndex());
        diagram.getAllSteps().add(step);
        diagram.setCurrentStepIndex(step.stepIndex);

        step.getDisplayedKnots().addAll(displayedKnots);
        step.getSelectedKnots().addAll(selectedKnots.stream().map(knot -> new NodeUtil().copyKnot(knot)).collect(Collectors.toSet()));

        return step;
    }

    public static void clearStepGreaterThan(Diagram diagram, int stepIndex) {
        if (diagram.getAllSteps().size() > stepIndex) {
            List<Step> toDiscard = new ArrayList<>(diagram.getAllSteps().stream()
                    .filter(step1 -> step1.stepIndex >= stepIndex)
                    .toList());
            diagram.getAllSteps().removeAll(toDiscard);
            diagram.setCurrentStepIndex(stepIndex);
        }
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

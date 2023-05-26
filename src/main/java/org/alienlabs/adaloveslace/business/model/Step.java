package org.alienlabs.adaloveslace.business.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;
import org.alienlabs.adaloveslace.App;
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

    @XmlTransient
    public static App app;

    private Set<Knot> displayedKnots = new HashSet<>();

    private Set<Knot> selectedKnots = new HashSet<>();

    // For JAXB
    public Step() {
        this.stepIndex = 0;
    }

    public Step(Diagram diagram, int stepIndex, App app) {
        Step.app = app;
        diagram.setCurrentStepIndex(stepIndex);
        this.stepIndex = stepIndex;
        diagram.getAllSteps().add(this);

        this.displayedKnots = new HashSet<>(diagram.getCurrentStep().displayedKnots);
        this.selectedKnots = new HashSet<>(diagram.getCurrentStep().selectedKnots);
    }

    /**
     * Creates a step given the displayed knots and the selected knots. A knot can only be one of those, not both.
     *
     * @param diagram the diagram onto which to work
     * @param chosenKnot the knot to add to the new Step as displayed or selected
     * @param shallSelect select the Knot if true, display it if false
     */
    public Step(final Diagram diagram, final Knot chosenKnot, final boolean shallSelect) {
        clearStepsGreaterThanPresentStep(diagram);

        diagram.setCurrentStepIndex(
                diagram.getCurrentStepIndex() + 1);
        this.stepIndex = diagram.getCurrentStepIndex();
        diagram.getAllSteps().add(this.stepIndex, this);

        if (shallSelect) {
            Set<Knot> selection = new HashSet<>(diagram.getAllSteps().get(this.stepIndex - 1).getSelectedKnots());
            selection.add(chosenKnot);
            this.selectedKnots = selection;

            this.displayedKnots = new HashSet<>(diagram.getAllSteps().get(this.stepIndex - 1).getDisplayedKnots());
        } else {
            Set<Knot> displayed = new HashSet<>(diagram.getAllSteps().get(this.stepIndex - 1).getDisplayedKnots());
            displayed.add(chosenKnot);
            this.displayedKnots = displayed;

            this.selectedKnots = new HashSet<>(diagram.getAllSteps().get(this.stepIndex - 1).getSelectedKnots());
        }

    }

    public static Step of(Diagram diagram, Set<Knot> displayedKnots, Set<Knot> selectedKnots) {
        Step step = new Step(diagram, diagram.getCurrentStepIndex() + 1, app);
        clearStepsGreaterThanPresentStep(diagram);
        diagram.getAllSteps().add(step);
        diagram.setCurrentStepIndex(step.stepIndex);

        step.getDisplayedKnots().addAll(displayedKnots);
        step.getSelectedKnots().addAll(selectedKnots.stream().map(knot -> new NodeUtil().copyKnot(knot)).collect(Collectors.toSet()));

        return step;
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

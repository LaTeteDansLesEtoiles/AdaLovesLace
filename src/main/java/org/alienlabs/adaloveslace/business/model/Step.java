package org.alienlabs.adaloveslace.business.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.util.NodeUtil;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * What is drawn on a Canvas at any given time: we can move back and forward the Step list in order to undo / redo
 * the user's actions.
 *
 * @see Diagram
 * @see Knot
 *
 */
@XmlRootElement(name = "Step")
@XmlAccessorType(XmlAccessType.FIELD)
public class Step implements Comparable<Step> {

    private final Integer stepIndex;

    private final Set<Knot> displayedKnots;

    private final Set<Knot> selectedKnots;

    // For JAXB
    public Step() {
        this(0);
    }
    public Step(int stepIndex) {
        this.stepIndex = stepIndex;
        this.displayedKnots = new HashSet<>();
        this.selectedKnots = new HashSet<>();
    }

    public Step(Set<Knot> displayedKnots, Set<Knot> selectedKnots, int stepIndex) {
        this.stepIndex = stepIndex;
        this.displayedKnots = new HashSet<>(displayedKnots.stream().filter(knot -> !selectedKnots.contains(knot)).toList());

        this.selectedKnots =  new HashSet<>(selectedKnots.stream().map(knot ->
            new NodeUtil().copyKnot(knot)).toList());
    }

    public static Step of(Set<Knot> displayedKnots, Set<Knot> selectedKnots, int stepIndex) {
        Step step = new Step(stepIndex);
        step.getDisplayedKnots().addAll(displayedKnots);
        step.getSelectedKnots().addAll(selectedKnots);

        return step;
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

    public static Integer incrementIndex(App app) {
        return app.getOptionalDotGrid().getDiagram().getCurrentStepIndex() + 1;
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

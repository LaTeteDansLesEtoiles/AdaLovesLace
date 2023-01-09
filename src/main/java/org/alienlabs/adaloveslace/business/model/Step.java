package org.alienlabs.adaloveslace.business.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.alienlabs.adaloveslace.util.NodeUtil;

import java.util.HashSet;
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
public class Step {

  private Set<Knot> displayedKnots;

  private Set<Knot> selectedKnots;

  public Step() {
    this.displayedKnots = new HashSet<>();
    this.selectedKnots = new HashSet<>();
  }

  public Step(Set<Knot> displayedKnots, Set<Knot> selectedKnots) {
    this.displayedKnots = new HashSet<>(displayedKnots.stream().filter(knot -> !selectedKnots.contains(knot)).toList());

    this.selectedKnots =  new HashSet<>(selectedKnots.stream().map(knot ->
      new NodeUtil().copyKnot(knot)).toList());
  }

  public static Step of(Set<Knot> displayedKnots, Set<Knot> selectedKnots) {
    Step step = new Step();
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

}

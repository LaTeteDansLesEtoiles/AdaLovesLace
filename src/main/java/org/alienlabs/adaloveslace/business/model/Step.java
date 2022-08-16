package org.alienlabs.adaloveslace.business.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.alienlabs.adaloveslace.util.NodeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * What is drawn on a Canvas at a scertain time: we can move back and forward the Step list in order to undo / redo
 * the user's actions.
 *
 * @see Diagram
 * @see Knot
 *
 */
@XmlRootElement(name = "Step")
@XmlAccessorType(XmlAccessType.FIELD)
public class Step {

  private List<Knot> displayedKnots;

  private List<Knot> selectedKnots;

  public Step() {
    this.displayedKnots = new ArrayList<>();
    this.selectedKnots = new ArrayList<>();
  }

  public Step(List<Knot> displayedKnots, List<Knot> selectedKnots) {
    this.displayedKnots = new ArrayList<>(displayedKnots.stream().map(knot ->
      new NodeUtil().copyKnot(knot)).toList());
    this.selectedKnots  = new ArrayList<>(selectedKnots.stream().map(knot -> new
      NodeUtil().copyKnot(knot)).toList());
    this.displayedKnots.addAll(this.selectedKnots);
  }

  public static Step of(List<Knot> displayedKnots, List<Knot> selectedKnots) {
    Step step = new Step();
    step.getDisplayedKnots().addAll(displayedKnots);
    step.getSelectedKnots().addAll(selectedKnots);

    return step;
  }

  public List<Knot> getDisplayedKnots() {
    return displayedKnots;
  }

  public List<Knot> getSelectedKnots() {
    return selectedKnots;
  }

  public void setDisplayedKnots(List<Knot> displayedKnots) {
    this.displayedKnots = displayedKnots;
  }

  public void setSelectedKnots(List<Knot> selectedKnots) {
    this.selectedKnots = selectedKnots;
  }

}

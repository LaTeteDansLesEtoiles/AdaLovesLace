package org.alienlabs.adaloveslace.business.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

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
    this.displayedKnots = displayedKnots;
    this.selectedKnots  = selectedKnots;
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

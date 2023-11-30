package org.alienlabs.adaloveslace.view.component.button.statewindow;

import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.util.Events;
import org.alienlabs.adaloveslace.util.NodeUtil;
import org.alienlabs.adaloveslace.view.component.button.ImageButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.alienlabs.adaloveslace.App.resourceBundle;
import static org.alienlabs.adaloveslace.business.model.Diagram.newStep;
import static org.alienlabs.adaloveslace.view.window.StateWindow.STATE_BUTTONS_HEIGHT;

public class UnselectableButton extends ImageButton {

  public static final String UNSELECTABLE_BUTTON_NAME = "UNSELECTABLE_BUTTON_NAME";

  private static final Logger logger                = LoggerFactory.getLogger(UnselectableButton.class);

  public UnselectableButton(App app, String buttonLabel) {
    super(buttonLabel);
    this.setOnMouseClicked(event -> onSetUnselectableModeAction(app));
    this.setPrefHeight(STATE_BUTTONS_HEIGHT);

    final Tooltip tooltip = new Tooltip();
    tooltip.setText(resourceBundle.getString("UNSELECTABLE_BUTTON_TOOLTIP"));
    this.setTooltip(tooltip);
    buildButtonImage("unselectable.png");
  }

  public static void onSetUnselectableModeAction(App app) {
    logger.debug("Setting unselectable");

    List<Knot> displayedKnots = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots());
    List<Knot> selectedKnots = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots());

    for (Knot knot : new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots())) {
      Knot copy = new NodeUtil().copyKnot(knot);
      copy.setSelectable(false);
      knot.getImageView().removeEventHandler(MouseEvent.MOUSE_MOVED, Events.getGridHoverEventHandler(app));
      knot.getImageView().removeEventHandler(MouseEvent.MOUSE_CLICKED, Events.getMouseClickEventHandler(app));

      selectedKnots.remove(knot);
      displayedKnots.add(copy);
      copy.setSelection(null);
      copy.setHovered(null);
    }

    newStep(displayedKnots, selectedKnots, true);
  }

}

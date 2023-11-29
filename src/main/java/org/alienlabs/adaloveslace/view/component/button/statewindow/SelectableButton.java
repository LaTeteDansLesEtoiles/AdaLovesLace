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

import static org.alienlabs.adaloveslace.business.model.Diagram.newStep;
import static org.alienlabs.adaloveslace.view.window.StateWindow.STATE_BUTTONS_HEIGHT;

public class SelectableButton extends ImageButton {

  public static final String SELECTABLE_BUTTON_NAME     = "SELECTABLE_BUTTON_NAME";
  public static final String BUTTON_TOOLTIP             = "Select this button to have\nall knots selectable again";

  private static final Logger logger                = LoggerFactory.getLogger(SelectableButton.class);

  public SelectableButton(App app, String buttonLabel) {
    super(buttonLabel);
    this.setOnMouseClicked(event -> onSetSelectableModeAction(app));
    this.setPrefHeight(STATE_BUTTONS_HEIGHT);

    final Tooltip tooltip = new Tooltip();
    tooltip.setText(BUTTON_TOOLTIP);
    this.setTooltip(tooltip);
    buildButtonImage("selectable.png");
  }

  public static void onSetSelectableModeAction(App app) {
    logger.debug("Setting selectable");

    List<Knot> displayedKnots = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots());
    List<Knot> selectedKnots = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots());

    for (Knot knot : app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots()) {
      if (!knot.isSelectable()) {
        Knot copy = new NodeUtil().copyKnot(knot);

        copy.setSelectable(true);
        copy.getImageView().addEventHandler(MouseEvent.MOUSE_MOVED, Events.getGridHoverEventHandler(app));
        copy.getImageView().addEventHandler(MouseEvent.MOUSE_CLICKED, Events.getMouseClickEventHandler(app));
        copy.setSelection(null);
        copy.setHovered(null);

        displayedKnots.remove(knot);
        displayedKnots.add(copy);
      }
    }

    newStep(displayedKnots, selectedKnots, true);
  }

}

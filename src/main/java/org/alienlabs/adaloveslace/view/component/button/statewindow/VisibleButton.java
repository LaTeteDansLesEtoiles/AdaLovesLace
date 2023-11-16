package org.alienlabs.adaloveslace.view.component.button.statewindow;

import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.business.model.MouseMode;
import org.alienlabs.adaloveslace.util.Events;
import org.alienlabs.adaloveslace.view.component.button.ImageButton;
import org.alienlabs.adaloveslace.view.window.StateWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.alienlabs.adaloveslace.view.window.GeometryWindow.GEOMETRY_BUTTONS_HEIGHT;

public class VisibleButton extends ImageButton {

  public static final String VISIBLE_BUTTON_NAME    = "VISIBLE_BUTTON_NAME";
  public static final String BUTTON_TOOLTIP         = "Select this button to have visible again\nall invisible knots\n";

  private static final Logger logger                = LoggerFactory.getLogger(VisibleButton.class);

  public VisibleButton(App app, StateWindow window, String buttonLabel) {
    super(buttonLabel);
    this.setOnMouseClicked(event -> onSetVisibleAction(app, window));
    this.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);

    final Tooltip tooltip = new Tooltip();
    tooltip.setText(BUTTON_TOOLTIP);
    this.setTooltip(tooltip);
    buildButtonImage("visible.png");
  }

  public static void onSetVisibleAction(App app, StateWindow window) {
    logger.debug("Setting draw mode");
    app.getOptionalDotGrid().getDiagram().setCurrentMode(MouseMode.DRAWING);
    app.getOptionalDotGrid().clearSelections();
    app.getOptionalDotGrid().clearHovered();
    app.getOptionalDotGrid().clearAllGuideLines();

    if (Events.getGridHoverEventHandler(app) != null) {
      app.getMainWindow().getGrid().removeEventHandler(MouseEvent.MOUSE_MOVED, Events.getGridHoverEventHandler(app));
    }

    for (Knot knot : app.getOptionalDotGrid().getDiagram().getCurrentStep().getAllVisibleKnots()) {
      knot.getImageView().removeEventHandler(MouseEvent.MOUSE_MOVED, Events.getGridHoverEventHandler(app));
      knot.getImageView().removeEventHandler(MouseEvent.MOUSE_CLICKED, Events.getMouseClickEventHandler(app));
    }
  }

}

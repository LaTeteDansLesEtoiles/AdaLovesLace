package org.alienlabs.adaloveslace.view.component.button.geometrywindow;

import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.business.model.MouseMode;
import org.alienlabs.adaloveslace.util.Events;
import org.alienlabs.adaloveslace.view.window.GeometryWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.alienlabs.adaloveslace.App.TOOLTIPS_DURATION;
import static org.alienlabs.adaloveslace.App.resourceBundle;
import static org.alienlabs.adaloveslace.view.window.GeometryWindow.GEOMETRY_BUTTONS_HEIGHT;

public class DrawingButton extends ToggleButton {

  public static final String DRAWING_BUTTON_NAME    = "DRAWING_BUTTON_NAME";

  private static final Logger logger                = LoggerFactory.getLogger(DrawingButton.class);

  public DrawingButton(App app, GeometryWindow window, String buttonLabel) {
    super(buttonLabel);
    this.setOnMouseClicked(event -> onSetDrawModeAction(app, window));
    this.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);

    final Tooltip tooltip = new Tooltip();
    tooltip.setText(resourceBundle.getString("DRAWING_BUTTON_TOOLTIP"));
    tooltip.setShowDuration(TOOLTIPS_DURATION);
    this.setTooltip(tooltip);
  }

  public static void onSetDrawModeAction(App app, GeometryWindow window) {
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

    window.getDrawingButton()     .setSelected(true);
    window.getSelectionButton()   .setSelected(false);
    window.getDeletionButton()    .setSelected(false);
    window.getDuplicationButton() .setSelected(false);
  }

}

package org.alienlabs.adaloveslace.view.component.button.geometrywindow;

import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.Knot;
import org.alienlabs.adaloveslace.domain.enumeration.MouseMode;
import org.alienlabs.adaloveslace.view.window.GeometryWindow;
import org.alienlabs.adaloveslace.view.window.event.GridEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

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
    GridEvents.removeEventsFromGrid(app);
    app.getMovablePane().addEventHandler(MouseEvent.MOUSE_CLICKED, GridEvents.getMouseClickEventHandler(app));
    app.getMovablePane().addEventHandler(MouseEvent.MOUSE_MOVED, GridEvents.getGridHoverEventHandler(app));
    app.getMovablePane().setOnMouseExited(GridEvents.getGridHoverExitEventHandler(app));
    app.getOptionalDotGrid().clearHandles();

    for (Knot knot : app.getOptionalDotGrid().getDiagram().getCurrentStep().getAllVisibleKnots()) {
      knot.getImageView().removeEventHandler(MouseEvent.MOUSE_MOVED, GridEvents.getGridHoverEventHandler(app));
      knot.getImageView().removeEventHandler(MouseEvent.MOUSE_CLICKED, GridEvents.getMouseClickEventHandler(app));
    }

    app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots().addAll(
            new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots())
    );
    app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().clear();

    window.getDrawingButton()     .setSelected(true);
    window.getSelectionButton()   .setSelected(false);
    window.getDeletionButton()    .setSelected(false);
    window.getDuplicationButton() .setSelected(false);
  }

}

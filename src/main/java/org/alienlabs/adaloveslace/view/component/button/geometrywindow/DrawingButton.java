package org.alienlabs.adaloveslace.view.component.button.geometrywindow;

import javafx.event.EventHandler;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.Knot;
import org.alienlabs.adaloveslace.domain.enumeration.MouseMode;
import org.alienlabs.adaloveslace.view.window.event.GridEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import static org.alienlabs.adaloveslace.App.TOOLTIPS_DURATION;
import static org.alienlabs.adaloveslace.App.resourceBundle;
import static org.alienlabs.adaloveslace.view.window.ToolboxWindow.GEOMETRY_BUTTONS_HEIGHT;

public class DrawingButton extends ToggleButton {

  public static final String DRAWING_BUTTON_NAME    = "DRAWING_BUTTON_NAME";

  private static final Logger logger                = LoggerFactory.getLogger(DrawingButton.class);

  public DrawingButton(App app, String buttonLabel) {
    super(buttonLabel);
    this.setOnMouseClicked(event -> onSetDrawModeAction(app));
    // TestFX may trigger ActionEvent instead of a raw mouse click on some controls.
    // Support both so robot clicks reliably switch modes.
    this.setOnAction(_ -> onSetDrawModeAction(app));
    this.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);
    this.setId("drawingButton");

    final Tooltip tooltip = new Tooltip();
    tooltip.setText(resourceBundle.getString("DRAWING_BUTTON_TOOLTIP"));
    tooltip.setShowDuration(TOOLTIPS_DURATION);
    this.setTooltip(tooltip);
  }

  public static void onSetDrawModeAction(App app) {
    logger.info("Setting draw mode");

    app.getOptionalDotGrid().getDiagram().setCurrentMode(MouseMode.DRAWING);
    GridEvents.removeEventsFromGrid(app);
    // Idempotent: tests and users may choose drawing mode more than once; remove before add so handlers
    // are not stacked (each duplicate would fire draw logic again for one physical click).
    EventHandler<MouseEvent> click = GridEvents.getMouseClickEventHandler(app);
    EventHandler<MouseEvent> hover = GridEvents.getGridHoverEventHandler(app);
    EventHandler<MouseEvent> exit = GridEvents.getGridHoverExitEventHandler(app);
    app.getMovablePane().removeEventHandler(MouseEvent.MOUSE_CLICKED, click);
    app.getMovablePane().removeEventHandler(MouseEvent.MOUSE_MOVED, hover);
    app.getMovablePane().addEventHandler(MouseEvent.MOUSE_CLICKED, click);
    app.getMovablePane().addEventHandler(MouseEvent.MOUSE_MOVED, hover);
    app.getMovablePane().setOnMouseExited(exit);
    app.getOptionalDotGrid().clearHandles();

    for (Knot knot : app.getOptionalDotGrid().getDiagram().getCurrentStep().getAllVisibleKnots()) {
      knot.getImageView().removeEventHandler(MouseEvent.MOUSE_MOVED, GridEvents.getGridHoverEventHandler(app));
      knot.getImageView().removeEventHandler(MouseEvent.MOUSE_CLICKED, GridEvents.getMouseClickEventHandler(app));
    }

    app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots().addAll(
            new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots())
    );
    app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().clear();

    app.getToolboxWindow().getDrawingButton()     .setSelected(true);
    app.getToolboxWindow().getSelectionButton()   .setSelected(false);
    app.getToolboxWindow().getDeletionButton()    .setSelected(false);
    app.getToolboxWindow().getDuplicationButton() .setSelected(false);
  }

}

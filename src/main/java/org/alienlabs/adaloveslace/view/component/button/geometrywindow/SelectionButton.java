package org.alienlabs.adaloveslace.view.component.button.geometrywindow;

import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.Knot;
import org.alienlabs.adaloveslace.domain.enumeration.MouseMode;
import org.alienlabs.adaloveslace.view.window.GeometryWindow;
import org.alienlabs.adaloveslace.view.window.event.GridEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.alienlabs.adaloveslace.App.TOOLTIPS_DURATION;
import static org.alienlabs.adaloveslace.App.resourceBundle;
import static org.alienlabs.adaloveslace.view.window.GeometryWindow.GEOMETRY_BUTTONS_HEIGHT;
import static org.alienlabs.adaloveslace.view.window.event.GridEvents.keyHandler;

public class SelectionButton extends ToggleButton {

  public static final String SELECTION_BUTTON_NAME    = "SELECTION_BUTTON_NAME";

  private static final Logger logger                  = LoggerFactory.getLogger(SelectionButton.class);

  public SelectionButton(App app, GeometryWindow window, String buttonLabel) {
    super(buttonLabel);
    this.setOnMouseClicked(_ -> onSetSelectionModeAction(app, window));
    this.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);

    final Tooltip tooltip = new Tooltip();
    tooltip.setText(resourceBundle.getString("SELECTION_BUTTON_TOOLTIP"));
    tooltip.setShowDuration(TOOLTIPS_DURATION);
    this.setTooltip(tooltip);
  }

  public static void onSetSelectionModeAction(App app, GeometryWindow window) {
    logger.debug("Setting selection mode");

    app.getOptionalDotGrid().getDiagram().setCurrentMode(MouseMode.SELECTION);
    app.getScene().addEventHandler(KeyEvent.KEY_PRESSED, keyHandler);

    for (Knot knot : app.getOptionalDotGrid().getDiagram().getCurrentStep().getAllVisibleKnots()) {
      putAllEventsOnKnot(app, knot);
    }

    GridEvents.removeEventsFromGrid(app);

    window.getDrawingButton()     .setSelected(false);
    window.getSelectionButton()   .setSelected(true);
    window.getDeletionButton()    .setSelected(false);
    window.getDuplicationButton() .setSelected(false);
  }

  public static void putAllEventsOnKnot(App app, Knot knot) {
    if (knot.isSelectable()) {
      knot.getImageView().setOnMouseMoved(GridEvents.getGridHoverEventHandler(app));
      knot.getImageView().setOnMouseClicked(GridEvents.getMouseClickEventHandler(app));

      if (knot.getHandle() != null) {
        knot.getHandle().setOnMousePressed(GridEvents.getDragInitiatedOverHandleEventHandler());
        knot.getHandle().setOnMouseDragged(GridEvents.getMouseDragOverHandleEventHandler());
        knot.getHandle().setOnMouseReleased(GridEvents.getMouseDragDroppedHandleEventHandler());
      }
    }
  }

}

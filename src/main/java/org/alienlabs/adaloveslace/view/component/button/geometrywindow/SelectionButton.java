package org.alienlabs.adaloveslace.view.component.button.geometrywindow;

import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
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
import static org.alienlabs.adaloveslace.util.Events.keyHandler;
import static org.alienlabs.adaloveslace.view.window.GeometryWindow.GEOMETRY_BUTTONS_HEIGHT;

public class SelectionButton extends ToggleButton {

  public static final String SELECTION_BUTTON_NAME    = "SELECTION_BUTTON_NAME";

  private static final Logger logger                  = LoggerFactory.getLogger(SelectionButton.class);

  public SelectionButton(App app, GeometryWindow window, String buttonLabel) {
    super(buttonLabel);
    this.setOnMouseClicked(event -> onSetSelectionModeAction(app, window));
    this.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);

    final Tooltip tooltip = new Tooltip();
    tooltip.setText(resourceBundle.getString("SELECTION_BUTTON_TOOLTIP"));
    tooltip.setShowDuration(TOOLTIPS_DURATION);
    this.setTooltip(tooltip);
  }

  public static void onSetSelectionModeAction(App app, GeometryWindow window) {
    logger.debug("Setting selection mode");

    app.getOptionalDotGrid().getDiagram().setCurrentMode(MouseMode.SELECTION);
    app.getOptionalDotGrid().getDiagram().resetKnotsText();
    app.getScene().removeEventHandler(KeyEvent.KEY_PRESSED, keyHandler);
    app.getOptionalDotGrid().getRoot().addEventHandler(MouseEvent.MOUSE_MOVED, Events.getGridHoverEventHandler(app));
    app.getRoot().addEventHandler(MouseEvent.MOUSE_CLICKED, Events.getMouseClickEventHandler(app));

    for (Knot knot : app.getOptionalDotGrid().getDiagram().getCurrentStep().getAllVisibleKnots()) {
      putAllEventsOnKnot(app, knot);
    }

    app.getRoot().addEventHandler(MouseEvent.MOUSE_CLICKED, Events.getMouseClickEventHandler(app));

    window.getDrawingButton()     .setSelected(false);
    window.getSelectionButton()   .setSelected(true);
    window.getDeletionButton()    .setSelected(false);
    window.getDuplicationButton() .setSelected(false);
  }

  public static void putAllEventsOnKnot(App app, Knot knot) {
    if (knot.isSelectable()) {
      knot.getImageView().addEventHandler(MouseEvent.MOUSE_MOVED, Events.getGridHoverEventHandler(app));
      knot.getImageView().addEventHandler(MouseEvent.MOUSE_CLICKED, Events.getMouseClickEventHandler(app));

      if (knot.getHandle() != null) {
        knot.getHandle().setOnMousePressed(Events.getDragInitiatedOverHandleEventHandler());
        knot.getHandle().setOnMouseDragged(Events.getMouseDragOverHandleEventHandler());
        knot.getHandle().setOnMouseReleased(Events.getMouseDragDroppedHandleEventHandler());
      }
    }
  }

}

package org.alienlabs.adaloveslace.view.component.button.geometrywindow;

import javafx.event.EventHandler;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.business.model.MouseMode;
import org.alienlabs.adaloveslace.util.NodeUtil;
import org.alienlabs.adaloveslace.view.window.GeometryWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;

import static org.alienlabs.adaloveslace.view.window.GeometryWindow.GEOMETRY_BUTTONS_HEIGHT;

public class SelectionButton extends ToggleButton {

  public static final String SELECTION_BUTTON_NAME    = "SELECTION_BUTTON_NAME";
  public static final String BUTTON_TOOLTIP           = "Select this button then click on a\nknot in the canvas to select it\n";

  private static final Logger logger                  = LoggerFactory.getLogger(SelectionButton.class);
  private static EventHandler<MouseEvent> gridHoverListener;

  public SelectionButton(App app, GeometryWindow window, String buttonLabel) {
    super(buttonLabel);
    this.setOnMouseClicked(event -> onSetSelectionModeAction(app, window));
    this.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);

    final Tooltip tooltip = new Tooltip();
    tooltip.setText(BUTTON_TOOLTIP);
    this.setTooltip(tooltip);
  }

  public static void onSetSelectionModeAction(App app, GeometryWindow window) {
    logger.info("Setting selection mode");
    app.getOptionalDotGrid().getDiagram().setCurrentMode(MouseMode.SELECTION);
    final NodeUtil nodeUtil = new NodeUtil();

    gridHoverListener = (mouseEvent) -> {
      logger.debug("MouseEvent: X= {}, Y= {}", mouseEvent.getScreenX() , mouseEvent.getScreenY());

      for (Knot knot : app.getOptionalDotGrid().getDiagram().getKnots()) {
        try {
          if (knot.isVisible() && nodeUtil.isMouseOverKnot(knot, mouseEvent.getScreenX(), mouseEvent.getScreenY()) &&
            !app.getOptionalDotGrid().getAllSelectedKnots().contains(knot) && // If a knot is already selected, don't hover over it
            !app.getOptionalDotGrid().getAllHoveredKnots().contains(knot)) { // If a knot is already hovered over, don't hover over it again
            logger.debug("Hover over not already selected nor hovered over knot: {}", knot);

            // We can have only one hovered over knot at once
            app.getOptionalDotGrid().clearHovered();
            app.getOptionalDotGrid().getAllHoveredKnots().clear();
            app.getOptionalDotGrid().drawHoveredKnot(knot);

            return;
          }
        } catch (MalformedURLException e) {
          logger.error("Error in mouse hover event!", e);
        }
      }
    };
    app.getMainWindow().getGrid().addEventHandler(MouseEvent.MOUSE_MOVED, gridHoverListener);

    window.getDrawingButton()     .setSelected(false);
    window.getSelectionButton()   .setSelected(true);
    window.getDeletionButton()    .setSelected(false);
    window.getDuplicationButton() .setSelected(false);
  }

  public static EventHandler<MouseEvent> getGridHoverListener() {
    return gridHoverListener;
  }

}

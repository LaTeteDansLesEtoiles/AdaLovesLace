package org.alienlabs.adaloveslace.util;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.view.component.OptionalDotGrid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.util.List;

import static org.alienlabs.adaloveslace.view.window.MainWindow.MOUSE_CLICKED;

public class Events {

  private static App app;

  private static final Logger logger = LoggerFactory.getLogger(Events.class);

  private Events() {
    // Not accessible on purpose since all the events are static
  }

  public static EventHandler<MouseEvent> mouseClickEventHandler = event -> {
    String eType = event.getEventType().toString();
    logger.info("Event type -> {},  current Step index {}, current mode: {}", eType,
      app.getOptionalDotGrid().getDiagram().getCurrentStepIndex(),
      app.getOptionalDotGrid().getDiagram().getCurrentMode());

    if (eType.equals(MOUSE_CLICKED)) {
      double x          = event.getX();
      double y          = event.getY();
      double screenX    = event.getScreenX();
      double screenY    = event.getScreenY();
      double yMinusTop  = y - OptionalDotGrid.TOP_MARGIN;

      logger.info("Coordinate X     -> {}", x);
      logger.info("Coordinate Y     -> {}, Y - TOP -> {}", y, yMinusTop);

      processMouseClick(x, y, screenX, screenY);
    }
  };

  private static void processMouseClick(double x, double y, double screenX, double screenY) {
    switch (app.getOptionalDotGrid().getDiagram().getCurrentMode()) {
      case DRAWING          -> app.getMainWindow().onClickWithDrawMode(app, app.getOptionalDotGrid().getDiagram(),
        app.getOptionalDotGrid().addKnot(app, x, y));
      case SELECTION        -> app.getMainWindow().onClickWithSelectionMode(app, screenX, screenY);
      case DELETION         -> app.getMainWindow().onClickWithDeletionMode(app, app.getOptionalDotGrid().getDiagram(), x, y) ;
      case DUPLICATION      -> {}
      case CREATE_PATTERN   -> {} // This is managed in CreatePatternButton
      default -> throw new IllegalArgumentException("Please provide a valid mode, not: " +
        app.getOptionalDotGrid().getDiagram().getCurrentMode());
    }
  }

  public static EventHandler<MouseEvent> gridHoverEventHandler = mouseEvent -> {
    logger.debug("MouseEvent: X= {}, Y= {}", mouseEvent.getScreenX(), mouseEvent.getScreenY());

    List<Knot> allVisibleKnots = app.getOptionalDotGrid().getAllVisibleKnots();

    for (Knot knot : allVisibleKnots) {
      try {
        // If a knot is already selected, we must still hover over it because we may want to unselect it afterwards
        // But if it's already hovered over, we shall not hover it again
        boolean isMouseOverKnot = new NodeUtil().isMouseOverKnot(knot, mouseEvent.getScreenX(), mouseEvent.getScreenY());

        if (knot.isVisible() && isMouseOverKnot) {
          logger.debug("Hover over not already hovered over knot: {}", knot);

          // We can have only one hovered over knot at once
          app.getOptionalDotGrid().getAllHoveredKnots().add(knot);
          app.getOptionalDotGrid().drawHoveredKnot(knot);
          app.getOptionalDotGrid().drawSelectedKnot(app.getOptionalDotGrid().getDiagram().getCurrentStep(), knot);
        } else if(knot.isVisible() && !isMouseOverKnot && app.getOptionalDotGrid().getAllHoveredKnots().contains(knot)) {
          app.getOptionalDotGrid().getAllHoveredKnots().remove(knot);
          app.getOptionalDotGrid().drawSelectedKnot(app.getOptionalDotGrid().getDiagram().getCurrentStep(), knot);

          app.getOptionalDotGrid().layoutChildren();
        }
      } catch (MalformedURLException e) {
        logger.error("Error in mouse hover event!", e);
      }
    }
  };

  public static EventHandler<MouseEvent> getGridHoverEventHandler(App app) {
    Events.app = app;
    return gridHoverEventHandler;
  }

  public static EventHandler<MouseEvent> getMouseClickEventHandler(App app) {
    Events.app = app;
    return mouseClickEventHandler;
  }

}

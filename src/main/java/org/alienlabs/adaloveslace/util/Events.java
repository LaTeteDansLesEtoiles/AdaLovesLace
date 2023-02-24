package org.alienlabs.adaloveslace.util;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.view.component.OptionalDotGrid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static org.alienlabs.adaloveslace.view.window.MainWindow.MOUSE_CLICKED;

public class Events {

  private static App app;

  private static final Logger logger = LoggerFactory.getLogger(Events.class);

  private Events() {
    // Not accessible on purpose since all the events are static
  }

  public static final EventHandler<MouseEvent> mouseClickEventHandler = event -> {
    String eType = event.getEventType().toString();
    logger.debug("Event type -> {},  current Step index {}, current mode: {}", eType,
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
      case DRAWING          -> app.getOptionalDotGrid().addKnot(app, x, y);
      case SELECTION, MOVE  -> app.getMainWindow().onClickWithSelectionMode(app, x, y);
      case DELETION         -> app.getMainWindow().onClickWithDeletionMode(app, app.getOptionalDotGrid().getDiagram(), x, y) ;
      case DUPLICATION      -> {}
      case CREATE_PATTERN   -> {} // This is managed in CreatePatternButton
      case MIRROR           -> {} // This is managed in CreatePatternButton
      default -> throw new IllegalArgumentException("Please provide a valid mode, not: " +
        app.getOptionalDotGrid().getDiagram().getCurrentMode());
    }
  }

  public static final EventHandler<MouseEvent> gridHoverEventHandler = mouseEvent -> {
    logger.debug("MouseEvent: X= {}, Y= {}", mouseEvent.getSceneX(), mouseEvent.getSceneY());

    Set<Knot> allKnots = app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots();
    allKnots.addAll(app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots());
    app.getOptionalDotGrid().clearHovered();

    for (Knot knot : allKnots) {
      // If a knot is already selected, we must still hover over it because we may want to unselect it afterwards
      // But if it's already hovered over, we shall not hover it again
      boolean isMouseOverAGivenKnot = new NodeUtil().isMouseOverKnot(knot, mouseEvent.getSceneX(), mouseEvent.getSceneY());
      app.getOptionalDotGrid().getDiagram().setCurrentKnot(knot);

      if (knot.isVisible() && isMouseOverAGivenKnot && !app.getOptionalDotGrid().getAllHoveredKnots().contains(knot)) {
        logger.debug("Hover over not already an hovered over knot: {}", knot);

        // We can have only one hovered over knot at once
        app.getOptionalDotGrid().getAllHoveredKnots().add(knot);
      } else if(knot.isVisible() && isMouseOverAGivenKnot && app.getOptionalDotGrid().getAllHoveredKnots().contains(knot)) {
        logger.debug("Hover over an already hovered over knot: {}", knot);
      } else if(!isMouseOverAGivenKnot && app.getOptionalDotGrid().getAllHoveredKnots().contains(knot)) {
        logger.debug("Don't hover over knot: {}", knot);
        app.getOptionalDotGrid().getAllHoveredKnots().remove(knot);
      }

      app.getOptionalDotGrid().drawHoveredOverOrSelectedKnot(false, knot);
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

package org.alienlabs.adaloveslace.util;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.alienlabs.adaloveslace.business.model.Diagram.newStep;
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

      logger.debug("Coordinate X     -> {}", x);
      logger.debug("Coordinate Y     -> {}", y);

      processMouseClick(x, y);
    }
  };

  // @see https://stackoverflow.com/questions/42782074/javafx-moving-objects-within-scrollpane-by-drag-and-drop
  // @see https://stackoverflow.com/questions/40982787/change-cursor-in-javafx-listview-during-drag-and-drop/40984625#40984625
  public static final EventHandler<MouseEvent> dragInitiatedOverOnHandle = event -> {
    String eType = event.getEventType().toString();
    logger.debug("Event type -> {},  current Step index {}, current mode: {}", eType,
            app.getOptionalDotGrid().getDiagram().getCurrentStepIndex(),
            app.getOptionalDotGrid().getDiagram().getCurrentMode());

    Circle handle = (Circle) event.getSource();
    Dragboard db = handle.startDragAndDrop(TransferMode.MOVE);

    ClipboardContent content = new ClipboardContent();
    content.put(DataFormat.PLAIN_TEXT, handle.getId());
    db.setContent(content);
    db.setDragView(null);
    handle.setCursor(Cursor.NONE);

    Optional<Knot> first = app.getOptionalDotGrid().getDiagram().getCurrentStep()
            .getSelectedKnots()
            .stream()
            .filter(knot -> knot.getHandle() != null)
            .findFirst();

    first.ifPresent(knot -> app.getOptionalDotGrid()
            .setDragOriginKnot(knot));

    event.consume();
  };

  public static final EventHandler<DragEvent> dragOverHandleWithSelectionMode = event -> {
    String eType = event.getEventType().toString();
    logger.debug("Event type -> {},  current Step index {}, current mode: {}", eType,
            app.getOptionalDotGrid().getDiagram().getCurrentStepIndex(),
            app.getOptionalDotGrid().getDiagram().getCurrentMode());

    double x          = event.getSceneX();
    double y          = event.getSceneY();

    logger.debug("Coordinate X     -> {}", x);
    logger.debug("Coordinate Y     -> {}", y);

    event.acceptTransferModes(TransferMode.MOVE);
    app.getMainWindow().onDragOverHandleWithSelectionMode(app, x, y);

    event.consume();
  };

  public static final EventHandler<DragEvent> dragDroppedHandleWithSelectionMode = event -> {
    String eType = event.getEventType().toString();
    logger.debug("Event type -> {},  current Step index {}, current mode: {}", eType,
            app.getOptionalDotGrid().getDiagram().getCurrentStepIndex(),
            app.getOptionalDotGrid().getDiagram().getCurrentMode());

    app.getOptionalDotGrid().clearAllKnotDecorations();
    app.getOptionalDotGrid().clearKnotHandles();
    app.getOptionalDotGrid().clearKnotSelections();

    Knot firstKnot = app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().stream()
            .min(Comparator.comparing(Knot::getX)
                    .thenComparing(Knot::getY))
            .get();
    app.getOptionalDotGrid().addSelectionAndHandleToAKnot(
            app.getOptionalDotGrid().getDragOriginKnot(),
            Color.rgb(0,0,255, 0.5), firstKnot);

    logger.debug("After event type -> {},  current Step index {}, current mode: {}", eType,
            app.getOptionalDotGrid().getDiagram().getCurrentStepIndex(),
            app.getOptionalDotGrid().getDiagram().getCurrentMode());

    app.getOptionalDotGrid().layoutChildren();

    event.consume();
  };

  public static void moveDraggedAndDroppedNodes(App app, double x, double y, Circle handle) {
    double deltaX = handle.getCenterX() < x ? -(handle.getCenterX() - x) : (x - handle.getCenterX());
    double deltaY = handle.getCenterY() < y ? -(handle.getCenterY() - y) : (y - handle.getCenterY());

    Set<Knot> displayedKnots = new TreeSet<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots());
    Set<Knot> selectedKnots = new TreeSet<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots());
    Set<Knot> copiedKnots = new TreeSet<>();

    for (Knot knot : selectedKnots) {
      knot.setX(knot.getX() + deltaX);
      knot.setY(knot.getY() + deltaY);
      Knot copiedKnot = new NodeUtil().copyKnot(knot);

      displayedKnots.remove(knot);
      copiedKnots.add(copiedKnot);

      if (app.getOptionalDotGrid().getDragOriginKnot().equals(knot)) {
        app.getOptionalDotGrid().setDragOriginKnot(copiedKnot);
      }

      logger.debug("Knot to move");
    }

    handle.setCenterX(handle.getCenterX() + deltaX);
    handle.setCenterY(handle.getCenterY() + deltaY);

    newStep(displayedKnots, copiedKnots, true, handle);
  }

  private static void processMouseClick(double x, double y) {
    switch (app.getOptionalDotGrid().getDiagram().getCurrentMode()) {
      case DRAWING          -> app.getOptionalDotGrid().getDiagram().drawKnot(x, y);
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

    Set<Knot> allKnots = new HashSet<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots());
    allKnots.addAll(new HashSet<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots()));

    boolean isMouseOverAGivenKnot = false;

    for (Knot knot : allKnots) {
      // If a knot is already selected, we must still hover over it because we may want to unselect it afterwards
      // But if it's already hovered over, we shall not hover it again
      isMouseOverAGivenKnot = new NodeUtil().isMouseOverKnot(knot, mouseEvent.getSceneX(), mouseEvent.getSceneY());
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

      knot.setHoveredKnot(isMouseOverAGivenKnot);
    }

    app.getOptionalDotGrid().drawHoveredOverOrSelectedKnot(app.getOptionalDotGrid().getDiagram().getCurrentStep().getAllVisibleKnots());
  };

  public static EventHandler<MouseEvent> getGridHoverEventHandler(App app) {
    Events.app = app;
    return gridHoverEventHandler;
  }

  public static EventHandler<MouseEvent> getMouseClickEventHandler(App app) {
    Events.app = app;
    return mouseClickEventHandler;
  }

  public static EventHandler<MouseEvent> getDragInitiatedOverHandleEventHandler() {
    return dragInitiatedOverOnHandle;
  }

  public static EventHandler<DragEvent> getMouseDragOverHandleEventHandler() {
    return dragOverHandleWithSelectionMode;
  }

  public static EventHandler<DragEvent> getMouseDragDroppedHandleEventHandler() {
    return dragDroppedHandleWithSelectionMode;
  }

}

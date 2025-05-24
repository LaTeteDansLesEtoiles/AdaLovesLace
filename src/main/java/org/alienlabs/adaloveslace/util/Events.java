package org.alienlabs.adaloveslace.util;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.business.model.MouseMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.alienlabs.adaloveslace.business.model.Diagram.*;
import static org.alienlabs.adaloveslace.view.window.MainWindow.MOUSE_CLICKED;

public class Events {

  static App app;

  private static double handleOffsetX;
  private static double handleOffsetY;
  private static Point2D previousEvent;

    private static final Logger logger = LoggerFactory.getLogger(Events.class);

  private Events() {
    // Not accessible on purpose since all the events are static
  }

  public static final EventHandler<KeyEvent> keyHandler = event -> {
    if (app.getOptionalDotGrid().getDiagram().getCurrentMode() == MouseMode.DRAWING) {
      logger.info("key pressed -> {}", event.getCode());

      switch (event.getCode()) {
        case BACK_SPACE:
          if (!typedText.isEmpty()) {
            typedText.deleteCharAt(typedText.length() - 1);
          }
          updateImage.run();

          break;
        case ENTER:
          typedText.append("\n");
          updateImage.run();

          break;
        default:
          if (!event.isControlDown() && !event.getText().isEmpty()) {
            typedText.append(event.getText());
            updateImage.run();
          }
      }
    }
  };

  public static final EventHandler<MouseEvent> mouseClickEventHandler = event -> {
    String eType = event.getEventType().toString();
    logger.info("Event type mouseClickEventHandler -> {}, source {} current Step index {}, current mode: {}",
            eType,
            event.getSource(),
            app.getOptionalDotGrid().getDiagram().getCurrentStepIndex(),
            app.getOptionalDotGrid().getDiagram().getCurrentMode());

    if (eType.equals(MOUSE_CLICKED)) {
      Point2D mouseInParent = app.getOptionalDotGrid().getRoot().sceneToLocal(event.getSceneX(), event.getSceneY());
      Double x = mouseInParent.getX();
      Double y = mouseInParent.getY();

      logger.info("Coordinate X     -> {}", x);
      logger.info("Coordinate Y     -> {}", y);

      processMouseClick(x, y);
    }
  };

  // @see https://stackoverflow.com/questions/42782074/javafx-moving-objects-within-scrollpane-by-drag-and-drop
  // @see https://stackoverflow.com/questions/40982787/change-cursor-in-javafx-listview-during-drag-and-drop/40984625#40984625
  public static final EventHandler<MouseEvent> dragInitiatedOverOnHandle = event -> {
    String eType = event.getEventType().toString();
    logger.info(
            "Event type -> dragInitiatedOverOnHandle {}, source {} current Step index {}, current mode {}, X {}, Y {}",
            eType,
            event.getSource(),
            app.getOptionalDotGrid().getDiagram().getCurrentStepIndex(),
            app.getOptionalDotGrid().getDiagram().getCurrentMode(),
            event.getX(),
            event.getY()
    );

    Circle handleSource = (Circle)event.getSource();
    Point2D handleCenterInScene = handleSource.localToScene(handleSource.getCenterX(), handleSource.getCenterY());
    handleOffsetX = event.getSceneX() - handleCenterInScene.getX();
    handleOffsetY = event.getSceneY() - handleCenterInScene.getY();
    previousEvent = null;

    app.getRoot().removeEventHandler(MouseEvent.MOUSE_CLICKED, Events.getMouseClickEventHandler(app));
    app.getOptionalDotGrid().getDiagram().setOldMode(app.getOptionalDotGrid().getDiagram().getCurrentMode());
    app.getOptionalDotGrid().getDiagram().setCurrentMode(MouseMode.DRAG_AND_DROP);

    List<Knot> displayedKnots = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots());
    List<Knot> selectedKnots = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots());
    List<Knot> copiedKnots = new ArrayList<>();

    for (Knot knot : selectedKnots) {
      Knot copiedKnot = new NodeUtil().copyKnot(knot);

      displayedKnots.remove(knot);
      copiedKnots.add(copiedKnot);
    }

    newStep(displayedKnots, copiedKnots, false);
    event.consume();
  };

  public static final EventHandler<MouseEvent> dragOverHandleWithSelectionMode = event -> {
    String eType = event.getEventType().toString();
    Circle sourceHandle = (Circle)event.getSource();
    logger.debug(
            "Event type dragOverHandleWithSelectionMode -> {}, source {} current Step index {}, current mode: {}",
            eType,
            sourceHandle,
            app.getOptionalDotGrid().getDiagram().getCurrentStepIndex(),
            app.getOptionalDotGrid().getDiagram().getCurrentMode()
    );

    double targetCircleCenterXScene = event.getSceneX() - handleOffsetX;
    double targetCircleCenterYScene = event.getSceneY() - handleOffsetY;
    Point2D currentEvent = new Point2D(targetCircleCenterXScene, targetCircleCenterYScene);

    if (previousEvent == null) {
      previousEvent = new Point2D(targetCircleCenterXScene, targetCircleCenterYScene);
      currentEvent = currentEvent.subtract(previousEvent);
    } else {
      if (!previousEvent.equals(currentEvent)) {
        currentEvent = currentEvent.subtract(previousEvent);
        previousEvent = new Point2D(targetCircleCenterXScene, targetCircleCenterYScene);
      } else {
        currentEvent = new Point2D(0, 0);
      }
    }

    List<Knot> selectedKnots = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots());
    List<Knot> copiedKnots = new ArrayList<>();

    Knot eventSourceKnot = selectedKnots.stream().filter(knot -> knot.getHandle().equals(sourceHandle)).findFirst().orElse(null);
    if (null == eventSourceKnot) {
      event.consume();
      return;
    }

    for (Knot knot : selectedKnots) {
      Knot copiedKnot = new NodeUtil().copyKnot(knot);

      copiedKnot.setX(knot.getX() + currentEvent.getX());
      copiedKnot.setY(knot.getY() + currentEvent.getY());
      copiedKnot.getImageView().setLayoutX(copiedKnot.getX());
      copiedKnot.getImageView().setLayoutY(copiedKnot.getY());
      copiedKnot.getHandle().setLayoutX(copiedKnot.getHandle().getLayoutX() + currentEvent.getX());
      copiedKnot.getHandle().setLayoutY(copiedKnot.getHandle().getLayoutY() + currentEvent.getY());
      copiedKnot.getSelection().setLayoutX(copiedKnot.getX());
      copiedKnot.getSelection().setLayoutY(copiedKnot.getY());
      copiedKnot.getHovered().setLayoutX(copiedKnot.getX());
      copiedKnot.getHovered().setLayoutY(copiedKnot.getY());

      copiedKnots.add(copiedKnot);
      Events.logger.debug("Knot to move");
    }

    app.getOptionalDotGrid().getDiagram().getCurrentStep().setSelectedKnots(copiedKnots);
    app.getOptionalDotGrid().layoutChildren();

    logger.debug("Event type: {}, X: {}, Y: {}", event.getEventType(), event.getX(), event.getY());
    event.consume();
  };

  public static final EventHandler<MouseEvent> dragDroppedHandleWithSelectionMode = event -> {
    String eType = event.getEventType().toString();

    logger.info(
            "Event type -> {},  current Step index: {}, current mode: {}, X: {}, Y: {}",
            eType,
            app.getOptionalDotGrid().getDiagram().getCurrentStepIndex(),
            app.getOptionalDotGrid().getDiagram().getCurrentMode(),
            event.getSceneX(),
            event.getSceneY()
    );

    app.getOptionalDotGrid().getDiagram().setCurrentMode(app.getOptionalDotGrid().getDiagram().getOldMode());
    app.getOptionalDotGrid().layoutChildren();

    event.consume();
    Platform.runLater(() ->
            app.getRoot().addEventHandler(MouseEvent.MOUSE_CLICKED, Events.getMouseClickEventHandler(app)));
  };

  private static void processMouseClick(double x, double y) {
    switch (app.getOptionalDotGrid().getDiagram().getCurrentMode()) {
      case DRAWING          -> app.getOptionalDotGrid().getDiagram().drawKnot(x, y);
      case SELECTION        -> app.getMainWindow().onClickWithSelectionMode(app);
      case DELETION         -> app.getMainWindow().onClickWithDeletionMode(app, app.getOptionalDotGrid().getDiagram()) ;
      case DUPLICATION      -> {}
      case CREATE_PATTERN   -> {} // This is managed in CreatePatternButton
      case MIRROR           -> {} // This is managed in CreatePatternButton
      case MOVE             -> {} // This is managed in the various [Arrow]Button
      case DRAG_AND_DROP    -> {} // This is managed in Events#dragInitiatedOverOnHandle()
      default -> throw new IllegalArgumentException("Please provide a valid mode, not: " +
        app.getOptionalDotGrid().getDiagram().getCurrentMode());
    }
  }

  public static final EventHandler<MouseEvent> gridHoverEventHandler = mouseEvent -> {
    logger.debug("MouseEvent: X= {}, Y= {}", mouseEvent.getSceneX(), mouseEvent.getSceneY());

    if (app.getOptionalDotGrid().getDiagram().getCurrentMode() == MouseMode.SELECTION
            || app.getOptionalDotGrid().getDiagram().getCurrentMode() == MouseMode.DRAG_AND_DROP
            || app.getOptionalDotGrid().getDiagram().getCurrentMode() == MouseMode.DELETION) {
      List<Knot> allKnots = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getAllVisibleKnots());
      boolean isMouseOverAGivenKnot = false;

    for (Knot knot : allKnots) {
      // If a knot is already selected, we must still hover over it because we may want to unselect it afterwards
      // But if it's already hovered over, we shall not hover it again
      isMouseOverAGivenKnot = new NodeUtil().isMouseOverKnot(knot);

        if (isMouseOverAGivenKnot) {
          // We can have only one hovered over knot at once
          logger.debug("Hover over knot: {}", knot);
          app.getOptionalDotGrid().getDiagram().setCurrentKnot(knot);
        }
      }

      app.getOptionalDotGrid().drawHoveredOverOrSelectedDecorations(allKnots);
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

  public static EventHandler<MouseEvent> getDragInitiatedOverHandleEventHandler() {
    return dragInitiatedOverOnHandle;
  }

  public static EventHandler<MouseEvent> getMouseDragOverHandleEventHandler() {
    return dragOverHandleWithSelectionMode;
  }

  public static EventHandler<MouseEvent> getMouseDragDroppedHandleEventHandler() {
    return dragDroppedHandleWithSelectionMode;
  }

}

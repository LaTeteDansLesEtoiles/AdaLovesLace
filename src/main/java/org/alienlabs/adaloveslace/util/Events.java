package org.alienlabs.adaloveslace.util;

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

  private static double offsetX;
  private static double offsetY;

  private static MouseMode previousMouseMode;
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

    offsetX = event.getX() - ((Circle) event.getSource()).getLayoutX();
    offsetY = event.getY() - ((Circle) event.getSource()).getLayoutY();

    app.getOptionalDotGrid().getRoot().removeEventHandler(MouseEvent.MOUSE_CLICKED, Events.getMouseClickEventHandler(app));
    previousMouseMode = app.getOptionalDotGrid().getDiagram().getCurrentMode();
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
    logger.debug(
            "Event type dragOverHandleWithSelectionMode -> {}, source {} current Step index {}, current mode: {}",
            eType,
            event.getSource(),
            app.getOptionalDotGrid().getDiagram().getCurrentStepIndex(),
            app.getOptionalDotGrid().getDiagram().getCurrentMode()
    );

    logger.debug("Coordinate X     -> {}", offsetX);
    logger.debug("Coordinate Y     -> {}", offsetY);

    List<Knot> selectedKnots = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots());
    List<Knot> copiedKnots = new ArrayList<>();

    Knot eventSourceKnot = selectedKnots.stream().filter(knot -> knot.getHandle().equals(event.getSource())).findFirst().orElse(null);
    double knotX = eventSourceKnot.getX() - event.getSceneX();
    double knotY = eventSourceKnot.getY() - event.getSceneY();

    for (Knot knot : selectedKnots) {
      Knot copiedKnot = new NodeUtil().copyKnot(knot);

      if (knot.equals(eventSourceKnot)) {
        copiedKnot.setX(event.getSceneX());
        copiedKnot.setY(event.getSceneY());
        copiedKnot.getImageView().setLayoutX(event.getSceneX());
        copiedKnot.getImageView().setLayoutY(event.getSceneY());
        copiedKnot.getHandle().setLayoutX(event.getSceneX() - offsetX);
        copiedKnot.getHandle().setLayoutY(event.getSceneY() - offsetY);
        copiedKnot.getSelection().setLayoutX(event.getSceneX());
        copiedKnot.getSelection().setLayoutY(event.getSceneY());
        copiedKnot.getHovered().setLayoutX(event.getSceneX());
        copiedKnot.getHovered().setLayoutY(event.getSceneY());
      } else {
        copiedKnot.setX(knot.getX() - knotX);
        copiedKnot.setY(knot.getY() - knotY);
        copiedKnot.getImageView().setLayoutX(knot.getImageView().getLayoutX() - knotX);
        copiedKnot.getImageView().setLayoutY(knot.getImageView().getLayoutY() - knotY);
        copiedKnot.getHandle().setLayoutX(knot.getHandle().getLayoutX() - knotX);
        copiedKnot.getHandle().setLayoutY(knot.getHandle().getLayoutY() - knotY);
        copiedKnot.getSelection().setLayoutX(knot.getSelection().getLayoutX() - knotX);
        copiedKnot.getSelection().setLayoutY(knot.getSelection().getLayoutY() - knotY);
        copiedKnot.getHovered().setLayoutX(knot.getSelection().getLayoutX() - knotX);
        copiedKnot.getHovered().setLayoutY(knot.getSelection().getLayoutY() - knotY);
      }

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

    app.getOptionalDotGrid().getDiagram().setCurrentMode(previousMouseMode);
    app.getOptionalDotGrid().layoutChildren();

    if (app.getOptionalDotGrid().getDiagram().getCurrentMode() == MouseMode.DRAWING) {
      app.getMainWindow().getGrid().addEventHandler(MouseEvent.MOUSE_CLICKED, Events.getMouseClickEventHandler(app));
    }

    event.consume();
  };

  private static void processMouseClick(double x, double y) {
    switch (app.getOptionalDotGrid().getDiagram().getCurrentMode()) {
      case DRAWING          -> app.getOptionalDotGrid().getDiagram().drawKnot(x, y);
      case SELECTION        -> app.getMainWindow().onClickWithSelectionMode(app);
      case DELETION         -> app.getMainWindow().onClickWithDeletionMode(app, app.getOptionalDotGrid().getDiagram()) ;
      case DUPLICATION      -> {}
      case CREATE_PATTERN   -> {} // This is managed in CreatePatternButton
      case MIRROR           -> {} // This is managed in CreatePatternButton
      default -> throw new IllegalArgumentException("Please provide a valid mode, not: " +
        app.getOptionalDotGrid().getDiagram().getCurrentMode());
    }
  }

  public static final EventHandler<MouseEvent> gridHoverEventHandler = mouseEvent -> {
    logger.debug("MouseEvent: X= {}, Y= {}", mouseEvent.getSceneX(), mouseEvent.getSceneY());

    if (app.getOptionalDotGrid().getDiagram().getCurrentMode() == MouseMode.SELECTION
    || app.getOptionalDotGrid().getDiagram().getCurrentMode() == MouseMode.DRAG_AND_DROP) {
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

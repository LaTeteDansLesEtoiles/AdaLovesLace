package org.alienlabs.adaloveslace.util;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.business.model.Pattern;
import org.alienlabs.adaloveslace.business.model.enumeration.MouseMode;
import org.alienlabs.adaloveslace.business.model.enumeration.PatternOrTextMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.alienlabs.adaloveslace.business.model.Diagram.isNewText;
import static org.alienlabs.adaloveslace.business.model.Diagram.newStep;
import static org.alienlabs.adaloveslace.business.model.Knot.NEW_TEXT;
import static org.alienlabs.adaloveslace.view.window.MainWindow.MOUSE_CLICKED;

public class Events {

  static App app;

  private static ImageView currentImageView;
  private static Pattern currentPattern;
  private static double handleOffsetX;
  private static double handleOffsetY;
  private static Point2D previousEvent;
  private static double dragStartX;
  private static double dragStartY;

  private static final Logger logger = LoggerFactory.getLogger(Events.class);

  private Events() {
    // Not accessible on purpose since all the events are static
  }

  public static final EventHandler<KeyEvent> keyHandler = event -> {
    if (app.getOptionalDotGrid().getDiagram().getCurrentMode() == MouseMode.DRAWING ||
            app.getOptionalDotGrid().getDiagram().getCurrentMode() == MouseMode.SELECTION) {
      logger.debug("key pressed -> {}", event.getCode());

      if ((app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().size() > 1) ||
              (app.getOptionalDotGrid().getDiagram().getCurrentKnot() != null &&
                      app.getOptionalDotGrid().getDiagram().getCurrentKnot().getPattern().isPresent())) {
        return;
      }

      isNewText = false;

      StringBuilder typedText = app.getOptionalDotGrid().getDiagram().getCurrentKnot().getTypedText();
      if (typedText == null) {
        typedText = new StringBuilder(NEW_TEXT);
      }

      switch (event.getCode()) {
        case BACK_SPACE:
          if (!typedText.isEmpty()) {
            typedText.deleteCharAt(typedText.length() - 1);
          } else {
            typedText.append(NEW_TEXT);
          }
          app.getOptionalDotGrid().getDiagram().getCurrentKnot().setTypedText(typedText);
          app.getOptionalDotGrid().getDiagram().getUpdateImage().run();

          break;
        case ENTER:
          typedText.append("\n");
          app.getOptionalDotGrid().getDiagram().getCurrentKnot().setTypedText(typedText);
          app.getOptionalDotGrid().getDiagram().getUpdateImage().run();

          break;
        default:
          if (!event.isControlDown() && !event.getText().isEmpty()) {
            typedText.append(event.getText());
            app.getOptionalDotGrid().getDiagram().getCurrentKnot().setTypedText(typedText);
            app.getOptionalDotGrid().getDiagram().getCurrentKnot().setText(Optional.of(typedText.toString()));
            app.getOptionalDotGrid().getDiagram().getUpdateImage().run();
          }
      }
    }
  };

  public static final EventHandler<MouseEvent> mouseClickEventHandler = event -> {
    String eType = event.getEventType().toString();
    logger.debug("Event type mouseClickEventHandler -> {}, source {} current Step index {}, current mode: {}",
            eType,
            event.getSource(),
            app.getOptionalDotGrid().getDiagram().getCurrentStepIndex(),
            app.getOptionalDotGrid().getDiagram().getCurrentMode());

    if (eType.equals(MOUSE_CLICKED) && event.getButton() == MouseButton.PRIMARY) {
      Point2D mouseInParent = app.getMovablePane().sceneToLocal(event.getSceneX(), event.getSceneY());
      Double x = mouseInParent.getX();
      Double y = mouseInParent.getY();

      logger.debug("Coordinate X     -> {}", x);
      logger.debug("Coordinate Y     -> {}", y);

      processMouseClick(x, y);
    }
  };

  public static final EventHandler<MouseEvent> mouseRightClickEventHandler = event -> {
    if (event.getButton() == MouseButton.SECONDARY) {
      dragStartX = event.getSceneX();
      dragStartY = event.getSceneY();
    }
  };

  public static final EventHandler<MouseEvent> mouseGridDraggedEventHandler = event -> {
    if (event.getButton() == MouseButton.SECONDARY) {
      Pane movablePane = app.getMovablePane();
      logger.debug("Pane in scene : {}", movablePane.localToScene(0, 0));

      double dx = event.getSceneX() - dragStartX;
      double dy = event.getSceneY() - dragStartY;
      movablePane.setTranslateX(movablePane.getTranslateX() + dx);
      movablePane.setTranslateY(movablePane.getTranslateY() + dy);

      dragStartX = event.getSceneX();
      dragStartY = event.getSceneY();
    }
  };

  public static final EventHandler<MouseEvent> mouseDoubleClickOnGridEventHendler =  event -> {
    if (event.getButton() == MouseButton.SECONDARY && event.getClickCount() == 2) {

      double sceneCenterX = app.getScene().getWidth() / 2;
      double sceneCenterY = app.getScene().getHeight() / 2;

      Pane movablePane = app.getMovablePane();
      Bounds paneBounds = movablePane.getLayoutBounds();
      double paneCenterX = paneBounds.getWidth() / 2;
      double paneCenterY = paneBounds.getHeight() / 2;

      movablePane.setTranslateX(sceneCenterX - paneCenterX);
      movablePane.setTranslateY(sceneCenterY - paneCenterY);
    }
  };

  // @see https://stackoverflow.com/questions/42782074/javafx-moving-objects-within-scrollpane-by-drag-and-drop
  // @see https://stackoverflow.com/questions/40982787/change-cursor-in-javafx-listview-during-drag-and-drop/40984625#40984625
  public static final EventHandler<MouseEvent> dragInitiatedOverOnHandle = event -> {
    String eType = event.getEventType().toString();
    logger.debug(
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

    app.getMovablePane().removeEventHandler(MouseEvent.MOUSE_CLICKED, Events.getMouseClickEventHandler(app));
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

    if (app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().size() == 1) {
      app.getOptionalDotGrid().getDiagram().setCurrentKnot(copiedKnots.getLast());
    }

    app.getOptionalDotGrid().layoutChildren();

    logger.debug("Event type: {}, X: {}, Y: {}", event.getEventType(), event.getX(), event.getY());
    event.consume();
  };

  public static final EventHandler<MouseEvent> dragDroppedHandleWithSelectionMode = event -> {
    String eType = event.getEventType().toString();

    logger.debug(
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
            app.getMovablePane().addEventHandler(MouseEvent.MOUSE_CLICKED, Events.getMouseClickEventHandler(app)));
  };

  private static void processMouseClick(double x, double y) {
    switch (app.getOptionalDotGrid().getDiagram().getCurrentMode()) {
      case DRAWING          -> app.getOptionalDotGrid().getDiagram().drawKnot(x, y);
      case SELECTION        -> app.getMainWindow().onClickWithSelectionMode(app);
      case DELETION         -> app.getMainWindow().onClickWithDeletionMode(app, app.getOptionalDotGrid().getDiagram()) ;
      case DUPLICATION      -> { /* This is managed in DuplicationButton */ }
      case CREATE_PATTERN   -> { /* This is managed in CreatePatternButton */ }
      case MIRROR           -> { /* This is managed in [Horizontal|Vertical]FlippingButton */ }
      case MOVE             -> { /* This is managed in the various [Arrow]Button */ }
      case DRAG_AND_DROP    -> { /* This is managed in Events#dragInitiatedOverOnHandle() */ }
      default -> throw new IllegalArgumentException("Please provide a valid mode, not: " +
        app.getOptionalDotGrid().getDiagram().getCurrentMode());
    }
  }

  private static Rectangle rectangle;
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
    } else if (app.getOptionalDotGrid().getDiagram().getCurrentMode() == MouseMode.DRAWING) {
      if (PatternOrTextMode.PATTERN == app.getOptionalDotGrid().getCurrentPatternOrTextModeProperty().get()) {
        if (null != currentImageView) {
          app.getMovablePane().getChildren().remove(currentImageView);
        }
        if (null == currentImageView
                || !app.getOptionalDotGrid().getCurrentPatternProperty().get().equals(currentPattern)) {
          currentPattern = app.getOptionalDotGrid().getCurrentPatternProperty().get();
          currentImageView = new ImageView(
                  new Image(
                          new File(app.getOptionalDotGrid().getCurrentPatternProperty().get().getAbsoluteFilename())
                                  .toURI().toString()
                  )
          );
        }

        Point2D mouseInParent = app.getMovablePane().sceneToLocal(mouseEvent.getSceneX(), mouseEvent.getSceneY());
        Double x = mouseInParent.getX();
        Double y = mouseInParent.getY();
        currentImageView.setLayoutX(x);
        currentImageView.setLayoutY(y);
        app.getMovablePane().getChildren().add(currentImageView);
      } else {
        if (null != rectangle) {
          app.getMovablePane().getChildren().remove(rectangle);
        }

        Point2D mouseInParent = app.getMovablePane().sceneToLocal(mouseEvent.getSceneX(), mouseEvent.getSceneY());
        Double x = mouseInParent.getX();
        Double y = mouseInParent.getY();

        rectangle = new Rectangle(
                25,
                40
        );
        rectangle.setLayoutX(x);
        rectangle.setLayoutY(y);
        rectangle.setStroke(Color.BLUE);
        rectangle.setStrokeWidth(2d);
        rectangle.setFill(Color.TRANSPARENT);

        app.getMovablePane().getChildren().add(rectangle);
      }
    }
  };

  public static final EventHandler<MouseEvent> gridHoverExitEventHandler = mouseEvent -> {
    if (null != currentImageView) {
      app.getMovablePane().getChildren().remove(currentImageView);
    }
    if (null != rectangle) {
      app.getMovablePane().getChildren().remove(rectangle);
    }
  };

  public static EventHandler<MouseEvent> getGridHoverEventHandler(App app) {
    Events.app = app;
    return gridHoverEventHandler;
  }

  public static EventHandler<MouseEvent> getGridHoverExitEventHandler(App app) {
    Events.app = app;
    return gridHoverExitEventHandler;
  }

  public static EventHandler<MouseEvent> getMouseClickEventHandler(App app) {
    Events.app = app;
    return mouseClickEventHandler;
  }

  public static EventHandler<MouseEvent> getMouseRightClickEventHandler(App app) {
    Events.app = app;
    return mouseRightClickEventHandler;
  }

  public static EventHandler<MouseEvent> getGridDraggedEventHandler(App app) {
    Events.app = app;
    return mouseGridDraggedEventHandler;
  }

  public static EventHandler<MouseEvent> getMouseDoubleRightClickOnGridEventHendler(App app) {
    Events.app = app;
    return mouseDoubleClickOnGridEventHendler;
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

  public static void removeEventsFromGrid(App app) {
    app.getOptionalDotGrid().clearCreatePatternRectangle();
    app.getMovablePane().setOnMouseMoved(null);
    app.getMovablePane().setOnMouseClicked(null);
  }

  public static void setCurrentImageView(ImageView currentImageView) {
    Events.currentImageView = currentImageView;
  }

}

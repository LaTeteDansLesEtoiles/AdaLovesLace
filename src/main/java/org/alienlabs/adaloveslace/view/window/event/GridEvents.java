package org.alienlabs.adaloveslace.view.window.event;

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
import org.alienlabs.adaloveslace.domain.Knot;
import org.alienlabs.adaloveslace.domain.Pattern;
import org.alienlabs.adaloveslace.domain.enumeration.MouseMode;
import org.alienlabs.adaloveslace.domain.enumeration.PatternOrTextMode;
import org.alienlabs.adaloveslace.util.NodeUtil;
import org.alienlabs.adaloveslace.view.component.GridUtil;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.ParentGridStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.alienlabs.adaloveslace.domain.Diagram.isNewText;
import static org.alienlabs.adaloveslace.domain.Diagram.newStep;
import static org.alienlabs.adaloveslace.domain.Knot.NEW_TEXT;
import static org.alienlabs.adaloveslace.view.window.MainWindow.MOUSE_CLICKED;

public class GridEvents {

  public static App app;

  private static ImageView currentImageView;
  private static Pattern currentPattern;
  private static double handleOffsetX;
  private static double handleOffsetY;
  private static Point2D previousEvent;
  private static double dragStartX;
  private static double dragStartY;

  private static final Logger logger = LoggerFactory.getLogger(GridEvents.class);

  private GridEvents() {
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

      if (app.getOptionalDotGrid().getDiagram().getCurrentKnot() == null) {
        Point2D mouseInParent = app.getMovablePane().sceneToLocal(app.getScene().getWidth() / 2, app.getScene().getHeight() / 2);
        double x = mouseInParent.getX();
        double y = mouseInParent.getY();

        Knot knot = new NodeUtil().newKnot(
          x, y,
          null, // imageView
          null, // pattern
          null, // currentKnot
          app.getOptionalDotGrid().getDiagram().getCurrentColor()
        );

        knot.setTypedText(new StringBuilder(NEW_TEXT));
        knot.setText(Optional.of(NEW_TEXT.toString()));

        ImageView imageView = new NodeUtil().createText(
          x, y,
          null,
          null,
          app.getOptionalDotGrid().getDiagram().getCurrentColor()
        );
        knot.setImageView(imageView);

        app.getOptionalDotGrid().getDiagram().setCurrentKnot(knot);
      }

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

  public static final EventHandler<MouseEvent> mouseGridDragReleasedEventHandler = event -> {
    if (event.getButton() == MouseButton.SECONDARY) {
      ParentGridStrategy.setGridHasBeenDrawn(false);
      app.getOptionalDotGrid().layoutChildren();
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

    app.getMovablePane().removeEventHandler(MouseEvent.MOUSE_CLICKED, GridEvents.getMouseClickEventHandler(app));
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

  private static List<Knot> dragKnots = null;

  public static final EventHandler<MouseEvent> dragOverHandleWithSelectionMode = event -> {
    Circle sourceHandle = (Circle)event.getSource();

    // Calculer le déplacement
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

    // Initialiser les listes de nœuds et positions si c'est le premier mouvement
    if (dragKnots == null) {
      List<Knot> selectedKnots = app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots();
      // Vérifier que le nœud source est dans la sélection
      Knot eventSourceKnot = selectedKnots.stream()
        .filter(knot -> knot.getHandle() != null && knot.getHandle().equals(sourceHandle))
        .findFirst()
        .orElse(null);
      if (null == eventSourceKnot) {
        event.consume();
        return;
      }

      // Copier les nœuds une seule fois au début du drag
      dragKnots = new ArrayList<>(selectedKnots.size());

      // Nettoyer les sélections et décorations
      app.getOptionalDotGrid().clearSelections();
      app.getOptionalDotGrid().clearHovered();

      // Nettoyer les sélections et hover des nœuds originaux
      for (Knot knot : selectedKnots) {
        if (knot.getSelection() != null) {
          app.getMovablePane().getChildren().remove(knot.getSelection());
          knot.setSelection(null);
        }
        if (knot.getHovered() != null) {
          app.getMovablePane().getChildren().remove(knot.getHovered());
          knot.setHovered(null);
        }
        // Garder les handles car ils sont nécessaires pour le drag and drop
      }

      // Ne pas ajouter les nœuds originaux à displayedKnots pour éviter qu'ils se déplacent
      // Les nœuds originaux restent dans selectedKnots mais ne sont pas visibles

      // Créer les copies avec leurs nouveaux rectangles de sélection
      for (Knot knot : selectedKnots) {
        // Copier le nœud avec sa position actuelle
        Knot copiedKnot = new NodeUtil().copyKnot(knot);
        dragKnots.add(copiedKnot);

        // Créer un nouveau rectangle de sélection pour la copie
        Rectangle rec = new GridUtil(app.getMovablePane()).newRectangle(copiedKnot, Color.BLUE);
        copiedKnot.setSelection(rec);
        app.getMovablePane().getChildren().add(rec);
      }


      // Mettre à jour la liste des nœuds sélectionnés
      app.getOptionalDotGrid().getDiagram().getCurrentStep().setSelectedKnots(dragKnots);
    }

    // Mettre à jour la position des nœuds
    for (int i = 0; i < dragKnots.size(); i++) {
      Knot copiedKnot = dragKnots.get(i);

      // Mettre à jour les positions
      copiedKnot.setX(copiedKnot.getX() + currentEvent.getX());
      copiedKnot.setY(copiedKnot.getY() + currentEvent.getY());
      copiedKnot.getImageView().setLayoutX(copiedKnot.getX());
      copiedKnot.getImageView().setLayoutY(copiedKnot.getY());
      copiedKnot.getSelection().setLayoutX(copiedKnot.getX());
      copiedKnot.getSelection().setLayoutY(copiedKnot.getY());

      // Mettre à jour la poignée si elle existe
      if (copiedKnot.getHandle() != null) {
        copiedKnot.getHandle().setLayoutX(copiedKnot.getHandle().getLayoutX() + currentEvent.getX());
        copiedKnot.getHandle().setLayoutY(copiedKnot.getHandle().getLayoutY() + currentEvent.getY());
      }

      // Mettre à jour le survol s'il existe
      if (copiedKnot.getHovered() != null) {
        copiedKnot.getHovered().setLayoutX(copiedKnot.getX());
        copiedKnot.getHovered().setLayoutY(copiedKnot.getY());
      }
    }

    // Mettre à jour la sélection
    app.getOptionalDotGrid().getDiagram().getCurrentStep().setSelectedKnots(dragKnots);
    if (dragKnots.size() == 1) {
      app.getOptionalDotGrid().getDiagram().setCurrentKnot(dragKnots.get(0));
    }

    event.consume();
  };

  public static final EventHandler<MouseEvent> dragDroppedHandleWithSelectionMode = event -> {
    // Nettoyer les variables de drag
    dragKnots = null;
    previousEvent = null;

    app.getOptionalDotGrid().getDiagram().setCurrentMode(app.getOptionalDotGrid().getDiagram().getOldMode());
    app.getOptionalDotGrid().layoutChildren();

    event.consume();
    Platform.runLater(() ->
            app.getMovablePane().addEventHandler(MouseEvent.MOUSE_CLICKED, GridEvents.getMouseClickEventHandler(app)));
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
        if ((null != app.getOptionalDotGrid().getCurrentPatternProperty().get()) && (null == currentImageView
                || !app.getOptionalDotGrid().getCurrentPatternProperty().get().equals(currentPattern))) {
          currentPattern = app.getOptionalDotGrid().getCurrentPatternProperty().get();
          currentImageView = new ImageView(
                  app.getOptionalDotGrid().getDiagram().getCurrentColor() != null
                          ? new NodeUtil().replaceColoredPixels(
                          new Image(
                                  new File(app.getOptionalDotGrid().getCurrentPatternProperty().get().getAbsoluteFilename())
                                          .toURI().toString()
                          ),
                          app.getOptionalDotGrid().getDiagram().getCurrentColor()
                  )
                          : new Image(
                          new File(app.getOptionalDotGrid().getCurrentPatternProperty().get().getAbsoluteFilename())
                                  .toURI().toString()
                  )
          );
        }

        if (null != currentImageView) {
          Point2D mouseInParent = app.getMovablePane().sceneToLocal(mouseEvent.getSceneX(), mouseEvent.getSceneY());
          Double x = mouseInParent.getX();
          Double y = mouseInParent.getY();

          Point2D coord = app.getGridStrategy().getDrawCoordinates(x, y);
          currentImageView.setLayoutX(coord.getX());
          currentImageView.setLayoutY(coord.getY());
          currentPattern.setCenterX(coord.getX());
          currentPattern.setCenterY(coord.getY());
          app.getMovablePane().getChildren().add(currentImageView);
        }
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
    GridEvents.app = app;
    return gridHoverEventHandler;
  }

  public static EventHandler<MouseEvent> getGridHoverExitEventHandler(App app) {
    GridEvents.app = app;
    return gridHoverExitEventHandler;
  }

  public static EventHandler<MouseEvent> getMouseClickEventHandler(App app) {
    GridEvents.app = app;
    return mouseClickEventHandler;
  }

  public static EventHandler<MouseEvent> getMouseRightClickEventHandler(App app) {
    GridEvents.app = app;
    return mouseRightClickEventHandler;
  }

  public static EventHandler<MouseEvent> getGridDraggedEventHandler(App app) {
    GridEvents.app = app;
    return mouseGridDraggedEventHandler;
  }

  public static EventHandler<MouseEvent> getGridDragReleasedEventHandler(App app) {
    GridEvents.app = app;
    return mouseGridDragReleasedEventHandler;
  }

  public static EventHandler<MouseEvent> getMouseDoubleRightClickOnGridEventHendler(App app) {
    GridEvents.app = app;
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
    GridEvents.currentImageView = currentImageView;
  }

}

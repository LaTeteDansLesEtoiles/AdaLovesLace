package org.alienlabs.adaloveslace.view.component;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.*;
import org.alienlabs.adaloveslace.util.Events;
import org.alienlabs.adaloveslace.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import static org.alienlabs.adaloveslace.App.PATTERNS_DIRECTORY_NAME;
import static org.alienlabs.adaloveslace.util.FileUtil.APP_FOLDER_IN_USER_HOME;
import static org.alienlabs.adaloveslace.util.NodeUtil.HANDLE_SIZE;

/**
 * A grid (= coordinate system) with dots (= used as landmarks for lace).
 */
public class OptionalDotGrid extends Pane {

  private static final double RADIUS    = 0.5d; // The dots are ellipses, this is their radius
  double GRID_WIDTH                     = 1240d;
  double GRID_HEIGHT                    = 600d;
  public static final double TOP_MARGIN = 10d;

  private final SimpleBooleanProperty   showHideGridProperty;
  private final SimpleObjectProperty<Pattern> currentPatternProperty;
  private boolean showHideGrid          = true;
  private boolean gridNeedsToBeRedrawn;

  private double desiredRadius;

  private Diagram diagram;

  private final List<Shape> grid = new ArrayList<>();

  private static final Logger logger = LoggerFactory.getLogger(OptionalDotGrid.class);
  private final Group root;
  private Knot dragOriginKnot;

  private App app;

  /**
   * We draw the dots on the grid using a Canvas.
   *
   * @see Canvas
   *
   */
  public OptionalDotGrid(App app, Diagram diagram, Group root) {
    this.app = app;
    this.root = root;
    this.diagram = Objects.requireNonNullElseGet(diagram, () -> new Diagram(app));

    this.desiredRadius = RADIUS;

    if (!this.diagram.getPatterns().isEmpty()) {
      this.diagram.setCurrentPattern(this.diagram.getPatterns().stream().findFirst().get());
      currentPatternProperty = new SimpleObjectProperty<>(this.diagram.getCurrentPattern());
    } else {
      currentPatternProperty = new SimpleObjectProperty<>();
    }

    currentPatternProperty.addListener(observable -> this.diagram.setCurrentPattern(currentPatternProperty.getValue()));

    showHideGridProperty = new SimpleBooleanProperty(this.showHideGrid);
    showHideGridProperty.addListener(observable -> {
      this.showHideGrid = showHideGridProperty.getValue();
      setNeedsLayout(true);
    });

    this.gridNeedsToBeRedrawn = true;
  }

  public OptionalDotGrid(App app, double width, double height, double desiredRadius, Diagram diagram, Group root) {
    this(app, diagram, root);
    GRID_WIDTH = width;
    GRID_HEIGHT = height;
    this.desiredRadius = desiredRadius;
  }

  @Override
  public void layoutChildren() {
    drawDiagram();

    if (this.gridNeedsToBeRedrawn) {
      drawGrid();
    }
  }

  private void drawDiagram() {
    // We shall not display the undone knots => delete them from canvas, then draw the grid again
    deleteKnotsFromCanvas();

    // If there are knots on the diagram, we must display them at each window refresh
    if (!this.diagram.getAllSteps().isEmpty() && this.diagram.getCurrentStepIndex() >= 0) {
      for (Knot knot : this.diagram.getCurrentStep().getDisplayedKnots()) {
        if (knot.isVisible()) {
          drawDisplayedKnot(knot);
        }
      }

      for (Knot knot : this.diagram.getCurrentStep().getSelectedKnots()) {
        if (knot.isVisible()) {
          drawSelectedKnot(this.diagram.getCurrentStep(), knot);
        }
      }

      drawHoveredOverOrSelectedKnot(this.diagram.getCurrentStep().getAllVisibleKnots());
    }
  }

  // We shall not display the undone knots => delete them from canvas, then draw the grid again
  public void deleteKnotsFromCanvas() {
    this.diagram.deleteNodesFromFollowingSteps(root);
    List<Node> nodeListToRemove = new ArrayList<>();
    Step step = this.diagram.getCurrentStep();

    for (Knot k : step.getSelectedKnots()) {
      if (k.getHovered() != null) {
        nodeListToRemove.add(k.getHovered());
      }
      if (k.getSelection() != null) {
        nodeListToRemove.add(k.getSelection());
      }
      if (k.getHandle() != null) {
        nodeListToRemove.add(k.getHandle());
      }
    }

    Step s = this.diagram.getCurrentStep();
    for (Knot k : s.getAllVisibleKnots()) {
      nodeListToRemove.add(k.getImageView());
    }

    root.getChildren().removeAll(nodeListToRemove);
  }

  public void drawHoveredOverOrSelectedKnot(List<Knot> knots) {
    for (Knot knot : knots) {

      if (!knot.isSelectable()) {
        continue;
      }

      // If selected & hovered: red
      if (knot.isHoveredKnot()
              && getDiagram().getCurrentStep().getSelectedKnots().contains(knot)) {
        Optional<Knot> firstKnot = getDiagram().getCurrentStep().getSelectedKnots().stream()
                .min(Comparator.comparing(Knot::getX)
                        .thenComparing(Knot::getY));

        if (firstKnot.isPresent()) {
          logger.debug("Adding red rectangle for Knot {}", knot);
          addSelectionAndHandleToAKnot(knot, Color.rgb(255, 0, 0, 0.5));
        } else {
          Platform.runLater(() -> {
            Rectangle rec = newRectangle(knot, Color.BLUE);
            knot.setSelection(rec);
            logger.debug("Adding hover {} for Knot {}", rec, knot);
            root.getChildren().add(rec);

            if (knot.getHandle() != null) {
              root.getChildren().remove(knot.getHandle());
            }
          });
        }
      } else if (knot.isHoveredKnot()) {
        // If hovered & not selected: gray
        Platform.runLater(() -> {
          Rectangle rec = newRectangle(knot, Color.GRAY);
          knot.setHovered(rec);
          logger.debug("Adding hover {} for Knot {}", rec, knot);
          root.getChildren().add(rec);

          if (knot.getHandle() != null) {
            root.getChildren().remove(knot.getHandle());
          }
        });
      } else if (getDiagram().getCurrentStep().getSelectedKnots().contains(knot)) {
        // If not hovered & selected: blue
        Optional<Knot> firstKnot = getDiagram().getCurrentStep().getSelectedKnots().stream()
                .min(Comparator.comparing(Knot::getX)
                        .thenComparing(Knot::getY));

        if (firstKnot.isPresent()) {
          Platform.runLater(() -> addSelectionAndHandleToAKnot(knot, Color.rgb(0, 0, 255, 0.5)));
        } else {
          Platform.runLater(() -> {
            Rectangle rec = newRectangle(knot, Color.BLUE);
            knot.setSelection(rec);
            logger.debug("Adding hover {} for Knot {}", rec, knot);
            root.getChildren().add(rec);

            if (knot.getHandle() != null) {
              root.getChildren().remove(knot.getHandle());
            }
          });
        }
      } else if (!knot.isHoveredKnot()
              && knot.getHovered() != null) {
        Platform.runLater(() -> {
          logger.debug("Removing node {} and hover {} from hovered {}", knot, knot.getHovered(), root.getChildren().remove(knot.getHovered()));
          knot.setHovered(null);
          layoutChildren();
        });
      }
    }
  }

  public void addSelectionAndHandleToAKnot(Knot knot, Color rgba) {
    Platform.runLater(() -> {
      Rectangle rec = newRectangle(knot, rgba);
      rec.addEventHandler(MouseEvent.MOUSE_MOVED, Events.getGridHoverEventHandler(app));

      knot.setHovered(rec);
      knot.setSelection(rec);
      root.getChildren().add(rec);

      // Only the first knot of a multi-selection has a handle
      getDiagram().deleteHandlesFromCurrentStep(getRoot());

      Circle handle = newHandle(knot, Color.rgb(0,0,255, 0.3), rec);
      knot.setHandle(handle);
      root.getChildren().add(handle);

      handle.setOnDragDetected(Events.getDragInitiatedOverHandleEventHandler());
      handle.setOnDragOver(Events.getMouseDragOverHandleEventHandler());
      handle.setOnDragDropped(Events.getMouseDragDroppedHandleEventHandler());
    });
  }

  // The handle is the top left corner of the rectangle of the zoomed, rotated knot
  // @see https://stackoverflow.com/questions/41898990/find-corners-of-a-rotated-rectangle-given-its-center-point-and-rotation
  // And invert "TOP LEFT VERTEX:" & "BOTTOM LEFT VERTEX:" (small error from the author)
  private Circle newHandle(Knot knot, Color handleColor, Rectangle rec) {
    Circle circle = new Circle(
            knot.getImageView().getBoundsInParent().getCenterX() -
                    (knot.getPattern().getWidth() / 2 * rec.getScaleX()) *
                            Math.cos(Math.toRadians(knot.getRotationAngle())) +
                    (knot.getPattern().getHeight() / 2 * rec.getScaleY()) *
                            Math.sin(Math.toRadians(knot.getRotationAngle())),
            knot.getImageView().getBoundsInParent().getCenterY() -
                    (knot.getPattern().getWidth() / 2 * rec.getScaleX()) *
                            Math.sin(Math.toRadians(knot.getRotationAngle())) -
                    (knot.getPattern().getHeight() / 2 * rec.getScaleY()) *
                            Math.cos(Math.toRadians(knot.getRotationAngle())),
            HANDLE_SIZE * computeZoomFactor(knot),
            handleColor);
    circle.setId(UUID.randomUUID().toString());
    return circle;
  }

  private Rectangle newRectangle(Knot knot, Color color) {
    Rectangle rec = new Rectangle(
            knot.getX(),
            knot.getY(),
            knot.getPattern().getWidth(),
            knot.getPattern().getHeight()
    );
    rec.setId(UUID.randomUUID().toString());
    rec.setStroke(color);
    rec.setStrokeWidth(2d);
    rec.setFill(Color.TRANSPARENT);
    rec.setScaleX(computeZoomFactor(knot));
    rec.setScaleY(computeZoomFactor(knot));
    rec.setRotate(knot.getRotationAngle());

    return rec;
  }

  public void drawGuideLines(final Step step, final Knot knot) {
    clearGuideLines(knot);

    Platform.runLater(() -> {
      if ((diagram.getCurrentMode() == MouseMode.SELECTION) || (diagram.getCurrentMode() == MouseMode.DELETION)
              || (diagram.getCurrentMode() == MouseMode.MOVE) || (diagram.getCurrentMode() == MouseMode.DRAWING)
              || (diagram.getCurrentMode() == MouseMode.DUPLICATION)) {

        // The black, thick lines that we use as guides
        getDiagram().deleteNodesFromFollowingSteps(root);

        for (Knot otherKnot : step.getAllVisibleKnots()) {
          if (!otherKnot.equals(knot) && otherKnot.isVisible()) {
            new GuideLinesUtil(knot, otherKnot, root);
          }
        }
      }
    });
  }

  public void clearGuideLines(final Knot knot) {
    root.getChildren().removeAll(knot.getGuideLines());
    knot.getGuideLines().clear();
  }

  public void clearSelections() {
    getDiagram().getCurrentStep().getSelectedKnots().stream().forEach(knot -> root.getChildren().remove(knot.getSelection()));
  }

  public void clearHovered() {
    getDiagram().getCurrentStep().getSelectedKnots().stream().forEach(knot -> root.getChildren().remove(knot.getHovered()));
  }

  public void clearHandles() {
    getDiagram().getCurrentStep().getSelectedKnots().stream().forEach(knot -> root.getChildren().remove(knot.getHandle()));
  }

  public void clearAllKnotDecorations() {
    clearSelections();
    clearHovered();
    clearAllGuideLines();
    clearHandles();
  }

  public void clearKnotSelections() {
    for (Knot knot : diagram.getCurrentStep().getSelectedKnots()) {
      if (knot.getSelection() != null) {
        root.getChildren().remove(knot.getSelection());
      }

      knot.setSelection(null);
    }
  }

  public void clearKnotHandles() {
    for (Knot knot : diagram.getCurrentStep().getSelectedKnots()) {
      if (knot.getHandle() != null) {
        root.getChildren().remove(knot.getHandle());
      }

      knot.setHandle(null);
    }
  }

  public void clearAllGuideLines() {
    for (Knot knot : getDiagram().getCurrentStep().getSelectedKnots()) {
      clearGuideLines(knot);
    }
  }

  private void drawDisplayedKnot(Knot knot) {
    ImageView iv = rotateKnot(knot);
    zoomAndFlipKnot(knot);

    double x = knot.getX();
    double y = knot.getY();

    iv.setX(x);
    iv.setY(y);

    knot.setImageView(iv);

    logger.debug("drawing top left corner of knot {} to ({},{})", knot.getPattern().getFilename(), x, y);
  }

  private void drawSelectedKnot(Step step, Knot knot) {
    ImageView iv = rotateKnot(knot);
    zoomAndFlipKnot(knot);

    double x = knot.getX();
    double y = knot.getY();

    iv.setX(x);
    iv.setY(y);
    knot.setImageView(iv);

    drawGuideLines(step, knot);

    logger.debug("drawing top left corner of knot {} to ({},{})", knot.getPattern().getFilename(), x, y);
  }

  // Zoom factor goes from -10 to 10, 0 being don't zoom knot, < 0 being shrink knot, > 0 being enlarge knot
  public double zoomAndFlipKnot(Knot knot) {
    flip(knot.isFlippedVertically(), Rotate.Y_AXIS, knot);
    flip(knot.isFlippedHorizontally(), Rotate.X_AXIS, knot);

    return zoom(knot);
  }

  private double zoom(Knot knot) {
    double scaleFactor = computeZoomFactor(knot);
    knot.getImageView().setScaleX(scaleFactor);
    knot.getImageView().setScaleY(scaleFactor);

    logger.debug("zoomed knot {} at zoom factor {} and scale factor {}",
            knot.getPattern().getFilename(), knot.getZoomFactor(), scaleFactor);

    return scaleFactor;
  }
  private void flip(boolean flip, Point3D axis, Knot knot) {
    Rotate rot = new Rotate();
    rot.setAxis(axis);
    rot.setAngle(flip ? 180d : 0d);
    rot.setPivotX(knot.getX() + knot.getPattern().getCenterX());
    rot.setPivotY(knot.getY() + knot.getPattern().getCenterY());

    knot.getImageView().getTransforms().add(rot);
  }

  public double computeZoomFactor(double factor) {
    if (factor == 0) {
      return 1;
    } else {
      return (10d + factor) / 10d;
    }
  }

  public double computeZoomFactor(Knot knot) {
    return computeZoomFactor(knot.getZoomFactor());
  }

  // Rotate knot with an angle in degrees
  private ImageView rotateKnot(Knot knot) {
    if (knot.getImageView() == null) {
      try (FileInputStream fis = new FileInputStream(APP_FOLDER_IN_USER_HOME + PATTERNS_DIRECTORY_NAME + File.separator
              + knot.getPattern().getFilename())) {
        new FileUtil().buildKnotImageView(knot, fis);
      } catch (IOException e) {
        logger.error("Problem with pattern resource file!", e);
      }
    }

    knot.getImageView().getTransforms().clear();
    knot.getImageView().setRotate(knot.getRotationAngle());

    if (!root.getChildren().contains(knot.getImageView())) {
      root.getChildren().add(knot.getImageView());
    }

    logger.debug("rotated knot {} at angle {}", knot.getPattern().getFilename(), knot.getRotationAngle());

    return knot.getImageView();
  }

  private void drawGrid() {
    double top = (int) snappedTopInset() + TOP_MARGIN;
    double right = (int) snappedRightInset();
    double bottom = (int) snappedBottomInset();
    double left = (int) snappedLeftInset();
    double width = (int) getWidth() - left - right;
    double height = (int) getHeight() - top - bottom - 20d;
    root.setLayoutX(left);
    root.setLayoutY(top);

    if (this.showHideGrid && this.gridNeedsToBeRedrawn) {
      this.diagram.drawGrid(width, height, desiredRadius, grid);
    } else {
      hideGrid();
    }

    this.gridNeedsToBeRedrawn = false;
  }

  public void hideGrid() {
    for (Shape shape : grid) {
      root.getChildren().remove(shape);
    }
  }

  public void setGridNeedsToBeRedrawn(boolean gridNeedsToBeRedrawn) {
    this.gridNeedsToBeRedrawn = gridNeedsToBeRedrawn;
  }

  @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
          value = "EI_EXPOSE_REP",
          justification = "A JavaFX property is meant to be modified from the outside")
  public SimpleObjectProperty<Pattern> getCurrentPatternProperty() {
    return this.currentPatternProperty;
  }

  public Diagram getDiagram() {
    return this.diagram;
  }

  public void setDiagram(Diagram diagram) {
    this.diagram = diagram;
  }

  @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
          value = "EI_EXPOSE_REP",
          justification = "A JavaFX property is meant to be modified from the outside")
  public SimpleBooleanProperty isShowHideGridProperty() {
    return this.showHideGridProperty;
  }

  public boolean isShowHideGrid() {
    return this.showHideGrid;
  }

  public void setShowHideGrid(boolean showHideGrid) {
    this.showHideGrid = showHideGrid;
  }

  public Group getRoot() {
    return this.root;
  }

  public Knot getDragOriginKnot() {
    return this.dragOriginKnot;
  }

  public void setDragOriginKnot(Knot dragOriginKnot) {
    this.dragOriginKnot = dragOriginKnot;
  }

}

package org.alienlabs.adaloveslace.view.component;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import org.alienlabs.adaloveslace.business.model.*;
import org.alienlabs.adaloveslace.util.Events;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import static java.util.Objects.requireNonNullElseGet;
import static org.alienlabs.adaloveslace.App.PATTERNS_DIRECTORY_NAME;
import static org.alienlabs.adaloveslace.util.FileUtil.APP_FOLDER_IN_USER_HOME;
import static org.alienlabs.adaloveslace.util.NodeUtil.HANDLE_SIZE;

/**
 * A grid (= coordinate system) with dots (= used as landmarks for lace).
 */
public class OptionalDotGrid extends Pane {

  public static final Color GRID_COLOR  = Color.gray(0d, 0.2d);
  private static final double RADIUS    = 0.5d; // The dots are ellipses, this is their radius
  double GRID_WIDTH                     = 1240d;
  double GRID_HEIGHT                    = 600d;
  public static final double TOP_MARGIN = 10d;

  private static final double SPACING_X = 25d; // The X space between the dots
  private static final double SPACING_Y = 10d; // The Y space between the dots

  private final SimpleBooleanProperty   showHideGridProperty;
  private final SimpleObjectProperty<Pattern> currentPatternProperty;
  private final SimpleObjectProperty<Diagram> diagramProperty;
  private boolean showHideGrid          = true;
  private boolean gridNeedsToBeRedrawn;

  private double desiredRadius;

  private Diagram diagram;

  private final Set<Shape> grid = new HashSet<>();

  private static final Logger logger = LoggerFactory.getLogger(OptionalDotGrid.class);
  private final Group root;
  private final Set<Knot> allHoveredKnots = new TreeSet<>();

  private Knot dragOriginKnot;

  /**
   * We draw the dots on the grid using a Canvas.
   *
   * @see Canvas
   *
   */
  public OptionalDotGrid(Diagram diagram, Group root) {
    this.root = root;
    this.diagram = requireNonNullElseGet(diagram, Diagram::new);

    this.desiredRadius = RADIUS;

    if (!this.diagram.getPatterns().isEmpty()) {
      this.diagram.setCurrentPattern(this.diagram.getPatterns().get(0));
      currentPatternProperty = new SimpleObjectProperty<>(this.diagram.getCurrentPattern());
    } else {
      currentPatternProperty = new SimpleObjectProperty<>();
    }

    diagramProperty = new SimpleObjectProperty<>(this.diagram);
    diagramProperty.addListener(observable -> this.setDiagram(diagramProperty.getValue()));
    currentPatternProperty.addListener(observable -> this.diagram.setCurrentPattern(currentPatternProperty.getValue()));

    showHideGridProperty = new SimpleBooleanProperty(this.showHideGrid);
    showHideGridProperty.addListener(observable -> {
      this.showHideGrid = showHideGridProperty.getValue();
      setNeedsLayout(true);
    });

    this.gridNeedsToBeRedrawn = true;
  }

  public OptionalDotGrid(double width, double height, double desiredRadius, Diagram diagram, Group root) {
    this(diagram, root);
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

    Platform.runLater(() -> {
      // If there are knots on the diagram, we must display them at each window refresh
      if (!this.diagram.getAllSteps().isEmpty() && this.diagram.getCurrentStepIndex() >= 0) {
        for (Knot knot : this.diagram.getCurrentStep().getDisplayedKnots()) {
          if (knot.isVisible()) {
            drawDisplayedKnot(knot);

            if ((diagram.getCurrentMode() == MouseMode.SELECTION) || (diagram.getCurrentMode() == MouseMode.MOVE) || (diagram.getCurrentMode() == MouseMode.DELETION)) {
              drawHoveredOverKnot(knot);
            }
          }
        }

        for (Knot knot : this.diagram.getCurrentStep().getSelectedKnots()) {
          if (knot.isVisible()) {
            drawSelectedKnot(this.diagram.getCurrentStep(), knot);
          }
        }

        drawHoveredOverOrSelectedKnot(this.diagram.getCurrentStep().getSelectedKnots());
      }
    });
  }

  // We shall not display the undone knots => delete them from canvas, then draw the grid again
  public void deleteKnotsFromCanvas() {
    this.diagram.deleteNodesFromCurrentStep(root);

    ArrayList<Node> nodeListToRemove    = new ArrayList<>();
    ArrayList<Knot> knotListToRemove    = new ArrayList<>();

    for (Knot knot : this.getDiagram().getCurrentStep().getDisplayedKnots()) {
      nodeListToRemove.add(knot.getImageView());
      knotListToRemove.add(knot);
    }

    diagram.getKnots().removeAll(knotListToRemove);
    logger.debug("removed? {}", root.getChildren().removeAll(nodeListToRemove));
  }

  public void drawHoveredOverKnot(Knot knot) {
    if (knot.getSelection() != null) {
      root.getChildren().remove(knot.getSelection());
    }

    if (this.diagram.getCurrentStep().getSelectedKnots().contains(knot)) {
      Rectangle rec = newRectangle(knot, Color.GRAY);

      root.getChildren().add(rec);
      knot.setSelection(rec);
    }

  }

  public void drawHoveredOverOrSelectedKnot(Set<Knot> knots) {
    Platform.runLater(() -> {
      for (Knot knot : knots) {

        // If selected & hovered: red
        if (knot.isHoveredKnot() &&
                getDiagram().getCurrentStep().getSelectedKnots().contains(knot)) {
          Knot firstKnot = getDiagram().getCurrentStep().getSelectedKnots().stream()
                  .min(Comparator.comparing(Knot::getX)
                          .thenComparing(Knot::getY))
                  .get();
          addSelectionAndHandleToAKnot(knot, Color.rgb(255, 0, 0, 0.5), firstKnot);
        } else if (knot.isHoveredKnot()) {
          // If hovered & not selected: gray
          Rectangle rec = newRectangle(knot, Color.GRAY);

          knot.setHovered(rec);
          root.getChildren().add(rec);

          if (knot.getHandle() != null) {
            root.getChildren().remove(knot.getHandle());
          }
        }

        if (!knot.isHoveredKnot() && getDiagram().getCurrentStep().getSelectedKnots().contains(knot)) {
          // If not hovered & selected: blue
          Knot firstKnot = getDiagram().getCurrentStep().getSelectedKnots().stream()
                  .min(Comparator.comparing(Knot::getX)
                          .thenComparing(Knot::getY))
                  .get();
          addSelectionAndHandleToAKnot(knot, Color.rgb(0,0,255, 0.5), firstKnot);
        } else if (!knot.isHoveredKnot()) {
          logger.debug("Removing from selection {}", root.getChildren().remove(knot.getSelection()));
          logger.debug("Removing from hovered {}", root.getChildren().remove(knot.getHovered()));
          logger.debug("Removing handle {}", root.getChildren().remove(knot.getHandle()));
          knot.setSelection(null);
          knot.setHovered(null);
          knot.setHandle(null);
        }
      }
    });
  }

  public void addSelectionAndHandleToAKnot(Knot knot, Color rgba, Knot firstKnot) {
    Rectangle rec = newRectangle(knot, rgba);

    knot.setHovered(rec);
    knot.setSelection(rec);
    root.getChildren().add(rec);

    // Only the first knot of a multi-selection has a handle
    if (firstKnot.equals(knot)) {
      getDiagram().deleteHandlesFromCurrentStep(getRoot());

      Circle handle = newHandle(knot, rgba, rec);
      knot.setHandle(handle);
      root.getChildren().add(handle);

      handle.setOnDragDetected(Events.getDragInitiatedOverHandleEventHandler());
      handle.setOnDragOver(Events.getMouseDragOverHandleEventHandler());
      handle.setOnDragDropped(Events.getMouseDragDroppedHandleEventHandler());
    }
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

  private Rectangle newRectangle(Knot knot, Color notHoveredAndSelected) {
    Rectangle rec = new Rectangle(knot.getX(), knot.getY(), knot.getPattern().getWidth(), knot.getPattern().getHeight());
    rec.setStroke(notHoveredAndSelected);
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
        getDiagram().deleteNodesFromCurrentStep(root);

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
    for (Knot knot : getDiagram().getKnots()) {
      clearGuideLines(knot);
    }
  }

  public Set<Knot> getAllHoveredKnots() {
    return this.allHoveredKnots;
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

    if (knot.getImageView() != null) {
      Scale scale = new Scale(scaleFactor, scaleFactor);
      scale.setPivotX(knot.getImageView().getX() + knot.getPattern().getWidth() / 2d);
      scale.setPivotY(knot.getImageView().getY() + knot.getPattern().getHeight() / 2d);

      knot.getImageView().getTransforms().add(scale);

      logger.debug("zoomed knot {} at zoom factor {} and scale factor {}",
        knot.getPattern().getFilename(), knot.getZoomFactor(), scaleFactor);
    } else {
      logger.debug("not zoomed knot {} because it does not have an ImageView", knot.getPattern().getFilename());
    }


    return scaleFactor;
  }
  private void flip(boolean flip, Point3D axis, Knot knot) {
    Rotate rot = new Rotate();
    rot.setAxis(axis);
    rot.setAngle(flip ? 180d : 0d);
    rot.setPivotX(knot.getImageView().getX() + knot.getPattern().getWidth() / 2d);
    rot.setPivotY(knot.getImageView().getY() + knot.getPattern().getHeight() / 2d);

    knot.getImageView().getTransforms().add(rot);

    Translate translate = new Translate(0d, 0d, knot.getPattern().getWidth() / 2.0);
    knot.getImageView().getTransforms().add(translate);
  }

  public double computeZoomFactor(double factor) {
    if (factor == 0) {
      return 1;
    } else {
      if (factor > 0) {
        return (10d + factor) / 10d;
      }
      return (20d + factor) / 20d;
    }
  }

  public double computeZoomFactor(Knot knot) {
    return computeZoomFactor(knot.getZoomFactor());
  }

  // Rotate knot with an angle in degrees
  private ImageView rotateKnot(Knot knot) {
    if (!root.getChildren().contains(knot.getImageView())) {
      root.getChildren().add(knot.getImageView());
    }

    knot.getImageView().getTransforms().clear();

    Rotate rot = new Rotate(knot.getRotationAngle(), Rotate.Z_AXIS);
    rot.setPivotX(knot.getImageView().getX() + knot.getPattern().getWidth() / 2d);
    rot.setPivotY(knot.getImageView().getY() + knot.getPattern().getHeight() / 2d);

    knot.getImageView().getTransforms().add(rot);
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
      drawGrid(width, height);
    } else {
      hideGrid();
    }

    this.gridNeedsToBeRedrawn = false;
  }

  private void hideGrid() {
    for (Shape shape : grid) {
      root.getChildren().remove(shape);
    }
  }

  private void drawGrid(double w, double h) {
    hideGrid();

    for (double x = 40d; x < (w - 185d); x += SPACING_X) {
      for (double y = 60d; y < (h - 50d); y += SPACING_Y) {
        double offsetY = (y % (2d * SPACING_Y)) == 0d ? SPACING_X / 2d : 0d;
        Ellipse ell = new Ellipse(x - this.desiredRadius + offsetY,y - this.desiredRadius,this.desiredRadius,this.desiredRadius); // A dot
        ell.setFill(GRID_COLOR);
        ell.toFront();

        grid.add(ell);
        root.getChildren().add(ell);
      }
    }
  }

  public void drawKnot(double x, double y) {
    Pattern currentPattern = this.diagram.getCurrentPattern();
    logger.info("Current pattern  -> {}", currentPattern);
    Knot currentKnot = null;

    try (FileInputStream fis = new FileInputStream(new File(APP_FOLDER_IN_USER_HOME + PATTERNS_DIRECTORY_NAME, currentPattern.getFilename()))) {
      Image image = new Image(fis);
      ImageView iv = new ImageView(image);

      iv.setX(x);
      iv.setY(y);
      iv.setRotate(0d);

      logger.debug("Top left corner of the knot {} is ({},{})", currentPattern.getFilename(), x, y);

      root.getChildren().add(iv);
      currentKnot = new Knot(x, y, currentPattern, iv);
      diagram.setCurrentKnot(currentKnot);

      this.diagram.addKnotWithStep(currentKnot);
    } catch (IOException e) {
      logger.error("Problem with pattern resource file!", e);
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

  @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
    value = "EI_EXPOSE_REP",
    justification = "A JavaFX property is meant to be modified from the outside")
  public SimpleObjectProperty<Diagram> getDiagramProperty() {
    return this.diagramProperty;
  }

  public Diagram getDiagram() {
    return this.diagram;
  }

  /** Use JavaFX property
   * @see OptionalDotGrid#getDiagramProperty()
   * */
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

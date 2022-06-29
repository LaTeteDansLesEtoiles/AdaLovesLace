package org.alienlabs.adaloveslace.view.component;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Shape;
import org.alienlabs.adaloveslace.business.model.Diagram;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.business.model.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.requireNonNullElseGet;
import static org.alienlabs.adaloveslace.view.window.GeometryWindow.ZOOM_SPINNER_MULTIPLY_FACTOR;
import static org.alienlabs.adaloveslace.view.window.GeometryWindow.ZOOM_SPINNER_ZOOM_FACTOR;

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
  private SimpleObjectProperty<Pattern> currentPatternProperty;
  private SimpleObjectProperty<Diagram> diagramProperty;
  private boolean showHideGrid          = true;

  private double desiredRadius;

  private Diagram diagram;

  private final Set<Shape> grid = new HashSet<>();

  private static final Logger logger = LoggerFactory.getLogger(OptionalDotGrid.class);
  private final Group root;

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
      currentPatternProperty.addListener(observable -> this.diagram.setCurrentPattern(currentPatternProperty.getValue()));
      diagramProperty = new SimpleObjectProperty<>(this.diagram);
      diagramProperty.addListener(observable -> this.setDiagram(diagramProperty.getValue()));
    }

    showHideGridProperty = new SimpleBooleanProperty(this.showHideGrid);
    showHideGridProperty.addListener(observable -> {
      this.showHideGrid = showHideGridProperty.getValue();
      setNeedsLayout(true);
    });
  }

  public OptionalDotGrid(double width, double height, double desiredRadius, Diagram diagram, Group root) {
    this(diagram, root);
    GRID_WIDTH = width;
    GRID_HEIGHT = height;
    this.desiredRadius = desiredRadius;
  }

  @Override
  public void layoutChildren() {
    initGrid();
    drawDiagram();
  }

  private void drawDiagram() {
    // We whall not display the undone knots => delete them from canvas, then draw the grid again
    deleteKnotsFromCanvas();

    // If there are knots on the diagram, we must display them at each window refresh
    for (Knot knot : this.diagram.getKnots().subList(0, this.diagram.getCurrentKnotIndex())) {
      drawKnotWithRotationAndZoom(knot);
    }
  }

  // We whall not display the undone knots => delete them from canvas, then draw the grid again
  private void deleteKnotsFromCanvas() {
    if (!this.diagram.getKnots().isEmpty()) {
      for (Knot knot : this.diagram.getKnots().subList(this.diagram.getCurrentKnotIndex(), this.diagram.getKnots().size())) {
        deleteKnotFromCanvas(knot);
      }
    }
  }

  private void deleteKnotFromCanvas(Knot knot) {
    root.getChildren().remove(knot.getImageView());
  }

  private void drawKnotWithRotationAndZoom(Knot knot) {
    ImageView iv = rotateKnot(knot);
    zoomKnot(knot, iv);

    double x = knot.getX();
    double y = knot.getY();
    logger.info("drawing top left corner of knot {} to ({},{})", knot.getPattern().getFilename(), x, y);
  }

  // Zoom factor goes from -10 to 10, 0 being don't zoom knot, < 0 being shrink knot, > 0 being enlarge knot
  private void zoomKnot(Knot knot, ImageView iv) {
    if (knot.getZoomFactor() != 0) {
      iv.setScaleX((knot.getZoomFactor() + ZOOM_SPINNER_ZOOM_FACTOR) * ZOOM_SPINNER_MULTIPLY_FACTOR);
      iv.setScaleY((knot.getZoomFactor() + ZOOM_SPINNER_ZOOM_FACTOR) * ZOOM_SPINNER_MULTIPLY_FACTOR);
    }
  }

  // Rotate knot with an angle in degrees
  private ImageView rotateKnot(Knot knot) {
    if (!root.getChildren().contains(knot.getImageView())) {
      root.getChildren().add(knot.getImageView());
    }

    knot.getImageView().setOpacity(1.0d);
    knot.getImageView().setRotate(knot.getRotationAngle());
    logger.info("rotating knot {} at angle {}", knot.getPattern().getFilename(), knot.getRotationAngle());

    return knot.getImageView();
  }

  private void initGrid() {
    double top = (int) snappedTopInset() + TOP_MARGIN;
    double right = (int) snappedRightInset();
    double bottom = (int) snappedBottomInset();
    double left = (int) snappedLeftInset();
    double width = (int) getWidth() - left - right;
    double height = (int) getHeight() - top - bottom - 20d;
    root.setLayoutX(left);
    root.setLayoutY(top);

    if (this.showHideGrid) {
      drawGrid(width, height);
    } else {
      hideGrid();
    }
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

        grid.add(ell);
        root.getChildren().add(ell);
      }
    }
  }

  public Knot addKnot(double centerX, double centerY) {
    Pattern currentPattern = this.diagram.getCurrentPattern();
    logger.info("Current pattern  -> {}", currentPattern);
    Knot currentKnot = null;

    try (FileInputStream fis = new FileInputStream(currentPattern.getAbsoluteFilename())) {
      Image image = new Image(fis);
      ImageView iv = new ImageView(image);

      double cornerX = centerX - currentPattern.getCenterX();
      double cornerY = centerY - currentPattern.getCenterY();
      iv.setX(cornerX);
      iv.setY(cornerY);
      iv.setRotate(0d);
      iv.setOpacity(1.0d);

      logger.info("Top left corner of the knot {} is ({},{})", currentPattern.getFilename(), cornerX, cornerY);
      logger.info("Center of the knot {} is ({},{})", currentPattern.getFilename(), centerX, centerY);

      root.getChildren().add(iv);
      currentKnot = new Knot(cornerX, cornerY, currentPattern, iv);
      this.diagram.addKnot(currentKnot);
      layoutChildren();
    } catch (IOException e) {
      logger.error("Problem with pattern resource file!", e);
    }

    return currentKnot;
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
  private void setDiagram(Diagram diagram) {
    this.diagram = diagram;
  }

  @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
    value = "EI_EXPOSE_REP",
    justification = "A JavaFX property is meant to be modified from the outside")
  public SimpleBooleanProperty isShowHideGridProperty() {
    return this.showHideGridProperty;
  }

}

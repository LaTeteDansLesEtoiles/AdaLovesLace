package org.alienlabs.adaloveslace.view.component;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.alienlabs.adaloveslace.business.model.Diagram;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.business.model.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * A grid (= coordinate system) with dots (= used as landmarks for lace).
 */
public class CanvasWithOptionalDotGrid extends Pane {

  public static final Color GRID_COLOR  = Color.gray(0d, 0.2d);
  private static final double RADIUS    = 2.5d; // The dots are ellipses, this is their radius
  double CANVAS_WIDTH                   = 1200d;
  double CANVAS_HEIGHT                  = 700d;
  public static final double TOP_MARGIN = 10d;

  private static final double SPACING_X = 25d; // The X space between the dots
  private static final double SPACING_Y = 10d; // The Y space between the dots

  private final SimpleBooleanProperty   showHideGridProperty;
  private SimpleObjectProperty<Pattern> currentPatternProperty;
  private SimpleObjectProperty<Diagram> diagramProperty;
  private boolean showHideGrid          = true;
  private double radius;

  private final Canvas canvas           = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT); // We draw the dots on the grid using a Canvas
  private final GraphicsContext graphicsContext2D;
  private Diagram diagram;

  private double top;
  private double right;
  private double bottom;
  private double left;
  private double width;
  private double height;

  private static final Logger logger = LoggerFactory.getLogger(CanvasWithOptionalDotGrid.class);

  /**
   * We draw the dots on the grid using a Canvas.
   *
   * @see Canvas
   *
   */
  public CanvasWithOptionalDotGrid(Diagram diagram) {
    if (diagram == null) {
      this.diagram = new Diagram();
    } else {
      this.diagram = diagram;
    }

    this.radius     = RADIUS;

    // TODO: display an error message if there is no pattern in the toolbox
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

    this.graphicsContext2D = this.canvas.getGraphicsContext2D();
    getChildren().addAll(this.canvas);
  }

  public CanvasWithOptionalDotGrid(double width, double height, double radius, Diagram diagram) {
    this(diagram);
    CANVAS_WIDTH = width;
    CANVAS_HEIGHT = height;
    this.radius = radius;
  }

  @Override
  public void layoutChildren() {
    initCanvasAndGrid();
    drawDiagram();
  }

  private void drawDiagram() {
    // We whall not display the undone knots => delete them, then draw the grid again
    if (!this.diagram.getKnots().isEmpty()) {
      for (Knot knot : this.diagram.getKnots().subList(this.diagram.getCurrentKnotIndex(), this.diagram.getKnots().size())) {
        try (FileInputStream fis = new FileInputStream(knot.getPattern().getAbsoluteFilename())) {
          Image image = new Image(fis);
          graphicsContext2D.clearRect(knot.getX(), knot.getY(),
            image.getWidth(), image.getHeight());
        } catch (IOException e) {
          logger.error("Problem with resource file!", e);
        }

        initCanvasAndGrid();
      }
    }

    // If there are knots on the diagram, we must display them at each window refresh
    for (Knot knot : this.diagram.getKnots().subList(0, this.diagram.getCurrentKnotIndex())) {

      try (FileInputStream fis = new FileInputStream(knot.getPattern().getAbsoluteFilename())) {
        Image image = new Image(fis);
        ImageView iv = new ImageView(image);
        iv.setRotate(knot.getRotationAngle());

        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        Image rotatedImage = iv.snapshot(params, null);

        graphicsContext2D.drawImage(rotatedImage, knot.getX(), knot.getY());

      } catch (IOException e) {
        logger.error("Problem with resource file!", e);
      }
    }


    // If there is no knot on the diagram, we must display a blank diagram
    if (this.diagram.getKnots() == null || this.diagram.getKnots().isEmpty()) {
        graphicsContext2D.rect(0d, 0d, CANVAS_WIDTH, CANVAS_HEIGHT);
    }
  }

  private void initCanvasAndGrid() {
    top     = (int)snappedTopInset() + TOP_MARGIN;
    right   = (int)snappedRightInset();
    bottom  = (int)snappedBottomInset();
    left    = (int)snappedLeftInset();
    width   = (int)getWidth() - left - right;
    height  = (int)getHeight() - top - bottom - 20d;
    this.canvas.setLayoutX(left);
    this.canvas.setLayoutY(top);

    if (width != this.canvas.getWidth() || height != this.canvas.getHeight()) {
      this.canvas.setWidth(width);
      this.canvas.setHeight(height);
    }

    fillEmptyRectangle();

    if (this.showHideGrid) {
      drawGrid(width, height);
    }
  }

  private void fillEmptyRectangle() {
    this.graphicsContext2D.clearRect(0d, 0d, width, height);
    this.graphicsContext2D.setFill(new Color(1.0d, 1.0d, 1.0d, 0.9d));
    this.graphicsContext2D.fillRect(40d, 40d, width - 87d, height - 70d);

    this.graphicsContext2D.setFill(GRID_COLOR);
  }

  private void drawGrid(double w, double h) {
    for (double x = 40d; x < (w - 40d); x += SPACING_X) {
      for (double y = 40d; y < (h - 20d); y += SPACING_Y) {
        double offsetY = (y % (2d * SPACING_Y)) == 0d ? SPACING_X / 2d : 0d;
        this.graphicsContext2D.fillOval(x - this.radius + offsetY,y - this.radius,this.radius * 2,this.radius * 2); // A dot
      }
    }
  }

  public Knot addKnot(double x, double y) {
    Pattern currentPattern = this.diagram.getCurrentPattern();
    logger.info("Current pattern  -> {}", currentPattern);
    Knot currentKnot = null;

    try (FileInputStream fis = new FileInputStream(currentPattern.getAbsoluteFilename())) {
      this.canvas.getGraphicsContext2D().drawImage(new Image(fis), x, y);
      currentKnot = new Knot(x, y, currentPattern);
      this.diagram.addKnot(currentKnot);
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
   * @see CanvasWithOptionalDotGrid#getDiagramProperty()
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

  @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
    value = "EI_EXPOSE_REP",
    justification = "The canvas can not be copied at will (because the copy would be asynchronous), so we are forced to reuse the same one")
  public Canvas getCanvas() {
    return this.canvas;
  }

}

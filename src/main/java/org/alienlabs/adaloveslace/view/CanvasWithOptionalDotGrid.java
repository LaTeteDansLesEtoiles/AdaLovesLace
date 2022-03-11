package org.alienlabs.adaloveslace.view;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
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

  private SimpleObjectProperty<Pattern> currentPatternProperty;

  static final double TOP_MARGIN        = 30d;

  private static final double SPACING_X = 25d; // The X space between the dots
  private static final double SPACING_Y = 10d; // The Y space between the dots
  private static final double RADIUS    = 2.5d;// The dots are ellipses, this is their radius

  private final Canvas canvas           = new Canvas(1200d, 700d); // We draw the dots on the grid using a Canvas
  private final Diagram diagram;
  private double top;
  private double right;
  private double bottom;
  private double left;
  private double width;
  private double height;
  private GraphicsContext graphicsContext2D;

  private static final Logger logger = LoggerFactory.getLogger(CanvasWithOptionalDotGrid.class);

  /**
   * We draw the dots on the grid using a Canvas.
   *
   * @see Canvas
   *
   */
  public CanvasWithOptionalDotGrid(Diagram diagram) {
    this.diagram = new Diagram(diagram);

    // TODO: display an error message if there is no pattern in the toolbox
    if (!this.diagram.getPatterns().isEmpty()) {
      this.diagram.setCurrentPattern(this.diagram.getPatterns().get(0));

      currentPatternProperty = new SimpleObjectProperty<>(this.diagram.getCurrentPattern());
      currentPatternProperty.addListener(observable -> this.diagram.setCurrentPattern(currentPatternProperty.getValue()));
    }

    getChildren().addAll(this.canvas);
  }

  @Override
  protected void layoutChildren() {
    drawGrid();
    drawCanvas();
  }

  private void drawCanvas() {
    for (Knot knot : this.diagram.getKnots()) {

      try (FileInputStream fis = new FileInputStream(knot.getPattern().filename())) {
        graphicsContext2D.drawImage(new Image(fis), knot.getX(), knot.getY());

      } catch (IOException e) {
        logger.error("Problem with resource file!", e);
      }
    }
  }

  private void drawGrid() {
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

      this.graphicsContext2D = this.canvas.getGraphicsContext2D();
      this.graphicsContext2D.clearRect(0d, 0d, width, height);
      this.graphicsContext2D.setFill(new Color(1.0d, 1.0d, 1.0d, 0.9d));
      this.graphicsContext2D.fillRect(40d, 40d, width - 87d, height - 70d);

      this.graphicsContext2D.setFill(Color.gray(0d,0.2d));

      drawGrid(width, height);
    }
  }

  private void drawGrid(double w, double h) {
    for (double x = 40d; x < (w - 40d); x += SPACING_X) {
      for (double y = 40d; y < (h - 20d); y += SPACING_Y) {
        double offsetY = (y % (2d * SPACING_Y)) == 0d ? SPACING_X / 2d : 0d;
        this.graphicsContext2D.fillOval(x - RADIUS + offsetY,y - RADIUS,RADIUS + RADIUS,RADIUS + RADIUS); // A dot
      }
    }
  }

  void addKnot(double x, double y) {
    Pattern currentPattern = this.diagram.getCurrentPattern();
    logger.info("Current pattern  -> {}", currentPattern);

    try (FileInputStream fis = new FileInputStream(currentPattern.filename())) {

      this.canvas.getGraphicsContext2D().drawImage(
        new Image(fis), x, y);
      this.diagram.addKnot(new Knot(x, y, currentPattern));

    } catch (IOException e) {
      logger.error("Problem with pattern resource file!", e);
    }
  }

  SimpleObjectProperty<Pattern> getCurrentPatternProperty() {
    return currentPatternProperty;
  }

}

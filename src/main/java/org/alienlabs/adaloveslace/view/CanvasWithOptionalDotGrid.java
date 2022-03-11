package org.alienlabs.adaloveslace.view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.alienlabs.adaloveslace.business.model.Diagram;

/**
 * A grid (= coordinate system) with dots (= used as landmarks for lace).
 */
public class CanvasWithOptionalDotGrid extends Pane {

  static final double TOP_MARGIN        = 30d;

  private static final double SPACING_X = 25d; // The X space between the dots
  private static final double SPACING_Y = 10d; // The Y space between the dots
  private static final double RADIUS    = 2.5d;// The dots are ellipses, this is their radius

  private final Canvas canvas           = new Canvas(1200d, 700d); // We draw the dots on the grid using a Canvas
  private Diagram diagram;
  private double top;
  private double right;
  private double bottom;
  private double left;
  private double width;
  private double height;

  /**
   * We draw the dots on the grid using a Canvas.
   *
   * @see Canvas
   * @param diagram the business bean containing our future drawing (and its knots)
   */
  public CanvasWithOptionalDotGrid(Diagram diagram) {
    this.diagram = diagram;
    getChildren().addAll(this.canvas);
  }

  @Override
  protected void layoutChildren() {
    drawGrid();
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

      GraphicsContext g = this.canvas.getGraphicsContext2D();
      g.clearRect(0d, 0d, width, height);
      g.setFill(new Color(1.0d, 1.0d, 1.0d, 0.9d));
      g.fillRect(40d, 40d, width - 87d, height - 70d);

      g.setFill(Color.gray(0d,0.2d));

      drawGrid(width, height, g);
    }
  }

  private void drawGrid(double w, double h, GraphicsContext g) {
    for (double x = 40d; x < (w - 40d); x += SPACING_X) {
      for (double y = 40d; y < (h - 20d); y += SPACING_Y) {
        double offsetY = (y % (2d * SPACING_Y)) == 0d ? SPACING_X / 2d : 0d;
        g.fillOval(x - RADIUS + offsetY,y - RADIUS,RADIUS + RADIUS,RADIUS + RADIUS); // A dot
      }
    }
  }

  Canvas getCanvas() {
    return this.canvas;
  }

}

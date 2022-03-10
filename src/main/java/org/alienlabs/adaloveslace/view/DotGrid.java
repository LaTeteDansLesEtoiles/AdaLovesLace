package org.alienlabs.adaloveslace.view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * A grid (= coordinate system) with dots (= used as landmarks for lace).
 */
public class DotGrid extends Pane {

  static final double TOP = 30d;

  private static final double SPACING_X = 25; // The X space between the dots
  private static final double SPACING_Y = 10; // The Y space between the dots
  private static final double RADIUS    = 2.5;// The dots are ellipses, this is their radius

  private final Canvas canvas           = new Canvas(1200d, 700d); // We draw the dots on the grid using a Canvas

  /**
   * We draw the dots on the grid using a Canvas.
   *
   * @see Canvas
   */
  public DotGrid() {
    getChildren().addAll(this.canvas);
  }

  @Override
  protected void layoutChildren() {
    final double top    = (int)snappedTopInset() + TOP;
    final double right  = (int)snappedRightInset();
    final double bottom = (int)snappedBottomInset();
    final double left   = (int)snappedLeftInset();
    final double w      = (int)getWidth() - left - right;
    final double h      = (int)getHeight() - top - bottom - 20d;

    this.canvas.setLayoutX(left);
    this.canvas.setLayoutY(top);

    if (w != this.canvas.getWidth() || h != this.canvas.getHeight()) {
      this.canvas.setWidth(w);
      this.canvas.setHeight(h);

      GraphicsContext g = this.canvas.getGraphicsContext2D();
      g.clearRect(0d, 0d, w, h);
      g.setFill(new Color(1.0d, 1.0d, 1.0d, 0.9d));
      g.fillRect(40d, 40d, w - 87d, h - 70d);

      g.setFill(Color.gray(0,0.2d));

      drawGrid(w, h, g);
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

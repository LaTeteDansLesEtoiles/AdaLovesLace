package org.alienlabs.adaloveslace.view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class DotGrid extends Pane {
  private static final double SPACING_X = 25;
  private static final double SPACING_Y = 10;
  private static final double RADIUS = 2.5;
  private final Canvas canvas = new Canvas(1200d, 700d);

  public DotGrid() {
    getChildren().add(canvas);
  }

  @Override
  protected void layoutChildren() {
    final int top = (int)snappedTopInset() - 10;
    final int right = (int)snappedRightInset();
    final int bottom = (int)snappedBottomInset();
    final int left = (int)snappedLeftInset();
    final int w = (int)getWidth() - left - right;
    final int h = (int)getHeight() - top - bottom - 20;

    canvas.setLayoutX(left);
    canvas.setLayoutY(top);

    if (w != canvas.getWidth() || h != canvas.getHeight()) {
      canvas.setWidth(w);
      canvas.setHeight(h);

      GraphicsContext g = canvas.getGraphicsContext2D();
      g.clearRect(0, 0, w, h);
      g.setFill(new Color(1.0d, 1.0d, 1.0d,0.9));
      g.fillRect(40, 40, w - 87, h - 70);

      g.setFill(Color.gray(0,0.2));

      for (int x = 40; x < (w - 40); x += SPACING_X) {
        for (int y = 40; y < (h - 20); y += SPACING_Y) {
          double offsetY = (y%(2*SPACING_Y)) == 0 ? SPACING_X /2 : 0;
          g.fillOval(x-RADIUS+offsetY,y-RADIUS,RADIUS+RADIUS,RADIUS+RADIUS);
        }
      }
    }
  }
}

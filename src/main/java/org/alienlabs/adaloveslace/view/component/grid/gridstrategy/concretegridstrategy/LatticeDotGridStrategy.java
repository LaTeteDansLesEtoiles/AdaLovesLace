package org.alienlabs.adaloveslace.view.component.grid.gridstrategy.concretegridstrategy;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import org.alienlabs.adaloveslace.domain.Coordinate;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.IDotGridStrategy;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.ParentGridStrategy;

public class LatticeDotGridStrategy implements IDotGridStrategy {

    private static final double SPACING_FOR_LATTICE = 25;

    private double width;
    private double height;

    /**
     * This shall only be called by the ParentGridStrategy.
     */
    public LatticeDotGridStrategy() {
        // This shall only be called by the ParentGridStrategy.
    }

    /**
     * This shall only be called by the ParentGridStrategy.
     */
    public void setViewPort(double width, double height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void drawGrid() {
        ParentGridStrategy.hideGrid();

        double a = SPACING_FOR_LATTICE;
        double h = a * Math.sqrt(3) / 2;
        double dx = height / Math.sqrt(3);

        for (double y = 0; y <= height; y += h) {
            Line line = new Line(0, y, width, y);
            line.setStroke(Color.BLACK);
            ParentGridStrategy.grid.add(line);
        }

        for (double x0 = -dx; x0 <= width; x0 += a) {
            Line line = new Line(x0, 0, x0 + dx, height);
            line.setStroke(Color.GRAY);
            ParentGridStrategy.grid.add(line);
        }

        for (double x0 = 0; x0 <= width + dx; x0 += a) {
            Line line = new Line(x0, 0, x0 - dx, height);
            line.setStroke(Color.GRAY);
            ParentGridStrategy.grid.add(line);
        }

        ParentGridStrategy.gridPane.getChildren().addAll(ParentGridStrategy.grid);
        ParentGridStrategy.gridPane.toBack();
        ParentGridStrategy.gridPane.getStyleClass().add("grid");
        ParentGridStrategy.gridPane.setBackground(null);
    }

    @Override
    public Coordinate getDrawCoordinates(double x, double y) {
        double a = SPACING_FOR_LATTICE;
        double h = a * Math.sqrt(3) / 2;

        double v = y / h;
        double u = (x - (a / 2) * v) / a;

        long ui = Math.round(u);
        long vi = Math.round(v);

        double sx = ui * a + vi * (a / 2);
        double sy = vi * h;

        return new Coordinate(sx, sy);
    }

}

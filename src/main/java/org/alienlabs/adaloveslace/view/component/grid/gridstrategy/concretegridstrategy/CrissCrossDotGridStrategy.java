package org.alienlabs.adaloveslace.view.component.grid.gridstrategy.concretegridstrategy;

import javafx.scene.shape.Line;
import org.alienlabs.adaloveslace.domain.Coordinate;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.IDotGridStrategy;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.ParentGridStrategy;

import static org.alienlabs.adaloveslace.domain.Diagram.*;

public class CrissCrossDotGridStrategy implements IDotGridStrategy {

    private double width;
    private double height;
    private double offsetX;

    /**
     * This shall only be called by the ParentGridStrategy.
     */
    public CrissCrossDotGridStrategy() {
        // This shall only be called by the ParentGridStrategy.
        offsetX = SPACING_X_FOR_CRISS_CROSS / 2d;
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

        for (double gridX = 0d; gridX < this.width; gridX += SPACING_X_FOR_CRISS_CROSS) {
            for (double gridY = 0d; gridY < this.height; gridY += SPACING_Y_FOR_CRISS_CROSS) {
                Line l1 = new Line(gridX + offsetX, gridY, width, gridY);
                l1.setStroke(CRISS_CROSS_GRID_COLOR);
                l1.setStrokeWidth(1);
                l1.toBack();

                ParentGridStrategy.grid.add(l1);

                Line l2 = new Line(gridX + offsetX, gridY, gridX + offsetX, height);
                l2.setStroke(CRISS_CROSS_GRID_COLOR);
                l2.setStrokeWidth(1);
                l2.toFront();

                ParentGridStrategy.grid.add(l2);
            }
        }
        ParentGridStrategy.gridPane.getChildren().addAll(ParentGridStrategy.grid);
        ParentGridStrategy.gridPane.toBack();
        ParentGridStrategy.gridPane.getStyleClass().add("grid");
        ParentGridStrategy.gridPane.setBackground(null);
    }

    @Override
    public Coordinate getDrawCoordinates(double x, double y) {
        return new Coordinate(nearestGridX(x), nearestGridY(y));
    }

    private double nearestGridX(double xDraw) {
        double delta = xDraw - offsetX;
        if (delta <= 0) {
            return offsetX;
        }

        double maxDelta = this.width;
        if (delta >= maxDelta) {
            double maxK = Math.floor(maxDelta / SPACING_X_FOR_CRISS_CROSS);
            return offsetX + maxK * SPACING_X_FOR_CRISS_CROSS;
        }

        double k = Math.floor(delta / SPACING_X_FOR_CRISS_CROSS);
        return offsetX + k * SPACING_X_FOR_CRISS_CROSS;
    }

    private double nearestGridY(double yDraw) {
        double spacing = SPACING_Y_FOR_CRISS_CROSS;
        double quotient = yDraw / spacing;
        double index   = Math.floor(quotient);
        double yGrid   = index * spacing;
        if (yGrid < 0)             yGrid = 0;
        if (yGrid > this.width)    yGrid = Math.floor(this.width / spacing) * spacing;
        return yGrid;
    }

}

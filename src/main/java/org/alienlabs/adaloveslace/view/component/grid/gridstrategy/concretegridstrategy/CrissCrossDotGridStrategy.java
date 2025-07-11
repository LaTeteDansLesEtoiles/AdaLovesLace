package org.alienlabs.adaloveslace.view.component.grid.gridstrategy.concretegridstrategy;

import javafx.geometry.Point2D;
import javafx.scene.shape.Line;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.IDotGridStrategy;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.ParentGridStrategy;

import static org.alienlabs.adaloveslace.domain.Diagram.*;

public class CrissCrossDotGridStrategy implements IDotGridStrategy {

    private double width;
    private double height;
    private double translateX;
    private double translateY;
    private final double offsetX;

    /**
     * This shall only be called by the ParentGridStrategy.
     */
    public CrissCrossDotGridStrategy() {
        // This shall only be called by the ParentGridStrategy.
        this.offsetX = SPACING_X_FOR_CRISS_CROSS;
    }

    /**
     * This shall only be called by the ParentGridStrategy.
     */
    public void setViewPort(double width, double height, double translateX, double translateY) {
        this.width = width;
        this.height = height;
        this.translateX = translateX;
        this.translateY = translateY;
    }

    @Override
    public void drawGrid() {
        ParentGridStrategy.hideGrid();

        for (double gridX = 0d; gridX < this.width - this.translateX; gridX += SPACING_X_FOR_CRISS_CROSS) {
            for (double gridY = 0d; gridY < this.height - this.translateY; gridY += SPACING_Y_FOR_CRISS_CROSS) {
                Line l1 = new Line(0, gridY, this.width - this.translateX, gridY);
                l1.setStroke(CRISS_CROSS_GRID_COLOR);
                l1.setStrokeWidth(1);
                l1.toBack();

                ParentGridStrategy.grid.add(l1);

                Line l2 = new Line(gridX, 0, gridX, this.height - this.translateY);
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
    public Point2D getSnapToGridDrawCoordinates(double x, double y) {
        return new Point2D(nearestGridX(x), nearestGridY(y));
    }

    private double nearestGridX(double xDraw) {
        double delta = xDraw - offsetX;
        if (delta <= 0) {
            return offsetX;
        }

        double maxDelta = this.width - this.translateX;
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
        if (yGrid > this.width - this.translateY)    yGrid = Math.floor((this.width - this.translateY) / spacing) * spacing;
        return yGrid;
    }

}

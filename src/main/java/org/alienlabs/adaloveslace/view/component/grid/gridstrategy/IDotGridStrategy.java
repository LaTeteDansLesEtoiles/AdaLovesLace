package org.alienlabs.adaloveslace.view.component.grid.gridstrategy;

import javafx.geometry.Point2D;

public interface IDotGridStrategy {

    /**
     * This shall only be called by the ParentGridStrategy.
     */
    void setViewPort(double width, double height, double translateX, double translateY);

    void drawGrid();

    Point2D getSnapToGridDrawCoordinates(double x, double y);

}

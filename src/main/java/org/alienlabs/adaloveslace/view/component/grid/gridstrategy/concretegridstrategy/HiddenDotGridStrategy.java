package org.alienlabs.adaloveslace.view.component.grid.gridstrategy.concretegridstrategy;

import javafx.geometry.Point2D;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.IDotGridStrategy;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.ParentGridStrategy;

public class HiddenDotGridStrategy implements IDotGridStrategy {

    /**
     * This shall only be called by the ParentGridStrategy.
     */
    @Override
    public void setViewPort(double width, double height, double translateX, double translateY) {
        // Nothing to do here
    }

    @Override
    public void drawGrid() {
        ParentGridStrategy.hideGrid();
    }

    @Override
    public Point2D getSnapToGridDrawCoordinates(double x, double y) {
        return new Point2D(x, y);
    }

}

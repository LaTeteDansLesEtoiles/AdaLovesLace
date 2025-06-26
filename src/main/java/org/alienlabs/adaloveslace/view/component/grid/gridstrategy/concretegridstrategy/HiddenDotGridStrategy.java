package org.alienlabs.adaloveslace.view.component.grid.gridstrategy.concretegridstrategy;

import org.alienlabs.adaloveslace.domain.Coordinate;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.IDotGridStrategy;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.ParentGridStrategy;

public class HiddenDotGridStrategy implements IDotGridStrategy {

    private double width;
    private double height;

    /**
     * This shall only be called by the ParentGridStrategy.
     */
    public HiddenDotGridStrategy() {
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
    }

    @Override
    public Coordinate getDrawCoordinates(double x, double y) {
        return new Coordinate(x, y);
    }

}

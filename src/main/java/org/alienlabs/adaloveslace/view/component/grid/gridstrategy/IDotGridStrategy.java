package org.alienlabs.adaloveslace.view.component.grid.gridstrategy;

import org.alienlabs.adaloveslace.business.model.Coordinate;

public interface IDotGridStrategy {

    /**
     * This shall only be called by the ParentGridStrategy.
     */
    void setViewPort(double width, double height);

    void drawGrid();

    Coordinate getDrawCoordinates(double x, double y);

}

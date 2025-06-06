package org.alienlabs.adaloveslace.view.component.grid.gridstrategy;

public interface IDotGridStrategy {

    /**
     * This shall only be called by the ParentGridStrategy.
     */
    void setViewPort(double width, double height);

    void drawGrid();

}

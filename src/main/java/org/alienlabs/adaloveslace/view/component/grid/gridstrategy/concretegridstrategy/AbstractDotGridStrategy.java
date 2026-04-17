package org.alienlabs.adaloveslace.view.component.grid.gridstrategy.concretegridstrategy;

import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.IDotGridStrategy;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.ParentGridStrategy;

abstract class AbstractDotGridStrategy implements IDotGridStrategy {

    protected double width;
    protected double height;
    protected double translateX;
    protected double translateY;

    @Override
    public void setViewPort(double width, double height, double translateX, double translateY) {
        this.width = width;
        this.height = height;
        this.translateX = translateX;
        this.translateY = translateY;
    }

    protected void finalizeGridPane() {
        ParentGridStrategy.gridPane.getChildren().addAll(ParentGridStrategy.grid);
        ParentGridStrategy.gridPane.toBack();
        ParentGridStrategy.gridPane.getStyleClass().add("grid");
        ParentGridStrategy.gridPane.setBackground(null);
    }
}

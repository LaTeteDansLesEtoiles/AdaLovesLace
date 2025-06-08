package org.alienlabs.adaloveslace.view.component.grid.gridstrategy.concretegridstrategy;

import javafx.scene.shape.Ellipse;
import org.alienlabs.adaloveslace.business.model.Coordinate;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.IDotGridStrategy;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.ParentGridStrategy;

import static org.alienlabs.adaloveslace.App.GRID_DOTS_RADIUS;
import static org.alienlabs.adaloveslace.business.model.Diagram.*;

public class StagerredDotGridStrategy implements IDotGridStrategy {

    private double width;
    private double height;

    /**
     * This shall only be called by the ParentGridStrategy.
     */
    public StagerredDotGridStrategy() {
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

        for (double gridX = 10d; gridX < this.width; gridX += SPACING_X_FOR_DOTS) {
            for (double gridY = 0d; gridY < (this.height - 50d); gridY += SPACING_Y_FOR_DOTS) {
                double offsetY = (gridY % (2d * SPACING_Y_FOR_DOTS)) == 0d ? SPACING_X_FOR_DOTS / 2d : 0d;
                Ellipse ell = new Ellipse(gridX - GRID_DOTS_RADIUS + offsetY,gridY - GRID_DOTS_RADIUS, GRID_DOTS_RADIUS, GRID_DOTS_RADIUS); // A dot
                ell.setFill(DOT_GRID_COLOR);
                ell.toBack();

                ParentGridStrategy.grid.add(ell);
            }

        }
        ParentGridStrategy.gridPane.getChildren().addAll(ParentGridStrategy.grid);
    }

    @Override
    public Coordinate getDrawCoordinates(double x, double y) {
        return new Coordinate(x, y);
    }

}

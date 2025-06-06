package org.alienlabs.adaloveslace.view.component.grid.gridstrategy.concretegridstrategy;

import javafx.animation.PauseTransition;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.IDotGridStrategy;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.ParentGridStrategy;

import static org.alienlabs.adaloveslace.business.model.Diagram.*;

public class CrissCrossDotGridStrategy implements IDotGridStrategy {

    private double width;
    private double height;

    private PauseTransition gridPause;
    private PauseTransition cleanUpPause;

    /**
     * This shall only be called by the ParentGridStrategy.
     */
    public CrissCrossDotGridStrategy() {
        // This shall only be called by the ParentGridStrategy.
    }

    /**
     * This shall only be called by the ParentGridStrategy.
     */
    public void setViewPort(double width, double height) {
        this.width = width;
        this.height = height;
        gridPause = new PauseTransition(Duration.millis(1000 / width));
        cleanUpPause = new PauseTransition(Duration.seconds(2));
    }

    @Override
    public void drawGrid() {
        gridPause.setOnFinished(_ -> draw());
        cleanUpPause.setOnFinished(_ -> draw());

        gridPause.playFromStart();
        cleanUpPause.play();
    }

    private void draw() {
        ParentGridStrategy.hideGrid();

        for (double gridX = 0d; gridX < this.width; gridX += SPACING_X_FOR_CRISS_CROSS) {
            for (double gridY = 0d; gridY < this.height; gridY += SPACING_Y_FOR_CRISS_CROSS) {
                double offsetY = SPACING_X_FOR_CRISS_CROSS / 2d;
                Line l1 = new Line(gridX + offsetY, gridY, width, gridY); // A dot
                l1.setStroke(CRISS_CROSS_GRID_COLOR);
                l1.setStrokeWidth(1);
                l1.toBack();

                ParentGridStrategy.grid.add(l1);

                Line l2 = new Line(gridX + offsetY, gridY, gridX + offsetY, height); // A dot
                l2.setStroke(CRISS_CROSS_GRID_COLOR);
                l2.setStrokeWidth(1);
                l2.toFront();

                ParentGridStrategy.grid.add(l2);
            }
        }
        ParentGridStrategy.root.getChildren().addAll(ParentGridStrategy.grid);
    }
}

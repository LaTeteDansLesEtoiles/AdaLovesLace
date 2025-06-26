package org.alienlabs.adaloveslace.view.component.grid.gridstrategy.concretegridstrategy;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import org.alienlabs.adaloveslace.business.model.Coordinate;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.IDotGridStrategy;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.ParentGridStrategy;

public class PolarDotGridStrategy implements IDotGridStrategy {

    private static final double RADIUS_STEP     = 25d;
    private static final double ANGLE_STEP_DEG  = 15d;

    private double width;
    private double height;

    /**
     * This shall only be called by the ParentGridStrategy.
     */
    public PolarDotGridStrategy() {
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

        double centerX = width  / 2.0;
        double centerY = height / 2.0;

        // Rayon max nécessaire pour recouvrir tout le canevas
        double maxRadius = Math.hypot(
                Math.max(centerX,  width  - centerX),
                Math.max(centerY,  height - centerY)
        );

        // 1) Cercles concentriques
        for (double r = RADIUS_STEP; r <= maxRadius; r += RADIUS_STEP) {
            Circle circle = new Circle(centerX, centerY, r);
            circle.setStroke(Color.BLACK);
            circle.setFill(null);
            ParentGridStrategy.grid.add(circle);
        }

        // 2) Rayons (lignes) à intervalles angulaires réguliers
        for (double angleDeg = 0; angleDeg < 360; angleDeg += ANGLE_STEP_DEG) {
            double theta = Math.toRadians(angleDeg);
            double endX = centerX + maxRadius * Math.cos(theta);
            double endY = centerY + maxRadius * Math.sin(theta);

            Line ray = new Line(centerX, centerY, endX, endY);
            ray.setStroke(Color.GRAY);
            ParentGridStrategy.grid.add(ray);
        }

        ParentGridStrategy.gridPane.getChildren().addAll(ParentGridStrategy.grid);
        ParentGridStrategy.gridPane.toBack();
        ParentGridStrategy.gridPane.getStyleClass().add("grid");
        ParentGridStrategy.gridPane.setBackground(null);
    }

    @Override
    public Coordinate getDrawCoordinates(double x, double y) {
        double centerX = width  / 2.0;
        double centerY = height / 2.0;

        double dx = x - centerX;
        double dy = y - centerY;
        double r     = Math.hypot(dx, dy);
        double theta = Math.toDegrees(Math.atan2(dy, dx));
        if (theta < 0) {
            theta += 360;
        }

        long ri = Math.round(r / RADIUS_STEP);
        long ai = Math.round(theta / ANGLE_STEP_DEG);

        double snappedR         = ri * RADIUS_STEP;
        double snappedThetaDeg  = ai * ANGLE_STEP_DEG;
        double snappedThetaRad  = Math.toRadians(snappedThetaDeg);

        double sx = centerX + snappedR * Math.cos(snappedThetaRad);
        double sy = centerY + snappedR * Math.sin(snappedThetaRad);

        return new Coordinate(sx, sy);
    }
    
}

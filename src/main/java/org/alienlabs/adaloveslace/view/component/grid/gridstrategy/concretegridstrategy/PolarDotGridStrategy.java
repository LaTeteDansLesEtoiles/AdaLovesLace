package org.alienlabs.adaloveslace.view.component.grid.gridstrategy.concretegridstrategy;

import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.IDotGridStrategy;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.ParentGridStrategy;

public class PolarDotGridStrategy implements IDotGridStrategy {

    private static final double RADIUS_STEP     = 25d;
    private static final double ANGLE_STEP_DEG  = 15d;

    private double width;
    private double height;
    private double translateX;
    private double translateY;

    /**
     * This shall only be called by the ParentGridStrategy.
     */
    public PolarDotGridStrategy() {
        // This shall only be called by the ParentGridStrategy.
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

        double centerX = width  / 2.0;
        double centerY = height / 2.0;
        double minX = translateX;
        double maxX = width;
        double minY = translateY;
        double maxY = height;
        double maxRadius = 0;
        for (double x : new double[]{minX, maxX}) {
            for (double y : new double[]{minY, maxY}) {
                maxRadius = Math.max(maxRadius, Math.hypot(x - centerX, y - centerY));
            }
        }
        double dr = Math.hypot(-translateX, -translateY);
        double rOffset = ((dr % RADIUS_STEP) + RADIUS_STEP) % RADIUS_STEP;
        double thetaOffset = Math.toDegrees(Math.atan2(-translateY, -translateX));

        for (double r = rOffset; r <= maxRadius + RADIUS_STEP; r += RADIUS_STEP) {
            Circle c = new Circle(centerX, centerY, r);
            c.setStroke(javafx.scene.paint.Color.GRAY);
            c.setFill(null);
            ParentGridStrategy.grid.add(c);
        }

        for (double angle = thetaOffset; angle < 360 + thetaOffset; angle += ANGLE_STEP_DEG) {
            double rad = Math.toRadians(angle);
            double endX = centerX + (maxRadius + RADIUS_STEP) * Math.cos(rad);
            double endY = centerY + (maxRadius + RADIUS_STEP) * Math.sin(rad);
            Line l = new Line(centerX, centerY, endX, endY);
            l.setStroke(javafx.scene.paint.Color.GRAY);
            ParentGridStrategy.grid.add(l);
        }

        ParentGridStrategy.gridPane.getChildren().addAll(ParentGridStrategy.grid);
        ParentGridStrategy.gridPane.toBack();
        ParentGridStrategy.gridPane.getStyleClass().add("grid");
        ParentGridStrategy.gridPane.setBackground(null);
    }

    @Override
    public Point2D getSnapToGridDrawCoordinates(double x, double y) {
        double cx = width  / 2.0;
        double cy = height / 2.0;
        double dx = x - cx;
        double dy = y - cy;
        double r = Math.hypot(dx, dy);
        double theta = Math.toDegrees(Math.atan2(dy, dx));
        if (theta < 0) theta += 360;

        double dr = Math.hypot(-translateX, -translateY);
        double rOffset = ((dr % RADIUS_STEP) + RADIUS_STEP) % RADIUS_STEP;
        double rRel = r - rOffset;
        double fIdx = Math.floor(rRel / RADIUS_STEP);
        double mIdx = fIdx + 0.5;
        double cIdx = fIdx + 1.0;

        double thetaOffset = Math.toDegrees(Math.atan2(-translateY, -translateX));
        if (thetaOffset < 0) thetaOffset += 360;
        double thetaRel = ((theta - thetaOffset) % 360 + 360) % 360;
        long aIdx = Math.round(thetaRel / ANGLE_STEP_DEG);

        double bestDist = Double.MAX_VALUE;
        Point2D best = null;

        for (double ridx : new double[]{fIdx, mIdx, cIdx}) {
            double rr = rOffset + ridx * RADIUS_STEP;
            for (long a = aIdx - 1; a <= aIdx + 1; a++) {
                double t = thetaOffset + a * ANGLE_STEP_DEG;
                double rad = Math.toRadians(t);
                double px = cx + rr * Math.cos(rad);
                double py = cy + rr * Math.sin(rad);
                double d = Math.hypot(px - x, py - y);
                if (d < bestDist) {
                    bestDist = d;
                    best = new Point2D(px, py);
                }
            }
        }

        return best;
    }
    
}

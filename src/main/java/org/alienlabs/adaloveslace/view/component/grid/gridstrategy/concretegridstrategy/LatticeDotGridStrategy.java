package org.alienlabs.adaloveslace.view.component.grid.gridstrategy.concretegridstrategy;

import javafx.geometry.Point2D;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.ParentGridStrategy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LatticeDotGridStrategy extends AbstractDotGridStrategy {

    private static final double SPACING_FOR_LATTICE = 25;

    /**
     * This shall only be called by the ParentGridStrategy.
     */
    public LatticeDotGridStrategy() {
        // This shall only be called by the ParentGridStrategy.
    }

    @Override
    public void drawGrid() {
        ParentGridStrategy.hideGrid();

        double dw = width - translateX;
        double dh = height - translateY;
        double a = SPACING_FOR_LATTICE;
        double h = a * Math.sqrt(3) / 2;
        double sqrt3 = Math.sqrt(3);

        int jMin = (int)Math.floor(translateY / h);
        int jMax = (int)Math.ceil((dh + translateY) / h);
        for (int j = jMin; j <= jMax; j++) {
            double y = j * h - translateY;
            if (y >= 0 && y <= dh) {
                ParentGridStrategy.grid.add(new Line(0, y, dw, y));
            }
        }

        Point2D n1 = new Point2D(-sqrt3/2, 1d/2);
        Point2D d1 = new Point2D(1d/2, sqrt3/2);
        Point2D n2 = new Point2D( sqrt3/2, 1d/2);
        Point2D d2 = new Point2D(1d/2, -sqrt3/2);

        addAngled(ParentGridStrategy.grid, dw, dh, a, translateX, translateY, n1, d1);
        addAngled(ParentGridStrategy.grid, dw, dh, a, translateX, translateY, n2, d2);

        finalizeGridPane();
    }

    private void addAngled(
            List<Shape> grid,
            double w,
            double h,
            double a,
            double translateX,
            double translateY,
            Point2D n,
            Point2D d
    ) {
        double cOff = n.dotProduct(new Point2D(-translateX, -translateY));
        Point2D[] corners = {
                new Point2D(0, 0), new Point2D(w, 0),
                new Point2D(0, h), new Point2D(w, h)
        };
        double nMin = Double.POSITIVE_INFINITY;
        double nMax = Double.NEGATIVE_INFINITY;
        for (Point2D c : corners) {
            double v = n.dotProduct(c);
            if (v < nMin) nMin = v;
            if (v > nMax) nMax = v;
        }
        int kMin = (int)Math.floor((nMin - cOff) / a);
        int kMax = (int)Math.ceil((nMax - cOff) / a);
        for (int k = kMin; k <= kMax; k++) {
            double c = k * a + cOff;
            Point2D p0 = n.multiply(c);
            List<Point2D> pts = computeIntersections(p0, d, w, h);
            if (pts.size() >= 2) {
                Point2D pA = pts.stream()
                        .min(Comparator.comparingDouble(pt -> param(p0, d, pt)))
                        .get();
                Point2D pB = pts.stream()
                        .max(Comparator.comparingDouble(pt -> param(p0, d, pt)))
                        .get();
                grid.add(new Line(pA.getX(), pA.getY(), pB.getX(), pB.getY()));
            }
        }
    }

    private double param(Point2D p0, Point2D d, Point2D p) {
        if (Math.abs(d.getX()) > Math.abs(d.getY())) {
            return (p.getX() - p0.getX()) / d.getX();
        } else {
            return (p.getY() - p0.getY()) / d.getY();
        }
    }

    private static List<Point2D> computeIntersections(Point2D p0, Point2D d, double w, double h) {
        List<Point2D> pts = new ArrayList<>();
        if (d.getX() != 0) {
            double t1 = (0    - p0.getX()) / d.getX();
            double y1 = p0.getY() + t1 * d.getY();
            if (y1 >= 0 && y1 <= h) pts.add(new Point2D(0, y1));
            double t2 = (w    - p0.getX()) / d.getX();
            double y2 = p0.getY() + t2 * d.getY();
            if (y2 >= 0 && y2 <= h) pts.add(new Point2D(w, y2));
        }
        if (d.getY() != 0) {
            double t3 = (0    - p0.getY()) / d.getY();
            double x3 = p0.getX() + t3 * d.getX();
            if (x3 >= 0 && x3 <= w) pts.add(new Point2D(x3, 0));
            double t4 = (h    - p0.getY()) / d.getY();
            double x4 = p0.getX() + t4 * d.getX();
            if (x4 >= 0 && x4 <= w) pts.add(new Point2D(x4, h));
        }
        return pts;
    }

    @Override
    public Point2D getSnapToGridDrawCoordinates(double x, double y) {
        double a = SPACING_FOR_LATTICE;
        double h = a * Math.sqrt(3) / 2;

        double v = y / h;
        double u = (x - (a / 2) * v) / a;

        long ui = Math.round(u);
        long vi = Math.round(v);

        double sx = ui * a + vi * (a / 2);
        double sy = vi * h;

        return new Point2D(sx, sy);
    }

}

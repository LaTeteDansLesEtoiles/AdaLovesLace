package org.alienlabs.adaloveslace.view.component.grid.gridstrategy;

import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.enumeration.GridType;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.concretegridstrategy.*;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ParentGridStrategy {

    public static Pane gridPane;
    public static List<Shape> grid = new ArrayList<>();
    private final Map<GridType, IDotGridStrategy> childStrategies = new EnumMap<>(GridType.class);
    public static App app;
    private static boolean gridHasBeenDrawn = false;

    public ParentGridStrategy(App app, Pane gridPane) {
        ParentGridStrategy.app = app;
        ParentGridStrategy.gridPane = gridPane;
        gridHasBeenDrawn = false;

        childStrategies.put(GridType.CRISS_CROSS, new CrissCrossDotGridStrategy());
        childStrategies.put(GridType.LATTICE, new LatticeDotGridStrategy());
        childStrategies.put(GridType.POLAR, new PolarDotGridStrategy());
        childStrategies.put(GridType.STAGGERED, new StagerredDotGridStrategy());
        childStrategies.put(GridType.HIDDEN, new HiddenDotGridStrategy());
    }

    public void drawGrid() {
        if (!gridHasBeenDrawn) {
            ParentGridStrategy.gridPane.setPrefWidth(app.getPrimaryStage().getWidth());
            ParentGridStrategy.gridPane.setPrefHeight(app.getPrimaryStage().getHeight());

            double width = app.getMovablePane().getWidth();
            double height = app.getMovablePane().getHeight();
            double translateX = app.getMovablePane().getTranslateX();
            double translateY = app.getMovablePane().getTranslateY();

            IDotGridStrategy childStrategy = childStrategies.get(app.getOptionalDotGrid().getDiagram().getCurrentGridType());
            childStrategy.setViewPort(width, height, translateX, translateY);
            childStrategy.drawGrid();

            gridHasBeenDrawn = true;
        }
    }

    public Point2D getDrawCoordinates(double x, double y) {
        return childStrategies.get(app.getOptionalDotGrid().getDiagram().getCurrentGridType()).getSnapToGridDrawCoordinates(x, y);
    }

    public void switchGridType() {
        GridType currentGridType = switch (app.getOptionalDotGrid().getDiagram().getCurrentGridType()) {
            case CRISS_CROSS -> GridType.LATTICE;
            case LATTICE -> GridType.POLAR;
            case POLAR -> GridType.STAGGERED;
            case STAGGERED -> GridType.HIDDEN;
            case HIDDEN -> GridType.CRISS_CROSS;
        };

        app.getOptionalDotGrid().getDiagram().setCurrentGridType(currentGridType);
    }

    public GridType getCurrentGridType() {
        return app.getOptionalDotGrid().getDiagram().getCurrentGridType();
    }

    public void setCurrentGridType(GridType currentGridType) {
        app.getOptionalDotGrid().getDiagram().setCurrentGridType(currentGridType);
    }

    public static void hideGrid() {
        ParentGridStrategy.gridPane.getChildren().clear();
        ParentGridStrategy.grid.clear();
    }

    public static List<Shape> getGrid() {
        return ParentGridStrategy.grid;
    }

    public static void setGridHasBeenDrawn(boolean gridHasBeenDrawn) {
        ParentGridStrategy.gridHasBeenDrawn = gridHasBeenDrawn;
    }

}

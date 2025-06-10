package org.alienlabs.adaloveslace.view.component.grid.gridstrategy;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Coordinate;
import org.alienlabs.adaloveslace.business.model.enumeration.GridType;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.concretegridstrategy.CrissCrossDotGridStrategy;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.concretegridstrategy.HiddenDotGridStrategy;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.concretegridstrategy.StagerredDotGridStrategy;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class ParentGridStrategy {

    public static List<Shape> grid = new ArrayList<>();

    private static  App app;
    public static Pane gridPane;
    private static boolean gridHasBeenDrawn = false;

    private GridType currentGridType = GridType.STAGGERED;
    private final EnumMap<GridType, IDotGridStrategy> childStrategies = new EnumMap<>(GridType.class);

    public ParentGridStrategy(App app, Pane gridPane) {
        ParentGridStrategy.app = app;
        ParentGridStrategy.gridPane = gridPane;
        gridHasBeenDrawn = false;

        childStrategies.put(GridType.STAGGERED, new StagerredDotGridStrategy());
        childStrategies.put(GridType.CRISS_CROSS, new CrissCrossDotGridStrategy());
        childStrategies.put(GridType.HIDDEN, new HiddenDotGridStrategy());
    }

    public void drawGrid() {
        if (!gridHasBeenDrawn) {
            ParentGridStrategy.gridPane.setPrefWidth(app.getPrimaryStage().getWidth());
            ParentGridStrategy.gridPane.setPrefHeight(app.getPrimaryStage().getHeight());

            double width = ParentGridStrategy.app.getMaxWidth();
            double height = ParentGridStrategy.app.getMaxHeight();

            IDotGridStrategy childStrategy = childStrategies.get(currentGridType);
            childStrategy.setViewPort(width, height);
            childStrategy.drawGrid();

            gridHasBeenDrawn = true;
        }
    }

    public Coordinate getDrawCoordinates(double x, double y) {
        return childStrategies.get(currentGridType).getDrawCoordinates(x, y);
    }

    public void switchGridType() {
        this.currentGridType = switch (currentGridType) {
            case STAGGERED -> GridType.CRISS_CROSS;
            case CRISS_CROSS -> GridType.HIDDEN;
            case HIDDEN -> GridType.STAGGERED;
        };
    }

    public GridType getCurrentGridType() {
        return this.currentGridType;
    }

    public void setCurrentGridType(GridType currentGridType) {
        this.currentGridType = currentGridType;
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

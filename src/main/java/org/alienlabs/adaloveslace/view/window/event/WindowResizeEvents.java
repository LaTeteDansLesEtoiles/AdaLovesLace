package org.alienlabs.adaloveslace.view.window.event;

import javafx.animation.PauseTransition;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.util.Duration;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.util.Preferences;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.ParentGridStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.alienlabs.adaloveslace.App.*;
import static org.alienlabs.adaloveslace.view.window.GeometryWindow.DEFAULT_GEOMETRY_WINDOW_HEIGHT;
import static org.alienlabs.adaloveslace.view.window.GeometryWindow.DEFAULT_GEOMETRY_WINDOW_WIDTH;
import static org.alienlabs.adaloveslace.view.window.StateWindow.DEFAULT_STATE_WINDOW_HEIGHT;
import static org.alienlabs.adaloveslace.view.window.StateWindow.DEFAULT_STATE_WINDOW_WIDTH;
import static org.alienlabs.adaloveslace.view.window.ToolboxWindow.DEFAULT_TOOLBOX_WINDOW_WIDTH;

public class WindowResizeEvents {

    private final App app;
    private final PauseTransition resizePause = new PauseTransition(Duration.millis(600));

    public static final String MAIN_WINDOW_WIDTH        = "MAIN_WINDOW_WIDTH";
    public static final String GRID_WIDTH               = "GRID_WIDTH";
    public static final String MAIN_WINDOW_HEIGHT       = "MAIN_WINDOW_HEIGHT";
    public static final String GRID_HEIGHT              = "GRID_HEIGHT";
    public static final String TOOLBOX_WINDOW_WIDTH     = "TOOLBOX_WINDOW_WIDTH";
    public static final String TOOLBOX_WINDOW_HEIGHT    = "TOOLBOX_WINDOW_HEIGHT";
    public static final String GEOMETRY_WINDOW_WIDTH    = "GEOMETRY_WINDOW_WIDTH";
    public static final String GEOMETRY_WINDOW_HEIGHT   = "GEOMETRY_WINDOW_HEIGHT";
    public static final String STATE_WINDOW_WIDTH       = "STATE_WINDOW_WIDTH";
    public static final String STATE_WINDOW_HEIGHT      = "STATE_WINDOW_HEIGHT";

    private static final Logger logger = LoggerFactory.getLogger(WindowResizeEvents.class);

    public WindowResizeEvents(App app) {
        this.app = app;
    }

    public void onWindowsResize() {
        this.onDoMainWindowResize();
        this.onDoToolboxWindowResize();
        this.onDoGeometryWindowResize();
        this.onDoStateWindowResize();
    }

    public void onDoMainWindowResize() {
        onDoResize();
        onDoMainWindowChangeWidth(this.app.getPrimaryStage().widthProperty());
        onDoMainWindowChangeHeight(this.app.getPrimaryStage().heightProperty());
    }

    public void onDoToolboxWindowResize() {
        onDoChangeWidth(this.app.getToolboxStage().widthProperty(), TOOLBOX_WINDOW_WIDTH, "Toolbox window width: {}");
        onDoChangeHeight(this.app.getToolboxStage().heightProperty(), TOOLBOX_WINDOW_HEIGHT, "Toolbox window height: {}");
    }

    public void onDoGeometryWindowResize() {
        onDoChangeWidth(this.app.getGeometryStage().widthProperty(), GEOMETRY_WINDOW_WIDTH, "Geometry window width: {}");
        onDoChangeHeight(this.app.getGeometryStage().heightProperty(), GEOMETRY_WINDOW_HEIGHT, "Geometry window height: {}");
    }
    public void onDoStateWindowResize() {
        onDoChangeWidth(this.app.getStateStage().widthProperty(), STATE_WINDOW_WIDTH, "State window width: {}");
        onDoChangeHeight(this.app.getStateStage().heightProperty(), STATE_WINDOW_HEIGHT, "State window height: {}");
    }

    private void onDoMainWindowChangeWidth(ReadOnlyDoubleProperty stage) {
        stage.addListener((obs, oldVal, newVal) ->
                {
                    logger.debug("Main window width: {}", newVal);
                    resizePause.playFromStart();
                    this.app.setGridWidth((double) newVal);

                    Preferences prefs = new Preferences();
                    prefs.setStringValue(WindowResizeEvents.MAIN_WINDOW_WIDTH, String.valueOf((double) newVal));
                    prefs.setStringValue(WindowResizeEvents.GRID_WIDTH, String.valueOf((double) newVal));
                }
        );
    }

    private void onDoMainWindowChangeHeight(ReadOnlyDoubleProperty stage) {
        stage.addListener((obs, oldVal, newVal) ->
                {
                    logger.debug("Main window height: {}", newVal);
                    resizePause.playFromStart();
                    this.app.setGridHeight((double) newVal);

                    Preferences prefs = new Preferences();
                    prefs.setStringValue(WindowResizeEvents.MAIN_WINDOW_HEIGHT, String.valueOf((double) newVal));
                    prefs.setStringValue(WindowResizeEvents.GRID_HEIGHT, String.valueOf((double) newVal));
                }
        );
    }

    private void onDoChangeWidth(ReadOnlyDoubleProperty stage, String preferenceName, String s) {
        stage.addListener((obs, oldVal, newVal) ->
                {
                    logger.debug(s, newVal);

                    Preferences prefs = new Preferences();
                    prefs.setStringValue(preferenceName, String.valueOf((double) newVal));
                }
        );
    }

    private void onDoChangeHeight(ReadOnlyDoubleProperty stage, String preferenceName, String s) {
        stage.addListener((obs, oldVal, newVal) ->
                {
                    logger.debug(s, newVal);

                    Preferences prefs = new Preferences();
                    prefs.setStringValue(preferenceName, String.valueOf((double) newVal));
                }
        );
    }

    private void onDoResize() {
        resizePause.setOnFinished(e -> {
            ParentGridStrategy.setGridHasBeenDrawn(false);
            this.app.getOptionalDotGrid().layoutChildren();
        });
    }

    public double getMainWindowWidth() {
        Preferences prefs = new Preferences();
        if (prefs.getStringValue(WindowResizeEvents.MAIN_WINDOW_WIDTH).isEmpty()) {
            return DEFAULT_MAIN_WINDOW_WIDTH;
        }

        return Double.parseDouble(prefs.getStringValue(WindowResizeEvents.MAIN_WINDOW_WIDTH));
    }

    public double getMainWindowHeight() {
        Preferences prefs = new Preferences();
        if (prefs.getStringValue(WindowResizeEvents.MAIN_WINDOW_HEIGHT).isEmpty()) {
            return DEFAULT_MAIN_WINDOW_HEIGHT;
        }

        return Double.parseDouble(prefs.getStringValue(WindowResizeEvents.MAIN_WINDOW_HEIGHT)) - 32d;
    }

    public double getGridWidth() {
        Preferences prefs = new Preferences();
        if (prefs.getStringValue(WindowResizeEvents.GRID_WIDTH).isEmpty()) {
            return DEFAULT_GRID_WIDTH;
        }

        return Double.parseDouble(prefs.getStringValue(WindowResizeEvents.GRID_WIDTH));
    }

    public double getGridHeight() {
        Preferences prefs = new Preferences();
        if (prefs.getStringValue(WindowResizeEvents.GRID_HEIGHT).isEmpty()) {
            return DEFAULT_GRID_HEIGHT;
        }

        return Double.parseDouble(prefs.getStringValue(WindowResizeEvents.GRID_HEIGHT)) - 32d;
    }

    public double getToolboxWindowWidth() {
        Preferences prefs = new Preferences();
        if (prefs.getStringValue(WindowResizeEvents.TOOLBOX_WINDOW_WIDTH).isEmpty()) {
            return DEFAULT_TOOLBOX_WINDOW_WIDTH;
        }

        return Double.parseDouble(prefs.getStringValue(WindowResizeEvents.TOOLBOX_WINDOW_WIDTH));
    }

    public double getToolboxWindowHeight() {
        Preferences prefs = new Preferences();
        if (prefs.getStringValue(WindowResizeEvents.TOOLBOX_WINDOW_HEIGHT).isEmpty()) {
            return 0d;
        }

        return Double.parseDouble(prefs.getStringValue(WindowResizeEvents.TOOLBOX_WINDOW_HEIGHT)) - 32d;
    }
    public double getGeometryWindowWidth() {
        Preferences prefs = new Preferences();
        if (prefs.getStringValue(WindowResizeEvents.GEOMETRY_WINDOW_WIDTH).isEmpty()) {
            return DEFAULT_GEOMETRY_WINDOW_WIDTH;
        }

        return Double.parseDouble(prefs.getStringValue(WindowResizeEvents.GEOMETRY_WINDOW_WIDTH));
    }

    public double getGeometryWindowHeight() {
        Preferences prefs = new Preferences();
        if (prefs.getStringValue(WindowResizeEvents.GEOMETRY_WINDOW_HEIGHT).isEmpty()) {
            return DEFAULT_GEOMETRY_WINDOW_HEIGHT;
        }

        return Double.parseDouble(prefs.getStringValue(WindowResizeEvents.GEOMETRY_WINDOW_HEIGHT)) - 48d;
    }
    public double getStateWindowWidth() {
        Preferences prefs = new Preferences();
        if (prefs.getStringValue(WindowResizeEvents.STATE_WINDOW_WIDTH).isEmpty()) {
            return DEFAULT_STATE_WINDOW_WIDTH;
        }

        return Double.parseDouble(prefs.getStringValue(WindowResizeEvents.STATE_WINDOW_WIDTH));
    }

    public double getStateWindowHeight() {
        Preferences prefs = new Preferences();
        if (prefs.getStringValue(WindowResizeEvents.STATE_WINDOW_HEIGHT).isEmpty()) {
            return DEFAULT_STATE_WINDOW_HEIGHT;
        }

        return Double.parseDouble(prefs.getStringValue(WindowResizeEvents.STATE_WINDOW_HEIGHT)) - 48d;
    }

}

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
    }

    public void onDoMainWindowResize() {
        onDoResize();
        onDoMainWindowChangeWidth(this.app.getPrimaryStage().widthProperty());
        onDoMainWindowChangeHeight(this.app.getPrimaryStage().heightProperty());
    }

    public void onDoToolboxWindowResize() {
        onDoChange(this.app.getToolboxStage().widthProperty(), TOOLBOX_WINDOW_WIDTH, "Toolbox window width: {}");
        onDoChange(this.app.getToolboxStage().heightProperty(), TOOLBOX_WINDOW_HEIGHT, "Toolbox window height: {}");
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

    private void onDoChange(ReadOnlyDoubleProperty stage, String preferenceName, String message) {
        stage.addListener((obs, oldVal, newVal) ->
                {
                    logger.debug(message, newVal);

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
        return readWindowPreference(WindowResizeEvents.MAIN_WINDOW_WIDTH, DEFAULT_MAIN_WINDOW_WIDTH, 0d);
    }

    public double getMainWindowHeight() {
        return readWindowPreference(WindowResizeEvents.MAIN_WINDOW_HEIGHT, DEFAULT_MAIN_WINDOW_HEIGHT, -32d);
    }

    public double getGridWidth() {
        return readWindowPreference(WindowResizeEvents.GRID_WIDTH, DEFAULT_GRID_WIDTH, 0d);
    }

    public double getGridHeight() {
        return readWindowPreference(WindowResizeEvents.GRID_HEIGHT, DEFAULT_GRID_HEIGHT, -32d);
    }

    public double getToolboxWindowWidth() {
        return readWindowPreference(WindowResizeEvents.TOOLBOX_WINDOW_WIDTH, DEFAULT_TOOLBOX_WINDOW_WIDTH, 0d);
    }

    public double getToolboxWindowHeight() {
        return readWindowPreference(WindowResizeEvents.TOOLBOX_WINDOW_HEIGHT, 0d, -32d);
    }

    private double readWindowPreference(String preferenceName, double defaultValue, double offset) {
        Preferences prefs = new Preferences();
        String value = prefs.getStringValue(preferenceName);
        if (value.isEmpty()) {
            return defaultValue;
        }
        return Double.parseDouble(value) + offset;
    }

}

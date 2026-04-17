package org.alienlabs.adaloveslace.view.window.event;

import javafx.beans.property.ReadOnlyDoubleProperty;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.util.Preferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.alienlabs.adaloveslace.App.DEFAULT_MAIN_WINDOW_X;
import static org.alienlabs.adaloveslace.App.DEFAULT_MAIN_WINDOW_Y;
import static org.alienlabs.adaloveslace.view.window.ToolboxWindow.DEFAULT_TOOLBOX_WINDOW_X;
import static org.alienlabs.adaloveslace.view.window.ToolboxWindow.DEFAULT_TOOLBOX_WINDOW_Y;

public class WindowRepositionEvents {

    private final App app;

    public static final String MAIN_WINDOW_X        = "MAIN_WINDOW_X";
    public static final String MAIN_WINDOW_Y        = "MAIN_WINDOW_Y";
    public static final String TOOLBOX_WINDOW_X     = "TOOLBOX_WINDOW_X";
    public static final String TOOLBOX_WINDOW_Y     = "TOOLBOX_WINDOW_Y";

    private static final Logger logger = LoggerFactory.getLogger(WindowRepositionEvents.class);

    public WindowRepositionEvents(App app) {
        this.app = app;
    }

    public void onWindowsReposition() {
        this.onDoMainWindowReposition();
        this.onDoToolboxWindowReposition();
    }

    public void onDoMainWindowReposition() {
        onDoChange(this.app.getPrimaryStage().xProperty(), MAIN_WINDOW_X, "Main window sceneX: {}");
        onDoChange(this.app.getPrimaryStage().yProperty(), MAIN_WINDOW_Y, "Main window sceneY: {}");
    }

    public void onDoToolboxWindowReposition() {
        onDoChange(this.app.getToolboxStage().xProperty(), TOOLBOX_WINDOW_X, "Toolbox window sceneX: {}");
        onDoChange(this.app.getToolboxStage().yProperty(), TOOLBOX_WINDOW_Y, "Toolbox window sceneY: {}");
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

    public double getMainWindowX() {
        return readWindowPreference(WindowRepositionEvents.MAIN_WINDOW_X, DEFAULT_MAIN_WINDOW_X, 0d);
    }

    public double getMainWindowY() {
        return readWindowPreference(WindowRepositionEvents.MAIN_WINDOW_Y, DEFAULT_MAIN_WINDOW_Y, -16d);
    }

    public double getToolboxWindowX() {
        return readWindowPreference(WindowRepositionEvents.TOOLBOX_WINDOW_X, DEFAULT_TOOLBOX_WINDOW_X, 0d);
    }

    public double getToolboxWindowY() {
        return readWindowPreference(WindowRepositionEvents.TOOLBOX_WINDOW_Y, DEFAULT_TOOLBOX_WINDOW_Y, -16d);
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

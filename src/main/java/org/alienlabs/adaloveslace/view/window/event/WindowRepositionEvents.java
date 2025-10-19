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
        // StateWindow est maintenant intégrée dans ToolboxWindow
        // this.onDoStateWindowReposition();
    }

    public void onDoMainWindowReposition() {
        onDoChangeX(this.app.getPrimaryStage().xProperty(), MAIN_WINDOW_X, "Main window sceneX: {}");
        onDoChangeY(this.app.getPrimaryStage().yProperty(), MAIN_WINDOW_Y, "Main window sceneY: {}");
    }

    public void onDoToolboxWindowReposition() {
        onDoChangeX(this.app.getToolboxStage().xProperty(), TOOLBOX_WINDOW_X, "Toolbox window sceneX: {}");
        onDoChangeY(this.app.getToolboxStage().yProperty(), TOOLBOX_WINDOW_Y, "Toolbox window sceneY: {}");
    }


    private void onDoChangeX(ReadOnlyDoubleProperty stage, String preferenceName, String s) {
        stage.addListener((obs, oldVal, newVal) ->
                {
                    logger.debug(s, newVal);

                    Preferences prefs = new Preferences();
                    prefs.setStringValue(preferenceName, String.valueOf((double) newVal));
                }
        );
    }

    private void onDoChangeY(ReadOnlyDoubleProperty stage, String preferenceName, String s) {
        stage.addListener((obs, oldVal, newVal) ->
                {
                    logger.debug(s, newVal);

                    Preferences prefs = new Preferences();
                    prefs.setStringValue(preferenceName, String.valueOf((double) newVal));
                }
        );
    }

    public double getMainWindowX() {
        Preferences prefs = new Preferences();
        if (prefs.getStringValue(WindowRepositionEvents.MAIN_WINDOW_X).isEmpty()) {
            return DEFAULT_MAIN_WINDOW_X;
        }

        return Double.parseDouble(prefs.getStringValue(WindowRepositionEvents.MAIN_WINDOW_X));
    }

    public double getMainWindowY() {
        Preferences prefs = new Preferences();
        if (prefs.getStringValue(WindowRepositionEvents.MAIN_WINDOW_Y).isEmpty()) {
            return DEFAULT_MAIN_WINDOW_Y;
        }

        return Double.parseDouble(prefs.getStringValue(WindowRepositionEvents.MAIN_WINDOW_Y)) - 16d;
    }

    public double getToolboxWindowX() {
        Preferences prefs = new Preferences();
        if (prefs.getStringValue(WindowRepositionEvents.TOOLBOX_WINDOW_X).isEmpty()) {
            return DEFAULT_TOOLBOX_WINDOW_X;
        }

        return Double.parseDouble(prefs.getStringValue(WindowRepositionEvents.TOOLBOX_WINDOW_X));
    }

    public double getToolboxWindowY() {
        Preferences prefs = new Preferences();
        if (prefs.getStringValue(WindowRepositionEvents.TOOLBOX_WINDOW_Y).isEmpty()) {
            return DEFAULT_TOOLBOX_WINDOW_Y;
        }

        return Double.parseDouble(prefs.getStringValue(WindowRepositionEvents.TOOLBOX_WINDOW_Y)) - 16d;
    }

}

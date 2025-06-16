package org.alienlabs.adaloveslace.view.window.event;

import javafx.beans.property.ReadOnlyDoubleProperty;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.util.Preferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.alienlabs.adaloveslace.App.DEFAULT_MAIN_WINDOW_X;
import static org.alienlabs.adaloveslace.App.DEFAULT_MAIN_WINDOW_Y;
import static org.alienlabs.adaloveslace.view.window.GeometryWindow.DEFAULT_GEOMETRY_WINDOW_X;
import static org.alienlabs.adaloveslace.view.window.GeometryWindow.DEFAULT_GEOMETRY_WINDOW_Y;
import static org.alienlabs.adaloveslace.view.window.StateWindow.DEFAULT_STATE_WINDOW_X;
import static org.alienlabs.adaloveslace.view.window.StateWindow.DEFAULT_STATE_WINDOW_Y;
import static org.alienlabs.adaloveslace.view.window.ToolboxWindow.DEFAULT_TOOLBOX_WINDOW_X;
import static org.alienlabs.adaloveslace.view.window.ToolboxWindow.DEFAULT_TOOLBOX_WINDOW_Y;

public class WindowRepositionEvents {

    private final App app;

    public static final String MAIN_WINDOW_X        = "MAIN_WINDOW_X";
    public static final String MAIN_WINDOW_Y        = "MAIN_WINDOW_Y";
    public static final String TOOLBOX_WINDOW_X     = "TOOLBOX_WINDOW_X";
    public static final String TOOLBOX_WINDOW_Y     = "TOOLBOX_WINDOW_Y";
    public static final String GEOMETRY_WINDOW_X    = "GEOMETRY_WINDOW_X";
    public static final String GEOMETRY_WINDOW_Y    = "GEOMETRY_WINDOW_Y";
    public static final String STATE_WINDOW_X       = "STATE_WINDOW_X";
    public static final String STATE_WINDOW_Y       = "STATE_WINDOW_Y";

    private static final Logger logger = LoggerFactory.getLogger(WindowRepositionEvents.class);

    public WindowRepositionEvents(App app) {
        this.app = app;
    }

    public void onWindowsReposition() {
        this.onDoMainWindowReposition();
        this.onDoToolboxWindowReposition();
        this.onDoGeometryWindowReposition();
        this.onDoStateWindowReposition();
    }

    public void onDoMainWindowReposition() {
        onDoChangeX(this.app.getPrimaryStage().xProperty(), MAIN_WINDOW_X, "Main window x: {}");
        onDoChangeY(this.app.getPrimaryStage().yProperty(), MAIN_WINDOW_Y, "Main window y: {}");
    }

    public void onDoToolboxWindowReposition() {
        onDoChangeX(this.app.getToolboxStage().xProperty(), TOOLBOX_WINDOW_X, "Toolbox window x: {}");
        onDoChangeY(this.app.getToolboxStage().yProperty(), TOOLBOX_WINDOW_Y, "Toolbox window y: {}");
    }

    public void onDoGeometryWindowReposition() {
        onDoChangeX(this.app.getGeometryStage().xProperty(), GEOMETRY_WINDOW_X, "Geometry window x: {}");
        onDoChangeY(this.app.getGeometryStage().yProperty(), GEOMETRY_WINDOW_Y, "Geometry window y: {}");
    }
    public void onDoStateWindowReposition() {
        onDoChangeX(this.app.getStateStage().xProperty(), STATE_WINDOW_X, "State window x: {}");
        onDoChangeY(this.app.getStateStage().yProperty(), STATE_WINDOW_Y, "State window y: {}");
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

        return Double.parseDouble(prefs.getStringValue(WindowRepositionEvents.MAIN_WINDOW_Y));
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

        return Double.parseDouble(prefs.getStringValue(WindowRepositionEvents.TOOLBOX_WINDOW_Y));
    }
    public double getGeometryWindowX() {
        Preferences prefs = new Preferences();
        if (prefs.getStringValue(WindowRepositionEvents.GEOMETRY_WINDOW_X).isEmpty()) {
            return DEFAULT_GEOMETRY_WINDOW_X;
        }

        return Double.parseDouble(prefs.getStringValue(WindowRepositionEvents.GEOMETRY_WINDOW_X));
    }

    public double getGeometryWindowY() {
        Preferences prefs = new Preferences();
        if (prefs.getStringValue(WindowRepositionEvents.GEOMETRY_WINDOW_Y).isEmpty()) {
            return DEFAULT_GEOMETRY_WINDOW_Y;
        }

        return Double.parseDouble(prefs.getStringValue(WindowRepositionEvents.GEOMETRY_WINDOW_Y));
    }
    public double getStateWindowX() {
        Preferences prefs = new Preferences();
        if (prefs.getStringValue(WindowRepositionEvents.STATE_WINDOW_X).isEmpty()) {
            return DEFAULT_STATE_WINDOW_X;
        }

        return Double.parseDouble(prefs.getStringValue(WindowRepositionEvents.STATE_WINDOW_X));
    }

    public double getStateWindowY() {
        Preferences prefs = new Preferences();
        if (prefs.getStringValue(WindowRepositionEvents.STATE_WINDOW_Y).isEmpty()) {
            return DEFAULT_STATE_WINDOW_Y;
        }

        return Double.parseDouble(prefs.getStringValue(WindowRepositionEvents.STATE_WINDOW_Y));
    }

}

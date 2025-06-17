package org.alienlabs.adaloveslace.util;

import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.view.component.button.geometrywindow.move.*;
import org.alienlabs.adaloveslace.view.component.button.statewindow.InvisibleButton;
import org.alienlabs.adaloveslace.view.component.button.statewindow.SelectableButton;
import org.alienlabs.adaloveslace.view.component.button.statewindow.UnselectableButton;
import org.alienlabs.adaloveslace.view.component.button.statewindow.VisibleButton;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.QuitButton;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.file.ExportImageButton;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.file.LoadButton;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.file.SaveAsButton;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid.RedoKnotButton;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid.ResetDiagramButton;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid.ShowHideGridButton;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid.UndoKnotButton;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.alienlabs.adaloveslace.App.LOCALE_COUNTRY;
import static org.alienlabs.adaloveslace.App.LOCALE_LANGUAGE;
import static org.alienlabs.adaloveslace.view.window.ToolboxWindow.restartApp;

public class KeyboardUtil {

    public void initializeKeyboardShorcuts(final App app) {
        Platform.runLater(() -> {
            app.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.UP),
                    () -> UpButton.onMoveKnotUpAction       (app));
            app.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.DOWN),
                    () -> DownButton.onMoveKnotDownAction   (app));
            app.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.LEFT),
                    () -> LeftButton.onMoveKnotLeftAction   (app));
            app.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.RIGHT),
                    () -> RightButton.onMoveKnotRightAction (app));
            app.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.B, KeyCombination.CONTROL_DOWN),
                    () -> SelectableButton.onSetSelectableModeAction(app));
            app.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN),
                    () -> UnselectableButton.onSetUnselectableModeAction(app));
            app.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN),
                    () -> VisibleButton.onSetVisibleAction   (app));
            app.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.W, KeyCombination.CONTROL_DOWN),
                    () -> InvisibleButton.onSetInvisibleAction (app));
            app.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN),
                    FastMoveModeButton::onSwitchFastModeAction);

            app.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN),
                    SaveAsButton::onSaveAsAction);

            app.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN),
                    () -> LoadButton.onLoadAction(app));

            app.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN),
                    ExportImageButton::onExportAction);

            app.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN),
                    QuitButton::onQuitAction);

            app.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN),
                    UndoKnotButton::undoKnot);

            app.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN),
                    RedoKnotButton::redoKnot);

            app.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN),
                    ResetDiagramButton::resetDiagram);

            app.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.G, KeyCombination.CONTROL_DOWN),
                    ShowHideGridButton::showHideGrid);

            app.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN, KeyCodeCombination.ALT_DOWN),
                    () -> {
                        Locale locale = new Locale("fr", "FR");
                        App.resourceBundle = ResourceBundle.getBundle("AdaLovesLace", locale);

                        Preferences prefs = new Preferences();
                        prefs.setStringValue(LOCALE_LANGUAGE, "fr");
                        prefs.setStringValue(LOCALE_COUNTRY, "FR");

                        restartApp();
                    } );

            app.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN, KeyCombination.ALT_DOWN),
                    () -> {
                        Locale locale = new Locale("en", "EN");
                        App.resourceBundle = ResourceBundle.getBundle("AdaLovesLace", locale);

                        Preferences prefs = new Preferences();
                        prefs.setStringValue(LOCALE_LANGUAGE, "en");
                        prefs.setStringValue(LOCALE_COUNTRY, "EN");

                        restartApp();
                    });
        });
    }

}

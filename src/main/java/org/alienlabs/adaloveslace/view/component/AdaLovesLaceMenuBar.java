package org.alienlabs.adaloveslace.view.component;

import javafx.collections.ObservableSet;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.util.Preferences;
import org.alienlabs.adaloveslace.util.PrintUtil;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.QuitButton;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.file.ExportImageButton;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.file.LoadButton;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.file.SaveAsButton;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.file.SaveButton;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid.RedoKnotButton;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid.ResetDiagramButton;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid.ShowHideGridButton;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid.UndoKnotButton;
import org.alienlabs.adaloveslace.view.window.ToolboxWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.alienlabs.adaloveslace.App.*;
import static org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid.ShowHideGridButton.SHOW_HIDE_GRID_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.window.MainWindow.*;
import static org.alienlabs.adaloveslace.view.window.ToolboxWindow.MENU_BAR_Y;

public class AdaLovesLaceMenuBar {

    private ObservableSet<Printer> printers;

    private static final Logger logger = LoggerFactory.getLogger(AdaLovesLaceMenuBar.class);

    public MenuBar createMenuBar(App app) {
        MenuBar menuBar = new MenuBar();

        Menu fileMenu     = new Menu(resourceBundle.getString(FILE));
        Menu editMenu     = new Menu(resourceBundle.getString(EDIT));
        Menu toolMenu     = new Menu(resourceBundle.getString(TOOL));
        Menu languageMenu = new Menu(resourceBundle.getString(LANGUAGE));

        MenuItem saveItem = new MenuItem(resourceBundle.getString(SAVE_FILE));
        saveItem.setOnAction(_ -> SaveButton.onSaveAction(app));
        saveItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));

        MenuItem saveAsItem = new MenuItem(resourceBundle.getString(SAVE_FILE_AS));
        saveAsItem.setOnAction(_ -> SaveAsButton.onSaveAsAction());
        saveAsItem.setAccelerator(SAVE_AS_KEY_COMBINATION);

        MenuItem loadItem = new MenuItem(resourceBundle.getString(LOAD_FILE));
        loadItem.setOnAction(_ -> LoadButton.onLoadAction());
        loadItem.setAccelerator(new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN));

        MenuItem exportImageItem = new MenuItem(resourceBundle.getString(EXPORT_IMAGE));
        exportImageItem.setOnAction(_ -> ExportImageButton.onExportAction());
        exportImageItem.setAccelerator(new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN));

        SeparatorMenuItem separator1 = new SeparatorMenuItem();

        MenuItem quitItem = new MenuItem(resourceBundle.getString(QUIT_APP));
        quitItem.setOnAction(_ -> QuitButton.onQuitAction());
        quitItem.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN));

        MenuItem undoKnotItem = new MenuItem(resourceBundle.getString(UNDO_KNOT));
        undoKnotItem.setOnAction(_ -> UndoKnotButton.undoKnot());
        undoKnotItem.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));

        MenuItem redoKnotItem = new MenuItem(resourceBundle.getString(REDO_KNOT));
        redoKnotItem.setOnAction(_ -> RedoKnotButton.redoKnot());
        redoKnotItem.setAccelerator(new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN));

        SeparatorMenuItem separator2 = new SeparatorMenuItem();

        MenuItem resetDiagramItem = new MenuItem(resourceBundle.getString(RESET_DIAGRAM));
        resetDiagramItem.setOnAction(_ -> ResetDiagramButton.resetDiagram());
        resetDiagramItem.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));

        MenuItem showHideGridItem = new MenuItem(resourceBundle.getString(SHOW_HIDE_GRID_BUTTON_NAME));
        showHideGridItem.setOnAction(_ -> ShowHideGridButton.showHideGrid());
        showHideGridItem.setAccelerator(new KeyCodeCombination(KeyCode.G, KeyCombination.CONTROL_DOWN));

        SeparatorMenuItem separator3 = new SeparatorMenuItem();

        MenuItem getPrintersItem = new MenuItem(resourceBundle.getString(GET_PRINTERS_BUTTON_NAME));
        getPrintersItem.setOnAction(_ -> {
            printers = Printer.getAllPrinters();
            TextArea printersTextArea = new TextArea();

            for (Printer printer : printers) {
                printersTextArea.appendText(printer.getName() + "\n");
            }
        });
        getPrintersItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));

        MenuItem printItem = new MenuItem(resourceBundle.getString(PRINT_BUTTON_NAME));
        printItem.setOnAction(_ -> {
            if (!printers.isEmpty()) {
                logger.debug("Printing attempt of diagram");

                Printer printer = printers.iterator().next();
                logger.debug("Printing attempt of diagram with printer {}", printer.getName());

                PrinterJob pJ = PrinterJob.createPrinterJob(printer);

                // Show the print setup dialog
                boolean proceed = pJ.showPrintDialog(app.getPrimaryStage());

                if (proceed) {
                    new PrintUtil(app).print(pJ);
                } else {
                    logger.debug("Printing diagram aborted by user!");
                }
            }
        });
        printItem.setAccelerator(new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN));

        MenuItem frenchItem = new MenuItem(FRENCH);
        frenchItem.setOnAction(_ -> {
            Locale locale = new Locale("fr", "FR");
            App.resourceBundle = ResourceBundle.getBundle("AdaLovesLace", locale);

            Preferences prefs = new Preferences();
            prefs.setStringValue(LOCALE_LANGUAGE, "fr");
            prefs.setStringValue(LOCALE_COUNTRY, "FR");

            ToolboxWindow.restartApp();
        } );
        frenchItem.setAccelerator(new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN));

        MenuItem englishItem = new MenuItem(ENGLISH);
        englishItem.setOnAction(_ -> {
            Locale locale = new Locale("en", "EN");
            App.resourceBundle = ResourceBundle.getBundle("AdaLovesLace", locale);

            Preferences prefs = new Preferences();
            prefs.setStringValue(LOCALE_LANGUAGE, "en");
            prefs.setStringValue(LOCALE_COUNTRY, "EN");

            ToolboxWindow.restartApp();
        });
        englishItem.setAccelerator(new KeyCodeCombination(KeyCode.U, KeyCombination.CONTROL_DOWN));

        fileMenu.getItems().addAll(saveItem, saveAsItem, loadItem, exportImageItem, separator1, quitItem);
        editMenu.getItems().addAll(undoKnotItem, redoKnotItem, separator2, resetDiagramItem);
        toolMenu.getItems().addAll(showHideGridItem, separator3, getPrintersItem, printItem);
        languageMenu.getItems().addAll(frenchItem, englishItem);

        menuBar.getStyleClass().add("main-menu");
        menuBar.getMenus().addAll(fileMenu, editMenu, toolMenu, languageMenu);
        menuBar.setTranslateY(MENU_BAR_Y);

        return menuBar;
    }

}

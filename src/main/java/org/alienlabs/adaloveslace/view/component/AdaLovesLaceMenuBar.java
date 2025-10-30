package org.alienlabs.adaloveslace.view.component;

import javafx.collections.ObservableSet;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.Knot;
import org.alienlabs.adaloveslace.util.FileUtil;
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.alienlabs.adaloveslace.App.*;
import static org.alienlabs.adaloveslace.domain.Diagram.newStep;
import static org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid.ShowHideGridButton.SHOW_HIDE_GRID_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.window.MainWindow.*;
import static org.alienlabs.adaloveslace.view.window.ToolboxWindow.MENU_BAR_Y;

public class AdaLovesLaceMenuBar {

    private ObservableSet<Printer> printers;

    private static final String LANGUAGE            = "Language";
    private static final String TOOL                = "Tool";
    private static final String QUICK_START         = "QuickStart";
    private static final String MISCELLANEOUS       = "Miscellaneous";
    private static final String CORNER_TO_CORNER    = "CornerToCorner";
    private static final String CROCHET             = "Crochet";
    private static final String KNITTING            = "Knitting";
    private static final String KNITTING_KNITTING   = "KnittingKnitting";
    private static final String EMBROIDERY          = "Embroidery";
    private static final String MOSAIC_CROCHET      = "MosaicCrochet";
    private static final String DIAMOND_PAINTING    = "DiamondPainting";
    private static final String PIXEL_ART           = "PixelArt";
    private static final String CROSS_STITCH        = "CrossStitch";
    private static final String TAPESTRY            = "Tapestry";
    private static final String PEYOTE_WEAVING      = "PeyoteWeaving";
    private static final String EDIT                = "Edit";
    private static final String FILE                = "File";

    private static final Logger logger              = LoggerFactory.getLogger(AdaLovesLaceMenuBar.class);

    public MenuBar createMenuBar(App app) {
        MenuBar menuBar = new MenuBar();

        Menu fileMenu       = new Menu(resourceBundle.getString(FILE));
        Menu editMenu       = new Menu(resourceBundle.getString(EDIT));
        Menu toolMenu       = new Menu(resourceBundle.getString(TOOL));
        Menu quickStartMenu = new Menu(resourceBundle.getString(QUICK_START));
        Menu languageMenu   = new Menu(resourceBundle.getString(LANGUAGE));

        MenuItem saveItem = new MenuItem(resourceBundle.getString(SAVE_FILE));
        saveItem.setOnAction(_ -> SaveButton.onSaveAction(app));
        saveItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));

        MenuItem saveAsItem = new MenuItem(resourceBundle.getString(SAVE_FILE_AS));
        saveAsItem.setOnAction(_ -> SaveAsButton.onSaveAsAction());
        saveAsItem.setAccelerator(SAVE_AS_KEY_COMBINATION);

        MenuItem loadItem = new MenuItem(resourceBundle.getString(LOAD_FILE));
        loadItem.setOnAction(_ -> LoadButton.onLoadAction(app));
        loadItem.setAccelerator(new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN));

        MenuItem exportImageItem = new MenuItem(resourceBundle.getString(EXPORT_IMAGE));
        exportImageItem.setOnAction(_ -> ExportImageButton.onExportAction());
        exportImageItem.setAccelerator(new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN));

        SeparatorMenuItem separator1 = new SeparatorMenuItem();

        MenuItem quitItem = new MenuItem(resourceBundle.getString(QUIT_APP));
        quitItem.setOnAction(event -> new QuitButton(app, resourceBundle.getString(QUIT_APP)).onQuitAction(event));
        quitItem.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN));

        MenuItem undoKnotItem = new MenuItem(resourceBundle.getString(UNDO_KNOT));
        undoKnotItem.setOnAction(_ -> UndoKnotButton.undoKnot());
        undoKnotItem.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));

        MenuItem redoKnotItem = new MenuItem(resourceBundle.getString(REDO_KNOT));
        redoKnotItem.setOnAction(_ -> RedoKnotButton.redoKnot());
        redoKnotItem.setAccelerator(new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN));

        MenuItem selectAllItem = new MenuItem(resourceBundle.getString(SELECT_ALL));
        selectAllItem.setOnAction(_ -> selectAllKnots(app));
        selectAllItem.setAccelerator(new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN));

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
                logger.info("Printing attempt of diagram");

                Printer printer = printers.iterator().next();
                logger.info("Printing attempt of diagram with printer {}", printer.getName());

                PrinterJob pJ = PrinterJob.createPrinterJob(printer);

                // Show the print setup dialog
                boolean proceed = pJ.showPrintDialog(app.getPrimaryStage());

                if (proceed) {
                    new PrintUtil(app).print(pJ);
                } else {
                    logger.info("Printing diagram aborted by user!");
                }
            }
        });
        printItem.setAccelerator(new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN));

        Menu crochetSubMenu         = new Menu(resourceBundle.getString(CROCHET));
        Menu knittingSubMenu        = new Menu(resourceBundle.getString(KNITTING));
        Menu embroiderySubMenu      = new Menu(resourceBundle.getString(EMBROIDERY));
        Menu miscellaneousSubMenu   = new Menu(resourceBundle.getString(MISCELLANEOUS));

        MenuItem cornerToCornerItem = new MenuItem(resourceBundle.getString(CORNER_TO_CORNER));
        cornerToCornerItem.setOnAction(_ ->
                this.loadQuickstartDiagram(app, "Corner to corner.lace")
        );
        MenuItem crochetItem = new MenuItem(resourceBundle.getString(CROCHET));
        crochetItem.setOnAction(_ ->
                this.loadQuickstartDiagram(app, "Crochet.lace")
        );
        MenuItem mosaicCrochetItem = new MenuItem(resourceBundle.getString(MOSAIC_CROCHET));
        mosaicCrochetItem.setOnAction(_ ->
                this.loadQuickstartDiagram(app, "Crochet mosaique.lace")
        );
        MenuItem knittingKnittingItem = new MenuItem(resourceBundle.getString(KNITTING_KNITTING));
        knittingKnittingItem.setOnAction(_ ->
                this.loadQuickstartDiagram(app, "Tricot.lace")
        );
        MenuItem diamondPaintingItem = new MenuItem(resourceBundle.getString(DIAMOND_PAINTING));
        diamondPaintingItem.setOnAction(_ ->
                this.loadQuickstartDiagram(app, "Diamond painting.lace")
        );
        MenuItem pixelArtItem = new MenuItem(resourceBundle.getString(PIXEL_ART));
        pixelArtItem.setOnAction(_ ->
                this.loadQuickstartDiagram(app, "Pixel-art.lace")
        );
        MenuItem crossStitchItem = new MenuItem(resourceBundle.getString(CROSS_STITCH));
        crossStitchItem.setOnAction(_ ->
                this.loadQuickstartDiagram(app, "Point de croix.lace")
        );
        MenuItem tapestryItem = new MenuItem(resourceBundle.getString(TAPESTRY));
        tapestryItem.setOnAction(_ ->
                this.loadQuickstartDiagram(app, "Tapisserie.lace")
        );
        MenuItem peyoteWeavingItem = new MenuItem(resourceBundle.getString(PEYOTE_WEAVING));
        peyoteWeavingItem.setOnAction(_ ->
                this.loadQuickstartDiagram(app, "Tissage peyote.lace")
        );

        MenuItem frenchItem = new MenuItem(FRENCH);
        frenchItem.setOnAction(_ -> {
            Locale locale = new Locale("fr", "FR");
            App.resourceBundle = ResourceBundle.getBundle("AdaLovesLace", locale);

            Preferences prefs = new Preferences();
            prefs.setStringValue(LOCALE_LANGUAGE, "fr");
            prefs.setStringValue(LOCALE_COUNTRY, "FR");

            ToolboxWindow.restartApp();
        } );
        frenchItem.setAccelerator(new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN, KeyCombination.ALT_DOWN));

        MenuItem englishItem = new MenuItem(ENGLISH);
        englishItem.setOnAction(_ -> {
            Locale locale = new Locale("en", "EN");
            App.resourceBundle = ResourceBundle.getBundle("AdaLovesLace", locale);

            Preferences prefs = new Preferences();
            prefs.setStringValue(LOCALE_LANGUAGE, "en");
            prefs.setStringValue(LOCALE_COUNTRY, "EN");

            ToolboxWindow.restartApp();
        });
        englishItem.setAccelerator(new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN, KeyCombination.ALT_DOWN));

        fileMenu.getItems().addAll(saveItem, saveAsItem, loadItem, exportImageItem, separator1, quitItem);
        editMenu.getItems().addAll(undoKnotItem, redoKnotItem, selectAllItem, separator2, resetDiagramItem);
        toolMenu.getItems().addAll(showHideGridItem, separator3, getPrintersItem, printItem);
        crochetSubMenu.getItems().addAll(cornerToCornerItem, crochetItem, mosaicCrochetItem, tapestryItem);
        knittingSubMenu.getItems().addAll(knittingKnittingItem);
        embroiderySubMenu.getItems().addAll(crossStitchItem);
        miscellaneousSubMenu.getItems().addAll(diamondPaintingItem, pixelArtItem, peyoteWeavingItem);
        quickStartMenu.getItems().addAll(crochetSubMenu, knittingSubMenu, embroiderySubMenu, miscellaneousSubMenu);
        languageMenu.getItems().addAll(frenchItem, englishItem);

        menuBar.getStyleClass().add("main-menu");
        menuBar.getMenus().addAll(fileMenu, editMenu, toolMenu, quickStartMenu, languageMenu);
        menuBar.setTranslateY(MENU_BAR_Y);

        return menuBar;
    }

    public static void selectAllKnots(App app) {
        List<Knot> allSelected = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getAllVisibleKnots());
        newStep(new ArrayList<>(), allSelected, true);
    }

    private void loadQuickstartDiagram(App app, String diagramName) {
        String path = DIAGRAMS_DIRECTORY + diagramName;
        URL url = getClass().getResource(path);
        logger.info("Found URL? {}", url);

        URL resourceUrl = AdaLovesLaceMenuBar.class.getResource(path);
        File toLoad;

        if (resourceUrl == null) {
            throw new IllegalArgumentException("Resource not found: " + diagramName);
        }

        if ("file".equals(resourceUrl.getProtocol())) {
            try {
                toLoad = Paths.get(resourceUrl.toURI()).toFile();
                new FileUtil().buildUiFromLaceFile(app, toLoad);
            } catch (URISyntaxException e) {
                logger.error("Error loading quickstart image: {}", diagramName, e);
            }
        } else {
            try (InputStream in = resourceUrl.openStream()) {
                File file = File.createTempFile(diagramName, "");
                file.deleteOnExit();
                Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);

                new FileUtil().buildUiFromLaceFile(app, file);
            } catch (IOException e) {
                logger.error("Error loading quickstart image: {}", diagramName, e);
            }
        }
    }

}

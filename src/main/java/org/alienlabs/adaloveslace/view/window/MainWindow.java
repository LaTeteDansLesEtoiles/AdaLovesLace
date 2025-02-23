package org.alienlabs.adaloveslace.view.window;

import javafx.collections.ObservableSet;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Diagram;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.util.Events;
import org.alienlabs.adaloveslace.util.NodeUtil;
import org.alienlabs.adaloveslace.util.Preferences;
import org.alienlabs.adaloveslace.util.PrintUtil;
import org.alienlabs.adaloveslace.view.component.OptionalDotGrid;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.alienlabs.adaloveslace.App.*;
import static org.alienlabs.adaloveslace.business.model.Diagram.newStep;
import static org.alienlabs.adaloveslace.util.Events.moveDraggedAndDroppedNodes;
import static org.alienlabs.adaloveslace.view.component.button.toolboxwindow.ShowHideGridButton.SHOW_HIDE_GRID_BUTTON_NAME;

public class MainWindow {

  private static final double FOOTER_X      = Double.parseDouble(resourceBundle.getString("FOOTER_X"));
  public static final double MENU_BAR_Y     = 0d;
  public static final double  NEW_KNOT_GAP  = 15d;
  public static final String LANGUAGE = "Language";
  public static final String TOOL = "Tool";
  public static final String EDIT = "Edit";
  public static final String FILE = "File";

  public MenuBar menuBar;
  private OptionalDotGrid optionalDotGrid;
  private TilePane footer;

  public static final String SAVE_FILE      = "Save";

  public static final String SAVE_FILE_AS   = "SaveAs";

  public static final String FRENCH         = "Français";

  public static final String ENGLISH        = "English";

  public static final String LOAD_FILE      = "Load";

  public static final String EXPORT_IMAGE   = "ExportAnImage";

  public static final String QUIT_APP       = "Quit";

  public static final String UNDO_KNOT      = "UndoKnot";

  public static final String REDO_KNOT      = "RedoKnot";

  public static final String RESET_DIAGRAM  = "ResetDiagram";

  public static final String MOUSE_CLICKED  = "MOUSE_CLICKED";

  public static final KeyCodeCombination SAVE_AS_KEY_COMBINATION = new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN);

  private ObservableSet<Printer> printers;
  private StackPane grid;

  private static final Logger logger        = LoggerFactory.getLogger(MainWindow.class);

  public MainWindow() {
    // Just to be able to unit test code using the UI without effectively instantiating the UI
  }

  public void createMenuBar(Group parent, App app, Stage primaryStage) {
    menuBar = new MenuBar();

    Menu fileMenu     = new Menu(resourceBundle.getString(FILE));
    Menu editMenu     = new Menu(resourceBundle.getString(EDIT));
    Menu toolMenu     = new Menu(resourceBundle.getString(TOOL));
    Menu languageMenu = new Menu(resourceBundle.getString(LANGUAGE));

    MenuItem saveItem = new MenuItem(resourceBundle.getString(SAVE_FILE));
    saveItem.setOnAction(actionEvent -> SaveButton.onSaveAction(app));
    saveItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));

    MenuItem saveAsItem = new MenuItem(resourceBundle.getString(SAVE_FILE_AS));
    saveAsItem.setOnAction(actionEvent -> SaveAsButton.onSaveAsAction(app));
    saveAsItem.setAccelerator(SAVE_AS_KEY_COMBINATION);

    MenuItem loadItem = new MenuItem(resourceBundle.getString(LOAD_FILE));
    loadItem.setOnAction(actionEvent -> LoadButton.onLoadAction(app));
    loadItem.setAccelerator(new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN));

    MenuItem exportImageItem = new MenuItem(resourceBundle.getString(EXPORT_IMAGE));
    exportImageItem.setOnAction(actionEvent -> ExportImageButton.onExportAction(app));
    exportImageItem.setAccelerator(new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN));

    SeparatorMenuItem separator1 = new SeparatorMenuItem();

    MenuItem quitItem = new MenuItem(resourceBundle.getString(QUIT_APP));
    quitItem.setOnAction(actionEvent -> QuitButton.onQuitAction());
    quitItem.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN));

    MenuItem undoKnotItem = new MenuItem(resourceBundle.getString(UNDO_KNOT));
    undoKnotItem.setOnAction(actionEvent -> UndoKnotButton.undoKnot(app));
    undoKnotItem.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));

    MenuItem redoKnotItem = new MenuItem(resourceBundle.getString(REDO_KNOT));
    redoKnotItem.setOnAction(actionEvent -> RedoKnotButton.redoKnot(app));
    redoKnotItem.setAccelerator(new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN));

    SeparatorMenuItem separator2 = new SeparatorMenuItem();

    MenuItem resetDiagramItem = new MenuItem(resourceBundle.getString(RESET_DIAGRAM));
    resetDiagramItem.setOnAction(actionEvent -> ResetDiagramButton.resetDiagram(app));
    resetDiagramItem.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));

    MenuItem showHideGridItem = new MenuItem(resourceBundle.getString(SHOW_HIDE_GRID_BUTTON_NAME));
    showHideGridItem.setOnAction(actionEvent -> ShowHideGridButton.showHideGrid(app));
    showHideGridItem.setAccelerator(new KeyCodeCombination(KeyCode.G, KeyCombination.CONTROL_DOWN));

    SeparatorMenuItem separator3 = new SeparatorMenuItem();

    MenuItem getPrintersItem = new MenuItem(resourceBundle.getString(GET_PRINTERS_BUTTON_NAME));
    getPrintersItem.setOnAction(event -> {
      printers = Printer.getAllPrinters();

      for (Printer printer : printers) {
        app.getToolboxWindow().getPrintersTextArea().appendText(printer.getName() + "\n");
      }
    });
    getPrintersItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));

    MenuItem printItem = new MenuItem(resourceBundle.getString(PRINT_BUTTON_NAME));
    printItem.setOnAction(actionEvent -> {
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
    frenchItem.setOnAction(actionEvent -> {
      Locale locale = new Locale("fr", "FR");
      App.resourceBundle = ResourceBundle.getBundle("AdaLovesLace", locale);

      Preferences prefs = new Preferences();
      prefs.setStringValue(LOCALE_LANGUAGE, "fr");
      prefs.setStringValue(LOCALE_COUNTRY, "FR");

      restartApp(app, primaryStage);
    } );
    frenchItem.setAccelerator(new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN));

    MenuItem englishItem = new MenuItem(ENGLISH);
    englishItem.setOnAction(actionEvent -> {
      Locale locale = new Locale("en", "EN");
      App.resourceBundle = ResourceBundle.getBundle("AdaLovesLace", locale);

      Preferences prefs = new Preferences();
      prefs.setStringValue(LOCALE_LANGUAGE, "en");
      prefs.setStringValue(LOCALE_COUNTRY, "EN");

      restartApp(app, primaryStage);
    });
    englishItem.setAccelerator(new KeyCodeCombination(KeyCode.U, KeyCombination.CONTROL_DOWN));

    fileMenu.getItems().addAll(saveItem, saveAsItem, loadItem, exportImageItem, separator1, quitItem);
    editMenu.getItems().addAll(undoKnotItem, redoKnotItem, separator2, resetDiagramItem);
    toolMenu.getItems().addAll(showHideGridItem, separator3, getPrintersItem, printItem);
    languageMenu.getItems().addAll(frenchItem, englishItem);

    menuBar.getMenus().addAll(fileMenu, editMenu, toolMenu, languageMenu);
    menuBar.setTranslateY(MENU_BAR_Y);
    parent.getChildren().addAll(menuBar);
  }

  private void restartApp(App app, Stage primaryStage) {
    app.getGeometryWindow().getGeometryStage().close();
    app.getToolboxWindow().getToolboxStage().close();
    app.getPrimaryStage().close();
    app.start(primaryStage);
  }

  public TilePane createFooter(String javafxVersion, String javaVersion) {
    footer = new TilePane(Orientation.VERTICAL);
    footer.getChildren().addAll(new Label(resourceBundle.getString("Pitch")));
    footer.getChildren().addAll(new Label(resourceBundle.getString("Copyright")));
    footer.getChildren().addAll(new Label(resourceBundle.getString("License")));
    footer.getChildren().addAll(new Label("JavaFX " + javafxVersion + resourceBundle.getString("RunningWith") + javaVersion));
    footer.setAlignment(Pos.BOTTOM_LEFT);
    footer.setTranslateX(FOOTER_X);
    footer.setTranslateY(MAIN_WINDOW_HEIGHT - 100d);
    return footer;
  }

  public StackPane createGrid(App app, final double width, final double height, final double radius,
                              final Diagram diagram, final Group canvas) {
    if (width == 0d || height == 0d) {
      this.optionalDotGrid = new OptionalDotGrid(app, diagram, canvas);
    } else {
      this.optionalDotGrid = new OptionalDotGrid(app, width, height, radius, diagram, canvas);
    }

    grid = new StackPane(this.optionalDotGrid);

    if (width != 0d && height != 0d) {
      grid.setPrefWidth(width);
      grid.setPrefHeight(height);
    }

    grid.setAlignment(Pos.TOP_LEFT);

    return grid;
  }

  public void onMainWindowClicked(final App app, final Group canvas) {
    canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, Events.getMouseClickEventHandler(app));
  }

  public void onDragOverHandleWithSelectionMode(App app, double x, double y) {
    Circle handle = (Circle)app.getOptionalDotGrid().getDragOriginKnot().getHandle();
    moveDraggedAndDroppedNodes(app, x, y, handle);
  }

  public void onClickWithSelectionMode(App app) {
    Iterator<Knot> it = optionalDotGrid.getDiagram().getCurrentStep().getAllVisibleKnots().iterator();
    boolean hasClickedOnAGivenKnot = false;
    List<Knot> displayedKnots = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots());
    List<Knot> selectedKnots = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots());

    // We iterate on the Knots as long as they are still Knots left to iterate
    // And we stop at the first clicked Knot
    while (it.hasNext()) {
      Knot knot = it.next();

      hasClickedOnAGivenKnot = new NodeUtil().isMouseOverKnot(knot);

      if (hasClickedOnAGivenKnot && (knot.getSelection() == null)) {
        logger.debug("Clicked Knot {} in order to select it", knot.getPattern().getFilename());

        // If the "Control" key is pressed, we are in multi-selection mode
        if (!app.getCurrentlyActiveKeys().containsKey(KeyCode.CONTROL)) {
          Knot copiedKnot = new NodeUtil().copyKnot(knot);
          displayedKnots.addAll(new ArrayList<>(selectedKnots));
          displayedKnots.remove(knot);
          selectedKnots.clear();
          selectedKnots.add(copiedKnot);

          app.getOptionalDotGrid().getDiagram().setCurrentKnot(copiedKnot);
          newStep(displayedKnots, selectedKnots, true);
        } else {
          Knot copiedKnot = new NodeUtil().copyKnot(knot);
          selectedKnots.add(copiedKnot);
          displayedKnots.remove(knot);

          app.getOptionalDotGrid().getDiagram().setCurrentKnot(copiedKnot);
          newStep(displayedKnots, selectedKnots, true);
        }

        break;
      } else if (hasClickedOnAGivenKnot) {
        logger.debug("Clicked Knot displayed {}, pattern {} in order to unselect it",
          app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots().contains(knot), knot.getPattern().getFilename());
        logger.debug("Clicked Knot selected {}, pattern {} in order to unselect it",
          app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().contains(knot), knot.getPattern().getFilename());

        // If the "Control" key is pressed, we are in multi-selection mode
        if (!app.getCurrentlyActiveKeys().containsKey(KeyCode.CONTROL)) {
          Knot copiedKnot = new NodeUtil().copyKnot(knot);
          displayedKnots.addAll(new ArrayList<>(selectedKnots));
          selectedKnots.clear();
          selectedKnots.add(copiedKnot);
          displayedKnots.remove(knot);

          app.getOptionalDotGrid().getDiagram().setCurrentKnot(copiedKnot);
          newStep(displayedKnots, selectedKnots, true);

          break;
        } else {
          Knot copiedKnot = new NodeUtil().copyKnot(knot);
          copiedKnot.setSelection(null);
          selectedKnots.remove(knot);
          displayedKnots.add(copiedKnot);

          app.getOptionalDotGrid().getDiagram().setCurrentKnot(copiedKnot);
          newStep(displayedKnots, selectedKnots, true);
          break;
        }
      }
    }

    // If we have clicked elsewhere, we deselect all knots
    if (!hasClickedOnAGivenKnot) {
      displayedKnots.addAll(new ArrayList<>(selectedKnots));
      selectedKnots.clear();

      newStep(displayedKnots, selectedKnots, true);
    }
  }

  public void onClickWithDeletionMode(App app, Diagram diagram) {
    for (Knot knot : app.getOptionalDotGrid().getDiagram().getCurrentStep().getAllVisibleKnots()) {
      if (new NodeUtil().isMouseOverKnot(knot)) {
        removeKnotIfClicked(app, diagram, knot);
        break;
      }
    }
  }

  private void removeKnotIfClicked(App app, Diagram diagram, Knot knot) {
    app.getOptionalDotGrid().getRoot().getChildren().remove(knot.getImageView());
    app.getOptionalDotGrid().getDiagram().deleteNodesFromFollowingSteps(app, knot);

    List<Knot> displayedKnotsToFilterOut = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots());
    displayedKnotsToFilterOut.remove(knot);
    List<Knot> selectedKnotsToFilterOut = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots());
    selectedKnotsToFilterOut.remove(knot);

    newStep(displayedKnotsToFilterOut, selectedKnotsToFilterOut, true);

    logger.debug("Removing Knot {}, current index = {}", knot, diagram.getCurrentStepIndex());
  }

  @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
    value = "EI_EXPOSE_REP",
    justification = "Copying a CanvasWithOptionalDotGrid, which is a stage, would mean working with another window")
  public OptionalDotGrid getOptionalDotGrid() {
    return this.optionalDotGrid;
  }

  public MenuBar getMenuBar() {
    return this.menuBar;
  }

  public TilePane getFooter() {
    return this.footer;
  }

  public StackPane getGrid() {
    return this.grid;
  }

  public void setOptionalDotGrid(OptionalDotGrid optionalDotGrid) {
    this.optionalDotGrid = optionalDotGrid;
  }

}

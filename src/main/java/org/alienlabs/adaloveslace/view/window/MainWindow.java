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
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Diagram;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.util.*;
import org.alienlabs.adaloveslace.view.component.OptionalDotGrid;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.alienlabs.adaloveslace.App.*;
import static org.alienlabs.adaloveslace.business.model.Knot.DEFAULT_ROTATION;
import static org.alienlabs.adaloveslace.business.model.Knot.DEFAULT_ZOOM;
import static org.alienlabs.adaloveslace.view.component.button.toolboxwindow.ShowHideGridButton.SHOW_HIDE_GRID_BUTTON_NAME;

public class MainWindow {

  private static final double FOOTER_X      = Double.parseDouble(resourceBundle.getString("FOOTER_X"));
  public static final double MENU_BAR_Y     = -10d;
  public static final double  NEW_KNOT_GAP  = 15d;
  public static final String LANGUAGE = "Language";
  public static final String TOOL = "Tool";
  public static final String EDIT = "Edit";
  public static final String FILE = "File";

  public final MenuBar menuBar;
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
    menuBar = new MenuBar();
  }

  public void createMenuBar(Group root, App app, Stage primaryStage) {
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
    root.getChildren().addAll(menuBar);
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
    return footer;
  }

  public StackPane createGrid(final double width, final double height, final double radius,
                              final Diagram diagram, final Group root) {
    if (width == 0d || height == 0d) {
      this.optionalDotGrid = new OptionalDotGrid(diagram, root);
    } else {
      this.optionalDotGrid = new OptionalDotGrid(width, height, radius, diagram, root);
    }

    grid = new StackPane(this.optionalDotGrid);

    if (width != 0d && height != 0d) {
      grid.setPrefWidth(width);
      grid.setPrefHeight(height);
    }

    grid.setAlignment(Pos.TOP_LEFT);

    return grid;
  }

  public void onMainWindowClicked(final App app, final Group root) {
    root.addEventHandler(MouseEvent.MOUSE_CLICKED, Events.getMouseClickEventHandler(app));
  }

  public void onClickWithDuplicationMode(App app, Diagram diagram, double x, double y) {
    for (Knot knot : diagram.getKnots()) {
      try {
        if ((new NodeUtil().isMouseOverKnot(knot, x, y)) && (duplicateKnot(app, x, y, knot) != null)) {
          continue;
        }
      } catch (MalformedURLException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public void onClickWithDrawMode(App app, Diagram diagram, Knot knot) {
    diagram.setCurrentKnot(knot);
    diagram.setKnotSelected(false);
    app.getOptionalDotGrid().layoutChildren();
  }

  public void onClickWithSelectionMode(App app, double x, double y) {
    Iterator<Knot> it = optionalDotGrid.getDiagram().getKnots().iterator();
    boolean hasClickedOnAKnot = false;

    // We iterate on the Knots as long as they are still Knots left to iterate
    // And we stop at the first clicked Knot
    while (it.hasNext() && !hasClickedOnAKnot) {
      Knot knot = it.next();
      Knot clickedKnot;

      try {
        hasClickedOnAKnot = (app.getOptionalDotGrid().getAllHoveredKnots().contains(knot)) && (new NodeUtil().isMouseOverKnot(knot, x, y));

        if (hasClickedOnAKnot && !app.getOptionalDotGrid().getAllSelectedKnots().contains(knot)) {
          logger.info("Clicked Knot index {}, uuid {} in order to select it",
            app.getOptionalDotGrid().getDiagram().getKnots().indexOf(knot), knot.getUuid());

          // If there is a current knot and we have clicked somewhere else than on a knot,
          // we shall restore the zoom & rotation spinners values with the values from the knot
          app.getOptionalDotGrid().getDiagram().setCurrentKnot(knot);

          // If the "Control" key is pressed, we are in multi-selection mode
          if (!app.getCurrentlyActiveKeys().containsKey(KeyCode.CONTROL)) {
            app.getOptionalDotGrid().clearSelections();
            app.getOptionalDotGrid().getAllSelectedKnots().clear();
          }

          app.getOptionalDotGrid().getAllSelectedKnots().add(knot);
          app.getOptionalDotGrid().layoutChildren();
        } else if (hasClickedOnAKnot) {
          logger.info("Clicked Knot index {}, uuid {} in order to unselect it",
            app.getOptionalDotGrid().getDiagram().getKnots().indexOf(knot), knot.getUuid());

          // If there is a current knot and we have clicked somewhere else than on a knot,
          // we shall restore the zoom & rotation spinners values with the values from the knot
          app.getOptionalDotGrid().getDiagram().setCurrentKnot(null);

          // If the "Control" key is pressed, we are in multi-selection mode
          if (!app.getCurrentlyActiveKeys().containsKey(KeyCode.CONTROL)) {
            app.getOptionalDotGrid().clearSelections();
            app.getOptionalDotGrid().getAllSelectedKnots().clear();
            app.getOptionalDotGrid().getAllSelectedKnots().add(knot);
          } else {
            app.getOptionalDotGrid().getAllSelectedKnots().remove(knot);
          }

          app.getOptionalDotGrid().layoutChildren();
        }
      } catch (MalformedURLException e) {
        throw new RuntimeException(e);
      }
    }

    // If there is a current knot and we have clicked somewhere else than on a knot,
    // we shall move the current knot
    if (!hasClickedOnAKnot && this.getOptionalDotGrid().getDiagram().getCurrentKnot() != null) {
      logger.info("Shall move knot");
      Knot currentKnot = this.getOptionalDotGrid().getDiagram().getCurrentKnot();
      app.getOptionalDotGrid().clearSelection(currentKnot);
      moveKnot(currentKnot, x - this.getOptionalDotGrid().getDiagram().getCurrentPattern().getWidth(),
        y - this.getOptionalDotGrid().getDiagram().getCurrentPattern().getHeight());
      app.getOptionalDotGrid().getAllSelectedKnots().add(app.getOptionalDotGrid().drawSelectedKnot(currentKnot));
      app.getOptionalDotGrid().clearAllGuideLines();
    }

  }

  private void moveKnot(Knot toMove, double x, double y) {
    toMove.setX(x);
    toMove.setY(y);
    toMove.getImageView().setX(x);
    toMove.getImageView().setY(y);

    optionalDotGrid.layoutChildren();
  }

  public void onClickWithDeletionMode(App app, Diagram diagram, double x, double y) {
    for (Knot knot : diagram.getKnots()) {

      try {
        if (removeKnotIfClicked(app, diagram, x, y, knot)) {
          return;
        }
      } catch (MalformedURLException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public Knot duplicateKnot(App app, double x, double y, Knot knot) {
    logger.info("Duplicating Knot {}", knot);

    try (FileInputStream fis = new FileInputStream(knot.getPattern().getAbsoluteFilename())) {
      Knot newKnot = newKnot(app, x, y, knot, fis);
      newKnot.setRotationAngle(knot.getRotationAngle());
      newKnot.setZoomFactor(knot.getZoomFactor());
      newKnot.setFlippedVertically(knot.isFlippedVertically());
      newKnot.setFlippedHorizontally(knot.isFlippedHorizontally());
      optionalDotGrid.layoutChildren();

      return newKnot;
    } catch (IOException e) {
      logger.error("Problem with pattern resource file!", e);
    }
    return null;
  }

  private Knot newKnot(App app, double x, double y, Knot knot, FileInputStream fis) {
    Knot newKnot = new Knot();
    newKnot.setX(x + NEW_KNOT_GAP);
    newKnot.setY(y + NEW_KNOT_GAP);
    newKnot.setPattern(knot.getPattern());
    newKnot.setRotationAngle(DEFAULT_ROTATION);
    newKnot.setZoomFactor(DEFAULT_ZOOM);
    newKnot.setVisible(true);

    new FileUtil().buildKnotImageView(app, newKnot, fis);
    return newKnot;
  }

  private boolean removeKnotIfClicked(App app, Diagram diagram, double x, double y, Knot knot) throws MalformedURLException {
    if (app.getOptionalDotGrid().getAllHoveredKnots().contains(knot)) {
      knot.setVisible(false);
      app.getOptionalDotGrid().getAllHoveredKnots().remove(knot);

      if (app.getOptionalDotGrid().getDiagram().getCurrentKnotIndex() == app.getOptionalDotGrid().getDiagram().getKnots().indexOf(knot)) {
        app.getOptionalDotGrid().getDiagram().setCurrentKnotIndex(app.getOptionalDotGrid().getDiagram().getCurrentKnotIndex() - 1);
      }

      if (knot.getSelection() != null) {
        app.getOptionalDotGrid().clearSelection(knot);
        app.getOptionalDotGrid().clearGuideLines(knot);
      }

      logger.info("Removing Knot {}, current index = {}", knot, diagram.getCurrentKnotIndex());
      app.getOptionalDotGrid().layoutChildren();
      return true;
    }

    return false;
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
    return footer;
  }

  public StackPane getGrid() {
    return grid;
  }

}

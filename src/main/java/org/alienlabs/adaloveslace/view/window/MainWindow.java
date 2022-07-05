package org.alienlabs.adaloveslace.view.window;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Diagram;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.util.FileUtil;
import org.alienlabs.adaloveslace.util.NodeUtil;
import org.alienlabs.adaloveslace.view.component.OptionalDotGrid;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.*;
import org.alienlabs.adaloveslace.view.component.spinner.RotationSpinner;
import org.alienlabs.adaloveslace.view.component.spinner.ZoomSpinner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;

import static org.alienlabs.adaloveslace.view.component.button.toolboxwindow.ShowHideGridButton.SHOW_HIDE_GRID_BUTTON_NAME;

public class MainWindow {

  private static final double FOOTER_X      = 60d;
  public static final double MENU_BAR_Y     = -10d;
  public static final double  NEW_KNOT_GAP  = 15d;
  public final MenuBar menuBar;
  private OptionalDotGrid optionalDotGrid;
  private TilePane footer;

  public static final String SAVE_FILE      = "Save";

  public static final String SAVE_FILE_AS   = "Save as";

  public static final String LOAD_FILE      = "Load";

  public static final String EXPORT_IMAGE   = "Export an image";

  public static final String QUIT_APP       = "Quit";

  public static final String UNDO_KNOT      = "Undo knot";

  public static final String REDO_KNOT      = "Redo knot";

  public static final String RESET_DIAGRAM  = "Reset diagram";

  public static final String MOUSE_CLICKED  = "MOUSE_CLICKED";

  public static final KeyCodeCombination SAVE_AS_KEY_COMBINATION = new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN);

  private static final Logger logger        = LoggerFactory.getLogger(MainWindow.class);

  public MainWindow() {
    menuBar = new MenuBar();
  }

  public void createMenuBar(Group root, App app) {
    Menu fileMenu = new Menu("File");
    Menu editMenu = new Menu("Edit");
    Menu toolMenu = new Menu("Tool");

    MenuItem saveItem = new MenuItem(SAVE_FILE);
    saveItem.setOnAction(actionEvent -> SaveButton.onSaveAction(app));
    saveItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));

    MenuItem saveAsItem = new MenuItem(SAVE_FILE_AS);
    saveAsItem.setOnAction(actionEvent -> SaveAsButton.onSaveAsAction(app));
    saveAsItem.setAccelerator(SAVE_AS_KEY_COMBINATION);

    MenuItem loadItem = new MenuItem(LOAD_FILE);
    loadItem.setOnAction(actionEvent -> LoadButton.onLoadAction(app));
    loadItem.setAccelerator(new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN));

    MenuItem exportImageItem = new MenuItem(EXPORT_IMAGE);
    exportImageItem.setOnAction(actionEvent -> ExportImageButton.onExportAction(app));
    exportImageItem.setAccelerator(new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN));

    SeparatorMenuItem separator1 = new SeparatorMenuItem();

    MenuItem quitItem = new MenuItem(QUIT_APP);
    quitItem.setOnAction(actionEvent -> QuitButton.onQuitAction());
    quitItem.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN));

    MenuItem undoKnotItem = new MenuItem(UNDO_KNOT);
    undoKnotItem.setOnAction(actionEvent -> UndoKnotButton.undoKnot(app));
    undoKnotItem.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));

    MenuItem redoKnotItem = new MenuItem(REDO_KNOT);
    redoKnotItem.setOnAction(actionEvent -> RedoKnotButton.redoKnot(app));
    redoKnotItem.setAccelerator(new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN));

    SeparatorMenuItem separator2 = new SeparatorMenuItem();

    MenuItem resetDiagramItem = new MenuItem(RESET_DIAGRAM);
    resetDiagramItem.setOnAction(actionEvent -> ResetDiagramButton.resetDiagram(app));
    resetDiagramItem.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));

    MenuItem showHideGridItem = new MenuItem(SHOW_HIDE_GRID_BUTTON_NAME);
    showHideGridItem.setOnAction(actionEvent -> ShowHideGridButton.showHideGrid(app));
    showHideGridItem.setAccelerator(new KeyCodeCombination(KeyCode.G, KeyCombination.CONTROL_DOWN));


    fileMenu.getItems().addAll(saveItem, saveAsItem, loadItem, exportImageItem, separator1, quitItem);
    editMenu.getItems().addAll(undoKnotItem, redoKnotItem, separator2, resetDiagramItem);
    toolMenu.getItems().addAll(showHideGridItem);

    menuBar.getMenus().addAll(fileMenu, editMenu, toolMenu);
    menuBar.setTranslateY(MENU_BAR_Y);
    root.getChildren().addAll(menuBar);
  }

  public TilePane createFooter(String javafxVersion, String javaVersion) {
    footer = new TilePane(Orientation.VERTICAL);
    footer.getChildren().addAll(new Label("Ada Loves Lace - A tatting lace patterns creation software"));
    footer.getChildren().addAll(new Label("© 2022 AlienLabs"));
    footer.getChildren().addAll(new Label("This is Free Software under GPL license"));
    footer.getChildren().addAll(new Label("JavaFX " + javafxVersion + ", running on Java " + javaVersion));
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

    StackPane grid = new StackPane(this.optionalDotGrid);

    if (width != 0d && height != 0d) {
      grid.setPrefWidth(width);
      grid.setPrefHeight(height);
    }

    grid.setAlignment(Pos.TOP_LEFT);

    return grid;
  }

  public void onMainWindowClicked(final Group root) {
    root.setOnMouseClicked(event -> {
      String eType = event.getEventType().toString();
      logger.info("Event type -> {},  current Knot index {}, current mode: {}", eType,
        this.getOptionalDotGrid().getDiagram().getCurrentKnotIndex(),
        this.getOptionalDotGrid().getDiagram().getCurrentMode());

      if (eType.equals(MOUSE_CLICKED)) {
        double x          = event.getX();
        double y          = event.getY();
        double yMinusTop  = y - OptionalDotGrid.TOP_MARGIN;

        logger.info("Coordinate X     -> {}", x);
        logger.info("Coordinate Y     -> {}, Y - TOP -> {}", y, yMinusTop);

        processMouseClick(x, y);
      }
    });
  }

  private void processMouseClick(double x, double y) {
    switch (this.getOptionalDotGrid().getDiagram().getCurrentMode()) {
      case DRAWING      -> onClickWithDrawMode(this.getOptionalDotGrid().getDiagram(),
        optionalDotGrid.addKnot(x, y), false);
      case SELECTION    -> onClickWithSelectionMode(x, y);
      case DELETION     -> onClickWithDeletionMode(this.getOptionalDotGrid().getDiagram(), x, y) ;
      case DUPLICATION  -> onClickWithDuplicationMode(this.getOptionalDotGrid().getDiagram(), x, y);
    }
  }

  private void onClickWithDrawMode(Diagram diagram, Knot knot, boolean knotSelected) {
    knot.setZoomFactor(1);
    diagram.setCurrentKnot(knot);
    diagram.setKnotSelected(knotSelected);
  }

  private void onClickWithSelectionMode(double x, double y) {
    Iterator<Knot> it = optionalDotGrid.getDiagram().getKnots().iterator();
    boolean hasClickedOnAKnot = false;

    // We iterate on the Knots as long as they are still Knots left to iterate
    // And we stop at the first clicked Knot
    while (it.hasNext() && !hasClickedOnAKnot) {
      Knot knot = it.next();
      try {
        hasClickedOnAKnot = new NodeUtil().isClicked(knot, x, y);

        if (hasClickedOnAKnot) {
          logger.info("Clicked Knot index {}", this.getOptionalDotGrid().getDiagram().getKnots().indexOf(knot));

          // If there is a current knot and we have clicked somewhere else than on a knot,
          // we shall restore the zoom & rotation spinners values with the values from the knot
          this.getOptionalDotGrid().getDiagram().setCurrentKnot(knot);
          RotationSpinner.restoreRotationSpinnersState(knot);
          ZoomSpinner.restoreZoomSpinnersState(knot);

        }
      } catch (MalformedURLException e) {
        throw new RuntimeException(e);
      }
    }

    // If there is a current knot and we have clicked somewhere else than on a knot,
    // we shall move the current knot
    if (!hasClickedOnAKnot) {
      moveKnot(this.getOptionalDotGrid().getDiagram().getCurrentKnot(), x, y);
    }

  }

  private void moveKnot(Knot toMove, double x, double y) {
    toMove.setX(x);
    toMove.setY(y);
    toMove.getImageView().setX(x);
    toMove.getImageView().setY(y);
    toMove.getImageView().setOpacity(1.0d);

    optionalDotGrid.layoutChildren();
  }

  private void onClickWithDeletionMode(Diagram diagram, double x, double y) {
    for (Knot knot : diagram.getKnots()) {
      try {
        if (removeKnotIfClicked(diagram, x, y, knot)) {
          optionalDotGrid.layoutChildren();

          return;
        }
      } catch (MalformedURLException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private void onClickWithDuplicationMode(Diagram diagram, double x, double y) {
    for (Knot knot : diagram.getKnots()) {
      try {
        if ((new NodeUtil().isClicked(knot, x, y)) && (duplicateKnot(diagram, x, y, knot))) {
          break;
        }
      } catch (MalformedURLException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private boolean duplicateKnot(Diagram diagram, double x, double y, Knot knot) {
    logger.info("Duplicating Knot {}", knot);

    try (FileInputStream fis = new FileInputStream(knot.getPattern().getAbsoluteFilename())) {
      Knot newKnot = newKnot(x, y, knot, fis);
      diagram.addKnot(newKnot);
      optionalDotGrid.layoutChildren();

      return true;
    } catch (IOException e) {
      logger.error("Problem with pattern resource file!", e);
    }
    return false;
  }

  private Knot newKnot(double x, double y, Knot knot, FileInputStream fis) {
    Knot newKnot = new Knot();
    newKnot.setX(x + NEW_KNOT_GAP);
    newKnot.setY(y + NEW_KNOT_GAP);
    newKnot.setPattern(knot.getPattern());
    newKnot.setRotationAngle(0);
    newKnot.setZoomFactor(1);
    newKnot.setVisible(true);

    new FileUtil().buildKnotImageView(newKnot, fis);
    return newKnot;
  }

  private boolean removeKnotIfClicked(Diagram diagram, double x, double y, Knot knot) throws MalformedURLException {
    if (new NodeUtil().isClicked(knot, x, y)) {
      logger.info("Removing Knot {}", knot);
      knot.setVisible(false);

      if (diagram.getCurrentKnotIndex() >= diagram.getKnots().size()) {
        // We shall shift left the current Knot index if it is after the end of the Knot list
        diagram.setCurrentKnotIndex(diagram.getKnots().size());
      }

      return true;
    }

    return false;
  }

  private boolean drawKnot(double x, double y, boolean hasClickedOnAKnot, Knot knot) {
    try {
      if (new NodeUtil().isClicked(knot, x, y)) {
        // We have clicked on a knot, it shall be the current knot
        logger.info("Knot is clicked: {}", knot.getPattern().getFilename());
        onClickWithDrawMode(optionalDotGrid.getDiagram(), knot, true);
        hasClickedOnAKnot = true;
      }
    } catch (MalformedURLException e) {
      logger.error("Error reading pattern image");
    }
    return hasClickedOnAKnot;
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

}

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
import org.alienlabs.adaloveslace.util.NodeUtil;
import org.alienlabs.adaloveslace.view.component.OptionalDotGrid;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.util.Iterator;

import static org.alienlabs.adaloveslace.view.component.button.toolboxwindow.ShowHideGridButton.SHOW_HIDE_GRID_BUTTON_NAME;

public class MainWindow {

  public final MenuBar menuBar;
  private OptionalDotGrid optionalDotGrid;

  public static final String SAVE_FILE      = "Save";

  public static final String SAVE_FILE_AS   = "Save as";

  public static final String LOAD_FILE      = "Load";

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


    fileMenu.getItems().addAll(saveItem, saveAsItem, loadItem, separator1, quitItem);
    editMenu.getItems().addAll(undoKnotItem, redoKnotItem, separator2, resetDiagramItem);
    toolMenu.getItems().addAll(showHideGridItem);

    menuBar.getMenus().addAll(fileMenu, editMenu, toolMenu);
    root.getChildren().addAll(menuBar);
  }

  public TilePane createFooter(String javafxVersion, String javaVersion) {
    TilePane footer           = new TilePane(Orientation.VERTICAL);
    footer.getChildren().addAll(new Label("Ada Loves Lace - A tatting lace patterns creation software"));
    footer.getChildren().addAll(new Label("© 2022 AlienLabs"));
    footer.getChildren().addAll(new Label("This is Free Software under GPL license"));
    footer.getChildren().addAll(new Label("JavaFX " + javafxVersion + ", running on Java " + javaVersion));
    footer.setAlignment(Pos.BOTTOM_LEFT);
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
      logger.info("Event type -> {}", eType);

      if (eType.equals(MOUSE_CLICKED)) {
        double x          = event.getX();
        double y          = event.getY();
        double yMinusTop  = y - OptionalDotGrid.TOP_MARGIN;

        logger.info("Coordinate X     -> {}", x);
        logger.info("Coordinate Y     -> {}, Y - TOP -> {}", y, yMinusTop);

        switch (this.getOptionalDotGrid().getDiagram().getCurrentMode()) {
          case DRAWING    -> onClickWithDrawMode(this.getOptionalDotGrid().getDiagram(),
            optionalDotGrid.addKnot(x, y), false);
          case SELECTION  -> onClickWithSelectionMode(x, y);
        }
      }
    });
  }

  private void onClickWithDrawMode(Diagram diagram, Knot optionalDotGrid, boolean knotSelected) {
    diagram.setCurrentKnot(optionalDotGrid);
    diagram.setKnotSelected(knotSelected);
  }

  private void onClickWithSelectionMode(double x, double y) {
    Iterator<Knot> it = optionalDotGrid.getDiagram().getKnots().iterator();
    boolean hasClickedOnAKnot = false;

    while (it.hasNext()) {
      hasClickedOnAKnot = drawKnot(x, y, hasClickedOnAKnot, it.next());
    }

    // If there is a current knot and we have clicked somewhere else than on a knot,
    // we shall move the current knot
    moveKnot(x, y, hasClickedOnAKnot);
  }

  private void moveKnot(double x, double y, boolean hasClickedOnAKnot) {
    if (optionalDotGrid.getDiagram().isKnotSelected() && !hasClickedOnAKnot) {
      Knot toMove = optionalDotGrid.getDiagram().getCurrentKnot();
      toMove.setX(x);
      toMove.setY(y);
      toMove.getImageView().setX(x);
      toMove.getImageView().setY(y);
      toMove.getImageView().setOpacity(1.0d);

      optionalDotGrid.layoutChildren();
    }
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

}

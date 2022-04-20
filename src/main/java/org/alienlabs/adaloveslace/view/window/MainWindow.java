package org.alienlabs.adaloveslace.view.window;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Diagram;
import org.alienlabs.adaloveslace.view.component.CanvasWithOptionalDotGrid;
import org.alienlabs.adaloveslace.view.component.button.QuitButton;
import org.alienlabs.adaloveslace.view.component.button.SaveAsButton;
import org.alienlabs.adaloveslace.view.component.button.ShowHideGridButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.alienlabs.adaloveslace.view.component.button.QuitButton.QUIT_APP;
import static org.alienlabs.adaloveslace.view.component.button.SaveAsButton.SAVE_FILE_AS_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.component.button.ShowHideGridButton.SHOW_HIDE_GRID_BUTTON_NAME;

public class MainWindow {

  public final MenuBar menuBar;
  private CanvasWithOptionalDotGrid canvasWithOptionalDotGrid;

  public static final String SAVE_FILE      = "Save";
  public static final String LOAD_FILE      = "Load";

  public static final String MOUSE_CLICKED  = "MOUSE_CLICKED";

  public static final KeyCodeCombination SAVE_AS_KEY_COMBINATION = new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN);

  private static final Logger logger        = LoggerFactory.getLogger(MainWindow.class);

  public MainWindow() {
    menuBar = new MenuBar();
  }

  public void createMenuBar(GridPane root, App app) {
    Menu fileMenu = new Menu("File");
    Menu toolMenu = new Menu("Tool");

    MenuItem showHideGridItem = new MenuItem(SHOW_HIDE_GRID_BUTTON_NAME);
    showHideGridItem.setOnAction(actionEvent -> ShowHideGridButton.showHideGrid(app));
    showHideGridItem.setAccelerator(new KeyCodeCombination(KeyCode.G, KeyCombination.CONTROL_DOWN));

    MenuItem saveItem = new MenuItem(SAVE_FILE);
    saveItem.setOnAction(actionEvent -> QuitButton.onQuitAction());
    saveItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));

    MenuItem saveAsItem = new MenuItem(SAVE_FILE_AS_BUTTON_NAME);
    saveAsItem.setOnAction(actionEvent -> SaveAsButton.onSaveAsAction(app, root));
    saveAsItem.setAccelerator(SAVE_AS_KEY_COMBINATION);

    MenuItem loadItem = new MenuItem(LOAD_FILE);
    loadItem.setOnAction(actionEvent -> QuitButton.onQuitAction());
    loadItem.setAccelerator(new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN));

    SeparatorMenuItem separator = new SeparatorMenuItem();

    MenuItem quitItem = new MenuItem(QUIT_APP);
    quitItem.setOnAction(actionEvent -> QuitButton.onQuitAction());
    quitItem.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN));

    fileMenu.getItems().addAll(saveItem, saveAsItem, loadItem, separator, quitItem);
    toolMenu.getItems().addAll(showHideGridItem);

    menuBar.getMenus().addAll(fileMenu, toolMenu);

    VBox vBox = new VBox(menuBar); //Gives vertical box
    root.getChildren().addAll(vBox);
  }

  public TilePane createFooter(String javafxVersion, String javaVersion) {
    TilePane footer           = new TilePane(Orientation.VERTICAL);
    footer.getChildren().addAll(new Label("Ada Loves Lace - A tatting lace patterns creation software"));
    footer.getChildren().addAll(new Label("© 2022 AlienLabs"));
    footer.getChildren().addAll(new Label("This is Free Software under GPL license"));
    footer.getChildren().addAll(new Label("JavaFX " + javafxVersion + ", running on Java " + javaVersion));
    footer.setAlignment(Pos.BOTTOM_CENTER);
    return footer;
  }

  public GridPane createGridPane(StackPane grid, TilePane footer) {
    GridPane root             = new GridPane();
    GridPane.setConstraints(grid, 0, 0);
    GridPane.setConstraints(footer, 0, 1);
    root.getChildren().addAll(grid, footer);
    return root;
  }

  public StackPane createGrid(final double width, final double height, final double radius, final Diagram diagram) {
    if (width == 0d || height == 0d) {
      this.canvasWithOptionalDotGrid = new CanvasWithOptionalDotGrid(diagram);
    } else {
      this.canvasWithOptionalDotGrid = new CanvasWithOptionalDotGrid(width, height, radius, diagram);
    }

    StackPane grid = new StackPane(this.canvasWithOptionalDotGrid);
    grid.setAlignment(Pos.TOP_LEFT);
    return grid;
  }

  public void onMainWindowClicked(final GridPane root) {
    root.setOnMouseClicked(event -> {
      String eType = event.getEventType().toString();
      logger.info("Event type -> {}", eType);

      if (eType.equals(MOUSE_CLICKED)) {
        double x                = event.getX();
        double y                = event.getY();
        double yMinusTop        = y - CanvasWithOptionalDotGrid.TOP_MARGIN;

        logger.info("Coordinate X     -> {}",                 x);
        logger.info("Coordinate Y     -> {}, Y - TOP -> {}",  y, yMinusTop);

        canvasWithOptionalDotGrid.addKnot(x, yMinusTop);
      }
    });
  }

  @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
    value = "EI_EXPOSE_REP",
    justification = "Copying a CanvasWithOptionalDotGrid, which is a stage, would mean working with another window")
  public CanvasWithOptionalDotGrid getCanvasWithOptionalDotGrid() {
    return this.canvasWithOptionalDotGrid;
  }

  public MenuBar getMenuBar() {
    return this.menuBar;
  }

}

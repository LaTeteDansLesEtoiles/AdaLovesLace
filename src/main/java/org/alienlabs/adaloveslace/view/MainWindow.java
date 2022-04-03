package org.alienlabs.adaloveslace.view;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import org.alienlabs.adaloveslace.business.model.Diagram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainWindow {

  public final MenuBar menuBar;
  private CanvasWithOptionalDotGrid canvasWithOptionalDotGrid;

  public static final String QUIT_APP = "Quit";
  public static final String MOUSE_CLICKED = "MOUSE_CLICKED";

  private static final Logger logger = LoggerFactory.getLogger(MainWindow.class);

  public MainWindow() {
    menuBar = new MenuBar();
  }

  public void createMenuBar(GridPane root) {
    Menu menu = new Menu("File");
    MenuItem menuItem = new MenuItem(QUIT_APP);
    menuItem.setOnAction(actionEvent -> onQuitAction());
    menuItem.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN));
    menu.getItems().addAll(menuItem);
    menuBar.getMenus().addAll(menu);
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

  public StackPane createGrid(final Object app, final Diagram diagram, final String classpath) {
    this.canvasWithOptionalDotGrid = new CanvasWithOptionalDotGrid(app, diagram, classpath);
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
    value = "DM_EXIT",
    justification = "We shall exit when we have to, since we are not in a lib")
  private void onQuitAction() {
    logger.info("Exiting app");
    System.exit(0);
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

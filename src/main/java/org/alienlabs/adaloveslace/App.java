package org.alienlabs.adaloveslace;

import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.alienlabs.adaloveslace.business.model.Diagram;
import org.alienlabs.adaloveslace.util.SystemInfo;
import org.alienlabs.adaloveslace.view.component.CanvasWithOptionalDotGrid;
import org.alienlabs.adaloveslace.view.window.GeometryWindow;
import org.alienlabs.adaloveslace.view.window.MainWindow;
import org.alienlabs.adaloveslace.view.window.ToolboxWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.alienlabs.adaloveslace.util.FileUtil.CLASSPATH_RESOURCES_PATH;
import static org.alienlabs.adaloveslace.view.window.ToolboxWindow.TILE_HEIGHT;
import static org.alienlabs.adaloveslace.view.window.ToolboxWindow.TILE_PADDING;

/**
 * JavaFX App
 */
public class App extends Application {

  public static final String ID                       = "#";
  public static final String TOOLBOX_BUTTON           = "toolbox-btn-";
  public static final String TOOLBOX_BUTTON_ID        = ID + TOOLBOX_BUTTON;
  public static final String ADA_LOVES_LACE           = "Ada Loves Lace";
  public static final String MAIN_WINDOW_TITLE        = ADA_LOVES_LACE;
  public static final String PROJECT_NAME             = "adaloveslace";
  public static final String TOOLBOX_TITLE            = "Toolbox";
  public static final String GEOMETRY_TITLE           = "Geometry";
  public static final String LACE_FILE_EXTENSION      = ".lace";
  public static final String PATTERNS_DIRECTORY_NAME  = "patterns";
  public static final String ERROR                    = "Error!";

  public static final double  MAIN_WINDOW_Y           = 10d;
  private static final double MAIN_WINDOW_X           = 50d;
  private static final double RADIUS                  = 2.5d;// The dots from the grid are ellipses, this is their radius

  private static final Logger logger = LoggerFactory.getLogger(App.class);

  private Stage toolboxStage;
  private Stage geometryStage;
  private Diagram diagram;
  private MainWindow mainWindow;

  @Override
  public void start(Stage primaryStage) {
    this.diagram = new Diagram();

    logger.info("Opening geometry window");
    showGeometryWindow(this);

    logger.info("Opening toolbox window");
    showToolboxWindow(this, this, CLASSPATH_RESOURCES_PATH);

    logger.info("Starting app: opening main window");
    showMainWindow(660d, 700d, 0d, 0d, RADIUS, primaryStage);
  }

  public void showMainWindow(double windowWidth, double windowHeight, double canvasWidth, double canvasHeight, double radius, Stage primaryStage) {
    this.mainWindow = new MainWindow();

    var javafxVersion = SystemInfo.javafxVersion();
    var javaVersion   = SystemInfo.javaVersion();

    TilePane footer           = mainWindow.createFooter(javafxVersion, javaVersion);
    StackPane grid            = mainWindow.createGrid(canvasWidth, canvasHeight, radius, this.diagram);
    GridPane root             = mainWindow.createGridPane(grid, footer);
    this.mainWindow.onMainWindowClicked(root);

    var scene                 = new Scene(root, windowWidth, windowHeight);
    primaryStage.setScene(scene);
    primaryStage.setX(MAIN_WINDOW_X);
    primaryStage.setY(MAIN_WINDOW_Y);
    primaryStage.setTitle(MAIN_WINDOW_TITLE);

    this.mainWindow.createMenuBar(root, this);
    primaryStage.show();
  }

  public ToolboxWindow showToolboxWindow(App app, Object classpathBase, String resourcesPath) {
    this.toolboxStage     = new Stage(StageStyle.DECORATED);

    TilePane patternsPane  = new TilePane(Orientation.HORIZONTAL);
    patternsPane.setVgap(TILE_PADDING);
    patternsPane.setPrefColumns(1);
    patternsPane.setPrefTileHeight(TILE_HEIGHT);
    patternsPane.setAlignment(Pos.TOP_CENTER);


    ToolboxWindow toolboxWindow = new ToolboxWindow();
    this.diagram = toolboxWindow.createToolboxPane(patternsPane, classpathBase, resourcesPath, app, this.diagram);
    TilePane buttonsPane = toolboxWindow.createToolboxButtons(app);

    toolboxWindow.createToolboxStage(this.toolboxStage, buttonsPane, patternsPane);
    return toolboxWindow;
  }

  public GeometryWindow showGeometryWindow(App app) {
    this.geometryStage     = new Stage(StageStyle.DECORATED);

    TilePane patternsPane  = new TilePane(Orientation.HORIZONTAL);
    patternsPane.setVgap(TILE_PADDING);
    patternsPane.setPrefColumns(1);
    patternsPane.setPrefTileHeight(TILE_HEIGHT);
    patternsPane.setAlignment(Pos.TOP_CENTER);


    GeometryWindow geometryWindow = new GeometryWindow();
    this.diagram = geometryWindow.createGeometryPane(patternsPane, app, this.diagram);
    TilePane buttonsPane = geometryWindow.createGeometryButtons(app);

    geometryWindow.createGeometryStage(this.geometryStage, buttonsPane, patternsPane);
    return geometryWindow;
  }

  public static void main(String[] args) {
    launch();
  }

  @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
    value = "EI_EXPOSE_REP",
    justification = "Copying a toolbox stage would mean working with another window")
  public Stage getToolboxStage() {
    return this.toolboxStage;
  }

  public void setDiagram(Diagram diagram) {
    this.diagram = diagram;
  }

  public CanvasWithOptionalDotGrid getCanvasWithOptionalDotGrid() {
    return this.mainWindow.getCanvasWithOptionalDotGrid();
  }

  public MainWindow getMainWindow() {
    return this.mainWindow;
  }

}

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
import org.alienlabs.adaloveslace.view.CanvasWithOptionalDotGrid;
import org.alienlabs.adaloveslace.view.MainWindow;
import org.alienlabs.adaloveslace.view.ToolboxWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.alienlabs.adaloveslace.util.FileUtil.CLASSPATH_RESOURCES_PATH;

/**
 * JavaFX App
 */
public class App extends Application {

  public static final String ID                 = "#";
  public static final String TOOLBOX_BUTTON     = "toolbox-btn-";
  public static final String TOOLBOX_BUTTON_ID  = ID + TOOLBOX_BUTTON;
  public static final String ADA_LOVES_LACE     = "Ada Loves Lace";
  public static final String MAIN_WINDOW_TITLE  = ADA_LOVES_LACE;
  public static final String TOOLBOX_TITLE      = "Toolbox";

  private static final Logger logger = LoggerFactory.getLogger(App.class);

  private Stage toolboxStage;
  private Diagram diagram;
  private MainWindow mainWindow;

  @Override
  public void start(Stage primaryStage) {
    this.diagram = new Diagram();

    logger.info("Opening toolbox window");
    showToolboxWindow(this, this, CLASSPATH_RESOURCES_PATH);

    logger.info("Starting app: opening main window");
    showMainWindow(660d, 700d, 0d, 0d, primaryStage);
  }

  public void showMainWindow(double windowWidth, double windowHeight, double canvasWidth, double canvasHeight, Stage primaryStage) {
    mainWindow = new MainWindow();

    var javafxVersion = SystemInfo.javafxVersion();
    var javaVersion   = SystemInfo.javaVersion();

    TilePane footer           = mainWindow.createFooter(javafxVersion, javaVersion);
    StackPane grid            = mainWindow.createGrid(canvasWidth, canvasHeight, this.diagram);
    GridPane root             = mainWindow.createGridPane(grid, footer);
    mainWindow.onMainWindowClicked(root);

    var scene                 = new Scene(root, windowWidth, windowHeight);
    primaryStage.setScene(scene);
    primaryStage.setTitle(MAIN_WINDOW_TITLE);

    mainWindow.createMenuBar(root);
    primaryStage.show();
  }

  public void showToolboxWindow(App app, Object classpathBase, String resourcesPath) {
    this.toolboxStage     = new Stage(StageStyle.DECORATED);
    TilePane toolboxPane  = new TilePane(Orientation.VERTICAL);
    toolboxPane.setAlignment(Pos.TOP_CENTER);

    ToolboxWindow toolboxWindow = new ToolboxWindow();
    this.diagram = toolboxWindow.createToolboxPane(toolboxPane, classpathBase, resourcesPath, app, this.diagram);
    toolboxWindow.createToolboxStage(this.toolboxStage, toolboxPane);
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
    this.diagram = new Diagram(diagram);
  }

  public CanvasWithOptionalDotGrid getCanvasWithOptionalDotGrid() {
    return this.mainWindow.getCanvasWithOptionalDotGrid();
  }

  public MainWindow getMainWindow() {
    return this.mainWindow;
  }

}

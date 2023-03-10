package org.alienlabs.adaloveslace;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.alienlabs.adaloveslace.business.model.Diagram;
import org.alienlabs.adaloveslace.util.SystemInfo;
import org.alienlabs.adaloveslace.view.component.OptionalDotGrid;
import org.alienlabs.adaloveslace.view.component.button.geometrywindow.move.DownButton;
import org.alienlabs.adaloveslace.view.component.button.geometrywindow.move.LeftButton;
import org.alienlabs.adaloveslace.view.component.button.geometrywindow.move.RightButton;
import org.alienlabs.adaloveslace.view.component.button.geometrywindow.move.UpButton;
import org.alienlabs.adaloveslace.view.window.GeometryWindow;
import org.alienlabs.adaloveslace.view.window.MainWindow;
import org.alienlabs.adaloveslace.view.window.ToolboxWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.alienlabs.adaloveslace.util.FileUtil.CLASSPATH_RESOURCES_PATH;
import static org.alienlabs.adaloveslace.view.window.GeometryWindow.GAP_BETWEEN_BUTTONS;

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
  public static final String USER_HOME                = "user.home";
  public static final String TOOLBOX_TITLE            = "Toolbox";
  public static final String GEOMETRY_TITLE           = "Geometry";
  public static final String LACE_FILE_EXTENSION      = ".lace";
  public static final String EXPORT_IMAGE_FILE_FORMAT = "png";
  public static final String EXPORT_IMAGE_FILE_TYPE   = ".png";
  public static final String EXPORT_PDF_FILE_TYPE     = ".pdf";
  public static final String PATTERNS_DIRECTORY_NAME  = "patterns";
  public static final String ERROR                    = "Error!";

  public static final double  MAIN_WINDOW_Y           = 20d;
  private static final double MAIN_WINDOW_X           = 50d;
  public static final double  MAIN_WINDOW_WIDTH       = 500d;
  public static final double  MAIN_WINDOW_HEIGHT      = 680d;
  public static final double  GRID_WIDTH              = 650d;
  public static final double  GRID_HEIGHT             = 650d;
  public static final int     ICON_SIZE               = 46;
  public static final int     SMALL_ICON_SIZE         = 23;

  public static final double  GRID_DOTS_RADIUS        = 2.5d;// The dots from the grid are ellipses, this is their radius

  private static final Logger logger = LoggerFactory.getLogger(App.class);

  private Stage toolboxStage;
  private Diagram diagram;
  private MainWindow mainWindow;
  private Group root;
  private Scene scene;
  private Stage primaryStage;
  private Stage geometryStage;
  private GeometryWindow geometryWindow;
  private ToolboxWindow toolboxWindow;

  @Override
  public void start(Stage primaryStage) {
    this.primaryStage = primaryStage;
    this.diagram = new Diagram();

    logger.info("Opening geometry window");
    showGeometryWindow(this);

    logger.info("Opening toolbox window");
    showToolboxWindow(this, this, CLASSPATH_RESOURCES_PATH);

    logger.info("Starting app: opening main window");
    showMainWindow(MAIN_WINDOW_WIDTH, MAIN_WINDOW_HEIGHT, GRID_WIDTH, GRID_HEIGHT, GRID_DOTS_RADIUS, primaryStage);
  }

  public void showMainWindow(double windowWidth, double windowHeight, double gridWidth, double gridHeight,
                             double gridDotsRadius, Stage primaryStage) {
    this.mainWindow = new MainWindow();

    var javafxVersion = SystemInfo.javafxVersion();
    var javaVersion   = SystemInfo.javaVersion();

    root                      = new Group();
    TilePane footer           = mainWindow.createFooter(javafxVersion, javaVersion);
    StackPane grid            = mainWindow.createGrid(gridWidth, gridHeight, gridDotsRadius, this.diagram, root);

    grid.getChildren().add(footer);
    root.getChildren().add(grid);
    this.mainWindow.onMainWindowClicked(this, root);

    scene = new Scene(root, windowWidth, windowHeight);
    primaryStage.setScene(scene);
    primaryStage.setX(MAIN_WINDOW_X);
    primaryStage.setY(MAIN_WINDOW_Y);
    primaryStage.setTitle(MAIN_WINDOW_TITLE);

    primaryStage.setOnCloseRequest(windowEvent -> {
      logger.info("You shall close the app by closing this window!");
      System.exit(0);
    });

    this.mainWindow.createMenuBar(root, this);
    this.primaryStage = primaryStage;
    primaryStage.show();
  }

  public ToolboxWindow showToolboxWindow(App app, Object classpathBase, String resourcesPath) {
    this.toolboxStage     = new Stage(StageStyle.DECORATED);

    GridPane parent       = newGridPane();
    ScrollPane scrollPane = new ScrollPane(parent);
    scrollPane.setFitToHeight(true);

    BorderPane borderPane = new BorderPane(scrollPane);
    borderPane.setPadding(new Insets(15));
    borderPane.getChildren().add(parent);

    toolboxWindow         = new ToolboxWindow();
    this.diagram          = toolboxWindow.createToolboxPane(parent, classpathBase, resourcesPath, app, this.diagram);
    int posY              = this.diagram.getPatterns().size() / 2 + 1;
    toolboxWindow.createToolboxButtons(parent, app, posY);
    toolboxWindow.createToolboxStage(borderPane, this.toolboxStage, parent, app, posY);
    return toolboxWindow;
  }

  public GeometryWindow showGeometryWindow(App app) {
    geometryStage   = new Stage(StageStyle.DECORATED);
    GridPane parent = newGridPane();
    geometryWindow  = new GeometryWindow();
    geometryWindow.createGeometryButtons(app, parent);
    geometryWindow.createMoveKnotButtons(app, parent);

    geometryWindow.createGeometryStage(app, geometryStage, parent);

    initializeKeyboardShorcuts();
    return geometryWindow;
  }

  public GridPane newGridPane() {
    GridPane parent = new GridPane();
    parent.setAlignment(Pos.TOP_CENTER);
    //Setting the padding
    parent.setPadding(new Insets(10, 10, 10, 10));
    //Setting the vertical and horizontal gaps between the columns
    parent.setVgap(GAP_BETWEEN_BUTTONS);
    parent.setHgap(GAP_BETWEEN_BUTTONS);
    return parent;
  }

  public static void main(String[] args) {
    launch();
  }

  public void initializeKeyboardShorcuts() {
    Platform.runLater(() -> {
      getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.UP),
        () -> UpButton.onMoveKnotUpAction       (this, this.getGeometryWindow()));
      getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.DOWN),
        () -> DownButton.onMoveKnotDownAction   (this, this.getGeometryWindow()));
      getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.LEFT),
        () -> LeftButton.onMoveKnotLeftAction   (this, this.getGeometryWindow()));
      getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.RIGHT),
        () -> RightButton.onMoveKnotRightAction (this, this.getGeometryWindow()));
    });
  }

  @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
    value = "EI_EXPOSE_REP",
    justification = "Copying a toolbox stage would mean working with another window")
  public Stage getToolboxStage() {
    return this.toolboxStage;
  }

  public ToolboxWindow getToolboxWindow() {
    return toolboxWindow;
  }

  public Stage getGeometryStage() {
    return geometryStage;
  }

  public Diagram getDiagram() {
    return diagram;
  }

  public void setDiagram(Diagram diagram) {
    this.diagram = diagram;
  }

  public OptionalDotGrid getOptionalDotGrid() {
    return this.mainWindow.getOptionalDotGrid();
  }

  public MainWindow getMainWindow() {
    return this.mainWindow;
  }

  public GeometryWindow getGeometryWindow() {
    return geometryWindow;
  }

  public Group getRoot() {
    return root;
  }

  public Scene getScene() {
    return scene;
  }

  public Stage getPrimaryStage() {
    return primaryStage;
  }

}

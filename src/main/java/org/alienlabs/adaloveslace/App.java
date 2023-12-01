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
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.alienlabs.adaloveslace.business.model.Diagram;
import org.alienlabs.adaloveslace.business.model.MouseMode;
import org.alienlabs.adaloveslace.util.Preferences;
import org.alienlabs.adaloveslace.util.SystemInfo;
import org.alienlabs.adaloveslace.view.component.OptionalDotGrid;
import org.alienlabs.adaloveslace.view.component.button.geometrywindow.move.*;
import org.alienlabs.adaloveslace.view.component.button.statewindow.InvisibleButton;
import org.alienlabs.adaloveslace.view.component.button.statewindow.SelectableButton;
import org.alienlabs.adaloveslace.view.component.button.statewindow.UnselectableButton;
import org.alienlabs.adaloveslace.view.component.button.statewindow.VisibleButton;
import org.alienlabs.adaloveslace.view.window.GeometryWindow;
import org.alienlabs.adaloveslace.view.window.MainWindow;
import org.alienlabs.adaloveslace.view.window.StateWindow;
import org.alienlabs.adaloveslace.view.window.ToolboxWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import static org.alienlabs.adaloveslace.util.FileUtil.CLASSPATH_RESOURCES_PATH;
import static org.alienlabs.adaloveslace.view.window.GeometryWindow.GAP_BETWEEN_BUTTONS;

/**
 * JavaFX App
 */
public class App extends Application {

  public static final String TOOLBOX_BUTTON           = "toolbox-btn-";
  public static final String ADA_LOVES_LACE           = "AdaLovesLace";
  public static final String MAIN_WINDOW_TITLE        = ADA_LOVES_LACE;
  public static final String PROJECT_NAME             = "adaloveslace";
  public static final String USER_HOME                = "user.home";
  public static final String TOOLBOX_TITLE            = "Toolbox";
  public static final String GEOMETRY_TITLE           = "Geometry";
  public static final String STATE_TITLE              = "State";
  public static final String LACE_FILE_EXTENSION      = ".lace";
  public static final String LACE_FILE_MIME_TYPE      = "application/lace";
  public static final String ADA_LOVES_LACE_WEB       = "https://dentelle.damemarie.club";
  public static final String ADA_LOVES_LACE_WEB_SHARE_ENDPOINT       = "/api/upload-diagram";
  public static final String EXPORT_IMAGE_FILE_FORMAT = "png";

  public static final String EXPORT_IMAGE_CONTENT_TYPE= "image/png";
  public static final String EXPORT_IMAGE_FILE_TYPE   = ".png";
  public static final String EXPORT_PDF_FILE_TYPE     = ".pdf";
  public static final String PATTERNS_DIRECTORY_NAME  = "patterns";
  public static final String ERROR                    = "Error!";
  public static final String ASSETS_DIRECTORY         = "assets/";
  public static final String GET_PRINTERS_BUTTON_NAME = "GetPrinters";
  public static final String PRINT_BUTTON_NAME        = "PrintDiagram";

  public static final double  MAIN_WINDOW_Y           = 20d;
  public static final double  MAIN_WINDOW_X           = 75d;
  public static final double  MAIN_WINDOW_WIDTH       = 520d;
  public static final double  MAIN_WINDOW_HEIGHT      = 680d;
  public static final double  GRID_WIDTH              = 650d;
  public static final double  GRID_HEIGHT             = 650d;
  public static final int     ICON_SIZE               = 46;
  public static final int     SMALL_ICON_SIZE         = 23;

  public static final double  GRID_DOTS_RADIUS        = 2.5d;// The dots from the grid are ellipses, this is their radius
  public static final String LOCALE_LANGUAGE = "LOCALE_LANGUAGE";
  public static final String LOCALE_COUNTRY = "LOCALE_COUNTRY";
  public static final String DEFAULT_LOCALE_LANGUAGE = "fr";
  public static final String DEFAULT_LOCALE_COUNTRY = "FR";
  public static final Duration TOOLTIPS_DURATION = Duration.seconds(60);

  public static ResourceBundle resourceBundle = ResourceBundle.getBundle(
          ADA_LOVES_LACE,
          new Locale(DEFAULT_LOCALE_LANGUAGE,
                  DEFAULT_LOCALE_COUNTRY)
  );

  private static final Logger logger = LoggerFactory.getLogger(App.class);

  private Stage toolboxStage;
  private Diagram diagram;
  private static MainWindow mainWindow;
  private Group root;
  private Scene scene;
  public Stage primaryStage;
  private Stage geometryStage;
  private GeometryWindow geometryWindow;
  private Stage stateStage;
  private StateWindow stateWindow;
  private ToolboxWindow toolboxWindow;

  private final Map<KeyCode, Boolean> currentlyActiveKeys = new EnumMap<>(KeyCode.class);

  @Override
  public void start(Stage primaryStage) {
    this.primaryStage = primaryStage;

    // If we restart the app (for language change)
    if (this.diagram == null) {
      this.diagram = new Diagram(this);
    }

    logger.debug("Starting app: opening main window");
    showMainWindow(MAIN_WINDOW_WIDTH, MAIN_WINDOW_HEIGHT, GRID_WIDTH, GRID_HEIGHT, GRID_DOTS_RADIUS, primaryStage, diagram);

    logger.debug("Opening toolbox window");
    showToolboxWindow(this, this, CLASSPATH_RESOURCES_PATH);

    logger.debug("Opening geometry window");
    showGeometryWindow(this);

    logger.debug("Opening state window");
    showStateWindow(this);
  }

  public void showMainWindow(double windowWidth, double windowHeight, double gridWidth, double gridHeight,
                             double gridDotsRadius, Stage primaryStage, Diagram diagram) {
    App.mainWindow = new MainWindow();
    this.diagram = diagram;

    var javafxVersion = SystemInfo.javafxVersion();
    var javaVersion   = SystemInfo.javaVersion();

    root                      = new Group();
    TilePane footer           = mainWindow.createFooter(javafxVersion, javaVersion);
    StackPane grid            = mainWindow.createGrid(this, gridWidth, gridHeight, gridDotsRadius, this.diagram, root);

    grid.getChildren().add(footer);
    root.getChildren().add(grid);
    App.mainWindow.onMainWindowClicked(this, root);

    scene = new Scene(root, windowWidth, windowHeight);
    scene.setFill(Color.TRANSPARENT);

    // For multi-selection with "Control" key
    scene.setOnKeyPressed(event -> {
      KeyCode codeString = event.getCode();
      if (!currentlyActiveKeys.containsKey(codeString)) {
        currentlyActiveKeys.put(codeString, true);
      }
    });
    scene.setOnKeyReleased(event ->
      currentlyActiveKeys.remove(event.getCode())
    );

    primaryStage.setScene(scene);
    primaryStage.setX(MAIN_WINDOW_X);
    primaryStage.setY(MAIN_WINDOW_Y);
    primaryStage.setTitle(resourceBundle.getString(MAIN_WINDOW_TITLE));

    primaryStage.setOnCloseRequest(windowEvent -> {
      logger.debug("You shall close the app by closing this window!");
      Platform.exit();
    });

    App.mainWindow.createMenuBar(root, this, primaryStage);
    this.getOptionalDotGrid().setDiagram(diagram);
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

    geometryWindow.createGeometryStage(geometryStage, parent);

    initializeKeyboardShorcuts();
    app.getOptionalDotGrid().getDiagram().setCurrentMode(MouseMode.DRAWING);
    return geometryWindow;
  }

  public StateWindow showStateWindow(App app) {
    stateStage   = new Stage(StageStyle.DECORATED);
    GridPane parent = newGridPane();
    stateWindow  = new StateWindow();
    stateWindow.createStateButtons(app, parent);
    stateWindow.createStateStage(stateStage, parent);

    return stateWindow;
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
    Preferences prefs = new Preferences();

    if ((!prefs.getStringValue(LOCALE_LANGUAGE).equals("")) && (!prefs.getStringValue(LOCALE_COUNTRY).equals(""))) {
      Locale locale = new Locale(prefs.getStringValue(LOCALE_LANGUAGE), prefs.getStringValue(LOCALE_COUNTRY));
      resourceBundle = ResourceBundle.getBundle(ADA_LOVES_LACE, locale);
    } else {
      Locale locale = new Locale(DEFAULT_LOCALE_LANGUAGE, DEFAULT_LOCALE_COUNTRY);
      resourceBundle = ResourceBundle.getBundle(ADA_LOVES_LACE, locale);

      prefs.setStringValue(LOCALE_LANGUAGE, DEFAULT_LOCALE_LANGUAGE);
      prefs.setStringValue(LOCALE_COUNTRY, DEFAULT_LOCALE_COUNTRY);
    }

    launch();
  }

  public void initializeKeyboardShorcuts() {
    Platform.runLater(() -> {
      getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.UP),
        () -> UpButton.onMoveKnotUpAction       (this));
      getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.DOWN),
        () -> DownButton.onMoveKnotDownAction   (this));
      getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.LEFT),
        () -> LeftButton.onMoveKnotLeftAction   (this));
      getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.RIGHT),
        () -> RightButton.onMoveKnotRightAction (this));
      getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.S),
        () -> SelectableButton.onSetSelectableModeAction(this));
      getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.T),
        () -> UnselectableButton.onSetUnselectableModeAction(this));
      getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.V),
        () -> VisibleButton.onSetVisibleAction   (this));
      getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.W),
        () -> InvisibleButton.onSetInvisibleAction (this));
      getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.F),
              FastMoveModeButton::onSwitchFastModeAction);
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

  public void setDiagram(Diagram diagram) {
    this.diagram = diagram;
  }

  public OptionalDotGrid getOptionalDotGrid() {
    return mainWindow.getOptionalDotGrid();
  }

  public void setOptionalDotGrid(OptionalDotGrid grid) {
    App.mainWindow.setOptionalDotGrid(grid);
  }

  public MainWindow getMainWindow() {
    return App.mainWindow;
  }

  public void setMainWindow(MainWindow mainWindow) {
    App.mainWindow = mainWindow;
  }

  public GeometryWindow getGeometryWindow() {
    return geometryWindow;
  }

  public StateWindow getStateWindow() {
    return this.stateWindow;
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

  public void setPrimaryStage(Stage primaryStage) {
    this.primaryStage = primaryStage;
  }

  public Stage getStateStage() {
    return this.stateStage;
  }

  public void setStateStage(Stage stateStage) {
    this.stateStage = stateStage;
  }

  public static void setResourceBundle(ResourceBundle resourceBundle) {
    App.resourceBundle = resourceBundle;
  }

  public Map<KeyCode, Boolean> getCurrentlyActiveKeys() {
    return currentlyActiveKeys;
  }

}

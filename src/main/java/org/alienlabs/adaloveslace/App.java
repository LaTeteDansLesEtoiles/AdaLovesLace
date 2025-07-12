package org.alienlabs.adaloveslace;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.alienlabs.adaloveslace.domain.Diagram;
import org.alienlabs.adaloveslace.domain.enumeration.MouseMode;
import org.alienlabs.adaloveslace.util.*;
import org.alienlabs.adaloveslace.view.component.AdaLovesLaceMenuBar;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.QuitButton;
import org.alienlabs.adaloveslace.view.component.grid.OptionalDotGrid;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.ParentGridStrategy;
import org.alienlabs.adaloveslace.view.window.GeometryWindow;
import org.alienlabs.adaloveslace.view.window.MainWindow;
import org.alienlabs.adaloveslace.view.window.StateWindow;
import org.alienlabs.adaloveslace.view.window.ToolboxWindow;
import org.alienlabs.adaloveslace.view.window.event.WindowRepositionEvents;
import org.alienlabs.adaloveslace.view.window.event.WindowResizeEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import static org.alienlabs.adaloveslace.util.FileUtil.CLASSPATH_RESOURCES_PATH;
import static org.alienlabs.adaloveslace.view.window.GeometryWindow.GAP_BETWEEN_BUTTONS;
import static org.alienlabs.adaloveslace.view.window.MainWindow.QUIT_APP;
import static org.alienlabs.adaloveslace.view.window.ToolboxWindow.BUTTON_SELECTED;
import static org.alienlabs.adaloveslace.view.window.event.GridEvents.getMouseDoubleRightClickOnGridEventHendler;

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
  public static final String ADA_LOVES_LACE_WEB       = "http://192.168.1.100:18082"; // https://app.adaloveslace.top
  public static final String ADA_LOVES_LACE_WEB_SHARE_ENDPOINT       = "/api/diagrams/upload-diagram";
  public static final String EXPORT_IMAGE_FILE_FORMAT = "png";

  public static final String EXPORT_IMAGE_CONTENT_TYPE= "image/png";
  public static final String EXPORT_IMAGE_FILE_TYPE   = ".png";
  public static final String EXPORT_PDF_FILE_TYPE     = ".pdf";
  public static final String PATTERNS_DIRECTORY_NAME  = "knots";
  public static final String BACKUP_DIRECTORY_NAME    = "backups";
  public static final String ASSETS_DIRECTORY         = "/assets/";
  public static final String DIAGRAMS_DIRECTORY       = "/diagrams/";
  public static final String GET_PRINTERS_BUTTON_NAME = "GetPrinters";
  public static final String PRINT_BUTTON_NAME        = "PrintDiagram";
  public static final String TEXT_BUTTON_NAME         = "TextButton";
  public static final String ADD_KNOT_BUTTON_NAME     = "AddKnotButton";
  public static final String RESET_KNOTS_BUTTON_NAME  = "ResetKnotsButton";
  public static final String ADD_KNOT_WITH_SIZE_DIALOG_TITLE   = "AddKnotWithSize";
  public static final String COLOR_BUTTON_NAME        = "ColorButton";

  public static final double DEFAULT_MAIN_WINDOW_X    = 75d;
  public static final double DEFAULT_MAIN_WINDOW_Y    = 5d;
  public static final double DEFAULT_MAIN_WINDOW_WIDTH = 525d;
  public static final double DEFAULT_MAIN_WINDOW_HEIGHT = 780d;
  public static final double DEFAULT_GRID_WIDTH       = 650d;
  public static final double DEFAULT_GRID_HEIGHT      = 550d;
  public static final int     ICON_SIZE               = 46;
  public static final int     SMALL_ICON_SIZE         = 23;
  public static final int     CANVAS_TEXT_FONT_SIZE   = 32;

  public static final double  GRID_DOTS_RADIUS        = 2.5d;// The dots from the grid are ellipses, this is their radius
  public static final String LOCALE_LANGUAGE          = "LOCALE_LANGUAGE";
  public static final String LOCALE_COUNTRY           = "LOCALE_COUNTRY";
  public static final String DEFAULT_LOCALE_LANGUAGE  = "fr";
  public static final String DEFAULT_LOCALE_COUNTRY   = "FR";
  public static final Duration TOOLTIPS_DURATION      = Duration.seconds(60);
  public static final double INITIAL_GRID_ZOOM_FACTOR = 1d;
  private ParentGridStrategy gridStrategy;

  public static ResourceBundle resourceBundle = ResourceBundle.getBundle(
          ADA_LOVES_LACE,
          new Locale(DEFAULT_LOCALE_LANGUAGE,
                  DEFAULT_LOCALE_COUNTRY)
  );

  private final Map<KeyCode, Boolean> currentlyActiveKeys = new EnumMap<>(KeyCode.class);

  private Stage toolboxStage;
  private Diagram diagram;
  private static MainWindow mainWindow;
  private Slider slider;
  private Scene scene;
  private Stage geometryStage;
  private GeometryWindow geometryWindow;
  private Stage stateStage;
  private StateWindow stateWindow;
  private static ToolboxWindow toolboxWindow;

  private Pane movablePane;
  private Stage primaryStage;
  private double gridWidth = DEFAULT_GRID_WIDTH;
  private double gridHeight = DEFAULT_GRID_HEIGHT;

  private static final Logger logger = LoggerFactory.getLogger(App.class);
  private WindowResizeEvents resizes;
  private WindowRepositionEvents windowRepositionEvents;

  @Override
  public void start(Stage primaryStage) {
    Application.Parameters params = getParameters();
    String filePath = "";

    if (params.getRaw() != null && !params.getRaw().isEmpty()) {
      filePath = String.join(" ", params.getRaw());
      logger.debug(filePath);
    }

    Font.loadFont(getClass().getResource("/fonts/PatrickHand-Regular.ttf").toExternalForm(), 12);
    primaryStage.initStyle(StageStyle.DECORATED);
    this.primaryStage = primaryStage;

    // If we restart the app (for language change)
    if (this.diagram == null) {
      this.diagram = new Diagram(this);
    }

    logger.debug("Starting app: opening main window");
    this.resizes = new WindowResizeEvents(this);
    this.windowRepositionEvents = new WindowRepositionEvents(this);

    showMainWindow(
            this.resizes.getMainWindowWidth(),
            this.resizes.getMainWindowHeight(),
            this.resizes.getGridWidth(),
            this.resizes.getGridHeight(),
            primaryStage,
            diagram
    );

    logger.debug("Opening toolbox window");
    showToolboxWindow(this, this, CLASSPATH_RESOURCES_PATH);

    logger.debug("Opening geometry window");
    showGeometryWindow(this);

    logger.debug("Opening state window");
    showStateWindow(this);

    if (!filePath.isEmpty()) {
      new FileUtil().buildUiFromLaceFile(this, new File(filePath));
      new NodeUtil().clearTechnicalElements(this);
    }

    this.resizes.onWindowsResize();
    this.windowRepositionEvents.onWindowsReposition();
    this.getPrimaryStage().requestFocus();
  }

  public void showMainWindow(double windowWidth, double windowHeight, double gridWidth, double gridHeight,
                             Stage primaryStage, Diagram diagram) {
    BorderPane root;
    App.mainWindow = new MainWindow();
    this.diagram = diagram;

    var javafxVersion = SystemInfo.javafxVersion();
    var javaVersion   = SystemInfo.javaVersion();

    movablePane               = new Pane();
    movablePane.getStyleClass().add("grid");

    StackPane grid            = mainWindow.createGrid(this, gridWidth, gridHeight, this.diagram, movablePane);

    root                      = new BorderPane();
    root.getStyleClass().add("grid");
    movablePane.getChildren().add(grid);
    root.setCenter(movablePane);

    App.mainWindow.onMainWindowClicked(this, movablePane);

    scene = new Scene(root, windowWidth, windowHeight);
    scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
    scene.setFill(Color.TRANSPARENT);

    onSceneKeyPressed();
    onSceneKeyReleased();

    // On double right-click, center the grid
    scene.setOnMouseClicked(getMouseDoubleRightClickOnGridEventHendler(this));

    primaryStage.setScene(scene);
    primaryStage.setX(this.windowRepositionEvents.getMainWindowX());
    primaryStage.setY(this.windowRepositionEvents.getMainWindowY());
    primaryStage.setTitle(resourceBundle.getString(MAIN_WINDOW_TITLE));

    onCloseApplication(primaryStage);

    slider = createZoomSlider();
    this.getOptionalDotGrid().setDiagram(diagram);
    this.primaryStage = primaryStage;
    grid.getStyleClass().add("grid");

    primaryStage.show();
  }

  private void onCloseApplication(Stage primaryStage) {
    primaryStage.setOnCloseRequest(event -> {
      logger.debug("You shall close the app by closing this window!");
      new QuitButton(this, resourceBundle.getString(QUIT_APP)).onQuitAction(event);
    });
  }

  private void onSceneKeyPressed() {
    // For multi-selection with "Control" key
    scene.setOnKeyPressed(event -> {
      KeyCode codeString = event.getCode();
      if (!currentlyActiveKeys.containsKey(codeString)) {
        currentlyActiveKeys.put(codeString, true);
      }
    });
  }

  private void onSceneKeyReleased() {
    scene.setOnKeyReleased(event ->
      currentlyActiveKeys.remove(event.getCode())
    );
  }

  private Slider createZoomSlider() {
    slider = new Slider();
    slider.setMin(0);
    slider.setMax(100);
    slider.setValue(50);
    slider.setShowTickLabels(true);
    slider.setShowTickMarks(true);
    slider.setMajorTickUnit(10);
    slider.setMinorTickCount(5);
    slider.setBlockIncrement(1);
    slider.setLayoutX(this.resizes.getMainWindowWidth() / 2d - 60d);
    slider.valueProperty().addListener((ov, oldVal, newVal) -> {
      double zoomFactor;

      if (newVal.doubleValue() < 50d) {
        zoomFactor = newVal.doubleValue() / 50d + 0.1d;
      } else if (newVal.doubleValue() > 50d) {
        zoomFactor = (newVal.doubleValue() - 40) / 10d;
      } else {
        zoomFactor = INITIAL_GRID_ZOOM_FACTOR;
      }

      movablePane.setScaleX(zoomFactor);
      movablePane.setScaleY(zoomFactor);
    });
    return slider;
  }

  public ToolboxWindow showToolboxWindow(App app, Object classpathBase, String resourcesPath) {
    this.toolboxStage     = new Stage(StageStyle.DECORATED);

    GridPane parent       = newGridPane();
    ScrollPane scrollPane = new ScrollPane(parent);
    scrollPane.setFitToHeight(true);

    toolboxWindow         = new ToolboxWindow();

    MenuBar menuBar       = new AdaLovesLaceMenuBar().createMenuBar(this);
    parent.add(menuBar, 0, 0);

    this.diagram          = toolboxWindow.createToolboxPane(parent, classpathBase, resourcesPath, app, this.diagram);
    int posY              = this.diagram.getPatterns().size() / 2 + 5;
    toolboxWindow.createToolboxButtons(parent, app, posY);
    toolboxWindow.createToolboxStage(this.toolboxStage, parent, app, posY);
    return toolboxWindow;
  }

  public GeometryWindow showGeometryWindow(App app) {
    geometryStage   = new Stage(StageStyle.DECORATED);
    GridPane parent = newGridPane();
    geometryWindow  = new GeometryWindow();
    geometryWindow.createGeometryButtons(app, parent);
    geometryWindow.createMoveKnotButtons(app, parent);

    geometryWindow.createGeometryStage(app, geometryStage, parent);

    new KeyboardUtil().initializeKeyboardShorcuts(this);
    app.getOptionalDotGrid().getDiagram().setCurrentMode(MouseMode.DRAWING);
    return geometryWindow;
  }

  public StateWindow showStateWindow(App app) {
    stateStage   = new Stage(StageStyle.DECORATED);
    GridPane parent = newGridPane();
    stateWindow  = new StateWindow();
    stateWindow.createStateButtons(app, parent);
    stateWindow.createStateStage(app, stateStage, parent);

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
    setLocale(prefs);
    launch(args);
  }

  private static void setLocale(Preferences prefs) {
    if ((!prefs.getStringValue(LOCALE_LANGUAGE).isEmpty()) && (!prefs.getStringValue(LOCALE_COUNTRY).isEmpty())) {
      Locale locale = new Locale(prefs.getStringValue(LOCALE_LANGUAGE), prefs.getStringValue(LOCALE_COUNTRY));
      resourceBundle = ResourceBundle.getBundle(ADA_LOVES_LACE, locale);
    } else {
      Locale locale = new Locale(DEFAULT_LOCALE_LANGUAGE, DEFAULT_LOCALE_COUNTRY);
      resourceBundle = ResourceBundle.getBundle(ADA_LOVES_LACE, locale);

      prefs.setStringValue(LOCALE_LANGUAGE, DEFAULT_LOCALE_LANGUAGE);
      prefs.setStringValue(LOCALE_COUNTRY, DEFAULT_LOCALE_COUNTRY);
    }
  }

  public void unselectPatternsAndTextButtons() {
    this.getToolboxWindow().getAllPatterns().forEach(toggleButton -> {
      toggleButton.setSelected(false);
      toggleButton.getStyleClass().remove(BUTTON_SELECTED);
    });
    this.getToolboxWindow().getTextButton().setSelected(false);
    this.getToolboxWindow().getTextButton().getStyleClass().remove(BUTTON_SELECTED);
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

  public Pane getMovablePane() {
    return movablePane;
  }

  public void setMovablePane(Pane movablePane) {
    this.movablePane = movablePane;
  }

  public Slider getSlider() {
    return slider;
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

  public double getGridWidth() {
    return this.gridWidth;
  }

  public void setGridWidth(double gridWidth) {
    this.gridWidth = gridWidth;
  }

  public double getGridHeight() {
    return this.gridHeight;
  }

  public void setGridHeight(double gridHeight) {
    this.gridHeight = gridHeight;
  }

  public ParentGridStrategy getGridStrategy() {
    return this.gridStrategy;
  }

  public void setGridStrategy(ParentGridStrategy gridStrategy) {
    this.gridStrategy = gridStrategy;
  }

  public static void setResourceBundle(ResourceBundle resourceBundle) {
    App.resourceBundle = resourceBundle;
  }

  public Map<KeyCode, Boolean> getCurrentlyActiveKeys() {
    return this.currentlyActiveKeys;
  }

  public WindowResizeEvents getResizes() {
    return this.resizes;
  }

  public void setResizes(WindowResizeEvents resizes) {
    this.resizes = resizes;
  }

  public WindowRepositionEvents getWindowRepositionEvents() {
    return this.windowRepositionEvents;
  }

  public void setWindowRepositionEvents(WindowRepositionEvents windowRepositionEvents) {
    this.windowRepositionEvents = windowRepositionEvents;
  }

}

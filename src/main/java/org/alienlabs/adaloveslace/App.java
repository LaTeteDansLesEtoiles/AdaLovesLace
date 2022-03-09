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
import org.alienlabs.adaloveslace.util.SystemInfo;
import org.alienlabs.adaloveslace.view.MainWindow;
import org.alienlabs.adaloveslace.view.ToolboxWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

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

  @Override
  public void start(Stage primaryStage) {
    logger.info("Starting app: opening main window");
    showMainWindow(primaryStage);

    String ps = File.separator;
    logger.info("Opening toolbox window");
    showToolboxWindow(this, ".*org" + ps + "alienlabs" + ps + "adaloveslace" + ps + ".*.jpg");
  }

  public void showMainWindow(Stage primaryStage) {
    MainWindow mainWindow     = new MainWindow();

    var javafxVersion = SystemInfo.javafxVersion();
    var javaVersion   = SystemInfo.javaVersion();

    TilePane footer           = mainWindow.createFooter(javafxVersion, javaVersion);
    StackPane grid            = mainWindow.createGrid();
    GridPane root             = mainWindow.createGridPane(grid, footer);
    mainWindow.onMainWindowClicked(root);

    var scene                 = new Scene(root, 800d, 720d);
    primaryStage.setScene(scene);
    primaryStage.setTitle(MAIN_WINDOW_TITLE);

    mainWindow.createMenuBar(root);
    primaryStage.show();
  }

  public void showToolboxWindow(Object app, String resourcesPath) {
    toolboxStage = new Stage(StageStyle.DECORATED);
    TilePane toolboxPane = new TilePane(Orientation.VERTICAL);
    toolboxPane.setAlignment(Pos.TOP_CENTER);

    ToolboxWindow toolboxWindow = new ToolboxWindow();
    toolboxWindow.createToolboxPane(toolboxPane, resourcesPath, app);
    toolboxWindow.createToolboxStage(toolboxStage, toolboxPane);
  }

  public static void main(String[] args) {
    launch();
  }

  public Stage getToolboxStage() {
    return toolboxStage;
  }

}

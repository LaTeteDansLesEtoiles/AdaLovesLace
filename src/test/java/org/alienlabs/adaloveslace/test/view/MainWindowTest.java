package org.alienlabs.adaloveslace.test.view;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.test.AppTestParent;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;
import org.testfx.robot.Motion;

import static org.alienlabs.adaloveslace.App.MAIN_WINDOW_TITLE;
import static org.alienlabs.adaloveslace.view.CanvasWithOptionalDotGrid.*;
import static org.alienlabs.adaloveslace.view.MainWindow.QUIT_APP;
import static org.junit.jupiter.api.Assertions.*;

class MainWindowTest extends AppTestParent {

  public static final double  NOT_WHITE_PIXEL_X = 75d;
  public static final long    NOT_WHITE_PIXEL_Y = 75l;
  private Stage primaryStage;
  private Color foundColorOnGrid;

  private static final Logger logger = LoggerFactory.getLogger(MainWindowTest.class);

  /**
   * Init method called before each test
   *
   * @param primaryStage The injected window (stage)
   */
  @Start
  public void start(Stage primaryStage) {
    this.primaryStage = primaryStage;
    super.start(this.primaryStage);
  }

  @Test
  void testMainWindowShallBeDisplayedByDefault() {
    assertTrue(isMainWindowDisplayed());
  }

  @Test
  void testMainWindowTitle() {
    assertEquals(MAIN_WINDOW_TITLE, getMainWindowTitle());
  }

  /**
   * Checks if the menubar contains a "quit app" button
   *
   * @param robot The injected FxRobot
   */
  @Test
  void quitAppMenuItemShallBeDisplayed(FxRobot robot) {
    MenuItem quit = getQuitMenuItem(getFileMenu());
    assertEquals(QUIT_APP, quit.getText());
  }

  /**
   * Checks if we are able to draw a snowflake (the second pattern) on the canvas
   *
   * @param robot The injected FxRobot
   */
  @Test
  void testDrawSnowflake(FxRobot robot) {
    selectSnowflake(robot);
    drawSnowflake(robot);

    // If we choose a point in the snowflake it must not be white
    moveMouseForSnowflake(robot);

    // If we choose a point in the snowflake it must not be of the same color than the grid background
    assertNotEquals(GRID_COLOR, foundColorOnGrid);
  }

  // Click on the snowflake in the toolbox to select it
  private void selectSnowflake(FxRobot robot) {
    Node button = app.getToolboxStage().getScene().lookup(SNOWFLAKE_BUTTON);
    Point2D snowflakeOnTheToolbox = new Point2D(app.getToolboxStage().getX() + button.getLayoutX() + 20l,
      app.getToolboxStage().getY() + button.getLayoutY() + 50l);
    robot.clickOn(snowflakeOnTheToolbox, Motion.DEFAULT, MouseButton.PRIMARY);
  }

  // Click on the grid with the snowflake selected in order to draw a snowflake on the grid
  private void drawSnowflake(FxRobot robot) {
    Canvas canvas = app.getMainWindow().getCanvasWithOptionalDotGrid().getCanvas();
    Point2D snowflakeOnTheGrid = new Point2D(this.primaryStage.getX() + canvas.getLayoutX() + 50l,
      this.primaryStage.getY() + canvas.getLayoutY() + 50l);
    robot.clickOn(snowflakeOnTheGrid, Motion.DEFAULT, MouseButton.PRIMARY);
  }

  // Move mouse and get the color of the pixel under the pointer
  private void moveMouseForSnowflake(FxRobot robot) {
    Canvas canvas = app.getMainWindow().getCanvasWithOptionalDotGrid().getCanvas();
    robot.moveTo(new Point2D(this.primaryStage.getX() + canvas.getLayoutX() + NOT_WHITE_PIXEL_X, this.primaryStage.getY() + canvas.getLayoutY() + NOT_WHITE_PIXEL_Y));
    getBackgroundColor(canvas);
  }


  // What is the color of a pixel under the snowflake, on the grid?
  private void getBackgroundColor(Canvas canvas) {
    // This is in order tp have time to copy the image to the canvas, otherwise the image is always white and we don't
    // have access to the UI thread for the copy without "Platform.runLater()"
    Platform.runLater(() -> {
      WritableImage writableImage = new WritableImage(Double.valueOf(CANVAS_WIDTH).intValue(), Double.valueOf(CANVAS_HEIGHT).intValue());

      canvas.snapshot(snapshotResult -> {
        logger.info("Snapshot done!");
        assertNotEquals(Color.WHITE, foundColorOnGrid);
        return null;
        }, new SnapshotParameters(), writableImage);

      app.getMainWindow().getCanvasWithOptionalDotGrid().setImage(writableImage);

      PixelReader pr = app.getMainWindow().getCanvasWithOptionalDotGrid().getImage().getPixelReader();
      foundColorOnGrid = pr.getColor(Double.valueOf(this.primaryStage.getX() + canvas.getLayoutX() + NOT_WHITE_PIXEL_X).intValue(),
      Double.valueOf(this.primaryStage.getY() + canvas.getLayoutY() + NOT_WHITE_PIXEL_Y).intValue());
    });
  }

  private String getMainWindowTitle() {
    return super.primaryStage.getTitle();
  }

  private boolean isMainWindowDisplayed() {
    return super.primaryStage.getScene().getWindow().isShowing();
  }

  private Menu getFileMenu() {
    return super.app.getMainWindow().getMenuBar().getMenus().get(0);
  }

  private MenuItem getQuitMenuItem(Menu menu) {
    return menu.getItems().get(0);
  }

}

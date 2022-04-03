package org.alienlabs.adaloveslace.test.view;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.test.AppTestParent;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.base.ColorMatchers;
import org.testfx.robot.Motion;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.alienlabs.adaloveslace.App.MAIN_WINDOW_TITLE;
import static org.alienlabs.adaloveslace.view.MainWindow.QUIT_APP;
import static org.junit.jupiter.api.Assertions.*;

class MainWindowTest extends AppTestParent {

  public static final double  WHITE_PIXEL_X       = 90d;
  public static final long    WHITE_PIXEL_Y       = 90l;
  public static final double  SNOWFLAKE_PIXEL_X   = 75d;
  public static final long    SNOWFLAKE_PIXEL_Y   = 90l;
  public static final double  GRAY_PIXEL_X        = 78d;
  public static final long    GRAY_PIXEL_Y        = 92l;
  public static final Color   GRAY_DOTS_COLOR     = Color.valueOf("0xedededff");
  public static final Color   SNOWFLAKE_DOT_COLOR = Color.valueOf("0xcececeff");
  private Stage primaryStage;
  private Color foundColorOnGrid;

  /**
   * Countdown latch
   */
  private CountDownLatch lock = new CountDownLatch(1);

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

    // Move mouse and get the color of the pixel under the pointer
    Canvas canvas = app.getMainWindow().getCanvasWithOptionalDotGrid().getCanvas();
    Point2D pointToMoveTo = new Point2D(this.primaryStage.getX() + canvas.getLayoutX() + SNOWFLAKE_PIXEL_X, this.primaryStage.getY() + canvas.getLayoutY() + SNOWFLAKE_PIXEL_Y);
    Point2D pointToMoveToInCanvas = new Point2D(canvas.getLayoutX() + SNOWFLAKE_PIXEL_X, canvas.getLayoutY() + SNOWFLAKE_PIXEL_Y);
    robot.moveTo(pointToMoveTo);

    // This is in order to have time to copy the image to the canvas, otherwise the image is always white and we don't
    // have access to the UI thread for the copy without "Platform.runLater()"
    foundColorOnGrid = getColor(canvas, pointToMoveToInCanvas);

    // If we choose a point in the snowflake it must not be of the same color than the grid dots
    assertFalse(ColorMatchers.isColor(GRAY_DOTS_COLOR).matches(foundColorOnGrid));

    // If we choose a point in the snowflake it must not be of the same color than the grid background
    assertFalse(ColorMatchers.isColor(Color.WHITE).matches(foundColorOnGrid));

    // If we choose a point in the snowflake it must be of the snowflake color
    FxAssert.verifyThat(foundColorOnGrid, ColorMatchers.isColor(SNOWFLAKE_DOT_COLOR));
  }

  /**
   * Checks if we are able to click nowhere on the canvas, i.e. somewhere where there is no pattern
   * and no grid dots: the pixel should be white.
   *
   * @param robot The injected FxRobot
   */
  @Test
  void testClickOutsideOfTheGrid(FxRobot robot) {
    Canvas canvas = app.getMainWindow().getCanvasWithOptionalDotGrid().getCanvas();

    // Move mouse and get the color of the pixel under the pointer
    Point2D pointToMoveTo = new Point2D(this.primaryStage.getX() + canvas.getLayoutX() + WHITE_PIXEL_X, this.primaryStage.getY() + canvas.getLayoutY() + WHITE_PIXEL_Y);
    Point2D pointToMoveToInCanvas = new Point2D(canvas.getLayoutX() + WHITE_PIXEL_X, canvas.getLayoutY() + WHITE_PIXEL_Y);
    robot.moveTo(pointToMoveTo);

    foundColorOnGrid = getColor(canvas, pointToMoveToInCanvas);
    FxAssert.verifyThat(foundColorOnGrid, ColorMatchers.isColor(Color.WHITE));
  }

  /**
   * Checks if we are able to click on the canvas, somewhere where there is no pattern
   * and a grid dot: the pixel should be gray.
   *
   * @param robot The injected FxRobot
   */
  @Test
  void testClickOnTheGrid(FxRobot robot) {
    Canvas canvas = app.getMainWindow().getCanvasWithOptionalDotGrid().getCanvas();

    // Move mouse and get the color of the pixel under the pointer
    Point2D pointToMoveTo = new Point2D(this.primaryStage.getX() + canvas.getLayoutX() + GRAY_PIXEL_X, this.primaryStage.getY() + canvas.getLayoutY() + GRAY_PIXEL_Y);
    Point2D pointToMoveToInCanvas = new Point2D(canvas.getLayoutX() + GRAY_PIXEL_X, canvas.getLayoutY() + GRAY_PIXEL_Y);
    robot.moveTo(pointToMoveTo);

    foundColorOnGrid = getColor(canvas, pointToMoveToInCanvas);
    FxAssert.verifyThat(foundColorOnGrid, ColorMatchers.isColor(GRAY_DOTS_COLOR));
  }

  // This is in order to have time to copy the image to the canvas, otherwise the image is always white and we don't
  // have access to the UI thread for the copy without "Platform.runLater()"
  private Color getColor(Canvas canvas, Point2D pointToMoveTo) {
    lock = new CountDownLatch(1);
    copyCanvas(canvas, pointToMoveTo);

    try {
      lock.await(5_000, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      logger.error("Interrupted!", e);
    }

    return this.foundColorOnGrid;
  }

  private void copyCanvas(Canvas canvas, Point2D pointToMoveTo) {
    Platform.runLater(() -> {
      Image snapshot = canvas.snapshot(null, null);
      logger.info("Snapshot done!");

      PixelReader pr = snapshot.getPixelReader();
      MainWindowTest.this.foundColorOnGrid = pr.getColor(Double.valueOf(pointToMoveTo.getX()).intValue(), Double.valueOf(pointToMoveTo.getY()).intValue());
      logger.info("# argb: {}", MainWindowTest.this.foundColorOnGrid);

      lock.countDown();
    });
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

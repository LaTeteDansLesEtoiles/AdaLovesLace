package org.alienlabs.adaloveslace.functionaltest.view;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.functionaltest.AppFunctionalTestParent;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.RedoKnotButton;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.ResetDiagramButton;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.UndoKnotButton;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.base.ColorMatchers;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.alienlabs.adaloveslace.App.MAIN_WINDOW_TITLE;
import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;

class MainWindowFunctionalTest extends AppFunctionalTestParent {

  public static final double  WHITE_PIXEL_X               = 86d;
  public static final long    WHITE_PIXEL_Y               = 75L;
  public static final Color   GRAY_DOTS_COLOR             = Color.valueOf("0xccccccff");

  private Stage primaryStage;

  private static final Logger logger                      = LoggerFactory.getLogger(MainWindowFunctionalTest.class);

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
   * Checks if we are able to draw a snowflake (the first and only pattern) on the canvas
   *
   * @param robot The injected FxRobot
   */
  @Test
  void testDrawSnowflake(FxRobot robot) {
    // Init
    selectAndClickOnSnowflake(robot);
    drawSnowflake(robot);
    selectSnowflake(robot);

    // Run
    // Move mouse and get the color of the pixel under the pointer
    Point2D pointToCheck = newPointOnGridForFirstNonGridNode();
    robot.moveTo(pointToCheck);

    // Verify
    foundColorOnGrid = getColor(pointToCheck);

    // If we choose a point in the snowflake it must not be of the same color than the grid dots
    assertFalse(ColorMatchers.isColor(GRAY_DOTS_COLOR).matches(foundColorOnGrid));

    // If we choose a point in the snowflake it must not be of the same color than the grid background
    assertFalse(ColorMatchers.isColor(Color.WHITE).matches(foundColorOnGrid));

    // If we choose a point in the snowflake it must be of the right color
    assertTrue(ColorMatchers.isColor(SNOWFLAKE_DOT_COLOR).matches(foundColorOnGrid),
      "Expected color: " + SNOWFLAKE_DOT_COLOR + ", actual color: " + foundColorOnGrid);
  }

  /**
   * Checks if we are able to click anywhere on the canvas, i.e. somewhere where there is no pattern
   * and no grid dots: the pixel should be white.
   *
   * @param robot The injected FxRobot
   */
  @Test
  void testClickOutsideOfAGridDot(FxRobot robot) {
    // Init
    // Move mouse and get the color of the pixel under the pointer
    Point2D pointToMoveTo = newPointOnGrid(WHITE_PIXEL_X, app.getRoot().getLayoutY() + WHITE_PIXEL_Y);

    // Run
    robot.moveTo(pointToMoveTo);
    Point2D pointToCheck = new Point2D(WHITE_PIXEL_X, WHITE_PIXEL_Y);
    foundColorOnGrid = getColor(pointToCheck);

    // Verify
    verifyThat(foundColorOnGrid, ColorMatchers.isColor(Color.WHITE));
  }

  /**
   * Checks if we are able to click on the canvas, somewhere where there is no pattern
   * and a grid dot: the pixel should be gray.
   *
   * @param robot The injected FxRobot
   */
  @Test
  void testClickOnTheGrid(FxRobot robot) {
    // Init
    // Move mouse and get the color of the pixel under the pointer
    Point2D pointToCheck = newPointOnGrid(GRAY_PIXEL_X, GRAY_PIXEL_Y);

    // Run
    robot.moveTo(pointToCheck);
    foundColorOnGrid = getColor(pointToCheck);

    // Verify
    // If we click on a grid dot, it is gray
    assertTrue(ColorMatchers.isColor(GRAY_DOTS_COLOR).matches(foundColorOnGrid));
  }


  /**
   * Checks if we can undo a snowflake (the second pattern) after we have drawn it on the canvas
   *
   * @param robot The injected FxRobot
   */
  @Test
  void testUndoSnowflake(FxRobot robot) {
    // Init
    selectAndClickOnSnowflake(robot);
    drawSnowflake(robot);

    Point2D snowflakePoint = newPointOnGrid(SNOWFLAKE_PIXEL_X, SNOWFLAKE_PIXEL_Y);

    // This is in order to have time to copy the image to the canvas, otherwise the image is always white and we don't
    // have access to the UI thread for the copy without "Platform.runLater()"
    Color foundColorOnGridBeforeUndo = getColor(snowflakePoint);

    // Run: issue an "Undo knot" command
    lock = new CountDownLatch(1);
    selectAndClickUndoKnot();

    // Verify
    // Move mouse and get the color of the pixel under the pointer
    snowflakePoint = newPointOnGrid(SNOWFLAKE_PIXEL_X, SNOWFLAKE_PIXEL_Y);
    Color foundColorOnGridAfterUndo = getColor(snowflakePoint);

    assertNotEquals(foundColorOnGridAfterUndo, foundColorOnGridBeforeUndo,
      "The color before and after undo must not be the same!");
  }

  /**
   * Checks if we can undo and redo a snowflake (the second pattern) after we have drawn it on the canvas
   *
   * @param robot The injected FxRobot
   */
  @Test
  void testRedoSnowflake(FxRobot robot) {
    // Init
    selectAndClickOnSnowflake(robot);
    drawSnowflake(robot);

    Point2D snowflakePoint = new Point2D(SNOWFLAKE_PIXEL_X, SNOWFLAKE_PIXEL_Y);

    // This is in order to have time to copy the image to the canvas, otherwise the image is always white and we don't
    // have access to the UI thread for the copy without "Platform.runLater()"
    Color foundColorOnGridBeforeRedo = getColor(snowflakePoint);

    // Issue an "Undo knot" command
    lock = new CountDownLatch(1);
    selectAndClickUndoKnot();

    // Run: Issue a "Redo knot" command
    lock = new CountDownLatch(1);
    selectAndClickRedoKnot();

    // Verify
    // Move mouse and get the color of the pixel under the pointer
    snowflakePoint = new Point2D(SNOWFLAKE_PIXEL_X, SNOWFLAKE_PIXEL_Y);
    Color foundColorOnGridAfterRedo = getColor(snowflakePoint);

    assertEquals(foundColorOnGridAfterRedo, foundColorOnGridBeforeRedo,
      "The color before and after redo must be the same!");
  }

  /**
   * Checks if we can reset a snowflake (the second pattern) after we have drawn it on the canvas
   *
   * @param robot The injected FxRobot
   */
  @Test
  void testResetSnowflake(FxRobot robot) {
    // Init
    selectAndClickOnSnowflake(robot);
    drawSnowflake(robot);

    // Move mouse and get the color of the pixel under the pointer
    Point2D pointToCheck = newPointOnGrid(SNOWFLAKE_PIXEL_X, SNOWFLAKE_PIXEL_Y);
    robot.moveTo(pointToCheck);

    Color foundColorOnGridBeforeReset = getColor(pointToCheck);

    // Run: issue a "Reset diagram" command
    lock = new CountDownLatch(1);
    selectAndClickResetDiagramButton();

    // Verify
    // Move mouse and get the color of the pixel under the pointer
    pointToCheck = newPointOnGrid(SNOWFLAKE_PIXEL_X, SNOWFLAKE_PIXEL_Y);
    robot.moveTo(pointToCheck);
    Color foundColorOnGridAfterReset = getColor(pointToCheck);

    assertNotEquals(foundColorOnGridAfterReset, foundColorOnGridBeforeReset,
      "The color before and after 'reset diagram' must not be the same!");
  }

  // Click on the 'undo knot' in the toolbox
  private void selectAndClickUndoKnot() {
    Platform.runLater(() -> {
      UndoKnotButton.undoKnot(app);
      lock.countDown();
    });

    try {
      lock.await(SLEEP_BETWEEN_ACTIONS_TIME, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      logger.error("Interrupted!", e);
    }
  }

  // Click on the 'redo knot' in the toolbox
  private void selectAndClickRedoKnot() {
    Platform.runLater(() -> {
      RedoKnotButton.redoKnot(app);
      lock.countDown();
    });

    try {
      lock.await(SLEEP_BETWEEN_ACTIONS_TIME, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      logger.error("Interrupted!", e);
    }
  }

  // Click on the 'reset diagram' in the toolbox
  private void selectAndClickResetDiagramButton() {
    Platform.runLater(() -> {
      ResetDiagramButton.resetDiagram(app);
      lock.countDown();
    });

    try {
      lock.await(SLEEP_BETWEEN_ACTIONS_TIME, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      logger.error("Interrupted!", e);
    }
  }

  private String getMainWindowTitle() {
    return this.primaryStage.getTitle();
  }

  private boolean isMainWindowDisplayed() {
    return this.primaryStage.getScene().getWindow().isShowing();
  }

}

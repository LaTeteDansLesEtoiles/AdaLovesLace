package org.alienlabs.adaloveslace.functionaltest.view.window;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.functionaltest.AppFunctionalTestParent;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.RedoKnotButton;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.ResetDiagramButton;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.UndoKnotButton;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.base.ColorMatchers;

import static org.alienlabs.adaloveslace.App.MAIN_WINDOW_TITLE;
import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;

class MainWindowFunctionalTest extends AppFunctionalTestParent {

  public static final double  WHITE_PIXEL_X               = 86d;
  public static final long    WHITE_PIXEL_Y               = 75L;
  public static final Color   GRAY_DOTS_COLOR             = Color.valueOf("0xccccccff");

  /**
   * Init method called before each test
   *
   * @param primaryStage The injected window (stage)
   */
  @Start
  public void start(Stage primaryStage) {
    super.start(primaryStage);
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
    // Given
    synchronizeTask(() -> selectAndClickOnSnowflakePatternButton(robot));
    synchronizeTask(() -> drawASnowflake(robot));
    sleepMainThread();

    // When
    // Move mouse and get the color of the pixel under the pointer
    Point2D pointToCheck = newPointOnGridForFirstNonGridNode();
    synchronizeTask(() -> robot.moveTo(pointToCheck));

    // Then
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
    // Given
    // Move mouse and get the color of the pixel under the pointer
    Point2D pointToMoveTo = newPointOnGrid(WHITE_PIXEL_X, app.getRoot().getLayoutY() + WHITE_PIXEL_Y);

    // When
    robot.moveTo(pointToMoveTo);
    Point2D pointToCheck = new Point2D(WHITE_PIXEL_X, WHITE_PIXEL_Y);
    foundColorOnGrid = getColor(pointToCheck);

    // Then
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
    // Given
    // Move mouse and get the color of the pixel under the pointer
    Point2D pointToCheck = newPointOnGrid(GRAY_PIXEL_X, GRAY_PIXEL_Y);

    // When
    robot.moveTo(pointToCheck);
    foundColorOnGrid = getColor(pointToCheck);

    // Then
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
    // Given
    synchronizeTask(() -> selectAndClickOnSnowflakePatternButton(robot));
    synchronizeTask(() -> drawASnowflake(robot));

    Point2D snowflakePoint = newPointOnGrid(FIRST_SNOWFLAKE_PIXEL_X, FIRST_SNOWFLAKE_PIXEL_Y);

    // This is in order to have time to copy the image to the canvas, otherwise the image is always white and we don't
    // have access to the UI thread for the copy without "Platform.runLater()"
    Color foundColorOnGridBeforeUndo = getColor(snowflakePoint);

    // When: issue an "Undo knot" command
    synchronizeTask(() -> UndoKnotButton.undoKnot(app));

    // Then
    // Move mouse and get the color of the pixel under the pointer
    snowflakePoint = newPointOnGrid(FIRST_SNOWFLAKE_PIXEL_X, FIRST_SNOWFLAKE_PIXEL_Y);
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
    // Given
    synchronizeTask(() -> selectAndClickOnSnowflakePatternButton(robot));
    synchronizeTask(() -> drawASnowflake(robot));

    Point2D snowflakePoint = new Point2D(FIRST_SNOWFLAKE_PIXEL_X, FIRST_SNOWFLAKE_PIXEL_Y);

    // This is in order to have time to copy the image to the canvas, otherwise the image is always white and we don't
    // have access to the UI thread for the copy without "Platform.runLater()"
    Color foundColorOnGridBeforeRedo = getColor(snowflakePoint);

    // Issue an "Undo knot" command
    synchronizeTask(() -> UndoKnotButton.undoKnot(app));

    // When: Issue a "Redo knot" command
    synchronizeTask(() -> RedoKnotButton.redoKnot(app));

    // Then
    // Move mouse and get the color of the pixel under the pointer
    snowflakePoint = new Point2D(FIRST_SNOWFLAKE_PIXEL_X, FIRST_SNOWFLAKE_PIXEL_Y);
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
  void testResetGrid(FxRobot robot) {
    // Given
    synchronizeTask(() -> selectAndClickOnSnowflakePatternButton(robot));
    synchronizeTask(() -> drawASnowflake(robot));

    // Move mouse and get the color of the pixel under the pointer
    Point2D pointToCheck = newPointOnGrid(FIRST_SNOWFLAKE_PIXEL_X, FIRST_SNOWFLAKE_PIXEL_Y);
    robot.moveTo(pointToCheck);

    Color foundColorOnGridBeforeReset = getColor(pointToCheck);

    // When: issue a "Reset diagram" command
    synchronizeTask(() -> ResetDiagramButton.resetDiagram(app));

    // Then
    // Move mouse and get the color of the pixel under the pointer
    pointToCheck = newPointOnGrid(FIRST_SNOWFLAKE_PIXEL_X, FIRST_SNOWFLAKE_PIXEL_Y);
    robot.moveTo(pointToCheck);
    Color foundColorOnGridAfterReset = getColor(pointToCheck);

    assertNotEquals(foundColorOnGridAfterReset, foundColorOnGridBeforeReset,
      "The color before and after 'reset diagram' must not be the same!");
  }

  private String getMainWindowTitle() {
    return this.app.primaryStage.getTitle();
  }

  private boolean isMainWindowDisplayed() {
    return this.app.primaryStage.getScene().getWindow().isShowing();
  }

}

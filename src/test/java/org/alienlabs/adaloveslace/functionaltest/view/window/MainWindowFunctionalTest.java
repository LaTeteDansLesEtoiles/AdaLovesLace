package org.alienlabs.adaloveslace.functionaltest.view.window;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.functionaltest.AppFunctionalTestParent;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid.RedoKnotButton;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid.ResetDiagramButton;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid.UndoKnotButton;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.base.ColorMatchers;

import static org.alienlabs.adaloveslace.App.MAIN_WINDOW_TITLE;
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
  @Override
  @Start
  public void start(Stage primaryStage) {
    super.start(primaryStage);
  }

  @Test
  void testMainWindowShallBeDisplayedByDefault() {
    verifyThat(this.app.getPrimaryStage().isShowing(), org.hamcrest.Matchers.is(true));
  }

  @Test
  void testMainWindowTitle() {
    verifyThat(this.app.getPrimaryStage().getTitle(), org.hamcrest.Matchers.is(MAIN_WINDOW_TITLE));
  }

  /**
   * Checks if we are able to draw a snowflake (the first and only pattern) on the canvas
   *
   * @param robot The injected FxRobot
   */
  @Test
  void testDrawSnowflake(FxRobot robot) {
    // Given
    selectAndClickOnSnowflakePatternButton(robot);
    drawASnowflake(robot);
    
    // Wait for the snowflake to be properly drawn
    assertCondition(() -> {
      Point2D pointToCheck = newPointOnGridForFirstNonGridNode();
      Color color = getColor(pointToCheck);
      return !ColorMatchers.isColor(GRAY_DOTS_COLOR).matches(color) && 
             !ColorMatchers.isColor(Color.WHITE).matches(color);
    }, "Snowflake to be properly drawn on canvas");

    // When
    // Move mouse and get the color of the pixel under the pointer
    Point2D pointToCheck = newPointOnGridForFirstNonGridNode();
    robot.moveTo(pointToCheck);

    // Then
    foundColorOnGrid = getColor(pointToCheck);

    // If we choose a point in the snowflake, it must not be of the same color as the grid dots
    verifyThat(foundColorOnGrid, org.hamcrest.Matchers.not(ColorMatchers.isColor(GRAY_DOTS_COLOR)));

    // If we choose a point in the snowflake, it must not be of the same color as the grid background
    verifyThat(foundColorOnGrid, org.hamcrest.Matchers.not(ColorMatchers.isColor(Color.WHITE)));

    // If we choose a point in the snowflake, it must be of the right color
    verifyThat(foundColorOnGrid, ColorMatchers.isColor(SNOWFLAKE_DOT_COLOR));
  }

  /**
   * Checks if we are able to click anywhere on the canvas, i.e., somewhere where there is no pattern
   * and no grid dots: the pixel should be white.
   *
   * @param robot The injected FxRobot
   */
  @Test
  void testClickOutsideOfAGridDot(FxRobot robot) {
    // Given
    // Move mouse and get the color of the pixel under the pointer
    Point2D pointToMoveTo = newPointOnGrid(WHITE_PIXEL_X, app.getMovablePane().getLayoutY() + WHITE_PIXEL_Y);

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
    verifyThat(foundColorOnGrid, ColorMatchers.isColor(GRAY_DOTS_COLOR));
  }


  /**
   * Checks if we can undo a snowflake (the second pattern) after we have drawn it on the canvas
   *
   * @param robot The injected FxRobot
   */
  @Test
  void testUndoSnowflake(FxRobot robot) {
    // Given
    selectAndClickOnSnowflakePatternButton(robot);
    drawASnowflake(robot);

    Point2D snowflakePoint = newPointOnGrid(FIRST_SNOWFLAKE_PIXEL_X, FIRST_SNOWFLAKE_PIXEL_Y);

    // This is to have time to copy the image to the canvas, otherwise the image is always white, and we don't
    // have access to the UI thread for the copy without "Platform.runLater()"
    Color foundColorOnGridBeforeUndo = getColor(snowflakePoint);

    // When: issue an "Undo knot" command
    UndoKnotButton.undoKnot();

    // Then
    // Move the mouse and get the color of the pixel under the pointer
    snowflakePoint = newPointOnGrid(FIRST_SNOWFLAKE_PIXEL_X, FIRST_SNOWFLAKE_PIXEL_Y);
    Color foundColorOnGridAfterUndo = getColor(snowflakePoint);

    verifyThat(foundColorOnGridAfterUndo, org.hamcrest.Matchers.not(ColorMatchers.isColor(foundColorOnGridBeforeUndo)));
  }

  /**
   * Checks if we can undo and redo a snowflake (the second pattern) after we have drawn it on the canvas
   *
   * @param robot The injected FxRobot
   */
  @Test
  void testRedoSnowflake(FxRobot robot) {
    // Given
    selectAndClickOnSnowflakePatternButton(robot);
    drawASnowflake(robot);

    Point2D snowflakePoint = new Point2D(FIRST_SNOWFLAKE_PIXEL_X, FIRST_SNOWFLAKE_PIXEL_Y);

    // This is to have time to copy the image to the canvas, otherwise the image is always white, and we don't
    // have access to the UI thread for the copy without "Platform.runLater()"
    Color foundColorOnGridBeforeRedo = getColor(snowflakePoint);

    // Issue an "Undo knot" command
    UndoKnotButton.undoKnot();

    // When: Issue a "Redo knot" command
    RedoKnotButton.redoKnot();

    // Then
    //  Move the mouse and get the color of the pixel under the pointer
    snowflakePoint = new Point2D(FIRST_SNOWFLAKE_PIXEL_X, FIRST_SNOWFLAKE_PIXEL_Y);
    Color foundColorOnGridAfterRedo = getColor(snowflakePoint);

    verifyThat(foundColorOnGridAfterRedo, ColorMatchers.isColor(foundColorOnGridBeforeRedo));
  }

  /**
   * Checks if we can reset a snowflake (the second pattern) after we have drawn it on the canvas
   *
   * @param robot The injected FxRobot
   */
  @Test
  void testResetGrid(FxRobot robot) {
    // Given
    selectAndClickOnSnowflakePatternButton(robot);
    drawASnowflake(robot);

    // Move mouse and get the color of the pixel under the pointer
    Point2D pointToCheck = newPointOnGrid(FIRST_SNOWFLAKE_PIXEL_X, FIRST_SNOWFLAKE_PIXEL_Y);
    robot.moveTo(pointToCheck);

    Color foundColorOnGridBeforeReset = getColor(pointToCheck);

    // When: issue a "Reset diagram" command
    ResetDiagramButton.resetDiagram();

    // Then
    //  Move the mouse and get the color of the pixel under the pointer
    pointToCheck = newPointOnGrid(FIRST_SNOWFLAKE_PIXEL_X, FIRST_SNOWFLAKE_PIXEL_Y);
    robot.moveTo(pointToCheck);
    Color foundColorOnGridAfterReset = getColor(pointToCheck);

    verifyThat(foundColorOnGridAfterReset, org.hamcrest.Matchers.not(ColorMatchers.isColor(foundColorOnGridBeforeReset)));
  }

  private String getMainWindowTitle() {
    return this.app.getPrimaryStage().getTitle();
  }

  private boolean isMainWindowDisplayed() {
    return this.app.getPrimaryStage().getScene().getWindow().isShowing();
  }

}

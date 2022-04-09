package org.alienlabs.adaloveslace.test.view;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.test.AppTestParent;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.base.ColorMatchers;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.robot.Motion;

import static org.alienlabs.adaloveslace.App.TOOLBOX_TITLE;
import static org.alienlabs.adaloveslace.test.view.MainWindowTest.WHITE_PIXEL_X;
import static org.alienlabs.adaloveslace.test.view.MainWindowTest.WHITE_PIXEL_Y;
import static org.alienlabs.adaloveslace.view.QuitButton.QUIT_APP;
import static org.alienlabs.adaloveslace.view.ShowHideGridButton.SHOW_HIDE_GRID_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.ToolboxWindow.*;
import static org.junit.jupiter.api.Assertions.*;

class ToolBoxTest extends AppTestParent {

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
  void testShowToolboxWindow() {
    assertEquals(TOOLBOX_TITLE, super.app.getToolboxStage().getTitle());
  }

  /**
   * Checks if 1st pattern toolbox button contains image "mandala_small.jpg"
   *
   * @param robot The injected FxRobot
   */
  @Test
  void should_contain_1st_pattern_button_with_text(FxRobot robot) {
    FxAssert.verifyThat(MANDALA_BUTTON, LabeledMatchers.hasText(MANDALA));
  }

  /**
   * Checks if 2nd pattern toolbox button contains image "snowflake_small.jpg"
   *
   * @param robot The injected FxRobot
   */
  @Test
  void should_contain_2nd_pattern_button_with_text(FxRobot robot) {
    FxAssert.verifyThat(SNOWFLAKE_BUTTON, LabeledMatchers.hasText(SNOWFLAKE));
  }

  /**
   * Checks if "show / hide grid" toolbox button contains right text
   *
   * @param robot The injected FxRobot
   */
  @Test
  void should_contain_show_hide_grid_button_with_text(FxRobot robot) {
    FxAssert.verifyThat(SHOW_HIDE_GRID_BUTTON_NAME, NodeMatchers.isVisible());
  }

  /**
   * Checks if "Quit" toolbox button contains right text
   *
   * @param robot The injected FxRobot
   */
  @Test
  void should_contain_quit_button_with_text(FxRobot robot) {
    FxAssert.verifyThat(QUIT_APP, NodeMatchers.isVisible());
  }

  /**
   * Checks if we are able to hide the dot grid: a canvas pixel should be white, then.
   *
   * @param robot The injected FxRobot
   */
  @Test
  void testHideGrid(FxRobot robot) {
    Canvas canvas = app.getMainWindow().getCanvasWithOptionalDotGrid().getCanvas();
    switchGrid(robot);

    // Move mouse and get the color of the pixel under the pointer
    Point2D pointToMoveTo = new Point2D(this.primaryStage.getX() + (canvas.getLayoutX() /2d) + GRAY_PIXEL_X, this.primaryStage.getY() + (canvas.getLayoutY() / 2d) + GRAY_PIXEL_Y);
    Point2D pointToMoveToInCanvas = new Point2D((canvas.getLayoutX() / 2d) + GRAY_PIXEL_X, (canvas.getLayoutY() / 2d) + GRAY_PIXEL_Y);
    robot.moveTo(pointToMoveTo);

    foundColorOnGrid = getColor(canvas, pointToMoveToInCanvas);
    // All we can say is that if we click on the empty canvas, then the pixel is white
    assertTrue(ColorMatchers.isColor(Color.WHITE).matches(foundColorOnGrid));
  }

  /**
   * Checks if we are able to hide the dot grid (a canvas pixel should be white, then)
   * and to display it again (a canvas pixel should not be white, then).
   *
   * @param robot The injected FxRobot
   */
  @Test
  void testHideAndShowAgainGrid(FxRobot robot) {
    Canvas canvas = app.getMainWindow().getCanvasWithOptionalDotGrid().getCanvas();
    // Hide the dot grid
    switchGrid(robot);

    // Move mouse and get the color of the pixel under the pointer
    Point2D pointToMoveTo = new Point2D(this.primaryStage.getX() + (canvas.getLayoutX() / 2d) +  WHITE_PIXEL_X, this.primaryStage.getY() + (canvas.getLayoutY() / 2d) + WHITE_PIXEL_Y);
    Point2D pointToMoveToInCanvas = new Point2D((canvas.getLayoutX() / 2d) + WHITE_PIXEL_X, (canvas.getLayoutY() / 2d) + WHITE_PIXEL_Y);
    robot.moveTo(pointToMoveTo);

    foundColorOnGrid = getColor(canvas, pointToMoveToInCanvas);
    // All we can say is that if we click on the empty canvas, the pixel is white
    assertTrue(ColorMatchers.isColor(Color.WHITE).matches(foundColorOnGrid));

    // Show the dot grid again
    switchGrid(robot);

    // Move mouse and get the color of the pixel under the pointer
    pointToMoveTo = new Point2D(this.primaryStage.getX() + (canvas.getLayoutX() / 2d) + WHITE_PIXEL_X, this.primaryStage.getY() + (canvas.getLayoutY() / 2d) + WHITE_PIXEL_Y);
    pointToMoveToInCanvas = new Point2D((canvas.getLayoutX() / 2d) + WHITE_PIXEL_X, (canvas.getLayoutY() / 2d) + WHITE_PIXEL_Y);
    robot.moveTo(pointToMoveTo);

    foundColorOnGrid = getColor(canvas, pointToMoveToInCanvas);
    // All we can say is that if we click on the grid, then the pixel is not white
    assertFalse(ColorMatchers.isColor(Color.WHITE).matches(foundColorOnGrid));
  }

  // Click on "show / hide dot grid" button in the toolbox
  private void switchGrid(FxRobot robot) {
    Point2D showHideGridButtonOnTheToolbox = new Point2D(app.getToolboxStage().getX() + TOOLBOX_WINDOW_WIDTH / 2d,
      app.getToolboxStage().getY() + (this.toolboxWindow.getResourceFiles().size() * TILE_HEIGHT + VERTICAL_PADDING + TILE_PADDING));
    robot.clickOn(showHideGridButtonOnTheToolbox, Motion.DEFAULT, MouseButton.PRIMARY);
  }

}

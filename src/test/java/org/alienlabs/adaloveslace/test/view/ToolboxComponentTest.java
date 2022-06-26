package org.alienlabs.adaloveslace.test.view;

import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.test.AppTestParent;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;
import org.testfx.robot.Motion;

import static org.alienlabs.adaloveslace.App.TOOLBOX_TITLE;
import static org.alienlabs.adaloveslace.view.window.ToolboxWindow.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ToolboxComponentTest extends AppTestParent {

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
    assertEquals(TOOLBOX_TITLE, this.app.getToolboxStage().getTitle());
  }

  /**
   * Checks if we are able to hide the dot grid: a canvas pixel should be white, then.
   *
   * @param robot The injected FxRobot
   */
  /*@Test
  void testHideGrid(FxRobot robot) {
    Canvas canvas = this.app.getMainWindow().getCanvasWithOptionalDotGrid().getCanvas();
    switchGrid(robot);

    // Move mouse and get the color of the pixel under the pointer
    Point2D pointToMoveTo = new Point2D(this.primaryStage.getX() + (canvas.getLayoutX() /2d) + GRAY_PIXEL_X,
      this.primaryStage.getY() + (canvas.getLayoutY() / 2d) + GRAY_PIXEL_Y);
    Point2D pointToMoveToInCanvas = new Point2D((canvas.getLayoutX() / 2d) + GRAY_PIXEL_X,
      (canvas.getLayoutY() / 2d) + GRAY_PIXEL_Y);
    robot.moveTo(pointToMoveTo);

    foundColorOnGrid = getColor(canvas, pointToMoveToInCanvas);
    // All we can say is that if we click on the empty canvas, then the pixel is white
    assertTrue(ColorMatchers.isColor(Color.WHITE).matches(foundColorOnGrid));
  }*/

  /**
   * Checks if we are able to hide the dot grid (a canvas pixel should be white, then)
   * and to display it again (a canvas pixel should not be white, then).
   *
   * @param robot The injected FxRobot
   */
  /*@Test
  void testHideAndShowAgainGrid(FxRobot robot) {
    Canvas canvas = this.app.getMainWindow().getCanvasWithOptionalDotGrid().getCanvas();
    // Hide the dot grid
    switchGrid(robot);

    // Move mouse and get the color of the pixel under the pointer
    Point2D pointToMoveTo = new Point2D(this.primaryStage.getX() + (canvas.getLayoutX() / 2d) +  WHITE_PIXEL_X, this.primaryStage.getY() + (canvas.getLayoutY() / 2d) + WHITE_PIXEL_Y);
    Point2D pointToMoveToInCanvas = new Point2D((canvas.getLayoutX() / 2d) + WHITE_PIXEL_X, (canvas.getLayoutY() / 2d) + NO_PIXEL_Y);
    robot.moveTo(pointToMoveTo);

    foundColorOnGrid = getColor(canvas, pointToMoveToInCanvas);

    // All we can say is that if we click on the empty canvas, the pixel is white
    assertTrue(ColorMatchers.isColor(Color.WHITE).matches(foundColorOnGrid));

    // Show the dot grid again
    switchGrid(robot);

    // Move mouse and get the color of the pixel under the pointer
    pointToMoveTo = new Point2D(this.primaryStage.getX() + (canvas.getLayoutX() / 2d) + WHITE_PIXEL_X,
      this.primaryStage.getY() + (canvas.getLayoutY() / 2d) + WHITE_PIXEL_Y);
    pointToMoveToInCanvas = new Point2D((canvas.getLayoutX() / 2d) + WHITE_PIXEL_X,
      (canvas.getLayoutY() / 2d) + WHITE_PIXEL_Y);
    robot.moveTo(pointToMoveTo);

    foundColorOnGrid = getColor(canvas, pointToMoveToInCanvas);
    // All we can say is that if we click on the grid, then the pixel is not white
    assertFalse(ColorMatchers.isColor(Color.WHITE).matches(foundColorOnGrid));
  }*/

  // Click on "show / hide dot grid" button in the toolbox
  private void switchGrid(FxRobot robot) {
    Point2D showHideGridButtonOnTheToolbox = new Point2D(this.app.getToolboxStage().getX() + TOOLBOX_WINDOW_WIDTH / 2d,
      this.app.getToolboxStage().getY() +
        (this.toolboxWindow.getClasspathResourceFiles().size() * TILE_HEIGHT + (VERTICAL_PADDING * 2d)) + 25d);
    robot.clickOn(showHideGridButtonOnTheToolbox, Motion.DEFAULT, MouseButton.PRIMARY);
  }

}

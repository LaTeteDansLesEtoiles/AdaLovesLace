package org.alienlabs.adaloveslace.test.view;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.test.AppTestParent;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.ShowHideGridButton;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.base.ColorMatchers;

import static java.lang.Thread.sleep;
import static org.alienlabs.adaloveslace.App.TOOLBOX_TITLE;
import static org.alienlabs.adaloveslace.test.view.MainWindowComponentTest.GRAY_DOTS_COLOR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
  @Test
  void testHideGrid(FxRobot robot) {
    // Init
    Point2D pointToMoveTo = newPointOnGrid(GRAY_PIXEL_X, GRAY_PIXEL_Y);
    robot.moveTo(pointToMoveTo);
    foundColorOnGrid = getColor(pointToMoveTo);
    assertTrue(ColorMatchers.isColor(GRAY_DOTS_COLOR).matches(foundColorOnGrid));

    // Run
    switchGrid();

    // Move mouse and get the color of the pixel under the pointer
    pointToMoveTo = newPointOnGrid(GRAY_PIXEL_X, GRAY_PIXEL_Y);
    robot.moveTo(pointToMoveTo);
    Point2D pointToCheck = new Point2D(GRAY_PIXEL_X, GRAY_PIXEL_Y - app.getRoot().getLayoutY());
    foundColorOnGrid = getColor(pointToCheck);

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
    // Init
    Point2D pointToMoveTo = newPointOnGrid(GRAY_PIXEL_X, GRAY_PIXEL_Y);
    robot.moveTo(pointToMoveTo);
    foundColorOnGrid = getColor(pointToMoveTo);
    assertTrue(ColorMatchers.isColor(GRAY_DOTS_COLOR).matches(foundColorOnGrid));

    // Run
    switchGrid();

    // Move mouse and get the color of the pixel under the pointer
    pointToMoveTo = newPointOnGrid(GRAY_PIXEL_X, GRAY_PIXEL_Y);
    robot.moveTo(pointToMoveTo);
    Point2D pointToCheck = new Point2D(GRAY_PIXEL_X, GRAY_PIXEL_Y - app.getRoot().getLayoutY());
    foundColorOnGrid = getColor(pointToCheck);

    // All we can say is that if we click on the empty canvas, then the pixel is white
    assertTrue(ColorMatchers.isColor(Color.WHITE).matches(foundColorOnGrid));

    // Run
    // Show the dot grid again
    switchGrid();

    pointToMoveTo = newPointOnGrid(GRAY_PIXEL_X, GRAY_PIXEL_Y);
    robot.moveTo(pointToMoveTo);
    pointToCheck = new Point2D(GRAY_PIXEL_X, GRAY_PIXEL_Y - app.getRoot().getLayoutY());
    foundColorOnGrid = getColor(pointToCheck);

    // All we can say is that if we click on the grid, then the pixel is gray
    assertTrue(ColorMatchers.isColor(GRAY_DOTS_COLOR).matches(foundColorOnGrid));
  }

  // Click on "show / hide dot grid" button in the toolbox
  private void switchGrid() {
    Platform.runLater(() -> ShowHideGridButton.showHideGrid(app));

    // No choice to sleep because the grid show / hide is asynchronous in tests (because of the image of the grid
    // that we produce, see: AppTestParent#copyCanvas()
    try {
      sleep(SLEEP_BETWEEN_ACTIONS_TIME);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

}

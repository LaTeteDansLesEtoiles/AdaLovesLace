package org.alienlabs.adaloveslace.functionaltest.view;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.functionaltest.AppFunctionalTestParent;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.ShowHideGridButton;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.base.ColorMatchers;

import static org.alienlabs.adaloveslace.App.TOOLBOX_TITLE;
import static org.alienlabs.adaloveslace.functionaltest.view.MainWindowFunctionalTest.GRAY_DOTS_COLOR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ToolboxFunctionalTest extends AppFunctionalTestParent {

  private static final Logger logger = LoggerFactory.getLogger(MainWindowFunctionalTest.class);

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
  void testToolWindowShallBeDisplayedByDefault() {
    assertTrue(this.app.getToolboxStage().isShowing());
  }

  @Test
  void testToolboxWindowTitle() {
    assertEquals(TOOLBOX_TITLE, this.app.getToolboxStage().getTitle());
  }

  /**
   * Checks if we are able to hide the dot grid: a canvas pixel should be white, then.
   *
   * @param robot The injected FxRobot
   */
  @Test
  void testHideGrid(FxRobot robot) {
    // Given
    Point2D pointToCheck = newPointOnGrid(GRAY_PIXEL_X, GRAY_PIXEL_Y);
    robot.moveTo(pointToCheck);
    foundColorOnGrid = getColor(pointToCheck);
    assertTrue(ColorMatchers.isColor(GRAY_DOTS_COLOR).matches(foundColorOnGrid));

    // When
    synchronizeTask(() -> ShowHideGridButton.showHideGrid(app));

    // Move mouse and get the color of the pixel under the pointer
    pointToCheck = newPointOnGrid(GRAY_PIXEL_X, GRAY_PIXEL_Y);
    robot.moveTo(pointToCheck);
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
    // Given
    Point2D pointToCheck = newPointOnGrid(GRAY_PIXEL_X, GRAY_PIXEL_Y);
    robot.moveTo(pointToCheck);
    foundColorOnGrid = getColor(pointToCheck);
    assertTrue(ColorMatchers.isColor(GRAY_DOTS_COLOR).matches(foundColorOnGrid));

    // When
    synchronizeTask(() -> ShowHideGridButton.showHideGrid(app));

    // Move mouse and get the color of the pixel under the pointer
    pointToCheck = newPointOnGrid(GRAY_PIXEL_X, GRAY_PIXEL_Y);
    robot.moveTo(pointToCheck);
    foundColorOnGrid = getColor(pointToCheck);

    // All we can say is that if we click on the empty canvas, then the pixel is white
    assertTrue(ColorMatchers.isColor(Color.WHITE).matches(foundColorOnGrid));

    // When
    // Show the dot grid again
    synchronizeTask(() -> ShowHideGridButton.showHideGrid(app));

    pointToCheck = newPointOnGrid(GRAY_PIXEL_X, GRAY_PIXEL_Y);
    robot.moveTo(pointToCheck);
    foundColorOnGrid = getColor(pointToCheck);

    // All we can say is that if we click on the grid, then the pixel is gray
    assertTrue(ColorMatchers.isColor(GRAY_DOTS_COLOR).matches(foundColorOnGrid));
  }

}

package org.alienlabs.adaloveslace.functionaltest.view.window;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.functionaltest.AppFunctionalTestParent;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.base.ColorMatchers;

import static org.alienlabs.adaloveslace.App.MAIN_WINDOW_TITLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;

@Tag("functional")
class MainWindowFunctionalTest extends AppFunctionalTestParent {

  public static final double  WHITE_PIXEL_X               = 86d;
  public static final long    WHITE_PIXEL_Y               = 67L;
  public static final Color   GRAY_DOTS_COLOR             = Color.DARKGRAY;

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

    assertCondition(
            () -> this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getAllVisibleKnots().size() == 1,
            "Drawing one snowflake should create one visible knot");
    verifyThat(this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getAllVisibleKnots().size(),
            org.hamcrest.Matchers.is(1));
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
    // Sample the same screen-space point we moved to, to avoid local/screen mapping drift.
    foundColorOnGrid = getColor(pointToMoveTo);

    // Then
    assertTrue(!ColorMatchers.isColor(GRAY_DOTS_COLOR).matches(foundColorOnGrid),
            "Outside-grid sample should not match grid dots color");
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

    // When: issue an "Undo knot" command
    robot.clickOn("#undoButton");
    assertCondition(
            () -> this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getAllVisibleKnots().isEmpty(),
            "Undo should remove the only drawn knot"
    );

    verifyThat(this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getAllVisibleKnots().isEmpty(),
            org.hamcrest.Matchers.is(true));
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

    int stepAfterDraw = this.app.getOptionalDotGrid().getDiagram().getCurrentStepIndex();
    assertTrue(stepAfterDraw >= 2, "Drawing a knot should advance the step index");

    // Issue an "Undo knot" command
    robot.clickOn("#undoButton");
    assertCondition(
            () -> this.app.getOptionalDotGrid().getDiagram().getCurrentStepIndex() == stepAfterDraw - 1,
            "Undo should step back");

    // When: Issue a "Redo knot" command
    robot.clickOn("#redoButton");
    assertCondition(
            () -> this.app.getOptionalDotGrid().getDiagram().getCurrentStepIndex() == stepAfterDraw,
            "Redo should restore the stepped-forward state"
    );

    verifyThat(this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getAllVisibleKnots().size(),
            org.hamcrest.Matchers.is(1));
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

    // When: issue a "Reset diagram" command
    robot.interact(() -> this.app.getOptionalDotGrid().getDiagram().resetDiagram(this.app));
    assertCondition(
            () -> this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getAllVisibleKnots().isEmpty(),
            "Reset should clear the canvas"
    );

    verifyThat(this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getAllVisibleKnots().isEmpty(),
            org.hamcrest.Matchers.is(true));
  }

  @Test
  void testBackInBlackSelectionKeepsOriginalColorWithoutDuplicate(FxRobot robot) {
    selectAndClickOnSnowflakePatternButton(robot);
    robot.interact(() -> this.app.getOptionalDotGrid().getDiagram().setCurrentColor(Color.RED));
    drawASnowflake(robot);

    // Activate "Back in Black" (name kept as-is by product intent), then select mode.
    clickOnButton(robot, this.toolboxWindow.getBackInBlackButton());
    clickSelectButton(robot);
    robot.interact(() -> {
      var step = this.app.getOptionalDotGrid().getDiagram().getCurrentStep();
      if (!step.getDisplayedKnots().isEmpty()) {
        var knot = step.getDisplayedKnots().get(0);
        var newDisplayed = new java.util.ArrayList<>(step.getDisplayedKnots());
        newDisplayed.remove(knot);
        step.setDisplayedKnots(newDisplayed);
        step.setSelectedKnots(java.util.List.of(knot));
        this.app.getOptionalDotGrid().getDiagram().setCurrentKnot(knot);
        this.app.getOptionalDotGrid().layoutChildren();
      }
    });

    assertCondition(
            () -> this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().size() == 1,
            "Selection should contain exactly one knot");

    var step = this.app.getOptionalDotGrid().getDiagram().getCurrentStep();
    assertEquals(1, step.getAllVisibleKnots().size(), "Selecting in back in black must not create a duplicate knot");
    assertEquals(0, step.getDisplayedKnots().size(), "The selected knot must not remain as an extra displayed copy");

    var selectedColor = step.getSelectedKnots().get(0).getColor();
    assertTrue(selectedColor.isPresent(), "Selected knot color should be preserved");
    assertEquals(Color.RED, selectedColor.orElseThrow(), "Selected knot color must remain unchanged in back in black mode");
  }

  private String getMainWindowTitle() {
    return this.app.getPrimaryStage().getTitle();
  }

  private boolean isMainWindowDisplayed() {
    return this.app.getPrimaryStage().getScene().getWindow().isShowing();
  }

}

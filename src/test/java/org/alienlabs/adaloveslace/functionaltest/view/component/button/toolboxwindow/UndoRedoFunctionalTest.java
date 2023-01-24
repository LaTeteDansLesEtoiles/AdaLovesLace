package org.alienlabs.adaloveslace.functionaltest.view.component.button.toolboxwindow;

import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.functionaltest.AppFunctionalTestParent;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.base.ColorMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;

class UndoRedoFunctionalTest extends AppFunctionalTestParent {

  /**
   * Init method called before each test
   * @param primaryStage The injected window (stage)
   */
  @Start
  public void start(Stage primaryStage) {
    super.start(primaryStage);
  }

  @Test
  void test_add_a_knot_then_undo_step_then_redo_step(FxRobot robot) {
    // Given
    synchronizeTask(() -> selectAndClickOnSnowflakePatternButton(robot));
    synchronizeTask(() -> drawFirstSnowflake(robot));

    // When
    synchronizeTask(() -> robot.clickOn(this.toolboxWindow.getUndoKnotButton(), MouseButton.PRIMARY));

    // Then
    // Logical state
    this.sleepMainThreadLonger();
    assertTrue(app.getDiagram().getCurrentStep().getDisplayedKnots().isEmpty(),
            "We should not have any Knot in this Step!");

    // Physical state
    Point2D snowflakeOnTheGrid = newPointOnGrid(FIRST_SNOWFLAKE_PIXEL_X + 20d, FIRST_SNOWFLAKE_PIXEL_Y + 20d);

    robot.moveTo(snowflakeOnTheGrid);
    this.sleepMainThread();
    foundColorOnGrid = getColor(snowflakeOnTheGrid);
    verifyThat(foundColorOnGrid, ColorMatchers.isColor(Color.WHITE));

    // When
    synchronizeTask(() -> robot.clickOn(this.toolboxWindow.getRedoKnotButton(), MouseButton.PRIMARY));

    // Then
    // Logical state
    this.sleepMainThreadLonger();
    assertEquals(1, app.getDiagram().getCurrentStep().getDisplayedKnots().size(),
            "We should  have 1 Knot in this Step!");

    // Physical state
    Point2D pointToCheck = newPointOnGridForFirstNonGridNode();
    robot.moveTo(pointToCheck);
    this.sleepMainThread();

    // Then
    foundColorOnGrid = getColor(pointToCheck);
    // If we choose a point in the snowflake it must be of the right color
    assertTrue(ColorMatchers.isColor(SNOWFLAKE_DOT_COLOR).matches(foundColorOnGrid),
            "Expected color: " + SNOWFLAKE_DOT_COLOR + ", actual color: " + foundColorOnGrid);
  }

  @Test
  void test_add_2_knots_then_undo_a_step_then_make_another_step(FxRobot robot) {
    // Given
    synchronizeTask(() -> selectAndClickOnSnowflakePatternButton(robot));
    synchronizeTask(() -> drawFirstSnowflake(robot));
    synchronizeTask(() -> drawSecondSnowflake(robot));

    // When
    synchronizeTask(() -> robot.clickOn(this.toolboxWindow.getUndoKnotButton(), MouseButton.PRIMARY));
    this.sleepMainThreadLonger();
    synchronizeTask(() -> drawOtherSnowflake(robot));

    // Then
    this.sleepMainThread();
    // Logical state
    assertEquals(2, app.getDiagram().getAllSteps().size(),
            "We should have two Steps in this diagram!");
    assertEquals(2, app.getDiagram().getCurrentStep().getDisplayedKnots().size(),
            "We should have 2 Knots in this Step!");

    // Physical state
    Point2D snowflakeOnTheGrid = newPointOnGrid(SECOND_SNOWFLAKE_PIXEL_X + 20d, SECOND_SNOWFLAKE_PIXEL_Y + 20d);
    robot.moveTo(snowflakeOnTheGrid);
    this.sleepMainThread();
    foundColorOnGrid = getColor(snowflakeOnTheGrid);
    verifyThat(foundColorOnGrid, ColorMatchers.isColor(Color.WHITE));
  }

  @Test
  void test_add_a_knot_then_turn_it_twice_then_undo_one_step(FxRobot robot) {
    // Given
    synchronizeTask(() -> drawSecondSnowflake(robot));
    initDrawAndSelectSnowFlake(robot);

    // When
    synchronizeTask(() -> incrementSpinner(this.geometryWindow.getRotationSpinner2()));
    synchronizeTask(() -> incrementSpinner(this.geometryWindow.getRotationSpinner2()));
    synchronizeTask(() -> robot.clickOn(this.toolboxWindow.getUndoKnotButton(), MouseButton.PRIMARY));

    // Then
    this.sleepMainThreadLonger();
    assertEquals(5, app.getDiagram().getAllSteps().size(),
            "We should now have 5 Steps in this Diagram!");
    assertEquals(4, app.getDiagram().getCurrentStepIndex(),
            "We should now be at the 4th Step in this Diagram!");
    assertEquals(1, app.getDiagram().getCurrentStep().getSelectedKnots().size(),
            "We should have 1 selected Knot in this Diagram!");
    assertEquals(10, app.getDiagram().getCurrentStep().getSelectedKnots().stream().findFirst().get().getRotationAngle(),
            "This Knot should not be turned!");
  }

  @Test
  void test_add_a_knot_then_zoom_it_twice_then_undo_one_step(FxRobot robot) {
    // Given
    synchronizeTask(() -> drawSecondSnowflake(robot));
    initDrawAndSelectSnowFlake(robot);

    // When
    synchronizeTask(() -> incrementSpinner(this.geometryWindow.getZoomSpinner2()));
    synchronizeTask(() -> incrementSpinner(this.geometryWindow.getZoomSpinner2()));
    synchronizeTask(() -> robot.clickOn(this.toolboxWindow.getUndoKnotButton(), MouseButton.PRIMARY));

    // Then
    this.sleepMainThreadLonger();
    assertEquals(5, app.getDiagram().getAllSteps().size(),
            "We should now have 5 Steps in this Diagram!");
    assertEquals(4, app.getDiagram().getCurrentStepIndex(),
            "We should now be at the 4th Step in this Diagram!");
    assertEquals(1, app.getDiagram().getCurrentStep().getSelectedKnots().size(),
            "We should have 1 selected Knot in this Diagram!");
    assertEquals(2, app.getDiagram().getCurrentStep().getSelectedKnots().stream().findFirst().get().getZoomFactor(),
            "This Knot should not be turned!");
  }

}

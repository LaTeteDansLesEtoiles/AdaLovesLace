package org.alienlabs.adaloveslace.functionaltest.view.component.button.toolboxwindow;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.functionaltest.AppFunctionalTestParent;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.base.ColorMatchers;

import static org.junit.jupiter.api.Assertions.*;

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
    void test_add_a_knot_then_undo_step_then_redo_step(final FxRobot robot) {
        // Given
        synchronizeTask(() -> selectAndClickOnSnowflakePatternButton(robot));
        synchronizeTask(() -> drawFirstSnowflake(robot));

        Point2D pointToCheck = newPointOnGrid(FIRST_SNOWFLAKE_PIXEL_X + 20d, FIRST_SNOWFLAKE_PIXEL_Y + 20d);
        robot.moveTo(pointToCheck);
        this.sleepMainThread();
        Color foundColorOnGridBeforeUndo = getColor(pointToCheck);

        // When
        synchronizeTask(() -> robot.clickOn(this.toolboxWindow.getUndoKnotButton()));

        // Then
        this.sleepMainThread();
        Point2D snowflakeOnTheGrid = newPointOnGrid(FIRST_SNOWFLAKE_PIXEL_X + 20d, FIRST_SNOWFLAKE_PIXEL_Y + 20d);

        robot.moveTo(snowflakeOnTheGrid);
        this.sleepMainThread();
        foundColorOnGrid = getColor(snowflakeOnTheGrid);
        assertNotEquals(foundColorOnGridBeforeUndo, foundColorOnGrid, "Both colors should not be the same!");

        // When
        synchronizeTask(() -> robot.clickOn(this.toolboxWindow.getRedoKnotButton()));

        // Then
        this.sleepMainThread();
        pointToCheck = newPointOnGrid(FIRST_SNOWFLAKE_PIXEL_X + 20d, FIRST_SNOWFLAKE_PIXEL_Y + 20d);
        robot.moveTo(pointToCheck);
        this.sleepMainThread();

        Color foundColorOnGridAfterRedo = getColor(pointToCheck);
        // If we choose a point in the snowflake it must be of the right color
        assertTrue(ColorMatchers.isColor(foundColorOnGridBeforeUndo).matches(foundColorOnGridAfterRedo),
                "Before undo color: " + foundColorOnGridBeforeUndo + ", after redo color: " + foundColorOnGridAfterRedo);
    }

    @Test
    void test_add_2_knots_then_undo_a_step_then_make_another_step(final FxRobot robot) {
        // Given
        synchronizeTask(() -> selectAndClickOnSnowflakePatternButton(robot));
        synchronizeTask(() -> drawFirstSnowflake(robot));
        synchronizeTask(() -> drawSecondSnowflake(robot));

        Point2D snowflakePoint = newPointOnGrid(SECOND_SNOWFLAKE_PIXEL_X + 20d, SECOND_SNOWFLAKE_PIXEL_Y + 20d);
        robot.moveTo(snowflakePoint);
        this.sleepMainThread();
        Color foundColorOnGridBeforeUndo = getColor(snowflakePoint);

        // When
        synchronizeTask(() -> robot.clickOn(this.toolboxWindow.getUndoKnotButton()));
        this.sleepMainThread();

        // Then
        Point2D snowflakeOnTheGrid = newPointOnGrid(SECOND_SNOWFLAKE_PIXEL_X + 20d, SECOND_SNOWFLAKE_PIXEL_Y + 20d);
        robot.moveTo(snowflakeOnTheGrid);
        this.sleepMainThread();

        foundColorOnGrid = getColor(snowflakeOnTheGrid);
        assertNotEquals(foundColorOnGridBeforeUndo, foundColorOnGrid);

        // When
        synchronizeTask(() -> robot.clickOn(this.toolboxWindow.getRedoKnotButton()));

        // Then
        this.sleepMainThread();
        snowflakeOnTheGrid = newPointOnGrid(SECOND_SNOWFLAKE_PIXEL_X + 20d, SECOND_SNOWFLAKE_PIXEL_Y + 20d);
        robot.moveTo(snowflakeOnTheGrid);
        this.sleepMainThread();

        Color foundColorOnGridAfterRedo = getColor(snowflakeOnTheGrid);
        assertTrue(ColorMatchers.isColor(foundColorOnGridBeforeUndo).matches(foundColorOnGridAfterRedo),
                "Before undo color: " + foundColorOnGridBeforeUndo + ", after redo color: " + foundColorOnGridAfterRedo);
    }

    @Test
    void test_add_a_knot_then_turn_it_twice_then_undo_one_step(final FxRobot robot) {
        // Given
        synchronizeTask(() -> drawSecondSnowflake(robot));
        initDrawAndSelectSnowFlake(robot);

        // When
        synchronizeTask(() -> incrementSpinner(this.geometryWindow.getRotationSpinner2()));
        synchronizeTask(() -> incrementSpinner(this.geometryWindow.getRotationSpinner2()));
        synchronizeTask(() -> robot.clickOn(this.toolboxWindow.getUndoKnotButton()));

        // Then
        assertEquals(6, app.getOptionalDotGrid().getDiagram().getCurrentStepIndex(),
                "We should be at Step #6!");
        assertEquals(1, app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().size(),
                "We should have 1 selected Knot in this Diagram!");
        assertEquals(10, app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().stream().findFirst().get().getRotationAngle(),
                "This Knot should not be turned!");
    }

    @Test
    void test_add_a_knot_then_zoom_it_twice_then_undo_one_step(final FxRobot robot) {
        // Given
        synchronizeTask(() -> drawSecondSnowflake(robot));
        initDrawAndSelectSnowFlake(robot);

        // When
        synchronizeTask(() -> incrementSpinner(this.geometryWindow.getZoomSpinner2()));
        synchronizeTask(() -> incrementSpinner(this.geometryWindow.getZoomSpinner2()));
        synchronizeTask(() -> robot.clickOn(this.toolboxWindow.getUndoKnotButton()));

        // Then
        this.sleepMainThread();
        assertEquals(6, app.getOptionalDotGrid().getDiagram().getCurrentStepIndex(),
                "We should be at Step #6!");
        assertEquals(1, app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().size(),
                "We should have 1 selected Knot in this Diagram!");
        assertEquals(2, app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().stream().findFirst().get().getZoomFactor(),
                "This Knot should not be turned!");
    }

}

package org.alienlabs.adaloveslace.functionaltest.view.component.button.toolboxwindow;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.functionaltest.AppFunctionalTestParent;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.RedoKnotButton;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.UndoKnotButton;
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
        synchronizeTask(() -> UndoKnotButton.undoKnot(app));

        // Then
        this.sleepMainThread();
        Point2D snowflakeOnTheGrid = newPointOnGrid(FIRST_SNOWFLAKE_PIXEL_X + 20d, FIRST_SNOWFLAKE_PIXEL_Y + 20d);

        robot.moveTo(snowflakeOnTheGrid);
        this.sleepMainThread();
        foundColorOnGrid = getColor(snowflakeOnTheGrid);
        assertNotEquals(foundColorOnGridBeforeUndo, foundColorOnGrid, "Both colors should not be the same!");

        // When
        synchronizeTask(() -> RedoKnotButton.redoKnot(app));

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
        synchronizeTask(() -> UndoKnotButton.undoKnot(app));
        this.sleepMainThread();

        // Then
        Point2D snowflakeOnTheGrid = newPointOnGrid(SECOND_SNOWFLAKE_PIXEL_X + 20d, SECOND_SNOWFLAKE_PIXEL_Y + 20d);
        robot.moveTo(snowflakeOnTheGrid);
        this.sleepMainThread();

        foundColorOnGrid = getColor(snowflakeOnTheGrid);
        assertNotEquals(foundColorOnGridBeforeUndo, foundColorOnGrid);

        // When
        synchronizeTask(() -> RedoKnotButton.redoKnot(app));

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
    void test_add_a_knot_then_turn_it_then_undo(final FxRobot robot) {
        // Given
        initDrawAndSelectSnowFlake(robot);

        // When
        synchronizeTask(() -> incrementSpinner(this.geometryWindow.getRotationSpinner3()));

        Point2D snowflakePoint = newPointOnGrid(FIRST_SNOWFLAKE_PIXEL_X + 40d, FIRST_SNOWFLAKE_PIXEL_Y + 40d);
        robot.moveTo(snowflakePoint);
        this.sleepMainThread();
        Color foundColorOnGridBeforeUndo = getColor(snowflakePoint);

        synchronizeTask(() -> UndoKnotButton.undoKnot(app));

        // Then
        this.sleepMainThread();
        Point2D snowflakeOnTheGrid = newPointOnGrid(FIRST_SNOWFLAKE_PIXEL_X + 40d, FIRST_SNOWFLAKE_PIXEL_Y + 40d);
        robot.moveTo(snowflakeOnTheGrid);
        this.sleepMainThread();

        Color foundColorOnGridAfterUndo = getColor(snowflakeOnTheGrid);
        assertFalse(ColorMatchers.isColor(foundColorOnGridBeforeUndo).matches(foundColorOnGridAfterUndo),
                "Before undo color: " + foundColorOnGridBeforeUndo + ", after redo color: " + foundColorOnGridAfterUndo);
    }

    @Test
    void test_add_a_knot_then_zoom_it_then_undo(final FxRobot robot) {
        // Given
        initDrawAndSelectSnowFlake(robot);

        // When
        synchronizeTask(() -> incrementSpinner(this.geometryWindow.getZoomSpinner3()));

        Point2D snowflakePoint = newPointOnGrid(FIRST_SNOWFLAKE_PIXEL_X + 40d, FIRST_SNOWFLAKE_PIXEL_Y  + 40d);
        robot.moveTo(snowflakePoint);
        this.sleepMainThread();
        Color foundColorOnGridBeforeUndo = getColor(snowflakePoint);

        synchronizeTask(() -> UndoKnotButton.undoKnot(app));

        // Then
        this.sleepMainThread();
        Point2D snowflakeOnTheGrid = newPointOnGrid(FIRST_SNOWFLAKE_PIXEL_X + 40d, FIRST_SNOWFLAKE_PIXEL_Y + 40d);
        robot.moveTo(snowflakeOnTheGrid);
        this.sleepMainThread();

        Color foundColorOnGridAfterUndo = getColor(snowflakeOnTheGrid);
        assertFalse(ColorMatchers.isColor(foundColorOnGridBeforeUndo).matches(foundColorOnGridAfterUndo),
                "Before undo color: " + foundColorOnGridBeforeUndo + ", after redo color: " + foundColorOnGridAfterUndo);
    }

}

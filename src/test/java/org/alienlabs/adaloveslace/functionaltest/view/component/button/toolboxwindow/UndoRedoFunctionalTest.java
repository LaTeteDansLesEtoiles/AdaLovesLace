package org.alienlabs.adaloveslace.functionaltest.view.component.button.toolboxwindow;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.functionaltest.AppFunctionalTestParent;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid.RedoKnotButton;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid.UndoKnotButton;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.base.ColorMatchers;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

class UndoRedoFunctionalTest extends AppFunctionalTestParent {

    /**
     * Init method called before each test
     * @param primaryStage The injected window (stage)
     */
    @Override
    @Start
    public void start(Stage primaryStage) {
        super.start(primaryStage);
    }

    /**
     * Checks if the 2 selected Knots are the right ones and if they are shifted on bottom right when duplicating knots.
     *
     */
    @Test
    @Disabled("Flaky in Jenkins")
    void should_duplicate_two_knots(final FxRobot robot) {
        // Given
    selectAndClickOnSnowflakePatternButton(robot);
    drawOtherSnowflake(robot); // Not to be duplicated
    drawASnowflake(robot); // To duplicate
    drawSecondSnowflake(robot); // To duplicate
    clickSelectButton(robot);
    selectSnowflake(FIRST_SNOWFLAKE_PIXEL_X, FIRST_SNOWFLAKE_PIXEL_Y, 0);

    selectSnowflake(SECOND_SNOWFLAKE_PIXEL_X, SECOND_SNOWFLAKE_PIXEL_Y, 1); // The first 2 snowflakes shall be selected, ready to be copied

    unselectControlKey(robot); // The first 2 snowflakes shall be selected, ready to be copied

        // When
        clickOnButton(robot, app.getGeometryWindow().getDuplicationButton()); // Copy the first 2 snowflakes

        // Then
        assertEquals(4, this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().size(),
                "We should have 4 selected knots");

        // First copied knot
        assertEquals(215d,
                this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().stream().
                        min(Comparator.comparing(knot -> Double.valueOf(knot.getX()))).get().getX());
        assertEquals(135d,
                this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().stream().
                        min(Comparator.comparing(knot -> Double.valueOf(knot.getX()))).get().getY());

        // Second copied knot
        assertEquals(245d,
                this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().stream().
                        sorted(Comparator.comparing(knot -> Double.valueOf(knot.getX()))).toList().get(1).getX());
        assertEquals(165d,
                this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().stream().
                        sorted(Comparator.comparing(knot -> Double.valueOf(knot.getX()))).toList().get(1).getY());
    }

    /**
     * Checks if the 2 selected Knots are the right ones and if the coies are undone.
     *
     */
    @Test
    void should_duplicate_two_knots_then_undo(final FxRobot robot) {
        // Given
        selectAndClickOnSnowflakePatternButton(robot);
        drawASnowflake(robot); // To duplicate
        drawSecondSnowflake(robot); // To duplicate
        drawOtherSnowflake(robot); // Not to be duplicated

        Platform.runLater(() -> {
            clickSelectButton(robot);
            selectTwoSnowflakes(robot); // The first 2 snowflakes shall be selected, ready to be copied
        });

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Platform.runLater(() -> {
            // When
            //clickOnButton(robot, app.getGeometryWindow().getDuplicationButton()); // Copy the first 2 snowflakes
            // clickOnButton(robot, app.getToolboxWindow().getUndoKnotButton());
        });

        // Then
        // First copied knots
        assertEquals(2, this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().size(),
                "We should have undone the 2 other knots, hence having only 2 selected knots");
        assertEquals(215d,
                this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().stream().
                        min(Comparator.comparing(knot -> Double.valueOf(knot.getX()))).get().getX());
        assertEquals(135d,
                this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().stream().
                        min(Comparator.comparing(knot -> Double.valueOf(knot.getX()))).get().getY());
    }

    @Test
    void test_add_a_knot_then_undo_step_then_redo_step(final FxRobot robot) {
        // Given
        selectAndClickOnSnowflakePatternButton(robot);
        drawASnowflake(robot);

        Platform.runLater(() -> {
            Point2D pointToCheck = newPointOnGrid(FIRST_SNOWFLAKE_PIXEL_X + 25d, FIRST_SNOWFLAKE_PIXEL_Y + 25d);
            robot.moveTo(pointToCheck);

            Color foundColorOnGridBeforeUndo = getColor(pointToCheck);

            // When
            robot.clickOn("#undoButton");

            // Then
            Point2D snowflakeOnTheGrid = newPointOnGrid(FIRST_SNOWFLAKE_PIXEL_X + 20d, FIRST_SNOWFLAKE_PIXEL_Y + 20d);

            robot.moveTo(snowflakeOnTheGrid);
            foundColorOnGrid = getColor(snowflakeOnTheGrid);
            assertNotEquals(foundColorOnGridBeforeUndo, foundColorOnGrid, "Both colors should not be the same!");

            // When
            RedoKnotButton.redoKnot();

            // Then
            robot.moveTo(newPointOnGrid(FIRST_SNOWFLAKE_PIXEL_X + 20d, FIRST_SNOWFLAKE_PIXEL_Y + 20d));

            Color foundColorOnGridAfterRedo = getColor(pointToCheck);
            // If we choose a point in the snowflake it must be of the right color
            assertTrue(ColorMatchers.isColor(foundColorOnGridBeforeUndo).matches(foundColorOnGridAfterRedo),
                    "Before undo color: " + foundColorOnGridBeforeUndo + ", after redo color: " + foundColorOnGridAfterRedo);
        });
    }

    @Test
    @Disabled("Flaky in Jenkins")
    void test_add_2_knots_then_undo_a_step_then_make_another_step(final FxRobot robot) {
        // Given
        selectAndClickOnSnowflakePatternButton(robot);
        drawASnowflake(robot);
        drawSecondSnowflake(robot);

        Point2D snowflakePoint = newPointOnGrid(SECOND_SNOWFLAKE_PIXEL_X + 20d, SECOND_SNOWFLAKE_PIXEL_Y + 20d);
        robot.moveTo(snowflakePoint);
        Color foundColorOnGridBeforeUndo = getColor(snowflakePoint);

        // When
        UndoKnotButton.undoKnot();

        // Then
        robot.moveTo(newPointOnGrid(SECOND_SNOWFLAKE_PIXEL_X + 20d, SECOND_SNOWFLAKE_PIXEL_Y + 20d));

        foundColorOnGrid = getColor(newPointOnGrid(SECOND_SNOWFLAKE_PIXEL_X + 20d, SECOND_SNOWFLAKE_PIXEL_Y + 20d));
        assertNotEquals(foundColorOnGridBeforeUndo, foundColorOnGrid);

        // When
        RedoKnotButton.redoKnot();

        // Then
        robot.moveTo(newPointOnGrid(SECOND_SNOWFLAKE_PIXEL_X + 20d, SECOND_SNOWFLAKE_PIXEL_Y + 20d));

        Color foundColorOnGridAfterRedo = getColor(newPointOnGrid(SECOND_SNOWFLAKE_PIXEL_X + 20d, SECOND_SNOWFLAKE_PIXEL_Y + 20d));
        assertTrue(ColorMatchers.isColor(foundColorOnGridBeforeUndo).matches(foundColorOnGridAfterRedo),
                "Before undo color: " + foundColorOnGridBeforeUndo + ", after redo color: " + foundColorOnGridAfterRedo);
    }

    @Test
    void test_add_a_knot_then_turn_it_then_undo(final FxRobot robot) {
        // Given
        selectAndClickOnSnowflakePatternButton(robot);
        drawASnowflake(robot);
        clickSelectButton(robot);
        selectSnowflake(FIRST_SNOWFLAKE_PIXEL_X, FIRST_SNOWFLAKE_PIXEL_Y, 0);

        // When
        incrementSpinner(robot, this.geometryWindow.getRotationSpinner3());

        Point2D snowflakePoint = newPointOnGrid(FIRST_SNOWFLAKE_PIXEL_X + 38d, FIRST_SNOWFLAKE_PIXEL_Y + 35d);
        robot.moveTo(snowflakePoint);

        Color foundColorOnGridBeforeUndo = getColor(snowflakePoint);
        clickOnButton(robot, app.getToolboxWindow().getUndoKnotButton());

        // Then
        robot.moveTo(snowflakePoint);

        Color foundColorOnGridAfterUndo = getColor(snowflakePoint);
        assertFalse(ColorMatchers.isColor(foundColorOnGridBeforeUndo).matches(foundColorOnGridAfterUndo),
                "Before undo color: " + foundColorOnGridBeforeUndo + ", after undo color: " + foundColorOnGridAfterUndo);
    }

    @Test
    void test_add_a_knot_then_zoom_it_then_undo(final FxRobot robot) {
        // Given
        selectAndClickOnSnowflakePatternButton(robot);
        drawASnowflake(robot);
        clickSelectButton(robot);
        selectSnowflake(FIRST_SNOWFLAKE_PIXEL_X, FIRST_SNOWFLAKE_PIXEL_Y, 0);

        // When
        incrementSpinner(robot, this.geometryWindow.getZoomSpinner3());

        Point2D snowflakePoint = newPointOnGrid(FIRST_SNOWFLAKE_PIXEL_X + 38d, FIRST_SNOWFLAKE_PIXEL_Y  + 35d);
        robot.moveTo(snowflakePoint);

        Color foundColorOnGridBeforeUndo = getColor(snowflakePoint);

        clickOnButton(robot, app.getToolboxWindow().getUndoKnotButton());

        // Then
        robot.moveTo(snowflakePoint);

        Color foundColorOnGridAfterUndo = getColor(snowflakePoint);
        assertFalse(ColorMatchers.isColor(foundColorOnGridBeforeUndo).matches(foundColorOnGridAfterUndo),
                "Before undo color: " + foundColorOnGridBeforeUndo + ", after undo color: " + foundColorOnGridAfterUndo);
    }

}

package org.alienlabs.adaloveslace.functionaltest.domain;

import javafx.stage.Stage;
import org.alienlabs.adaloveslace.functionaltest.AppFunctionalTestParent;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid.RedoKnotButton;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid.UndoKnotButton;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("functional")
class StepsFunctionalTest extends AppFunctionalTestParent {

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
    void should_create_history_when_drawing_steps(FxRobot robot) {
        robot.clickOn("#drawingButton");
        robot.clickOn(toolboxWindow.getSnowflakeButton());
        int initialSteps = app.getOptionalDotGrid().getDiagram().getAllSteps().size();

        // Use the same grid-relative coordinates as the stable snowflake helpers (small Y values hit window chrome).
        drawSnowFlake(robot, FIRST_SNOWFLAKE_PIXEL_X, FIRST_SNOWFLAKE_PIXEL_Y);
        WaitForAsyncUtils.waitForFxEvents();
        drawSnowFlake(robot, SECOND_SNOWFLAKE_PIXEL_X, SECOND_SNOWFLAKE_PIXEL_Y);
        WaitForAsyncUtils.waitForFxEvents();
        drawSnowFlake(robot, SECOND_SNOWFLAKE_PIXEL_X + 95d, SECOND_SNOWFLAKE_PIXEL_Y + 95d);
        WaitForAsyncUtils.waitForFxEvents();

        assertCondition(
                () -> app.getOptionalDotGrid().getDiagram().getCurrentStep().getAllVisibleKnots().size() == 3,
                "Drawing three knots should make three visible knots");
        assertTrue(app.getOptionalDotGrid().getDiagram().getAllSteps().size() > initialSteps);
    }

    @Test
    void should_undo_and_redo_last_step(FxRobot robot) {
        robot.clickOn("#drawingButton");
        robot.clickOn(toolboxWindow.getSnowflakeButton());
        drawSnowFlake(robot, FIRST_SNOWFLAKE_PIXEL_X, FIRST_SNOWFLAKE_PIXEL_Y);
        WaitForAsyncUtils.waitForFxEvents();
        drawSnowFlake(robot, SECOND_SNOWFLAKE_PIXEL_X, SECOND_SNOWFLAKE_PIXEL_Y);
        WaitForAsyncUtils.waitForFxEvents();
        drawSnowFlake(robot, SECOND_SNOWFLAKE_PIXEL_X + 95d, SECOND_SNOWFLAKE_PIXEL_Y + 95d);
        WaitForAsyncUtils.waitForFxEvents();

        robot.interact(UndoKnotButton::undoKnot);
        assertCondition(
                () -> app.getOptionalDotGrid().getDiagram().getCurrentStep().getAllVisibleKnots().size() == 2,
                "Undo should remove the latest knot from current visible state");

        robot.interact(RedoKnotButton::redoKnot);
        assertCondition(
                () -> app.getOptionalDotGrid().getDiagram().getCurrentStep().getAllVisibleKnots().size() == 3,
                "Redo should restore the latest knot in current visible state");
    }

}

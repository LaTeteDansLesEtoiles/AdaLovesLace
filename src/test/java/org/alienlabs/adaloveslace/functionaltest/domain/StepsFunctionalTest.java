package org.alienlabs.adaloveslace.functionaltest.domain;

import javafx.stage.Stage;
import org.alienlabs.adaloveslace.functionaltest.AppFunctionalTestParent;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid.RedoKnotButton;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid.UndoKnotButton;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.testfx.api.FxAssert.verifyThat;

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
    void test_add_3_steps_undo_a_step_add_a_step(FxRobot robot) {
        // Given a diagram with an empty step
        selectAndClickOnSnowflakePatternButton(robot);

        // When
        drawSnowFlake(robot, 60, 70);

        drawSnowFlake(robot, 300, 70);

        drawSnowFlake(robot, 140, 70);

        UndoKnotButton.undoKnot();

        drawSnowFlake(robot, 220, 70);

        // Then
        verifyThat(app.getOptionalDotGrid().getDiagram().getAllSteps(), hasSize(4));

        verifyThat(app.getOptionalDotGrid().getDiagram().getAllSteps().get(3).getDisplayedKnots(), hasSize(3));

        verifyThat(app.getOptionalDotGrid().getDiagram().getAllSteps().get(1).getDisplayedKnots(), hasSize(1));
        verifyThat(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 2)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .findFirst()
                        .get()
                        .getX(), is(60));

        // There is always a Y offset of -10 pixels between where we clicked and where the knot appears
        verifyThat(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 2)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .findFirst()
                        .get()
                        .getY(), is(60));

        verifyThat(app.getOptionalDotGrid().getDiagram().getAllSteps().get(2).getDisplayedKnots(), hasSize(2));
        verifyThat(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 3)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getX() == 300), is(true));
        verifyThat(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 3)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getY() == 60), is(true));

        verifyThat(app.getOptionalDotGrid().getDiagram().getAllSteps().get(3).getDisplayedKnots(), hasSize(3));
        verifyThat(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 4)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getX() == 220), is(true));
        verifyThat(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 4)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getY() == 60), is(true));
    }

    @Test
    void test_add_3_steps_undo_and_redo_a_step_and_add_a_step(FxRobot robot) {
        // Given a diagram with an empty step
        selectAndClickOnSnowflakePatternButton(robot);

        // When
        drawSnowFlake(robot, 60, 70);

        drawSnowFlake(robot, 40, 45);

        drawSnowFlake(robot, 110, 120);

        UndoKnotButton.undoKnot();
        RedoKnotButton.redoKnot();

        drawSnowFlake(robot, 80, 85);

        // Then
        verifyThat(app.getOptionalDotGrid().getDiagram().getAllSteps(), hasSize(6));

        verifyThat(app.getOptionalDotGrid().getDiagram().getAllSteps().get(1).getDisplayedKnots(), hasSize(1));
        verifyThat(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 2)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .findFirst()
                        .get()
                        .getX(), is(60));

        // There is always a Y offset of -10 pixels between where we clicked and where the knot appears
        verifyThat(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 2)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .findFirst()
                        .get()
                        .getY(), is(60));

        verifyThat(app.getOptionalDotGrid().getDiagram().getAllSteps().get(2).getDisplayedKnots(), hasSize(2));
        verifyThat(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 3)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getX() == 40), is(true));
        verifyThat(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 3)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getY() == 35), is(true));

        verifyThat(app.getOptionalDotGrid().getDiagram().getAllSteps().get(3).getDisplayedKnots(), hasSize(3));
        verifyThat(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 4)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getX() == 110), is(true));
        verifyThat(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 4)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getY() == 110), is(true));

        verifyThat(app.getOptionalDotGrid().getDiagram().getAllSteps().get(4).getDisplayedKnots(), hasSize(4));
        verifyThat(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 5)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getX() == 80), is(true));
        verifyThat(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 5)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getY() == 75), is(true));
    }

    @Test
    void test_add_3_steps_and_do_many_undo_redo(FxRobot robot) {
        // Given a diagram with an empty step
        selectAndClickOnSnowflakePatternButton(robot);

        // When
        drawSnowFlake(robot, 60, 70);

        drawSnowFlake(robot, 110, 120);

        drawSnowFlake(robot, 160, 160);

        UndoKnotButton.undoKnot();
        UndoKnotButton.undoKnot();
        UndoKnotButton.undoKnot();
        UndoKnotButton.undoKnot();
        UndoKnotButton.undoKnot();
        UndoKnotButton.undoKnot();
        UndoKnotButton.undoKnot();
        UndoKnotButton.undoKnot();
        UndoKnotButton.undoKnot();
        UndoKnotButton.undoKnot();
        UndoKnotButton.undoKnot();
        UndoKnotButton.undoKnot();
        UndoKnotButton.undoKnot();

        RedoKnotButton.redoKnot();
        RedoKnotButton.redoKnot();
        RedoKnotButton.redoKnot();

        drawSnowFlake(robot, 220, 160);

        // Then
        verifyThat(app.getOptionalDotGrid().getDiagram().getAllSteps(), hasSize(5));

        verifyThat(app.getOptionalDotGrid().getDiagram().getAllSteps().get(1).getDisplayedKnots(), hasSize(1));
        verifyThat(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 2)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .findFirst()
                        .get()
                        .getX(), is(60));
        
        // There is always a Y offset of -10 pixels between where we clicked and where the knot appears
        verifyThat(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 2)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .findFirst()
                        .get()
                        .getY(), is(60));

        verifyThat(app.getOptionalDotGrid().getDiagram().getAllSteps().get(2).getDisplayedKnots(), hasSize(2));
        verifyThat(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 3)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getX() == 110), is(true));
        verifyThat(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 3)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getY() == 110), is(true));

        verifyThat(app.getOptionalDotGrid().getDiagram().getAllSteps().get(3).getDisplayedKnots(), hasSize(3));
        verifyThat(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 4)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getX() == 160), is(true));
        verifyThat(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 4)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getY() == 150), is(true));
        
        verifyThat(app.getOptionalDotGrid().getDiagram().getAllSteps().get(4).getDisplayedKnots(), hasSize(4));
        verifyThat(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 5)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getX() == 220), is(true));
        verifyThat(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 5)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getY() == 150), is(true));
    }

    @Test
    void test_add_3_steps_and_do_many_redo_undo(FxRobot robot) {
        // Given a diagram with an empty step
        selectAndClickOnSnowflakePatternButton(robot);

        // When
        drawSnowFlake(robot, 60, 70);

        drawSnowFlake(robot, 110, 120);

        drawSnowFlake(robot, 160, 160);

        UndoKnotButton.undoKnot();

        RedoKnotButton.redoKnot();
        RedoKnotButton.redoKnot();
        RedoKnotButton.redoKnot();
        RedoKnotButton.redoKnot();
        RedoKnotButton.redoKnot();

        UndoKnotButton.undoKnot();

        drawSnowFlake(robot, 220, 160);

        // Then
        verifyThat(app.getOptionalDotGrid().getDiagram().getAllSteps(), hasSize(4));

        verifyThat(app.getOptionalDotGrid().getDiagram().getAllSteps().get(1).getDisplayedKnots(), hasSize(1));
        verifyThat(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 2)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .findFirst()
                        .get()
                        .getX(), is(60));

        // There is always a Y offset of -10 pixels between where we clicked and where the knot appears
        verifyThat(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 2)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .findFirst()
                        .get()
                        .getY(), is(60));

        verifyThat(app.getOptionalDotGrid().getDiagram().getAllSteps().get(2).getDisplayedKnots(), hasSize(2));
        verifyThat(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 3)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getX() == 110), is(true));
        verifyThat(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 3)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getY() == 110), is(true));

        verifyThat(app.getOptionalDotGrid().getDiagram().getAllSteps().get(3).getDisplayedKnots(), hasSize(3));
        verifyThat(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 4)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getX() == 220), is(true));
        verifyThat(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 4)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getY() == 150), is(true));
    }

}

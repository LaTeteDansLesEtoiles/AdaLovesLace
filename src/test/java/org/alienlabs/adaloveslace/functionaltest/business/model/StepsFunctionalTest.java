package org.alienlabs.adaloveslace.functionaltest.business.model;

import javafx.stage.Stage;
import org.alienlabs.adaloveslace.functionaltest.AppFunctionalTestParent;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.RedoKnotButton;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.UndoKnotButton;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StepsFunctionalTest extends AppFunctionalTestParent {

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
    void test_add_3_steps_undo_a_step_add_a_step(FxRobot robot) {
        // Given a diagram with an empty step
        synchronizeTask(() -> selectAndClickOnSnowflakePatternButton(robot));

        // When
        synchronizeTask(() -> drawSnowFlake(robot, 60, 70));

        synchronizeTask(() -> drawSnowFlake(robot, 300, 70));

        synchronizeTask(() -> drawSnowFlake(robot, 140, 70));

        synchronizeTask(() -> UndoKnotButton.undoKnot(app));

        synchronizeTask(() -> drawSnowFlake(robot, 220, 70));

        // Then
        assertEquals(4,
                app.getOptionalDotGrid().getDiagram().getAllSteps().size(),
                "We should have 4 Steps!");

        assertEquals(3,
                app.getOptionalDotGrid().getDiagram().getAllSteps().get(3).getDisplayedKnots().size(),
                "We should have 3 visible Steps in the last Step!");

        assertEquals(1,
                app.getOptionalDotGrid().getDiagram().getAllSteps().get(1).getDisplayedKnots().size(),
                "We should have only one knot in the 1st Step!");
        assertEquals(60,
                app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 2)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .findFirst()
                        .get()
                        .getX(),
                "We should have only one knot in the 1st non-empty Step, at X=60!");

        // There is always a Y offset of -10 pixels between where we clicked and where the knot appears
        assertEquals(60,
                app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 2)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .findFirst()
                        .get()
                        .getY(),
                "We should have only one knot in the 1st non-empty Step, at Y=60!");

        assertEquals(2,
                app.getOptionalDotGrid().getDiagram().getAllSteps().get(2).getDisplayedKnots().size(),
                "We should have only two knots in the 2nd non-empty Step!");
        assertTrue(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 3)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getX() == 300),
                "We should have a knot in the 2nd non-empty Step, at X=300!");
        assertTrue(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 3)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getY() == 60),
                "We should have a knot in the 2nd non-empty Step, at Y=60!");

        assertEquals(3,
                app.getOptionalDotGrid().getDiagram().getAllSteps().get(3).getDisplayedKnots().size(),
                "We should have 3 knots in the 3rd non-empty Step!");
        assertTrue(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 4)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getX() == 220),
                "We should have a knot in the 3rd non-empty Step, at X=220!");
        assertTrue(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 4)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getY() == 60),
                "We should have a knot in the 3rd non-empty Step, at Y=60!");
    }

    @Test
    void test_add_3_steps_undo_and_redo_a_step_and_add_a_step(FxRobot robot) {
        // Given a diagram with an empty step
        synchronizeTask(() -> selectAndClickOnSnowflakePatternButton(robot));

        // When
        synchronizeTask(() -> drawSnowFlake(robot, 60, 70));

        synchronizeTask(() -> drawSnowFlake(robot, 40, 45));

        synchronizeTask(() -> drawSnowFlake(robot, 110, 120));

        synchronizeTask(() -> UndoKnotButton.undoKnot(app));
        synchronizeTask(() -> RedoKnotButton.redoKnot(app));

        synchronizeTask(() -> drawSnowFlake(robot, 80, 85));

        // Then
        assertEquals(6,
                app.getOptionalDotGrid().getDiagram().getAllSteps().size(),
                "We should have 5 Steps!");

        assertEquals(1,
                app.getOptionalDotGrid().getDiagram().getAllSteps().get(1).getDisplayedKnots().size(),
                "We should have only one knot in the 1st Step!");
        assertEquals(60,
                app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 2)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .findFirst()
                        .get()
                        .getX(),
                "We should have only one knot in the 1st non-empty Step, at X=60!");

        // There is always a Y offset of -10 pixels between where we clicked and where the knot appears
        assertEquals(60,
                app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 2)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .findFirst()
                        .get()
                        .getY(),
                "We should have only one knot in the 1st non-empty Step, at Y=60!");

        assertEquals(2,
                app.getOptionalDotGrid().getDiagram().getAllSteps().get(2).getDisplayedKnots().size(),
                "We should have only two knots in the 2nd non-empty Step!");
        assertTrue(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 3)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getX() == 40),
                "We should have a knot in the 2nd non-empty Step, at X=20!");
        assertTrue(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 3)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getY() == 35),
                "We should have a knot in the 2nd non-empty Step, at Y=35!");

        assertEquals(3,
                app.getOptionalDotGrid().getDiagram().getAllSteps().get(3).getDisplayedKnots().size(),
                "We should have 3 knots in the 3rd non-empty Step!");
        assertTrue(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 4)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getX() == 110),
                "We should have a knot in the 3rd non-empty Step, at X=100!");
        assertTrue(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 4)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getY() == 110),
                "We should have a knot in the 3rd non-empty Step, at Y=55!");

        assertEquals(4,
                app.getOptionalDotGrid().getDiagram().getAllSteps().get(4).getDisplayedKnots().size(),
                "We should have 3 knots in the 3rd non-empty Step!");
        assertTrue(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 5)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getX() == 80),
                "We should have a knot in the 3rd non-empty Step, at X=80!");
        assertTrue(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 5)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getY() == 75),
                "We should have a knot in the 3rd non-empty Step, at Y=75!");
    }

    @Test
    void test_add_3_steps_and_do_many_undo_redo(FxRobot robot) {
        // Given a diagram with an empty step
        synchronizeTask(() -> selectAndClickOnSnowflakePatternButton(robot));

        // When
        synchronizeTask(() -> drawSnowFlake(robot, 60, 70));

        synchronizeTask(() -> drawSnowFlake(robot, 110, 120));

        synchronizeTask(() -> drawSnowFlake(robot, 160, 160));

        synchronizeTask(() -> UndoKnotButton.undoKnot(app));
        synchronizeTask(() -> UndoKnotButton.undoKnot(app));
        synchronizeTask(() -> UndoKnotButton.undoKnot(app));
        synchronizeTask(() -> UndoKnotButton.undoKnot(app));
        synchronizeTask(() -> UndoKnotButton.undoKnot(app));
        synchronizeTask(() -> UndoKnotButton.undoKnot(app));
        synchronizeTask(() -> UndoKnotButton.undoKnot(app));
        synchronizeTask(() -> UndoKnotButton.undoKnot(app));
        synchronizeTask(() -> UndoKnotButton.undoKnot(app));
        synchronizeTask(() -> UndoKnotButton.undoKnot(app));
        synchronizeTask(() -> UndoKnotButton.undoKnot(app));
        synchronizeTask(() -> UndoKnotButton.undoKnot(app));

        synchronizeTask(() -> RedoKnotButton.redoKnot(app));
        synchronizeTask(() -> RedoKnotButton.redoKnot(app));
        synchronizeTask(() -> RedoKnotButton.redoKnot(app));

        synchronizeTask(() -> drawSnowFlake(robot, 220, 160));

        // Then
        assertEquals(5,
                app.getOptionalDotGrid().getDiagram().getAllSteps().size(),
                "We should have 5 Steps!");

        assertEquals(1,
                app.getOptionalDotGrid().getDiagram().getAllSteps().get(1).getDisplayedKnots().size(),
                "We should have only one knot in the 1st Step!");
        assertEquals(60,
                app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 2)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .findFirst()
                        .get()
                        .getX(),
                "We should have only one knot in the 1st non-empty Step, at X=60!");
        
        // There is always a Y offset of -10 pixels between where we clicked and where the knot appears
        assertEquals(60,
                app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 2)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .findFirst()
                        .get()
                        .getY(),
                "We should have only one knot in the 1st non-empty Step, at Y=60!");

        assertEquals(2,
                app.getOptionalDotGrid().getDiagram().getAllSteps().get(2).getDisplayedKnots().size(),
                "We should have only two knots in the 2nd non-empty Step!");
        assertTrue(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 3)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getX() == 110),
                "We should have a knot in the 2nd non-empty Step, at X=110!");
        assertTrue(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 3)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getY() == 110),
                "We should have a knot in the 2nd non-empty Step, at Y=110!");

        assertEquals(3,
                app.getOptionalDotGrid().getDiagram().getAllSteps().get(3).getDisplayedKnots().size(),
                "We should have 3 knots in the 3rd non-empty Step!");
        assertTrue(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 4)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getX() == 160),
                "We should have a knot in the 3rd non-empty Step, at X=160!");
        assertTrue(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 4)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getY() == 150),
                "We should have a knot in the 3rd non-empty Step, at Y=150!");
        
        assertEquals(4,
                app.getOptionalDotGrid().getDiagram().getAllSteps().get(4).getDisplayedKnots().size(),
                "We should have 3 knots in the 3rd non-empty Step!");
        assertTrue(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 5)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getX() == 220),
                "We should have a knot in the 3rd non-empty Step, at X=220!");
        assertTrue(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 5)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getY() == 150),
                "We should have a knot in the 3rd non-empty Step, at Y=150!");
    }

    @Test
    void test_add_3_steps_and_do_many_redo_undo(FxRobot robot) {
        // Given a diagram with an empty step
        synchronizeTask(() -> selectAndClickOnSnowflakePatternButton(robot));

        // When
        synchronizeTask(() -> drawSnowFlake(robot, 60, 70));

        synchronizeTask(() -> drawSnowFlake(robot, 110, 120));

        synchronizeTask(() -> drawSnowFlake(robot, 160, 160));

        synchronizeTask(() -> UndoKnotButton.undoKnot(app));

        synchronizeTask(() -> RedoKnotButton.redoKnot(app));
        synchronizeTask(() -> RedoKnotButton.redoKnot(app));
        synchronizeTask(() -> RedoKnotButton.redoKnot(app));
        synchronizeTask(() -> RedoKnotButton.redoKnot(app));
        synchronizeTask(() -> RedoKnotButton.redoKnot(app));

        synchronizeTask(() -> UndoKnotButton.undoKnot(app));

        synchronizeTask(() -> drawSnowFlake(robot, 220, 160));

        // Then
        assertEquals(4,
                app.getOptionalDotGrid().getDiagram().getAllSteps().size(),
                "We should have 5 Steps!");

        assertEquals(1,
                app.getOptionalDotGrid().getDiagram().getAllSteps().get(1).getDisplayedKnots().size(),
                "We should have only one knot in the 1st Step!");
        assertEquals(60,
                app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 2)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .findFirst()
                        .get()
                        .getX(),
                "We should have only one knot in the 1st non-empty Step, at X=60!");

        // There is always a Y offset of -10 pixels between where we clicked and where the knot appears
        assertEquals(60,
                app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 2)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .findFirst()
                        .get()
                        .getY(),
                "We should have only one knot in the 1st non-empty Step, at Y=60!");

        assertEquals(2,
                app.getOptionalDotGrid().getDiagram().getAllSteps().get(2).getDisplayedKnots().size(),
                "We should have only two knots in the 2nd non-empty Step!");
        assertTrue(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 3)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getX() == 110),
                "We should have a knot in the 2nd non-empty Step, at X=110!");
        assertTrue(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 3)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getY() == 110),
                "We should have a knot in the 2nd non-empty Step, at Y=110!");

        assertEquals(3,
                app.getOptionalDotGrid().getDiagram().getAllSteps().get(3).getDisplayedKnots().size(),
                "We should have 3 knots in the 3rd non-empty Step!");
        assertTrue(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 4)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getX() == 220),
                "We should have a knot in the 3rd non-empty Step, at X=160!");
        assertTrue(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 4)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getY() == 150),
                "We should have a knot in the 3rd non-empty Step, at Y=150!");
    }

}

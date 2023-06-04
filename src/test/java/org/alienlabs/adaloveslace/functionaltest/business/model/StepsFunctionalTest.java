package org.alienlabs.adaloveslace.functionaltest.business.model;

import javafx.stage.Stage;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Diagram;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.business.model.Pattern;
import org.alienlabs.adaloveslace.functionaltest.AppFunctionalTestParent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StepsFunctionalTest extends AppFunctionalTestParent {

    private Diagram diagram;

    /**
     * Init method called before each test
     *
     * @param primaryStage The injected window (stage)
     */
    @Start
    public void start(Stage primaryStage) {
        super.start(primaryStage);
    }

    @BeforeEach
    void beforeEach() {
        this.app = new App();
        this.app.setPrimaryStage(primaryStage);
        this.diagram = new Diagram(this.app);
        app.getOptionalDotGrid().setDiagram(this.diagram);
    }

    @Test
    void test_add_3_steps_undo_a_step_add_a_step() {
        // Given a diagram with an empty step

        // When
        Knot knot1 = new Knot(10, 15, new Pattern(), null);
//        app.getOptionalDotGrid().getDiagram().addKnotWithStep(knot1, false);

        Knot knot2 = new Knot(20, 25, new Pattern(), null);
//        app.getOptionalDotGrid().getDiagram().addKnotWithStep(knot2, false);

        Knot knot3 = new Knot(30, 35, new Pattern(), null);
//        app.getOptionalDotGrid().getDiagram().addKnotWithStep(knot3, false);

        this.diagram.undoLastStep(app);

        Knot knot4 = new Knot(40, 45, new Pattern(), null);
//        app.getOptionalDotGrid().getDiagram().addKnotWithStep(knot4, false);

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
        assertEquals(10,
                app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 1)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .findFirst()
                        .get()
                        .getX(),
                "We should have only one knot in the 1st non-empty Step, at X=10!");
        assertEquals(15,
                app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 1)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .findFirst()
                        .get()
                        .getY(),
                "We should have only one knot in the 1st non-empty Step, at Y=15!");

        assertEquals(2,
                app.getOptionalDotGrid().getDiagram().getAllSteps().get(2).getDisplayedKnots().size(),
                "We should have only two knots in the 2nd non-empty Step!");
        assertTrue(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 2)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getX() == 20),
                "We should have a knot in the 2nd non-empty Step, at X=20!");
        assertTrue(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 2)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getY() == 25),
                "We should have a knot in the 2nd non-empty Step, at Y=25!");

        assertEquals(3,
                app.getOptionalDotGrid().getDiagram().getAllSteps().get(3).getDisplayedKnots().size(),
                "We should have 3 knots in the 3rd non-empty Step!");
        assertTrue(app.getOptionalDotGrid().getDiagram().getAllSteps().get(3)
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getX() == 40),
                "We should have a knot in the 3rd non-empty Step, at X=40!");
        assertTrue(app.getOptionalDotGrid().getDiagram().getAllSteps().get(3)
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getY() == 45),
                "We should have a knot in the 3rd non-empty Step, at Y=45!");
    }

    @Test
    void test_add_3_steps_undo_and_redo_a_step_and_add_a_step() {
        // Given a diagram with an empty step

        // When
        Knot knot1 = new Knot(10, 15, new Pattern(), null);
//        app.getOptionalDotGrid().getDiagram().addKnotWithStep(knot1, false);

        Knot knot2 = new Knot(20, 25, new Pattern(), null);
//        app.getOptionalDotGrid().getDiagram().addKnotWithStep(knot2, false);

        Knot knot3 = new Knot(30, 35, new Pattern(), null);
//        app.getOptionalDotGrid().getDiagram().addKnotWithStep(knot3, false);

        app.getOptionalDotGrid().getDiagram().undoLastStep(app);
        app.getOptionalDotGrid().getDiagram().redoLastStep(app);

        Knot knot4 = new Knot(40, 45, new Pattern(), null);
//        app.getOptionalDotGrid().getDiagram().addKnotWithStep(knot4, false);

        // Then
        assertEquals(5,
                app.getOptionalDotGrid().getDiagram().getAllSteps().size(),
                "We should have 4 Steps!");

        assertEquals(1,
                app.getOptionalDotGrid().getDiagram().getAllSteps().get(1).getDisplayedKnots().size(),
                "We should have only one knot in the 1st Step!");
        assertEquals(10,
                app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 1)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .findFirst()
                        .get()
                        .getX(),
                "We should have only one knot in the 1st non-empty Step, at X=10!");
        assertEquals(15,
                app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 1)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .findFirst()
                        .get()
                        .getY(),
                "We should have only one knot in the 1st non-empty Step, at Y=15!");

        assertEquals(2,
                app.getOptionalDotGrid().getDiagram().getAllSteps().get(2).getDisplayedKnots().size(),
                "We should have only two knots in the 2nd non-empty Step!");
        assertTrue(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 2)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getX() == 20),
                "We should have a knot in the 2nd non-empty Step, at X=20!");
        assertTrue(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 2)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getY() == 25),
                "We should have a knot in the 2nd non-empty Step, at Y=25!");

        assertEquals(3,
                app.getOptionalDotGrid().getDiagram().getAllSteps().get(3).getDisplayedKnots().size(),
                "We should have 3 knots in the 3rd non-empty Step!");
        assertTrue(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 3)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getX() == 30),
                "We should have a knot in the 3rd non-empty Step, at X=30!");
        assertTrue(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 3)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getY() == 35),
                "We should have a knot in the 3rd non-empty Step, at Y=35!");
    }

    @Test
    void test_add_3_steps_and_do_many_undo_redo() {
        // Given a diagram with an empty step

        // When
        Knot knot1 = new Knot(10, 15, new Pattern(), null);
//        app.getOptionalDotGrid().getDiagram().addKnotWithStep(knot1, false);

        Knot knot2 = new Knot(20, 25, new Pattern(), null);
//        app.getOptionalDotGrid().getDiagram().addKnotWithStep(knot2, false);

        Knot knot3 = new Knot(30, 35, new Pattern(), null);
//        app.getOptionalDotGrid().getDiagram().addKnotWithStep(knot3, false);

        app.getOptionalDotGrid().getDiagram().undoLastStep(app);
        app.getOptionalDotGrid().getDiagram().undoLastStep(app);
        app.getOptionalDotGrid().getDiagram().undoLastStep(app);
        app.getOptionalDotGrid().getDiagram().undoLastStep(app);
        app.getOptionalDotGrid().getDiagram().undoLastStep(app);
        app.getOptionalDotGrid().getDiagram().undoLastStep(app);
        app.getOptionalDotGrid().getDiagram().undoLastStep(app);
        app.getOptionalDotGrid().getDiagram().undoLastStep(app);
        app.getOptionalDotGrid().getDiagram().undoLastStep(app);
        app.getOptionalDotGrid().getDiagram().undoLastStep(app);
        app.getOptionalDotGrid().getDiagram().undoLastStep(app);

        app.getOptionalDotGrid().getDiagram().redoLastStep(app);
        app.getOptionalDotGrid().getDiagram().redoLastStep(app);
        app.getOptionalDotGrid().getDiagram().redoLastStep(app);
        app.getOptionalDotGrid().getDiagram().redoLastStep(app);
        app.getOptionalDotGrid().getDiagram().redoLastStep(app);
        app.getOptionalDotGrid().getDiagram().redoLastStep(app);
        app.getOptionalDotGrid().getDiagram().redoLastStep(app);
        app.getOptionalDotGrid().getDiagram().redoLastStep(app);
        app.getOptionalDotGrid().getDiagram().redoLastStep(app);
        app.getOptionalDotGrid().getDiagram().redoLastStep(app);
        app.getOptionalDotGrid().getDiagram().redoLastStep(app);

        Knot knot4 = new Knot(40, 45, new Pattern(), null);
//        app.getOptionalDotGrid().getDiagram().addKnotWithStep(knot4, false);

        // Then
        assertEquals(5,
                app.getOptionalDotGrid().getDiagram().getAllSteps().size(),
                "We should have 5 Steps!");

        assertEquals(1,
                app.getOptionalDotGrid().getDiagram().getAllSteps().get(1).getDisplayedKnots().size(),
                "We should have only one knot in the 1st Step!");
        assertEquals(10,
                app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 1)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .findFirst()
                        .get()
                        .getX(),
                "We should have only one knot in the 1st non-empty Step, at X=10!");
        assertEquals(15,
                app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 1)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .findFirst()
                        .get()
                        .getY(),
                "We should have only one knot in the 1st non-empty Step, at Y=15!");

        assertEquals(2,
                app.getOptionalDotGrid().getDiagram().getAllSteps().get(2).getDisplayedKnots().size(),
                "We should have only two knots in the 2nd non-empty Step!");
        assertTrue(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 2)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getX() == 20),
                "We should have a knot in the 2nd non-empty Step, at X=20!");
        assertTrue(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 2)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getY() == 25),
                "We should have a knot in the 2nd non-empty Step, at Y=25!");

        assertEquals(3,
                app.getOptionalDotGrid().getDiagram().getAllSteps().get(3).getDisplayedKnots().size(),
                "We should have 3 knots in the 3rd non-empty Step!");
        assertTrue(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 3)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getX() == 30),
                "We should have a knot in the 3rd non-empty Step, at X=30!");
        assertTrue(app.getOptionalDotGrid().getDiagram().getAllSteps().stream().filter(step -> step.getStepIndex() == 3)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getY() == 35),
                "We should have a knot in the 3rd non-empty Step, at Y=35!");
    }

}

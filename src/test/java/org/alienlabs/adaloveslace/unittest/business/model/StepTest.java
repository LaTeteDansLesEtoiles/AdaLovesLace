package org.alienlabs.adaloveslace.unittest.business.model;

import org.alienlabs.adaloveslace.business.model.Diagram;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.business.model.Pattern;
import org.alienlabs.adaloveslace.business.model.Step;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StepTest {

    private Diagram diagram;

    @BeforeEach
    void beforeEach() {
        this.diagram = new Diagram();
    }

    @Test
    void test_add_a_step() {
        // Given
        Set<Knot> knots = new HashSet<>();
        knots.add(new Knot(10, 15, new Pattern(), null));

        // When
        Step step = new Step(this.diagram, knots, new HashSet<>());
        this.diagram.getAllSteps().add(step);

        // Then
        assertEquals(1,
                this.diagram.getAllSteps().size(),
                "We should have only one Step!");
        assertEquals(1,
                this.diagram.getAllSteps().get(0).getDisplayedKnots().size(),
                "We should have only one knot in this Step!");
        assertEquals(10,
                this.diagram.getAllSteps().get(0).getDisplayedKnots().stream().findFirst().get().getX(),
                "We should have only one knot in this Step, at X=10!");
        assertEquals(15,
                this.diagram.getAllSteps().get(0).getDisplayedKnots().stream().findFirst().get().getY(),
                "We should have only one knot in this Step, at Y=15!");
    }

    @Test
    void test_add_3_steps() {
        // Given
        Set<Knot> knotsStep1 = new HashSet<>();
        Set<Knot> knotsStep2 = new HashSet<>();
        Set<Knot> knotsStep3 = new HashSet<>();

        // When
        knotsStep1.add(new Knot(10, 15, new Pattern(), null));
        Step step1 = new Step(this.diagram, knotsStep1, new HashSet<>());
        this.diagram.getAllSteps().add(step1);

        knotsStep2.add(new Knot(20, 25, new Pattern(), null));
        Step step2 = new Step(this.diagram, knotsStep2, new HashSet<>());
        this.diagram.getAllSteps().add(step2);

        knotsStep3.add(new Knot(30, 35, new Pattern(), null));
        Step step3 = new Step(this.diagram, knotsStep3, new HashSet<>());
        this.diagram.getAllSteps().add(step3);

        // Then
        assertEquals(3,
                this.diagram.getAllSteps().size(),
                "We should have 3 Steps!");

        assertEquals(1,
                this.diagram.getAllSteps().get(0).getDisplayedKnots().size(),
                "We should have only one knot in the 1st Step!");
        assertEquals(10,
                this.diagram.getAllSteps().get(0).getDisplayedKnots().stream().findFirst().get().getX(),
                "We should have only one knot in the 1st Step, at X=10!");
        assertEquals(15,
                this.diagram.getAllSteps().get(0).getDisplayedKnots().stream().findFirst().get().getY(),
                "We should have only one knot in the 1st Step, at Y=15!");

        assertEquals(1,
                this.diagram.getAllSteps().get(1).getDisplayedKnots().size(),
                "We should have only one knot in the 2nd Step!");
        assertEquals(20,
                this.diagram.getAllSteps().get(1).getDisplayedKnots().stream().findFirst().get().getX(),
                "We should have only one knot in the 2nd Step, at X=20!");
        assertEquals(25,
                this.diagram.getAllSteps().get(1).getDisplayedKnots().stream().findFirst().get().getY(),
                "We should have only one knot in the 2nd Step, at Y=25!");

        assertEquals(1,
                this.diagram.getAllSteps().get(2).getDisplayedKnots().size(),
                "We should have only one knot in the 3rd Step!");
        assertEquals(30,
                this.diagram.getAllSteps().get(2).getDisplayedKnots().stream().findFirst().get().getX(),
                "We should have only one knot in the 3rd Step, at X=30!");
        assertEquals(35,
                this.diagram.getAllSteps().get(2).getDisplayedKnots().stream().findFirst().get().getY(),
                "We should have only one knot in the 3rd Step, at Y=35!");
    }

    @Test
    void test_add_a_knot_to_a_step() {
        // Given
        Set<Knot> knotsStep1 = new HashSet<>();
        knotsStep1.add(new Knot(10, 15, new Pattern(), null));
        Step step1 = new Step(this.diagram, knotsStep1, new HashSet<>());

        this.diagram.getAllSteps().add(step1);
        this.diagram.setCurrentStepIndex(1);

        // When
        this.diagram.addKnotWithStep(new Knot(20, 25, new Pattern(), null));

        // Then
        assertEquals(2,
                this.diagram.getAllSteps().size(),
                "We should have 2 Steps!");

        assertEquals(2,
                this.diagram.getAllSteps().get(1).getDisplayedKnots().size(),
                "We should have 2 knots in the 2nd Step!");
        assertTrue(this.diagram.getAllSteps().get(1).
                        getDisplayedKnots().
                        stream().
                        anyMatch(knot -> knot.getX() == 20),
                "We should have only one knot at X=20 in the 2nd Step!");
        assertTrue(this.diagram.getAllSteps().get(1).
                        getDisplayedKnots().
                        stream().
                        anyMatch(knot -> knot.getY() == 25),
                "We should have only one knot at Y=25 in the 2nd Step!");
    }

    @Test
    void test_add_several_knots_to_a_step() {
        // Given
        Knot knotStep1 = new Knot(10, 15, new Pattern(), null);
        diagram.addKnotWithStep(knotStep1);

        Set<Knot> knotsStep2 = new HashSet<>();
        Knot firstKnotsStep2 = new Knot(20, 25, new Pattern(), null);
        knotsStep2.add(firstKnotsStep2);

        Knot secondKnotsStep2 = new Knot(30, 35, new Pattern(), null);
        knotsStep2.add(secondKnotsStep2);

        // When
        knotsStep2.add(knotStep1);
        this.diagram.addKnotsWithStep(knotsStep2, new HashSet<>());

        // Then
        assertEquals(2,
                this.diagram.getAllSteps().size(),
                "We should have 2 Steps!");

        assertEquals(3,
                this.diagram.getAllSteps().get(1).getDisplayedKnots().size(),
                "We should have 3 knots in the 2nd Step!");

        assertTrue(this.diagram.getAllSteps().get(1).
                        getDisplayedKnots().
                        stream().
                        anyMatch(knot -> knot.getX() == 10),
                "We should have only one knot at X=10 in the 2nd Step!");
        assertTrue(this.diagram.getAllSteps().get(1).
                        getDisplayedKnots().
                        stream().
                        anyMatch(knot -> knot.getY() == 15),
                "We should have only one knot at Y=15 in the 2nd Step!");

        assertTrue(this.diagram.getAllSteps().get(1).
                        getDisplayedKnots().
                        stream().
                        anyMatch(knot -> knot.getX() == 20),
                "We should have only one knot at X=20 in the 2nd Step!");
        assertTrue(this.diagram.getAllSteps().get(1).
                        getDisplayedKnots().
                        stream().
                        anyMatch(knot -> knot.getY() == 25),
                "We should have only one knot at Y=25 in the 2nd Step!");
        assertTrue(this.diagram.getAllSteps().get(1).
                        getDisplayedKnots().
                        stream().
                        anyMatch(knot -> knot.getX() == 30),
                "We should have only one knot at X=30 in the 2nd Step!");
        assertTrue(this.diagram.getAllSteps().get(1).
                        getDisplayedKnots().
                        stream().
                        anyMatch(knot -> knot.getY() == 35),
                "We should have only one knot at Y=35 in the 2nd Step!");
    }

    @Test
    void test_add_several_knots_then_undo_a_step_then_make_another_step() {
        // Given
        Knot knotStep1 = new Knot(10, 15, new Pattern(), null);
        diagram.addKnotWithStep(knotStep1);

        Knot knotStep2 = new Knot(20, 25, new Pattern(), null);
        this.diagram.addKnotWithStep(knotStep2);

        Knot knotStep3 = new Knot(30, 35, new Pattern(), null);
        this.diagram.addKnotWithStep(knotStep3);

        // When
        this.diagram.setCurrentStepIndex(2);
        Knot knotStep32 = new Knot(40, 45, new Pattern(), null);
        this.diagram.addKnotWithStep(knotStep32);

        // Then
        assertEquals(3,
                this.diagram.getAllSteps().size(),
                "We should have 3 Steps!");

        assertEquals(3,
                this.diagram.getAllSteps().get(2).getDisplayedKnots().size(),
                "We should have 3 knots in the 3rd Step!");

        assertTrue(this.diagram.getAllSteps().get(2).
                        getDisplayedKnots().
                        stream().
                        anyMatch(knot -> knot.getX() == 10),
                "We should have only one knot at X=10 in the 3rd Step!");
        assertTrue(this.diagram.getAllSteps().get(2).
                        getDisplayedKnots().
                        stream().
                        anyMatch(knot -> knot.getY() == 15),
                "We should have only one knot at Y=15 in the 3rd Step!");

        assertTrue(this.diagram.getAllSteps().get(2).
                        getDisplayedKnots().
                        stream().
                        anyMatch(knot -> knot.getX() == 20),
                "We should have only one knot at X=20 in the 3rd Step!");
        assertTrue(this.diagram.getAllSteps().get(2).
                        getDisplayedKnots().
                        stream().
                        anyMatch(knot -> knot.getY() == 25),
                "We should have only one knot at Y=25 in the 3rd Step!");
        assertTrue(this.diagram.getAllSteps().get(2).
                        getDisplayedKnots().
                        stream().
                        anyMatch(knot -> knot.getX() == 40),
                "We should have only one knot at X=40 in the 3rd Step!");
        assertTrue(this.diagram.getAllSteps().get(2).
                        getDisplayedKnots().
                        stream().
                        anyMatch(knot -> knot.getY() == 45),
                "We should have only one knot at Y=45 in the 3rd Step!");
    }

    @Test
    void test_add_several_knots_then_undo_a_step_then_redo_this_step() {
        // Given
        Knot knotStep1 = new Knot(10, 15, new Pattern(), null);
        diagram.addKnotWithStep(knotStep1);

        Knot knotStep2 = new Knot(20, 25, new Pattern(), null);
        this.diagram.addKnotWithStep(knotStep2);

        Knot knotStep3 = new Knot(30, 35, new Pattern(), null);
        this.diagram.addKnotWithStep(knotStep3);

        // When
        this.diagram.setCurrentStepIndex(2);
        this.diagram.setCurrentStepIndex(3);

        // Then
        assertEquals(3,
                this.diagram.getAllSteps().size(),
                "We should have 3 Steps!");

        assertEquals(3,
                this.diagram.getAllSteps().get(2).getStepIndex(),
                "We should have 3 as stepIndex of the last Step!");

        assertEquals(3,
                this.diagram.getAllSteps().get(2).getDisplayedKnots().size(),
                "We should have 3 knots in the 3rd Step!");

        assertTrue(this.diagram.getAllSteps().get(2).
                        getDisplayedKnots().
                        stream().
                        anyMatch(knot -> knot.getX() == 10),
                "We should have only one knot at X=10 in the 3rd Step!");
        assertTrue(this.diagram.getAllSteps().get(2).
                        getDisplayedKnots().
                        stream().
                        anyMatch(knot -> knot.getY() == 15),
                "We should have only one knot at Y=15 in the 3rd Step!");

        assertTrue(this.diagram.getAllSteps().get(2).
                        getDisplayedKnots().
                        stream().
                        anyMatch(knot -> knot.getX() == 20),
                "We should have only one knot at X=20 in the 3rd Step!");
        assertTrue(this.diagram.getAllSteps().get(2).
                        getDisplayedKnots().
                        stream().
                        anyMatch(knot -> knot.getY() == 25),
                "We should have only one knot at Y=25 in the 3rd Step!");
        assertTrue(this.diagram.getAllSteps().get(2).
                        getDisplayedKnots().
                        stream().
                        anyMatch(knot -> knot.getX() == 30),
                "We should have only one knot at X=30 in the 3rd Step!");
        assertTrue(this.diagram.getAllSteps().get(2).
                        getDisplayedKnots().
                        stream().
                        anyMatch(knot -> knot.getY() == 35),
                "We should have only one knot at Y=35 in the 3rd Step!");
    }

}

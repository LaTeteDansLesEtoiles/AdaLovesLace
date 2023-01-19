package org.alienlabs.adaloveslace.unittest.business.model;

import org.alienlabs.adaloveslace.business.model.Diagram;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.business.model.Pattern;
import org.alienlabs.adaloveslace.business.model.Step;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StepTest {

    @Test
    void test_add_a_step() {
        // Given
        Diagram diagram = new Diagram();
        Set<Knot> knots = new HashSet<>();

        // When
        knots.add(new Knot(10, 15, new Pattern(), null));
        Step step = new Step(diagram, knots, new HashSet<>());
        diagram.getAllSteps().add(step);

        // Then
        assertEquals(1,
                diagram.getAllSteps().size(),
                "We should have only one Step!");
        assertEquals(1,
                diagram.getAllSteps().get(0).getDisplayedKnots().size(),
                "We should have only one knot in this Step!");
        assertEquals(10,
                diagram.getAllSteps().get(0).getDisplayedKnots().stream().findFirst().get().getX(),
                "We should have only one knot in this Step, at X=10!");
        assertEquals(15,
                diagram.getAllSteps().get(0).getDisplayedKnots().stream().findFirst().get().getY(),
                "We should have only one knot in this Step, at Y=15!");
    }

    @Test
    void test_add_3_steps() {
        // Given
        Diagram diagram = new Diagram();
        Set<Knot> knotsStep1 = new HashSet<>();
        Set<Knot> knotsStep2 = new HashSet<>();
        Set<Knot> knotsStep3 = new HashSet<>();

        // When
        knotsStep1.add(new Knot(10, 15, new Pattern(), null));
        Step step1 = new Step(diagram, knotsStep1, new HashSet<>());
        diagram.getAllSteps().add(step1);

        knotsStep2.add(new Knot(20, 25, new Pattern(), null));
        Step step2 = new Step(diagram, knotsStep2, new HashSet<>());
        diagram.getAllSteps().add(step2);

        knotsStep3.add(new Knot(30, 35, new Pattern(), null));
        Step step3 = new Step(diagram, knotsStep3, new HashSet<>());
        diagram.getAllSteps().add(step3);

        // Then
        assertEquals(3,
                diagram.getAllSteps().size(),
                "We should have 3 Steps!");

        assertEquals(1,
                diagram.getAllSteps().get(0).getDisplayedKnots().size(),
                "We should have only one knot in the 1st Step!");
        assertEquals(10,
                diagram.getAllSteps().get(0).getDisplayedKnots().stream().findFirst().get().getX(),
                "We should have only one knot in the 1st Step, at X=10!");
        assertEquals(15,
                diagram.getAllSteps().get(0).getDisplayedKnots().stream().findFirst().get().getY(),
                "We should have only one knot in the 1st Step, at Y=15!");

        assertEquals(1,
                diagram.getAllSteps().get(1).getDisplayedKnots().size(),
                "We should have only one knot in the 2nd Step!");
        assertEquals(20,
                diagram.getAllSteps().get(1).getDisplayedKnots().stream().findFirst().get().getX(),
                "We should have only one knot in the 2nd Step, at X=20!");
        assertEquals(25,
                diagram.getAllSteps().get(1).getDisplayedKnots().stream().findFirst().get().getY(),
                "We should have only one knot in the 2nd Step, at Y=25!");

        assertEquals(1,
                diagram.getAllSteps().get(2).getDisplayedKnots().size(),
                "We should have only one knot in the 3rd Step!");
        assertEquals(30,
                diagram.getAllSteps().get(2).getDisplayedKnots().stream().findFirst().get().getX(),
                "We should have only one knot in the 3rd Step, at X=30!");
        assertEquals(35,
                diagram.getAllSteps().get(2).getDisplayedKnots().stream().findFirst().get().getY(),
                "We should have only one knot in the 3rd Step, at Y=35!");
    }
}

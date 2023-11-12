package org.alienlabs.adaloveslace.unittest.business.model;

import javafx.scene.Group;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Diagram;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.business.model.Pattern;
import org.alienlabs.adaloveslace.view.component.OptionalDotGrid;
import org.alienlabs.adaloveslace.view.window.MainWindow;
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
        App app = new App();
        app.setMainWindow(new MainWindow());
        this.diagram = new Diagram(app);
        app.setOptionalDotGrid(new OptionalDotGrid(app, this.diagram, new Group()));
        app.setDiagram(this.diagram);
    }

    @Test
    void test_add_a_step() {
        // Given
        Knot knot1 = new Knot(10, 15, new Pattern(), null);
        Set<Knot> knots1 = new HashSet<>();
        knots1.add(knot1);
        Set<Knot> selectedKnots1 = new HashSet<>();

        // When
        Diagram.newStep(knots1, selectedKnots1, false);

        // Then
        assertEquals(2,
                this.diagram.getAllSteps().size(),
                "We should have 2 Steps!");
        assertEquals(1,
                this.diagram.getAllSteps().get(1).getDisplayedKnots().size(),
                "We should have only one knot in this Step!");
        assertEquals(10,
                this.diagram.getAllSteps().get(1).getDisplayedKnots().stream().findFirst().get().getX(),
                "We should have only one knot in this Step, at X=10!");
        assertEquals(15,
                this.diagram.getAllSteps().get(1).getDisplayedKnots().stream().findFirst().get().getY(),
                "We should have only one knot in this Step, at Y=15!");
    }

    @Test
    void test_add_3_steps_displayed() {
        // Given a diagram with an empty step

        // When
        Knot knot1 = new Knot(10, 15, new Pattern(), null);
        Set<Knot> knots1 = new HashSet<>();
        knots1.add(knot1);
        Set<Knot> selectedKnots1 = new HashSet<>();

        Diagram.newStep(knots1, selectedKnots1, false);

        Knot knot2 = new Knot(20, 25, new Pattern(), null);
        Knot knot3 = new Knot(30, 35, new Pattern(), null);
        Set<Knot> knots2 = new HashSet<>();
        knots2.add(knot2);
        knots2.add(knot3);
        Set<Knot> selectedKnots2 = new HashSet<>();

        Diagram.newStep(knots2, selectedKnots2, false);

        Knot knot4 = new Knot(40, 45, new Pattern(), null);
        Set<Knot> knots4 = new HashSet<>();
        knots4.add(knot4);
        Set<Knot> selectedKnots4 = new HashSet<>();

        Diagram.newStep(knots4, selectedKnots4, false);

        // Then
        assertEquals(4,
                this.diagram.getAllSteps().size(),
                "We should have 4 Steps!");

        assertEquals(1,
                this.diagram.getAllSteps().get(1).getDisplayedKnots().size(),
                "We should have only one knot in the 1st Step!");
        assertEquals(10,
                this.diagram.getAllSteps().stream().filter(step -> step.getStepIndex() == 1)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .findFirst()
                        .get()
                        .getX(),
                "We should have only one knot in the 2nd non-empty Step, at X=20!");
        assertEquals(15,
                this.diagram.getAllSteps().stream().filter(step -> step.getStepIndex() == 1)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .findFirst()
                        .get()
                        .getY(),
                "We should have only one knot in the 2nd non-empty Step, at Y=25!");

        assertEquals(2,
                this.diagram.getAllSteps().stream().filter(step -> step.getStepIndex() == 2)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .size(),
                "We should have two knots in the 2nd non-empty Step!");
        assertTrue(this.diagram.getAllSteps().stream().filter(step -> step.getStepIndex() == 3)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getX() == 40),
                "We should have a knot in the 3rd non-empty Step, at X=30!");
        assertTrue(this.diagram.getAllSteps().stream().filter(step -> step.getStepIndex() == 3)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(k -> k.getY() == 45),
                "We should have a knot in the 3rd non-empty Step, at Y=35!");
    }

    @Test
    void test_add_3_steps_selected() {
        // Given a diagram with an empty step

        // When
        Knot knot1 = new Knot(10, 15, new Pattern(), null);
        Set<Knot> knots1 = new HashSet<>();
        knots1.add(knot1);
        Set<Knot> selectedKnots1 = new HashSet<>();

        Diagram.newStep(knots1, selectedKnots1, false);

        Knot knot2 = new Knot(20, 25, new Pattern(), null);
        Knot knot3 = new Knot(30, 35, new Pattern(), null);
        Set<Knot> selectedKnots2 = new HashSet<>();
        selectedKnots2.add(knot2);
        selectedKnots2.add(knot3);
        Set<Knot> knots2 = new HashSet<>();

        Diagram.newStep(knots2, selectedKnots2, false);

        Knot knot4 = new Knot(40, 45, new Pattern(), null);
        Set<Knot> selectedKnots4 = new HashSet<>();
        selectedKnots4.add(knot4);
        Set<Knot> knots4 = new HashSet<>();

        Diagram.newStep(knots4, selectedKnots4, false);

        // Then
        assertEquals(4,
                this.diagram.getAllSteps().size(),
                "We should have 4 Steps!");

        assertEquals(1,
                this.diagram.getAllSteps().get(1).getDisplayedKnots().size(),
                "We should have only one knot in the 1st Step!");
        assertEquals(10,
                this.diagram.getAllSteps().stream().filter(step -> step.getStepIndex() == 1)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .findFirst()
                        .get()
                        .getX(),
                "We should have only one knot in the 2nd non-empty Step, at X=20!");
        assertEquals(15,
                this.diagram.getAllSteps().stream().filter(step -> step.getStepIndex() == 1)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .findFirst()
                        .get()
                        .getY(),
                "We should have only one knot in the 2nd non-empty Step, at Y=25!");

        assertEquals(2,
                this.diagram.getAllSteps().stream().filter(step -> step.getStepIndex() == 2)
                        .findFirst()
                        .get()
                        .getSelectedKnots()
                        .size(),
                "We should have two knots in the 2nd non-empty Step!");
        assertTrue(this.diagram.getAllSteps().stream().filter(step -> step.getStepIndex() == 3)
                        .findFirst()
                        .get()
                        .getSelectedKnots()
                        .stream()
                        .anyMatch(k -> k.getX() == 40),
                "We should have a knot in the 3rd non-empty Step, at X=30!");
        assertTrue(this.diagram.getAllSteps().stream().filter(step -> step.getStepIndex() == 3)
                        .findFirst()
                        .get()
                        .getSelectedKnots()
                        .stream()
                        .anyMatch(k -> k.getY() == 45),
                "We should have a knot in the 3rd non-empty Step, at Y=35!");
    }

    @Test
    void test_add_2_steps_selected_and_1_displayed() {
        // Given a diagram with an empty step

        // When
        Knot knot1 = new Knot(10, 15, new Pattern(), null);
        Set<Knot> selectedKnots1 = new HashSet<>();
        selectedKnots1.add(knot1);
        Set<Knot> knots1 = new HashSet<>();

        Diagram.newStep(knots1, selectedKnots1, false);

        Knot knot2 = new Knot(20, 25, new Pattern(), null);
        Knot knot3 = new Knot(30, 35, new Pattern(), null);
        Set<Knot> selectedKnots2 = new HashSet<>();
        selectedKnots2.add(knot2);
        selectedKnots2.add(knot3);
        Set<Knot> knots2 = new HashSet<>();

        Diagram.newStep(knots2, selectedKnots2, false);

        Set<Knot> knots3 = new HashSet<>();
        knots3.add(knot3);
        Set<Knot> selectedKnots3 = new HashSet<>();

        Diagram.newStep(knots3, selectedKnots3, false);

        // Then
        assertEquals(4,
                this.diagram.getAllSteps().size(),
                "We should have 4 Steps!");

        assertEquals(1,
                this.diagram.getAllSteps().get(1).getSelectedKnots().size(),
                "We should have only one knot in the 1st Step!");
        assertEquals(10,
                this.diagram.getAllSteps().get(1).getSelectedKnots().stream().findFirst().get().getX(),
                "We should have only one knot in the 1st Step, at X=10!");
        assertEquals(15,
                this.diagram.getAllSteps().get(1).getSelectedKnots().stream().findFirst().get().getY(),
                "We should have only one knot in the 1st Step, at Y=15!");

        assertEquals(2,
                this.diagram.getAllSteps().get(2).getSelectedKnots().size(),
                "We should have 2 knots in the 2nd Step!");
        assertTrue(this.diagram.getAllSteps().get(2).getSelectedKnots().stream().anyMatch(k -> k.getX() == 20),
                "We should have a knot in the 2nd Step, at X=20!");
        assertTrue(this.diagram.getAllSteps().get(2).getSelectedKnots().stream().anyMatch(k -> k.getY() == 25),
                "We should have a knot in the 2nd Step, at Y=25!");
        assertTrue(this.diagram.getAllSteps().get(2).getSelectedKnots().stream().anyMatch(k -> k.getX() == 30),
                "We should have a knot in the 2nd Step, at X=30!");
        assertTrue(this.diagram.getAllSteps().get(2).getSelectedKnots().stream().anyMatch(k -> k.getY() == 35),
                "We should have a knot in the 2nd Step, at Y=35!");

        assertEquals(0,
                this.diagram.getAllSteps().get(3).getSelectedKnots().size(),
                "We should have 2 selected knots in the 3rd Step!");
        assertEquals(1,
                this.diagram.getAllSteps().get(3).getDisplayedKnots().size(),
                "We should have only one displayed knots in the 3rd Step!");
        assertTrue(this.diagram.getAllSteps().get(3).getDisplayedKnots().stream().anyMatch(k -> k.getX() == 30),
                "We should have only one knot in the 3rd Step, at X=30!");
        assertTrue(this.diagram.getAllSteps().get(3).getDisplayedKnots().stream().anyMatch(k -> k.getY() == 35),
                "We should have only one knot in the 3rd Step, at Y=35!");
    }

    @Test
    void test_add_a_knot_to_a_step() {
        // Given
        Knot knot1 = new Knot(10, 15, new Pattern(), null);
        Set<Knot> knots1 = new HashSet<>();
        knots1.add(knot1);
        Set<Knot> selectedKnots1 = new HashSet<>();

        Diagram.newStep(knots1, selectedKnots1, false);

        // When
        Knot knot2 = new Knot(20, 25, new Pattern(), null);
        Set<Knot> knots2 = new HashSet<>();
        knots2.add(knot2);
        Set<Knot> selectedKnots2 = new HashSet<>();

        Diagram.newStep(knots2, selectedKnots2, false);

        // Then
        assertEquals(3,
                this.diagram.getAllSteps().size(),
                "We should have 3 Steps!");

        assertEquals(1,
                this.diagram.getAllSteps().get(2).getDisplayedKnots().size(),
                "We should have 2 knots in the 2nd Step!");
        assertTrue(this.diagram.getAllSteps().get(2).
                        getDisplayedKnots().
                        stream().
                        anyMatch(knot -> knot.getX() == 20),
                "We should have only one knot at X=20 in the 2nd Step!");
        assertTrue(this.diagram.getAllSteps().get(2).
                        getDisplayedKnots().
                        stream().
                        anyMatch(knot -> knot.getY() == 25),
                "We should have only one knot at Y=25 in the 2nd Step!");
    }

    @Test
    void test_add_several_knots() {
        // Given
        Knot knot1 = new Knot(10, 15, new Pattern(), null);
        Set<Knot> knots1 = new HashSet<>();
        knots1.add(knot1);
        Set<Knot> selectedKnots1 = new HashSet<>();

        Diagram.newStep(knots1, selectedKnots1, false);

        Knot firstKnotsStep2 = new Knot(20, 25, new Pattern(), null);
        Set<Knot> knots2 = new HashSet<>();
        knots2.add(firstKnotsStep2);
        Set<Knot> selectedKnots2 = new HashSet<>();

        Diagram.newStep(knots2, selectedKnots2, false);

        Knot firstKnotsStep3 = new Knot(30, 35, new Pattern(), null);
        Knot secondKnotsStep3 = new Knot(40, 45, new Pattern(), null);
        Set<Knot> knots3 = new HashSet<>();
        knots3.add(firstKnotsStep3);
        knots3.add(secondKnotsStep3);
        Set<Knot> selectedKnots3 = new HashSet<>();

        // When
        Diagram.newStep(knots3, selectedKnots3, false);

        // Then
        assertEquals(4,
                this.diagram.getAllSteps().size(),
                "We should have 5 Steps (non-empty step included)!");

        assertEquals(2,
                this.diagram.getAllSteps().stream().filter(step -> step.getStepIndex() == 3)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .size(),
                "We should have 2 displayed knots in the 3rd non-empty Step!");

        assertEquals(0,
                this.diagram.getAllSteps().stream().filter(step -> step.getStepIndex() == 3)
                        .findFirst()
                        .get()
                        .getSelectedKnots()
                        .size(),
                "We should have 4 selected knot in the 3rd non-empty Step!");

        assertTrue(this.diagram.getAllSteps().stream().filter(step -> step.getStepIndex() == 3)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream().
                        anyMatch(knot -> knot.getX() == 30),
                    "We should have a knot at X=30 in the 4th non-empty Step!");
        assertTrue(this.diagram.getAllSteps().stream().filter(step -> step.getStepIndex() == 3)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(knot -> knot.getY() == 35),
                "We should have a knot at Y=35 in the 4th non-empty Step!");
        assertTrue(this.diagram.getAllSteps().stream().filter(step -> step.getStepIndex() == 3)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(knot -> knot.getX() == 40),
                "We should have a knot at X=40 in the 4th non-empty Step!");
        assertTrue(this.diagram.getAllSteps().stream().filter(step -> step.getStepIndex() == 3)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(knot -> knot.getY() == 45),
                "We should have a knot at Y=45 in the 4th non-empty Step!");
    }

}

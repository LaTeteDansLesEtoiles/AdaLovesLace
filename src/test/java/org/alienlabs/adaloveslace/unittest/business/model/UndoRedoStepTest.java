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

class UndoRedoStepTest {

    private Diagram diagram;
    private App app;

    @BeforeEach
    void beforeEach() {
        app = new App();
        app.setMainWindow(new MainWindow());
        this.diagram = new Diagram(app);
        app.setOptionalDotGrid(new OptionalDotGrid(app, this.diagram, new Group()));
        app.setDiagram(this.diagram);
    }

    @Test
    void test_add_several_knots_then_undo_a_step_then_make_another_step() {
        // Given
        Knot knotStep1 = new Knot(10, 15, new Pattern(), null);
        Set<Knot> knots1 = new HashSet<>();
        knots1.add(knotStep1);
        Set<Knot> selectedKnots1 = new HashSet<>();

        Diagram.newStep(knots1, selectedKnots1, false);

        Knot knotStep2 = new Knot(20, 25, new Pattern(), null);
        Set<Knot> knots2 = new HashSet<>();
        knots1.add(knotStep2);
        Set<Knot> selectedKnots2 = new HashSet<>();

        Diagram.newStep(knots2, selectedKnots2, false);

        Knot knotStep3 = new Knot(30, 35, new Pattern(), null);
        Set<Knot> knots3 = new HashSet<>();
        knots1.add(knotStep3);
        Set<Knot> selectedKnots3 = new HashSet<>();

        Diagram.newStep(knots3, selectedKnots3, false);

        Knot knotStep4 = new Knot(50, 55, new Pattern(), null);
        Set<Knot> knots4 = new HashSet<>();
        knots1.add(knotStep4);
        Set<Knot> selectedKnots4 = new HashSet<>();

        Diagram.newStep(knots4, selectedKnots4, false);

        // When
        this.diagram.undoLastStep(app, false);

        Knot knotStep31 = new Knot(10, 15, new Pattern(), null);
        Set<Knot> knots31 = new HashSet<>();
        knots31.add(knotStep31);
        Set<Knot> selectedKnots31 = new HashSet<>();

        Diagram.newStep(knots31, selectedKnots31, false);

        Knot knotStep32 = new Knot(20, 25, new Pattern(), null);
        Set<Knot> knots32 = new HashSet<>();
        knots32.add(knotStep32);
        Set<Knot> selectedKnots32 = new HashSet<>();

        Diagram.newStep(knots32, selectedKnots32, false);

        Knot knotStep33 = new Knot(30, 35, new Pattern(), null);
        Set<Knot> knots33 = new HashSet<>();
        knots33.add(knotStep33);

        Set<Knot> selectedKnots33 = new HashSet<>();

        Diagram.newStep(knots33, selectedKnots33, false);

        Knot knotStep34 = new Knot(40, 45, new Pattern(), null);
        Set<Knot> displayedKnots = new HashSet<>();
        displayedKnots.add(knotStep31);
        displayedKnots.add(knotStep32);
        displayedKnots.add(knotStep33);
        displayedKnots.add(knotStep34);
        Set<Knot> selectedKnots = new HashSet<>();

        Diagram.newStep(displayedKnots, selectedKnots, false);

        // Then
        assertEquals(7,
                this.diagram.getCurrentStepIndex(),
                "We should be at 7th Step, empty Step included!");

        assertEquals(8,
                this.diagram.getAllSteps().size(),
                "We should have 8 Steps in total, empty Step included!");

        assertEquals(4,
                this.diagram.getAllSteps().stream().filter(step -> step.getStepIndex() == 7)
                        .findFirst()
                        .get()
                        .getDisplayedKnots().size(),
                "We should have 4 knots in the 7th Step!");

        assertTrue(this.diagram.getAllSteps().stream().filter(step -> step.getStepIndex() == 7)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(knot -> knot.getX() == 10),
                "We should have a knot at X=10 in the 4th Step!");
        assertTrue(this.diagram.getAllSteps().stream().filter(step -> step.getStepIndex() == 7)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(knot -> knot.getY() == 15),
                "We should have a knot at Y=15 in the 4th Step!");

        assertTrue(this.diagram.getAllSteps().stream().filter(step -> step.getStepIndex() == 7)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(knot -> knot.getX() == 20),
                "We should have a knot at X=20 in the 4th Step!");
        assertTrue(this.diagram.getAllSteps().stream().filter(step -> step.getStepIndex() == 7)
                        .findFirst()
                        .get()
                        .getDisplayedKnots().
                        stream().
                        anyMatch(knot -> knot.getY() == 25),
                "We should have a knot at Y=25 in the 4th Step!");
        assertTrue(this.diagram.getAllSteps().stream().filter(step -> step.getStepIndex() == 7)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(knot -> knot.getX() == 30),
                "We should have a knot at X=10 in the 4th Step!");
        assertTrue(this.diagram.getAllSteps().stream().filter(step -> step.getStepIndex() == 7)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(knot -> knot.getY() == 35),
                "We should have a knot at Y=15 in the 4th Step!");

        assertTrue(this.diagram.getAllSteps().stream().filter(step -> step.getStepIndex() == 7)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(knot -> knot.getX() == 40),
                "We should have a knot at X=20 in the 4th Step!");
        assertTrue(this.diagram.getAllSteps().stream().filter(step -> step.getStepIndex() == 7)
                        .findFirst()
                        .get()
                        .getDisplayedKnots().
                        stream().
                        anyMatch(knot -> knot.getY() == 45),
                "We should have a knot at Y=25 in the 4th Step!");
    }

    @Test
    void test_add_several_knots_then_undo_a_step_then_redo_this_step() {
        // Given
        Knot knotStep1 = new Knot(10, 15, new Pattern(), null);
        Set<Knot> knots1 = new HashSet<>();
        knots1.add(knotStep1);
        Set<Knot> selectedKnots1 = new HashSet<>();

        Diagram.newStep(knots1, selectedKnots1, false);

        Knot knotStep2 = new Knot(20, 25, new Pattern(), null);
        Set<Knot> knots2 = new HashSet<>();
        knots2.add(knotStep2);
        Set<Knot> selectedKnots2 = new HashSet<>();

        Diagram.newStep(knots2, selectedKnots2, false);

        Knot knotStep3 = new Knot(30, 35, new Pattern(), null);
        Set<Knot> knots3 = new HashSet<>();
        knots3.add(knotStep3);
        Set<Knot> selectedKnots3 = new HashSet<>();

        Diagram.newStep(knots3, selectedKnots3, false);

        // When
        this.diagram.undoLastStep(app, false);
        this.diagram.redoLastStep(app, false);

        // Then
        assertEquals(4,
                this.diagram.getAllSteps().size(),
                "We should have 4 Steps!");

        assertEquals(2,
                this.diagram.getAllSteps().get(2).getStepIndex(),
                "We should have 2 as stepIndex of the 3rd step!");

        assertEquals(3,
                this.diagram.getAllSteps().get(3).getStepIndex(),
                "We should have 3 as stepIndex of the 4th step!");

        assertEquals(1,
                this.diagram.getAllSteps().get(3).getDisplayedKnots().size(),
                "We should have 3 knots in the 4th Step!");

        assertTrue(this.diagram.getAllSteps().get(3).
                        getDisplayedKnots().
                        stream().
                        anyMatch(knot -> knot.getX() == 30),
                "We should have only one knot at X=30 in the 4th Step!");
        assertTrue(this.diagram.getAllSteps().get(3).
                        getDisplayedKnots().
                        stream().
                        anyMatch(knot -> knot.getY() == 35),
                "We should have only one knot at Y=35 in the 4th Step!");
    }

    @Test
    void test_add_several_selected_knots_then_undo_a_step_then_redo_this_step() {
        // Given
        Knot knotStep1 = new Knot(10, 15, new Pattern(), null);
        Set<Knot> knots1 = new HashSet<>();
        knots1.add(knotStep1);
        Set<Knot> selectedKnots1 = new HashSet<>();

        Diagram.newStep(knots1, selectedKnots1, false);

        Knot knotStep2 = new Knot(20, 25, new Pattern(), null);
        Set<Knot> knots2 = new HashSet<>();
        knots2.add(knotStep2);
        Set<Knot> selectedKnots2 = new HashSet<>();

        Diagram.newStep(knots2, selectedKnots2, false);

        Knot knotStep3 = new Knot(30, 35, new Pattern(), null);
        Set<Knot> selectedKnots3 = new HashSet<>();
        selectedKnots3.add(knotStep3);
        Set<Knot> knots3 = new HashSet<>();

        Diagram.newStep(knots3, selectedKnots3, false);

        // When
        this.diagram.undoLastStep(app, false);
        this.diagram.redoLastStep(app, false);

        // Then
        assertEquals(4,
                this.diagram.getAllSteps().size(),
                "We should have 4 Steps!");

        assertEquals(2,
                this.diagram.getAllSteps().get(2).getStepIndex(),
                "We should have 2 as stepIndex of the 3rd step!");

        assertEquals(3,
                this.diagram.getAllSteps().get(3).getStepIndex(),
                "We should have 3 as stepIndex of the 4th step!");

        assertEquals(1,
                this.diagram.getAllSteps().get(3).getSelectedKnots().size(),
                "We should have 3 knots in the 4th Step!");

        assertTrue(this.diagram.getAllSteps().get(3).
                        getSelectedKnots().
                        stream().
                        anyMatch(knot -> knot.getX() == 30),
                "We should have only one knot at X=30 in the 4th Step!");
        assertTrue(this.diagram.getAllSteps().get(3).
                        getSelectedKnots().
                        stream().
                        anyMatch(knot -> knot.getY() == 35),
                "We should have only one knot at Y=35 in the 4th Step!");
    }

}

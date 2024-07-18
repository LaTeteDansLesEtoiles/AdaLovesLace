package org.alienlabs.adaloveslace.unittest.business.model;

import javafx.scene.Group;
import javafx.scene.image.ImageView;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Diagram;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.business.model.Pattern;
import org.alienlabs.adaloveslace.view.component.OptionalDotGrid;
import org.alienlabs.adaloveslace.view.window.MainWindow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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
        List<Knot> knots1 = new ArrayList<>();
        knots1.add(knotStep1);
        List<Knot> selectedKnots1 = new ArrayList<>();

        Diagram.newStep(knots1, selectedKnots1, false);

        Knot knotStep2 = new Knot(20, 25, new Pattern(), null);
        List<Knot> knots2 = new ArrayList<>();
        knots1.add(knotStep2);
        List<Knot> selectedKnots2 = new ArrayList<>();

        Diagram.newStep(knots2, selectedKnots2, false);

        Knot knotStep3 = new Knot(30, 35, new Pattern(), null);
        List<Knot> knots3 = new ArrayList<>();
        knots1.add(knotStep3);
        List<Knot> selectedKnots3 = new ArrayList<>();

        Diagram.newStep(knots3, selectedKnots3, false);

        Knot knotStep4 = new Knot(50, 55, new Pattern(), null);
        List<Knot> knots4 = new ArrayList<>();
        knots1.add(knotStep4);
        List<Knot> selectedKnots4 = new ArrayList<>();

        Diagram.newStep(knots4, selectedKnots4, false);

        // When
        this.diagram.undoLastStep(app, false);

        Knot knotStep31 = new Knot(10, 15, new Pattern(), null);
        List<Knot> knots31 = new ArrayList<>();
        knots31.add(knotStep31);
        List<Knot> selectedKnots31 = new ArrayList<>();

        Diagram.newStep(knots31, selectedKnots31, false);

        Knot knotStep32 = new Knot(20, 25, new Pattern(), null);
        List<Knot> knots32 = new ArrayList<>();
        knots32.add(knotStep32);
        List<Knot> selectedKnots32 = new ArrayList<>();

        Diagram.newStep(knots32, selectedKnots32, false);

        Knot knotStep33 = new Knot(30, 35, new Pattern(), null);
        List<Knot> knots33 = new ArrayList<>();
        knots33.add(knotStep33);

        List<Knot> selectedKnots33 = new ArrayList<>();

        Diagram.newStep(knots33, selectedKnots33, false);

        Knot knotStep34 = new Knot(40, 45, new Pattern(), null);
        List<Knot> displayedKnots = new ArrayList<>();
        displayedKnots.add(knotStep31);
        displayedKnots.add(knotStep32);
        displayedKnots.add(knotStep33);
        displayedKnots.add(knotStep34);
        List<Knot> selectedKnots = new ArrayList<>();

        Diagram.newStep(displayedKnots, selectedKnots, false);

        // Then
        assertEquals(8,
                this.diagram.getCurrentStepIndex(),
                "We should be at 8th Step, empty Step included!");

        assertEquals(8,
                this.diagram.getAllSteps().size(),
                "We should have 8 Steps in total, empty Step included!");

        assertEquals(4,
                this.diagram.getAllSteps().stream().filter(step -> step.getStepIndex() == 8)
                        .findFirst()
                        .get()
                        .getDisplayedKnots().size(),
                "We should have 4 knots in the 7th Step!");

        assertTrue(this.diagram.getAllSteps().stream().filter(step -> step.getStepIndex() == 8)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(knot -> knot.getX() == 10),
                "We should have a knot at X=10 in the 8th Step!");
        assertTrue(this.diagram.getAllSteps().stream().filter(step -> step.getStepIndex() == 8)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(knot -> knot.getY() == 15),
                "We should have a knot at Y=15 in the 8th Step!");

        assertTrue(this.diagram.getAllSteps().stream().filter(step -> step.getStepIndex() == 8)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(knot -> knot.getX() == 20),
                "We should have a knot at X=20 in the 8th Step!");
        assertTrue(this.diagram.getAllSteps().stream().filter(step -> step.getStepIndex() == 8)
                        .findFirst()
                        .get()
                        .getDisplayedKnots().
                        stream().
                        anyMatch(knot -> knot.getY() == 25),
                "We should have a knot at Y=25 in the 8th Step!");
        assertTrue(this.diagram.getAllSteps().stream().filter(step -> step.getStepIndex() == 8)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(knot -> knot.getX() == 30),
                "We should have a knot at X=10 in the 8th Step!");
        assertTrue(this.diagram.getAllSteps().stream().filter(step -> step.getStepIndex() == 8)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(knot -> knot.getY() == 35),
                "We should have a knot at Y=15 in the 4th Step!");

        assertTrue(this.diagram.getAllSteps().stream().filter(step -> step.getStepIndex() == 8)
                        .findFirst()
                        .get()
                        .getDisplayedKnots()
                        .stream()
                        .anyMatch(knot -> knot.getX() == 40),
                "We should have a knot at X=20 in the 8th Step!");
        assertTrue(this.diagram.getAllSteps().stream().filter(step -> step.getStepIndex() == 8)
                        .findFirst()
                        .get()
                        .getDisplayedKnots().
                        stream().
                        anyMatch(knot -> knot.getY() == 45),
                "We should have a knot at Y=25 in the 8th Step!");
    }

    @Test
    void test_add_several_knots_then_undo_a_step_then_redo_this_step() {
        // Given
        Knot knotStep1 = new Knot(10, 15, new Pattern(), new ImageView());
        List<Knot> knots1 = new ArrayList<>();
        knots1.add(knotStep1);
        List<Knot> selectedKnots1 = new ArrayList<>();

        Diagram.newStep(knots1, selectedKnots1, false);

        Knot knotStep2 = new Knot(20, 25, new Pattern(), new ImageView());
        List<Knot> knots2 = new ArrayList<>();
        knots2.add(knotStep2);
        List<Knot> selectedKnots2 = new ArrayList<>();

        Diagram.newStep(knots2, selectedKnots2, false);

        Knot knotStep3 = new Knot(30, 35, new Pattern(), new ImageView());
        List<Knot> knots3 = new ArrayList<>();
        knots3.add(knotStep3);
        List<Knot> selectedKnots3 = new ArrayList<>();

        Diagram.newStep(knots3, selectedKnots3, false);

        // When
        this.diagram.undoLastStep(app, false);
        this.diagram.redoLastStep(app, false);

        // Then
        assertEquals(4,
                this.diagram.getAllSteps().size(),
                "We should have 4 Steps!");

        assertEquals(3,
                this.diagram.getAllSteps().get(2).getStepIndex(),
                "We should have 3 as stepIndex of the 3rd step!");

        assertEquals(4,
                this.diagram.getAllSteps().get(3).getStepIndex(),
                "We should have 4 as stepIndex of the 4th step!");

        assertEquals(1,
                this.diagram.getAllSteps().get(3).getDisplayedKnots().size(),
                "We should have 1 knots in the 4th Step!");

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
        Knot knotStep1 = new Knot(10, 15, new Pattern(), new ImageView());
        List<Knot> knots1 = new ArrayList<>();
        knots1.add(knotStep1);
        List<Knot> selectedKnots1 = new ArrayList<>();

        Diagram.newStep(knots1, selectedKnots1, false);

        Knot knotStep2 = new Knot(20, 25, new Pattern(), new ImageView());
        List<Knot> knots2 = new ArrayList<>();
        knots2.add(knotStep2);
        List<Knot> selectedKnots2 = new ArrayList<>();

        Diagram.newStep(knots2, selectedKnots2, false);

        Knot knotStep3 = new Knot(30, 35, new Pattern(), new ImageView());
        List<Knot> selectedKnots3 = new ArrayList<>();
        selectedKnots3.add(knotStep3);
        List<Knot> knots3 = new ArrayList<>();

        Diagram.newStep(knots3, selectedKnots3, false);

        // When
        this.diagram.undoLastStep(app, false);
        this.diagram.redoLastStep(app, false);

        // Then
        assertEquals(4,
                this.diagram.getAllSteps().size(),
                "We should have 4 Steps!");

        assertEquals(3,
                this.diagram.getAllSteps().get(2).getStepIndex(),
                "We should have 3 as stepIndex of the 3rd step!");

        assertEquals(4,
                this.diagram.getAllSteps().get(3).getStepIndex(),
                "We should have 4 as stepIndex of the 4th step!");

        assertEquals(1,
                this.diagram.getAllSteps().get(3).getSelectedKnots().size(),
                "We should have 1 selected knot in the 4th Step!");

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

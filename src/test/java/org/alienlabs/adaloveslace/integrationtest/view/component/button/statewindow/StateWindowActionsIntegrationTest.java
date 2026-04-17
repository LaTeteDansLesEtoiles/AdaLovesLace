package org.alienlabs.adaloveslace.integrationtest.view.component.button.statewindow;

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.Diagram;
import org.alienlabs.adaloveslace.domain.Knot;
import org.alienlabs.adaloveslace.domain.enumeration.MouseMode;
import org.alienlabs.adaloveslace.view.component.button.statewindow.InvisibleButton;
import org.alienlabs.adaloveslace.view.component.button.statewindow.SelectableButton;
import org.alienlabs.adaloveslace.view.component.button.statewindow.UnselectableButton;
import org.alienlabs.adaloveslace.view.component.button.statewindow.VisibleButton;
import org.alienlabs.adaloveslace.view.component.grid.OptionalDotGrid;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.ParentGridStrategy;
import org.alienlabs.adaloveslace.view.window.MainWindow;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testfx.util.WaitForAsyncUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("integration")
class StateWindowActionsIntegrationTest {

  @BeforeAll
  static void startJavaFx() {
    try {
      Platform.startup(() -> {
      });
    } catch (IllegalStateException alreadyStarted) {
      // JavaFX toolkit already initialized in this JVM
    }
  }

  private App app;
  private Diagram diagram;

  @BeforeEach
  void setUp() throws Exception {
    CountDownLatch done = new CountDownLatch(1);
    AtomicReference<Throwable> setupError = new AtomicReference<>();
    Platform.runLater(() -> {
      try {
        app = new App();
        app.setMainWindow(new MainWindow());
        Pane movable = new Pane();
        movable.setPrefSize(640d, 480d);
        app.setMovablePane(movable);
        Stage stage = new Stage();
        stage.setWidth(800d);
        stage.setHeight(700d);
        app.setPrimaryStage(stage);
        diagram = new Diagram(app);
        OptionalDotGrid grid = new OptionalDotGrid(app, diagram, movable);
        app.setOptionalDotGrid(grid);
        app.setDiagram(diagram);
        app.setGridStrategy(new ParentGridStrategy(app, grid.getGridPane()));
        ParentGridStrategy.setGridHasBeenDrawn(false);
      } catch (Throwable t) {
        setupError.set(t);
      } finally {
        done.countDown();
      }
    });
    if (!done.await(30, TimeUnit.SECONDS)) {
      throw new AssertionError("Timed out waiting for FX fixture setup");
    }
    if (setupError.get() != null) {
      throw new RuntimeException(setupError.get());
    }
  }

  @Test
  void selectable_action_makes_displayed_unselectable_knots_selectable() {
    int stepsBefore = diagram.getAllSteps().size();
    Platform.runLater(() -> {
      Knot k = new Knot(
          100d,
          200d,
          Optional.empty(),
          Optional.of("x"),
          Optional.of(Color.BLACK),
          new ImageView(new WritableImage(2, 2)));
      k.setSelectable(false);
      List<Knot> displayed = new ArrayList<>(List.of(k));
      List<Knot> selected = new ArrayList<>();
      Diagram.newStep(displayed, selected, false);
      assertFalse(diagram.getCurrentStep().getDisplayedKnots().get(0).isSelectable());

      SelectableButton.onSetSelectableModeAction(app);
    });
    WaitForAsyncUtils.waitForFxEvents();

    assertTrue(diagram.getCurrentStep().getDisplayedKnots().get(0).isSelectable());
    assertTrue(diagram.getAllSteps().size() > stepsBefore);
  }

  @Test
  void unselectable_action_moves_selected_knots_to_displayed_as_unselectable() {
    Platform.runLater(() -> {
      Knot k = new Knot(
          100d,
          200d,
          Optional.empty(),
          Optional.of("x"),
          Optional.of(Color.BLACK),
          new ImageView(new WritableImage(2, 2)));
      List<Knot> displayed = new ArrayList<>();
      List<Knot> selected = new ArrayList<>(List.of(k));
      Diagram.newStep(displayed, selected, false);
      assertEquals(1, diagram.getCurrentStep().getSelectedKnots().size());
      assertTrue(diagram.getCurrentStep().getDisplayedKnots().isEmpty());

      UnselectableButton.onSetUnselectableModeAction(app);
    });
    WaitForAsyncUtils.waitForFxEvents();

    assertTrue(diagram.getCurrentStep().getSelectedKnots().isEmpty());
    assertEquals(1, diagram.getCurrentStep().getDisplayedKnots().size());
    assertFalse(diagram.getCurrentStep().getDisplayedKnots().get(0).isSelectable());
  }

  @Test
  void visible_and_invisible_actions_switch_to_drawing_mode() {
    Platform.runLater(() -> {
      diagram.setCurrentMode(MouseMode.SELECTION);
      VisibleButton.onSetVisibleAction(app);
    });
    WaitForAsyncUtils.waitForFxEvents();
    assertEquals(MouseMode.DRAWING, diagram.getCurrentMode());

    Platform.runLater(() -> {
      diagram.setCurrentMode(MouseMode.MOVE);
      InvisibleButton.onSetInvisibleAction(app);
    });
    WaitForAsyncUtils.waitForFxEvents();
    assertEquals(MouseMode.DRAWING, diagram.getCurrentMode());
  }
}

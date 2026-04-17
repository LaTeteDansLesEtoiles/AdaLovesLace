package org.alienlabs.adaloveslace.integrationtest.view.component.button.geometrywindow.move;

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
import org.alienlabs.adaloveslace.view.component.button.geometrywindow.move.FastMoveModeButton;
import org.alienlabs.adaloveslace.view.component.button.geometrywindow.move.LeftButton;
import org.alienlabs.adaloveslace.view.component.button.geometrywindow.move.RightButton;
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

@Tag("integration")
class MoveKnotActionsIntegrationTest {

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
        FastMoveModeButton.resetStateForTests();
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

        Knot k = new Knot(
            100d,
            200d,
            Optional.empty(),
            Optional.of("x"),
            Optional.of(Color.BLACK),
            new ImageView(new WritableImage(2, 2)));
        List<Knot> displayed = new ArrayList<>(List.of(k));
        List<Knot> selected = new ArrayList<>();
        Diagram.newStep(displayed, selected, false);

        Knot onStep = diagram.getCurrentStep().getDisplayedKnots().get(0);
        diagram.getCurrentStep().setSelectedKnots(new ArrayList<>(List.of(onStep)));

        assertFalse(diagram.getCurrentStep().getSelectedKnots().isEmpty());
        assertEquals(MouseMode.DRAWING, diagram.getCurrentMode());
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
  void first_nudge_right_enters_move_mode_and_offsets_x_by_slow_speed() {
    double xBefore = diagram.getCurrentStep().getSelectedKnots().get(0).getX();
    runOnFx(() -> RightButton.onMoveKnotRightAction(app));
    WaitForAsyncUtils.waitForFxEvents();
    assertEquals(MouseMode.MOVE, diagram.getCurrentMode());
    assertEquals(
        xBefore + FastMoveModeButton.SLOW_MODE_SPEED,
        diagram.getCurrentStep().getSelectedKnots().get(0).getX(),
        1e-6);
  }

  @Test
  void second_nudge_right_while_in_move_mode_offsets_again() {
    runOnFx(() -> RightButton.onMoveKnotRightAction(app));
    WaitForAsyncUtils.waitForFxEvents();
    double xAfterFirst = diagram.getCurrentStep().getSelectedKnots().get(0).getX();
    runOnFx(() -> RightButton.onMoveKnotRightAction(app));
    WaitForAsyncUtils.waitForFxEvents();
    assertEquals(MouseMode.MOVE, diagram.getCurrentMode());
    assertEquals(
        xAfterFirst + FastMoveModeButton.SLOW_MODE_SPEED,
        diagram.getCurrentStep().getSelectedKnots().get(0).getX(),
        1e-6);
  }

  @Test
  void nudge_left_after_right_enters_move_decrements_x() {
    runOnFx(() -> RightButton.onMoveKnotRightAction(app));
    WaitForAsyncUtils.waitForFxEvents();
    double xBeforeLeft = diagram.getCurrentStep().getSelectedKnots().get(0).getX();
    runOnFx(() -> LeftButton.onMoveKnotLeftAction(app));
    WaitForAsyncUtils.waitForFxEvents();
    assertEquals(
        xBeforeLeft - FastMoveModeButton.SLOW_MODE_SPEED,
        diagram.getCurrentStep().getSelectedKnots().get(0).getX(),
        1e-6);
  }

  private static void runOnFx(Runnable r) {
    AtomicReference<Throwable> err = new AtomicReference<>();
    Platform.runLater(() -> {
      try {
        r.run();
      } catch (Throwable t) {
        err.set(t);
      }
    });
    WaitForAsyncUtils.waitForFxEvents();
    if (err.get() != null) {
      throw new AssertionError(err.get());
    }
  }
}

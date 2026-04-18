package org.alienlabs.adaloveslace.integrationtest.view.component.button.geometrywindow.move;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.Diagram;
import org.alienlabs.adaloveslace.domain.Knot;
import org.alienlabs.adaloveslace.domain.enumeration.MouseMode;
import org.alienlabs.adaloveslace.testutil.FxAwait;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    FxAwait.runAndWait(() -> {
      FastMoveModeButton.resetStateForTests();
      app = new App();
      app.setMainWindow(new MainWindow());
      Pane movable = new Pane();
      movable.setPrefSize(640d, 480d);
      app.setMovablePane(movable);
      Stage stage = new Stage();
      stage.setScene(new Scene(movable, 800d, 700d));
      app.setPrimaryStage(stage);
      stage.show();
      diagram = new Diagram(app);
      OptionalDotGrid grid = new OptionalDotGrid(app, diagram, movable);
      app.setOptionalDotGrid(grid);
      app.setDiagram(diagram);
      app.setGridStrategy(new ParentGridStrategy(app, grid.getGridPane()));
      ParentGridStrategy.setGridHasBeenDrawn(false);

      // Each nudge calls playFromStart() on this shared pause; its onFinished duplicates steps and restores mode
      // after 750ms — too slow for deterministic assertions on Jenkins. Integration tests only assert immediate moves.
      OptionalDotGrid.moveKnotPause.stop();
      OptionalDotGrid.moveKnotPause.setOnFinished(__ -> {
      });

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
    });
  }

  @Test
  void first_nudge_right_enters_move_mode_and_offsets_x_by_slow_speed() throws Exception {
    double[] xBefore = new double[1];
    FxAwait.runAndWait(() -> xBefore[0] = diagram.getCurrentStep().getSelectedKnots().get(0).getX());
    FxAwait.runAndWait(() -> RightButton.onMoveKnotRightAction(app));
    FxAwait.runAndWait(() -> {
      assertEquals(MouseMode.MOVE, diagram.getCurrentMode());
      assertEquals(
          xBefore[0] + FastMoveModeButton.SLOW_MODE_SPEED,
          diagram.getCurrentStep().getSelectedKnots().get(0).getX(),
          1e-6);
    });
  }

  @Test
  void second_nudge_right_while_in_move_mode_offsets_again() throws Exception {
    FxAwait.runAndWait(() -> RightButton.onMoveKnotRightAction(app));
    double[] xAfterFirst = new double[1];
    FxAwait.runAndWait(() -> xAfterFirst[0] = diagram.getCurrentStep().getSelectedKnots().get(0).getX());
    FxAwait.runAndWait(() -> RightButton.onMoveKnotRightAction(app));
    FxAwait.runAndWait(() -> {
      assertEquals(MouseMode.MOVE, diagram.getCurrentMode());
      assertEquals(
          xAfterFirst[0] + FastMoveModeButton.SLOW_MODE_SPEED,
          diagram.getCurrentStep().getSelectedKnots().get(0).getX(),
          1e-6);
    });
  }

  @Test
  void nudge_left_after_right_enters_move_decrements_x() throws Exception {
    FxAwait.runAndWait(() -> RightButton.onMoveKnotRightAction(app));
    double[] xBeforeLeft = new double[1];
    FxAwait.runAndWait(() -> xBeforeLeft[0] = diagram.getCurrentStep().getSelectedKnots().get(0).getX());
    FxAwait.runAndWait(() -> LeftButton.onMoveKnotLeftAction(app));
    FxAwait.runAndWait(() -> assertEquals(
        xBeforeLeft[0] - FastMoveModeButton.SLOW_MODE_SPEED,
        diagram.getCurrentStep().getSelectedKnots().get(0).getX(),
        1e-6));
  }
}

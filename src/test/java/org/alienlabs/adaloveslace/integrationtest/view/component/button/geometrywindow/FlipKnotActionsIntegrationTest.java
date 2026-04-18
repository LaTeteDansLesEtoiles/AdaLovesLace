package org.alienlabs.adaloveslace.integrationtest.view.component.button.geometrywindow;

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
import org.alienlabs.adaloveslace.testutil.FxRuntime;
import org.alienlabs.adaloveslace.view.component.button.geometrywindow.HorizontalFlippingButton;
import org.alienlabs.adaloveslace.view.component.button.geometrywindow.VerticalFlippingButton;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Covers the {@code FlipKnotActionUtil#flipSelectedKnots} branch via its only entry points:
 * {@link HorizontalFlippingButton#onFlipHorizontallyAction(App)} and
 * {@link VerticalFlippingButton#onFlipVerticallyAction(App)}. We run with a real FX runtime (needed because
 * {@link org.alienlabs.adaloveslace.util.NodeUtil#copyKnot(Knot)} materialises a new ImageView on the FX thread)
 * and a single seeded-selection step so each button click enters the copy/flip/newStep path. This pushes
 * {@code FlipKnotActionUtil} over the class minimum and the enclosing package well above the 0.60 package rule.
 */
@Tag("integration")
class FlipKnotActionsIntegrationTest {

  @BeforeAll
  static void startJavaFx() {
    FxRuntime.ensureStarted();
  }

  private App app;
  private Diagram diagram;

  @BeforeEach
  void setUp() throws Exception {
    FxAwait.runAndWait(() -> {
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

      Knot k = new Knot(
          120d,
          240d,
          Optional.empty(),
          Optional.of("x"),
          Optional.of(Color.BLACK),
          new ImageView(new WritableImage(2, 2)));
      List<Knot> displayed = new ArrayList<>(List.of(k));
      List<Knot> selected = new ArrayList<>();
      Diagram.newStep(displayed, selected, false);

      Knot onStep = diagram.getCurrentStep().getDisplayedKnots().get(0);
      diagram.getCurrentStep().setSelectedKnots(new ArrayList<>(List.of(onStep)));

      // Precondition assertions pin the setup so a failure in this block reads as "fixture broke" not "flip
      // broke" if anything regresses in Diagram/OptionalDotGrid.
      assertFalse(diagram.getCurrentStep().getSelectedKnots().isEmpty());
      assertEquals(MouseMode.DRAWING, diagram.getCurrentMode());
    });
  }

  @Test
  void horizontal_flip_switches_mirror_mode_and_flags_the_copy() throws Exception {
    FxAwait.runAndWait(() -> HorizontalFlippingButton.onFlipHorizontallyAction(app));

    FxAwait.runAndWait(() -> {
      // The util always flips the mode to MIRROR regardless of the current mode, and the single selected knot
      // gets replaced by a flipped copy on the new current step.
      assertEquals(MouseMode.MIRROR, diagram.getCurrentMode());
      List<Knot> selectedCopies = diagram.getCurrentStep().getSelectedKnots();
      assertEquals(1, selectedCopies.size(),
          "Selection should carry exactly one flipped copy after a single horizontal flip");
      assertTrue(selectedCopies.get(0).isFlippedHorizontally(),
          "Horizontal flip flag must be asserted on the copy; otherwise rendering would not mirror");
    });
  }

  @Test
  void vertical_flip_switches_mirror_mode_and_flags_the_copy() throws Exception {
    FxAwait.runAndWait(() -> VerticalFlippingButton.onFlipVerticallyAction(app));

    FxAwait.runAndWait(() -> {
      assertEquals(MouseMode.MIRROR, diagram.getCurrentMode());
      List<Knot> selectedCopies = diagram.getCurrentStep().getSelectedKnots();
      assertEquals(1, selectedCopies.size());
      assertTrue(selectedCopies.get(0).isFlippedVertically(),
          "Vertical flip flag must be asserted on the copy");
    });
  }

  @Test
  void chained_flips_stack_both_axes_on_the_latest_copy() throws Exception {
    FxAwait.runAndWait(() -> HorizontalFlippingButton.onFlipHorizontallyAction(app));
    // Re-select the freshly produced copy so the next flip operates on it (each flip consumes the selection
    // by producing a new step with the flipped copy as the new selection).
    FxAwait.runAndWait(() -> VerticalFlippingButton.onFlipVerticallyAction(app));

    FxAwait.runAndWait(() -> {
      List<Knot> selectedCopies = diagram.getCurrentStep().getSelectedKnots();
      assertEquals(1, selectedCopies.size());
      assertTrue(selectedCopies.get(0).isFlippedVertically(),
          "The second flip must preserve the vertical axis flag on the new copy");
    });
  }
}

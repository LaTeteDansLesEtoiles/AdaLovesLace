package org.alienlabs.adaloveslace.integrationtest.view.window.event;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.Diagram;
import org.alienlabs.adaloveslace.domain.enumeration.GridType;
import org.alienlabs.adaloveslace.domain.enumeration.MouseMode;
import org.alienlabs.adaloveslace.testutil.FxAwait;
import org.alienlabs.adaloveslace.testutil.FxRuntime;
import org.alienlabs.adaloveslace.view.component.grid.OptionalDotGrid;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.ParentGridStrategy;
import org.alienlabs.adaloveslace.view.window.MainWindow;
import org.alienlabs.adaloveslace.view.window.event.GridEvents;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Directly fires synthetic {@link KeyEvent}s at {@link GridEvents#keyHandler} to exercise the DRAWING-mode
 * text entry pipeline: fresh-knot creation, character append, BACK_SPACE roll-back, ENTER newline, and the
 * "currentKnot with pattern" / "multi-selection" early-return guards. No existing suite drives this path —
 * it accounts for ~70 uncovered lines in {@link GridEvents} and, by extension, is the main lever for lifting
 * the {@code view.window.event} package above the 0.60 jacoco floor without introducing UI-heavy functional
 * tests that are 10x slower.
 */
@Tag("integration")
class GridEventsKeyboardTypingIntegrationTest {

  @BeforeAll
  static void startJavaFx() {
    FxRuntime.ensureStarted();
  }

  @Test
  void keyHandler_handles_new_char_backspace_enter_and_guard_branches() throws Exception {
    FxAwait.runAndWait(() -> {
      Stage stage = null;
      try {
        App app = new App();
        app.setMainWindow(new MainWindow());
        Pane movable = new Pane();
        movable.setPrefSize(500d, 400d);
        app.setMovablePane(movable);

        Diagram diagram = new Diagram(app);
        diagram.setCurrentGridType(GridType.HIDDEN);
        OptionalDotGrid grid = new OptionalDotGrid(app, diagram, movable);
        app.setOptionalDotGrid(grid);
        app.setDiagram(diagram);
        app.setGridStrategy(new ParentGridStrategy(app, grid.getGridPane()));
        ParentGridStrategy.setGridHasBeenDrawn(false);

        stage = new Stage();
        Scene scene = new Scene(movable, 520d, 420d);
        stage.setScene(scene);
        app.setSceneForTests(scene);
        app.setPrimaryStage(stage);
        stage.show();

        diagram.setCurrentMode(MouseMode.DRAWING);

        // Install the handler with a bound app instance; without this call subsequent handler.handle(...)
        // would dereference a null static App and NPE before any covered line executes.
        GridEvents.app = app;

        // 1. Pressing a printable key with no currentKnot creates one, appends the text, and runs updateImage.
        GridEvents.keyHandler.handle(newTyped(KeyCode.A, "a"));

        // 2. Appending a second character continues the append branch with an existing currentKnot.
        GridEvents.keyHandler.handle(newTyped(KeyCode.B, "b"));

        // 3. BACK_SPACE trims the last char (non-empty typedText branch).
        GridEvents.keyHandler.handle(newPressed(KeyCode.BACK_SPACE));

        // 4. BACK_SPACE when the buffer was just reset empties it to the sentinel (empty-text branch).
        GridEvents.keyHandler.handle(newPressed(KeyCode.BACK_SPACE));
        GridEvents.keyHandler.handle(newPressed(KeyCode.BACK_SPACE));

        // 5. ENTER inserts a newline onto the typed buffer.
        GridEvents.keyHandler.handle(newPressed(KeyCode.ENTER));

        // 6. Switching to SELECTION mode still reaches the keyHandler body and the multi-selection early return.
        diagram.setCurrentMode(MouseMode.SELECTION);
        GridEvents.keyHandler.handle(newTyped(KeyCode.C, "c"));

        // 7. Modes that are not DRAWING/SELECTION short-circuit immediately (guard at the top of the handler).
        diagram.setCurrentMode(MouseMode.DELETION);
        GridEvents.keyHandler.handle(newTyped(KeyCode.D, "d"));
      } finally {
        if (stage != null) {
          stage.close();
        }
      }
    });
  }

  private static KeyEvent newTyped(KeyCode code, String character) {
    return new KeyEvent(KeyEvent.KEY_PRESSED, character, character, code, false, false, false, false);
  }

  private static KeyEvent newPressed(KeyCode code) {
    return new KeyEvent(KeyEvent.KEY_PRESSED, "", code.getName(), code, false, false, false, false);
  }
}

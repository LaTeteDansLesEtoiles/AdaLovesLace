package org.alienlabs.adaloveslace.integrationtest.view.window.event;

import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.Diagram;
import org.alienlabs.adaloveslace.view.component.grid.OptionalDotGrid;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.ParentGridStrategy;
import org.alienlabs.adaloveslace.view.window.MainWindow;
import org.alienlabs.adaloveslace.view.window.event.GridEvents;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testfx.util.WaitForAsyncUtils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertNull;

@Tag("integration")
class GridEventsRemoveEventsFromGridIntegrationTest {

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

  @BeforeEach
  void setUp() throws Exception {
    CountDownLatch done = new CountDownLatch(1);
    AtomicReference<Throwable> setupError = new AtomicReference<>();
    Platform.runLater(() -> {
      try {
        app = new App();
        app.setMainWindow(new MainWindow());
        Pane movable = new Pane();
        movable.setPrefSize(400d, 300d);
        app.setMovablePane(movable);
        Stage stage = new Stage();
        app.setPrimaryStage(stage);
        Diagram diagram = new Diagram(app);
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
  void remove_events_from_grid_clears_movable_mouse_handlers() {
    Platform.runLater(() -> {
      app.getMovablePane().setOnMouseMoved(e -> {
      });
      app.getMovablePane().setOnMouseClicked(e -> {
      });
      GridEvents.removeEventsFromGrid(app);
      assertNull(app.getMovablePane().getOnMouseMoved());
      assertNull(app.getMovablePane().getOnMouseClicked());
    });
    WaitForAsyncUtils.waitForFxEvents();
  }
}

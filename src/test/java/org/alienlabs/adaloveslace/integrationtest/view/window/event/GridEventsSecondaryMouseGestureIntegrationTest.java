package org.alienlabs.adaloveslace.integrationtest.view.window.event;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.Diagram;
import org.alienlabs.adaloveslace.domain.enumeration.GridType;
import org.alienlabs.adaloveslace.view.component.grid.OptionalDotGrid;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.ParentGridStrategy;
import org.alienlabs.adaloveslace.view.window.MainWindow;
import org.alienlabs.adaloveslace.view.window.event.GridEvents;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("integration")
class GridEventsSecondaryMouseGestureIntegrationTest {

  @BeforeAll
  static void startJavaFx() {
    try {
      Platform.startup(() -> {
      });
    } catch (IllegalStateException ignored) {
    }
  }

  @Test
  void grid_mouse_handler_factories_return_handlers() throws Exception {
    CountDownLatch done = new CountDownLatch(1);
    AtomicReference<Throwable> err = new AtomicReference<>();

    Platform.runLater(() -> {
      try {
        App app = new App();
        app.setMainWindow(new MainWindow());
        Pane movable = new Pane();
        movable.setPrefSize(500, 400);
        app.setMovablePane(movable);
        Diagram diagram = new Diagram(app);
        diagram.setCurrentGridType(GridType.HIDDEN);
        OptionalDotGrid grid = new OptionalDotGrid(app, diagram, movable);
        app.setOptionalDotGrid(grid);
        app.setDiagram(diagram);
        app.setGridStrategy(new ParentGridStrategy(app, grid.getGridPane()));

        Stage primary = new Stage();
        Scene scene = new Scene(movable, 600, 500);
        primary.setScene(scene);
        app.setSceneForTests(scene);
        app.setPrimaryStage(primary);
        primary.show();

        assertNotNull(GridEvents.getMouseRightClickEventHandler(app));
        assertNotNull(GridEvents.getGridDraggedEventHandler(app));
        assertNotNull(GridEvents.getGridDragReleasedEventHandler(app));
        assertNotNull(GridEvents.getMouseDoubleRightClickOnGridEventHendler(app));

        double sx = 120;
        double sy = 140;
        double screenX = scene.getWindow().getX() + sx;
        double screenY = scene.getWindow().getY() + sy;

        GridEvents.getMouseRightClickEventHandler(app).handle(new MouseEvent(
            MouseEvent.MOUSE_PRESSED,
            sx, sy, screenX, screenY,
            MouseButton.SECONDARY,
            1,
            false, false, false, false,
            false, false, false, false,
            false, false,
            null
        ));

        GridEvents.getGridDraggedEventHandler(app).handle(new MouseEvent(
            MouseEvent.MOUSE_DRAGGED,
            sx + 10, sy + 5, screenX + 10, screenY + 5,
            MouseButton.SECONDARY,
            1,
            false, false, false, false,
            false, false, false, false,
            false, false,
            null
        ));

        GridEvents.getGridDragReleasedEventHandler(app).handle(new MouseEvent(
            MouseEvent.MOUSE_RELEASED,
            sx + 10, sy + 5, screenX + 10, screenY + 5,
            MouseButton.SECONDARY,
            1,
            false, false, false, false,
            false, false, false, false,
            false, false,
            null
        ));

        GridEvents.getMouseDoubleRightClickOnGridEventHendler(app).handle(new MouseEvent(
            MouseEvent.MOUSE_CLICKED,
            300, 250, scene.getWindow().getX() + 300, scene.getWindow().getY() + 250,
            MouseButton.SECONDARY,
            2,
            false, false, false, false,
            false, false, false, false,
            false, false,
            null
        ));

        primary.close();
      } catch (Throwable t) {
        err.set(t);
      } finally {
        done.countDown();
      }
    });

    assertTrue(done.await(30, TimeUnit.SECONDS));
    if (err.get() != null) {
      throw new RuntimeException(err.get());
    }
  }
}

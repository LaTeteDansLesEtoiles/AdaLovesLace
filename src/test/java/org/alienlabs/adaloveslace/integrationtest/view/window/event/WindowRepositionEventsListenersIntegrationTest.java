package org.alienlabs.adaloveslace.integrationtest.view.window.event;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.Diagram;
import org.alienlabs.adaloveslace.util.Preferences;
import org.alienlabs.adaloveslace.view.component.grid.OptionalDotGrid;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.ParentGridStrategy;
import org.alienlabs.adaloveslace.view.window.MainWindow;
import org.alienlabs.adaloveslace.view.window.event.WindowRepositionEvents;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("integration")
class WindowRepositionEventsListenersIntegrationTest {

  private final Map<String, String> previous = new LinkedHashMap<>();

  @BeforeAll
  static void startJavaFx() {
    try {
      Platform.startup(() -> {
      });
    } catch (IllegalStateException ignored) {
    }
  }

  @AfterEach
  void restorePreferences() {
    Preferences p = new Preferences();
    for (Map.Entry<String, String> e : previous.entrySet()) {
      if (e.getValue() == null || e.getValue().isEmpty()) {
        p.setStringValue(e.getKey(), null);
      } else {
        p.setStringValue(e.getKey(), e.getValue());
      }
    }
    previous.clear();
  }

  private void remember(String key, Preferences p) {
    if (!previous.containsKey(key)) {
      previous.put(key, p.getStringValue(key));
    }
  }

  @Test
  void main_and_toolbox_reposition_listeners_persist_coordinates() throws Exception {
    Preferences p = new Preferences();
    remember(WindowRepositionEvents.MAIN_WINDOW_X, p);
    remember(WindowRepositionEvents.MAIN_WINDOW_Y, p);
    remember(WindowRepositionEvents.TOOLBOX_WINDOW_X, p);
    remember(WindowRepositionEvents.TOOLBOX_WINDOW_Y, p);

    CountDownLatch done = new CountDownLatch(1);
    AtomicReference<Throwable> err = new AtomicReference<>();

    Platform.runLater(() -> {
      Stage primary = null;
      Stage toolbox = null;
      try {
        App app = new App();
        app.setMainWindow(new MainWindow());
        Pane movable = new Pane();
        movable.setPrefSize(400, 300);
        app.setMovablePane(movable);
        Diagram diagram = new Diagram(app);
        OptionalDotGrid grid = new OptionalDotGrid(app, diagram, movable);
        app.setOptionalDotGrid(grid);
        app.setDiagram(diagram);
        app.setGridStrategy(new ParentGridStrategy(app, grid.getGridPane()));
        ParentGridStrategy.setGridHasBeenDrawn(false);

        primary = new Stage();
        primary.setScene(new Scene(new Pane(), 640, 480));
        primary.setX(100);
        primary.setY(200);
        app.setPrimaryStage(primary);

        toolbox = new Stage();
        toolbox.setScene(new Scene(new Pane(), 280, 180));
        toolbox.setX(900);
        toolbox.setY(120);
        app.setToolboxStageForTests(toolbox);

        WindowRepositionEvents repos = new WindowRepositionEvents(app);
        repos.onWindowsReposition();

        primary.setX(151);
        primary.setY(88);
        toolbox.setX(1205);
        toolbox.setY(44);

        assertTrue(p.getStringValue(WindowRepositionEvents.MAIN_WINDOW_X).startsWith("151"));
        assertTrue(p.getStringValue(WindowRepositionEvents.MAIN_WINDOW_Y).startsWith("88"));
        assertTrue(p.getStringValue(WindowRepositionEvents.TOOLBOX_WINDOW_X).startsWith("1205"));
        assertTrue(p.getStringValue(WindowRepositionEvents.TOOLBOX_WINDOW_Y).startsWith("44"));
      } catch (Throwable t) {
        err.set(t);
      } finally {
        if (toolbox != null) {
          toolbox.close();
        }
        if (primary != null) {
          primary.close();
        }
        done.countDown();
      }
    });

    assertTrue(done.await(30, TimeUnit.SECONDS));
    if (err.get() != null) {
      throw new RuntimeException(err.get());
    }
  }
}

package org.alienlabs.adaloveslace.integrationtest.view.window.event;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.Diagram;
import org.alienlabs.adaloveslace.testutil.FxAwait;
import org.alienlabs.adaloveslace.testutil.FxRuntime;
import org.alienlabs.adaloveslace.util.Preferences;
import org.alienlabs.adaloveslace.view.component.grid.OptionalDotGrid;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.ParentGridStrategy;
import org.alienlabs.adaloveslace.view.window.MainWindow;
import org.alienlabs.adaloveslace.view.window.event.WindowResizeEvents;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("integration")
class WindowResizeEventsListenersIntegrationTest {

  private final Map<String, String> previous = new LinkedHashMap<>();

  @BeforeAll
  static void startJavaFx() {
    FxRuntime.ensureStarted();
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
  void main_and_toolbox_resize_listeners_persist_dimensions() throws Exception {
    Preferences p = new Preferences();
    remember(WindowResizeEvents.MAIN_WINDOW_WIDTH, p);
    remember(WindowResizeEvents.GRID_WIDTH, p);
    remember(WindowResizeEvents.TOOLBOX_WINDOW_WIDTH, p);

    FxAwait.runAndWait(() -> {
      Stage primary = null;
      Stage toolbox = null;
      try {
        App app = new App();
        app.setMainWindow(new MainWindow());
        Pane movable = new Pane();
        movable.setPrefSize(500, 400);
        app.setMovablePane(movable);
        Diagram diagram = new Diagram(app);
        OptionalDotGrid grid = new OptionalDotGrid(app, diagram, movable);
        app.setOptionalDotGrid(grid);
        app.setDiagram(diagram);
        app.setGridStrategy(new ParentGridStrategy(app, grid.getGridPane()));
        ParentGridStrategy.setGridHasBeenDrawn(false);

        primary = new Stage();
        primary.setScene(new Scene(new Pane(), 640, 480));
        app.setPrimaryStage(primary);

        toolbox = new Stage();
        toolbox.setScene(new Scene(new Pane(), 300, 200));
        app.setToolboxStageForTests(toolbox);

        primary.show();
        toolbox.show();

        WindowResizeEvents resize = new WindowResizeEvents(app);
        resize.onWindowsResize();

        primary.setWidth(641);
        toolbox.setWidth(901);

        assertEquals(641d, app.getGridWidth(), 1e-6);
        assertTrue(p.getStringValue(WindowResizeEvents.MAIN_WINDOW_WIDTH).startsWith("641"));
        assertTrue(p.getStringValue(WindowResizeEvents.TOOLBOX_WINDOW_WIDTH).startsWith("901"));
      } finally {
        if (toolbox != null) {
          toolbox.close();
        }
        if (primary != null) {
          primary.close();
        }
      }
    });
  }
}

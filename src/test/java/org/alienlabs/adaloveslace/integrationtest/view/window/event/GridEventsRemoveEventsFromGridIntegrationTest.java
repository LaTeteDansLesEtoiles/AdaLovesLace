package org.alienlabs.adaloveslace.integrationtest.view.window.event;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.Diagram;
import org.alienlabs.adaloveslace.testutil.FxAwait;
import org.alienlabs.adaloveslace.testutil.FxRuntime;
import org.alienlabs.adaloveslace.view.component.grid.OptionalDotGrid;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.ParentGridStrategy;
import org.alienlabs.adaloveslace.view.window.MainWindow;
import org.alienlabs.adaloveslace.view.window.event.GridEvents;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

@Tag("integration")
class GridEventsRemoveEventsFromGridIntegrationTest {

  @BeforeAll
  static void startJavaFx() {
    FxRuntime.ensureStarted();
  }

  private App app;

  @BeforeEach
  void setUp() throws Exception {
    FxAwait.runAndWait(() -> {
      app = new App();
      app.setMainWindow(new MainWindow());
      Pane movable = new Pane();
      movable.setPrefSize(400d, 300d);
      app.setMovablePane(movable);
      Stage stage = new Stage();
      stage.setScene(new Scene(movable, 640d, 480d));
      app.setPrimaryStage(stage);
      stage.show();
      Diagram diagram = new Diagram(app);
      OptionalDotGrid grid = new OptionalDotGrid(app, diagram, movable);
      app.setOptionalDotGrid(grid);
      app.setDiagram(diagram);
      app.setGridStrategy(new ParentGridStrategy(app, grid.getGridPane()));
      ParentGridStrategy.setGridHasBeenDrawn(false);
    });
  }

  @Test
  void remove_events_from_grid_clears_movable_mouse_handlers() throws Exception {
    FxAwait.runAndWait(() -> {
      app.getMovablePane().setOnMouseMoved(e -> {
      });
      app.getMovablePane().setOnMouseClicked(e -> {
      });
      GridEvents.removeEventsFromGrid(app);
      assertNull(app.getMovablePane().getOnMouseMoved());
      assertNull(app.getMovablePane().getOnMouseClicked());
    });
  }
}

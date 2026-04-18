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
import org.alienlabs.adaloveslace.domain.enumeration.MouseMode;
import org.alienlabs.adaloveslace.domain.enumeration.PatternOrTextMode;
import org.alienlabs.adaloveslace.testutil.FxAwait;
import org.alienlabs.adaloveslace.view.component.grid.OptionalDotGrid;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.ParentGridStrategy;
import org.alienlabs.adaloveslace.view.window.MainWindow;
import org.alienlabs.adaloveslace.view.window.event.GridEvents;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("integration")
class GridEventsGridHoverSmokeIntegrationTest {

  @BeforeAll
  static void startJavaFx() {
    try {
      Platform.startup(() -> {
      });
    } catch (IllegalStateException ignored) {
    }
  }

  private static MouseEvent mouseMoved(double sx, double sy, double screenX, double screenY) {
    return new MouseEvent(
        MouseEvent.MOUSE_MOVED,
        sx, sy, screenX, screenY,
        MouseButton.NONE,
        0,
        false, false, false, false,
        false, false, false, false, false, false,
        null
    );
  }

  @Test
  void grid_hover_covers_selection_and_drawing_text_paths_on_one_fx_pulse() throws Exception {
    FxAwait.runAndWait(() -> {
      Stage primary = null;
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
        ParentGridStrategy.setGridHasBeenDrawn(false);

        primary = new Stage();
        Scene scene = new Scene(movable, 520, 420);
        primary.setScene(scene);
        app.setSceneForTests(scene);
        app.setPrimaryStage(primary);
        primary.show();

        double screenX = 900 + 55;
        double screenY = 700 + 60;

        app.getOptionalDotGrid().getDiagram().setCurrentMode(MouseMode.SELECTION);
        GridEvents.getGridHoverEventHandler(app).handle(mouseMoved(55, 60, screenX, screenY));

        app.getOptionalDotGrid().getDiagram().setCurrentMode(MouseMode.DRAWING);
        app.getOptionalDotGrid().getCurrentPatternOrTextModeProperty().set(PatternOrTextMode.TEXT);
        GridEvents.getGridHoverEventHandler(app).handle(mouseMoved(40, 50, screenX - 10, screenY - 5));
        GridEvents.getGridHoverEventHandler(app).handle(mouseMoved(90, 70, screenX + 20, screenY + 10));
        GridEvents.getGridHoverExitEventHandler(app).handle(mouseMoved(0, 0, screenX, screenY));
      } finally {
        if (primary != null) {
          primary.close();
        }
      }
    });
    FxAwait.syncFx();
  }
}

package org.alienlabs.adaloveslace.integrationtest.view.window.event;

import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
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

@Tag("integration")
class GridEventsMouseClickPassiveModesIntegrationTest {

  @BeforeAll
  static void startJavaFx() {
    FxRuntime.ensureStarted();
  }

  @Test
  void primary_click_runs_for_duplication_create_pattern_mirror_and_move_modes() throws Exception {
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

        double sx = 120;
        double sy = 140;
        double screenX = 500 + sx;
        double screenY = 400 + sy;

        for (MouseMode mode : new MouseMode[] {
            MouseMode.DUPLICATION, MouseMode.CREATE_PATTERN, MouseMode.MIRROR, MouseMode.MOVE}) {
          diagram.setCurrentMode(mode);
          GridEvents.getMouseClickEventHandler(app).handle(new MouseEvent(
              MouseEvent.MOUSE_CLICKED,
              sx, sy, screenX, screenY,
              MouseButton.PRIMARY,
              1,
              false, false, false, false,
              false, false, false, false, false, false,
              null
          ));
        }
      } finally {
        if (primary != null) {
          primary.close();
        }
      }
    });
  }
}

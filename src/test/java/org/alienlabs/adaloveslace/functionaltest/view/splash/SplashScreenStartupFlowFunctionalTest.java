package org.alienlabs.adaloveslace.functionaltest.view.splash;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.Diagram;
import org.alienlabs.adaloveslace.util.SplashStartupTasks;
import org.alienlabs.adaloveslace.view.component.grid.OptionalDotGrid;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.ParentGridStrategy;
import org.alienlabs.adaloveslace.view.splash.SplashScreen;
import org.alienlabs.adaloveslace.view.window.MainWindow;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Drives {@link SplashScreen#show(App)} through startup phases and completion so splash package lines are covered
 * without writing into the real user home directory.
 */
@Tag("functional")
@ExtendWith(ApplicationExtension.class)
class SplashScreenStartupFlowFunctionalTest {

  @Start
  void start(Stage host) {
    host.setWidth(40);
    host.setHeight(40);
    host.show();
  }

  @AfterEach
  void resetPhasesFactory() {
    SplashScreen.setSplashPhasesFactoryForTests(null);
    Platform.runLater(() -> {
      for (Window w : Window.getWindows()) {
        if (w instanceof Stage st && st.getScene() != null) {
          var sc = st.getScene();
          if (Math.abs(sc.getWidth() - 600d) < 1e-3 && Math.abs(sc.getHeight() - 475d) < 1e-3) {
            st.close();
          }
        }
      }
    });
    WaitForAsyncUtils.waitForFxEvents();
  }

  @Test
  void splash_show_runs_phases_then_main_application_hook(FxRobot robot, @TempDir Path temp) throws Exception {
    File root = temp.resolve("splash-ft").toFile();
    CountDownLatch mainShown = new CountDownLatch(1);

    App app = new App() {
      @Override
      public void showMainApplication() {
        mainShown.countDown();
      }
    };

    robot.interact(() -> {
      SplashScreen.setSplashPhasesFactoryForTests(c -> SplashStartupTasks.forTesting(c, root));

      Stage primary = new Stage();
      primary.setScene(new Scene(new Pane(), 200, 150));
      app.setPrimaryStage(primary);

      Stage toolbox = new Stage();
      toolbox.setScene(new Scene(new Pane(), 120, 100));
      app.setToolboxStageForTests(toolbox);

      app.setMainWindow(new MainWindow());
      Pane movable = new Pane();
      movable.setPrefSize(400, 300);
      app.setMovablePane(movable);
      Diagram diagram = new Diagram(app);
      app.setDiagram(diagram);
      OptionalDotGrid grid = new OptionalDotGrid(app, diagram, movable);
      app.setOptionalDotGrid(grid);
      app.setGridStrategy(new ParentGridStrategy(app, grid.getGridPane()));
      ParentGridStrategy.setGridHasBeenDrawn(false);

      new SplashScreen().show(app);
    });

    assertTrue(mainShown.await(60, TimeUnit.SECONDS), "showMainApplication should run after splash completes");
  }
}

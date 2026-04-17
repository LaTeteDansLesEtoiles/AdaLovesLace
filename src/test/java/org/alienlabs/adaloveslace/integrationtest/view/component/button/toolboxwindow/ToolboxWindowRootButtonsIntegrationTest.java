package org.alienlabs.adaloveslace.integrationtest.view.component.button.toolboxwindow;

import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.Diagram;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.QuitButton;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.ShareButton;
import org.alienlabs.adaloveslace.view.component.grid.OptionalDotGrid;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.ParentGridStrategy;
import org.alienlabs.adaloveslace.view.window.MainWindow;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testfx.util.WaitForAsyncUtils;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("integration")
class ToolboxWindowRootButtonsIntegrationTest {

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
        App.resourceBundle = ResourceBundle.getBundle("AdaLovesLace", Locale.of("en", "EN"));
        app = new App();
        app.setMainWindow(new MainWindow());
        Pane movable = new Pane();
        movable.setPrefSize(640d, 480d);
        app.setMovablePane(movable);
        Stage stage = new Stage();
        stage.setWidth(800d);
        stage.setHeight(700d);
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
  void share_button_constructs_with_graphic() {
    Platform.runLater(() -> {
      ShareButton share = new ShareButton(app, App.resourceBundle.getString(ShareButton.SHARE_BUTTON_NAME));
      assertNotNull(share.getGraphic());
    });
    WaitForAsyncUtils.waitForFxEvents();
  }

  @Test
  void quit_button_constructs_with_graphic() {
    Platform.runLater(() -> {
      QuitButton quit = new QuitButton(app, App.resourceBundle.getString(MainWindow.QUIT_APP));
      assertNotNull(quit.getGraphic());
    });
    WaitForAsyncUtils.waitForFxEvents();
  }
}

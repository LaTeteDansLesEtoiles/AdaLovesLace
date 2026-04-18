package org.alienlabs.adaloveslace.integrationtest.view.component.button.toolboxwindow;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.Diagram;
import org.alienlabs.adaloveslace.testutil.FxAwait;
import org.alienlabs.adaloveslace.testutil.FxRuntime;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.QuitButton;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.ShareButton;
import org.alienlabs.adaloveslace.view.component.grid.OptionalDotGrid;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.ParentGridStrategy;
import org.alienlabs.adaloveslace.view.window.MainWindow;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("integration")
class ToolboxWindowRootButtonsIntegrationTest {

  @BeforeAll
  static void startJavaFx() {
    FxRuntime.ensureStarted();
  }

  private App app;

  @BeforeEach
  void setUp() throws Exception {
    FxAwait.runAndWait(() -> {
      App.resourceBundle = ResourceBundle.getBundle("AdaLovesLace", Locale.of("en", "EN"));
      app = new App();
      app.setMainWindow(new MainWindow());
      Pane movable = new Pane();
      movable.setPrefSize(640d, 480d);
      app.setMovablePane(movable);
      Stage stage = new Stage();
      stage.setScene(new Scene(movable, 800d, 700d));
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
  void share_button_constructs_with_graphic() throws Exception {
    FxAwait.runAndWait(() -> {
      ShareButton share = new ShareButton(app, App.resourceBundle.getString(ShareButton.SHARE_BUTTON_NAME));
      assertNotNull(share.getGraphic());
    });
  }

  @Test
  void quit_button_constructs_with_graphic() throws Exception {
    FxAwait.runAndWait(() -> {
      QuitButton quit = new QuitButton(app, App.resourceBundle.getString(MainWindow.QUIT_APP));
      assertNotNull(quit.getGraphic());
    });
  }
}

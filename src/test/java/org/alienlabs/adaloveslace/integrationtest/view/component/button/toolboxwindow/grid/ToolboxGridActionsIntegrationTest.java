package org.alienlabs.adaloveslace.integrationtest.view.component.button.toolboxwindow.grid;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.Diagram;
import org.alienlabs.adaloveslace.domain.Knot;
import org.alienlabs.adaloveslace.domain.enumeration.GridType;
import org.alienlabs.adaloveslace.domain.enumeration.MouseMode;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid.CreatePatternButton;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid.RedoKnotButton;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid.ShowHideGridButton;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid.UndoKnotButton;
import org.alienlabs.adaloveslace.view.component.grid.OptionalDotGrid;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.ParentGridStrategy;
import org.alienlabs.adaloveslace.view.window.MainWindow;
import org.alienlabs.adaloveslace.view.window.ToolboxWindow;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testfx.util.WaitForAsyncUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("integration")
class ToolboxGridActionsIntegrationTest {

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
  private Diagram diagram;

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
        diagram = new Diagram(app);
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

  @AfterEach
  void clearToolboxWindowStatic() throws Exception {
    Field f = App.class.getDeclaredField("toolboxWindow");
    f.setAccessible(true);
    f.set(null, null);
  }

  @Test
  void undo_then_redo_restores_step_index() {
    Platform.runLater(() -> {
      Knot k = new Knot(
          10d,
          20d,
          Optional.empty(),
          Optional.of("t"),
          Optional.of(Color.BLACK),
          new ImageView(new WritableImage(2, 2)));
      Diagram.newStep(new ArrayList<>(List.of(k)), new ArrayList<>(), false);
      int afterFirst = diagram.getCurrentStepIndex();
      assertTrue(afterFirst >= 2, "Expected a real history step after newStep");

      UndoKnotButton undoControl = new UndoKnotButton("", app);
      assertNotNull(undoControl);
      UndoKnotButton.undoKnot();
      assertTrue(diagram.getCurrentStepIndex() < afterFirst);

      RedoKnotButton redoControl = new RedoKnotButton("", app);
      assertNotNull(redoControl);
      RedoKnotButton.redoKnot();
      assertEquals(afterFirst, diagram.getCurrentStepIndex());
    });
    WaitForAsyncUtils.waitForFxEvents();
  }

  @Test
  void create_pattern_action_sets_mode_and_mouse_handlers() {
    Platform.runLater(() -> {
      Knot k = new Knot(
          30d,
          40d,
          Optional.empty(),
          Optional.of("t"),
          Optional.of(Color.BLACK),
          new ImageView(new WritableImage(2, 2)));
      Diagram.newStep(new ArrayList<>(List.of(k)), new ArrayList<>(), false);

      CreatePatternButton.onCreatePatternModeAction(app);

      assertEquals(MouseMode.CREATE_PATTERN, diagram.getCurrentMode());
      assertNotNull(CreatePatternButton.getMouseMovedListener());
      assertNotNull(CreatePatternButton.getMouseClickedListener());
    });
    WaitForAsyncUtils.waitForFxEvents();
  }

  @Test
  void show_hide_grid_advances_type_and_updates_label() throws Exception {
    Platform.runLater(() -> {
      try {
        ToolboxWindow toolboxWindow = Mockito.mock(ToolboxWindow.class);
        Label gridNameLabel = new Label("");
        Mockito.when(toolboxWindow.getGridNameLabel()).thenReturn(gridNameLabel);
        setStaticToolboxWindow(toolboxWindow);

        GridType before = diagram.getCurrentGridType();
        ShowHideGridButton gridToggle = new ShowHideGridButton("", app);
        assertNotNull(gridToggle);
        ShowHideGridButton.showHideGrid();

        assertTrue(diagram.getCurrentGridType() != before);
        String expected = App.resourceBundle.getString(diagram.getCurrentGridType().name());
        assertEquals(expected, gridNameLabel.getText());
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });
    WaitForAsyncUtils.waitForFxEvents();
  }

  private static void setStaticToolboxWindow(ToolboxWindow toolboxWindow) throws Exception {
    Field f = App.class.getDeclaredField("toolboxWindow");
    f.setAccessible(true);
    f.set(null, toolboxWindow);
  }
}

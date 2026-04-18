package org.alienlabs.adaloveslace.functionaltest;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Spinner;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.Diagram;
import org.alienlabs.adaloveslace.util.ImageUtil;
import org.alienlabs.adaloveslace.view.component.button.geometrywindow.DrawingButton;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid.UndoKnotButton;
import org.alienlabs.adaloveslace.view.window.ToolboxWindow;
import org.alienlabs.adaloveslace.view.window.event.WindowRepositionEvents;
import org.alienlabs.adaloveslace.view.window.event.WindowResizeEvents;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.util.WaitForAsyncUtils;

import java.util.Comparator;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.alienlabs.adaloveslace.App.EXPORT_IMAGE_FILE_TYPE;
import static org.alienlabs.adaloveslace.util.FileUtil.FILE_SEPARATOR;

@ExtendWith(ApplicationExtension.class)
@Tag("functional")
public class AppFunctionalTestParent {

  public Stage primaryStage;
  public ToolboxWindow toolboxWindow;
  public App app;

  // For tests:
  public static final long   SLEEP_TIME                   = Long.getLong("SLEEP_TIME", 2_500L);
  public static final long   WAIT_TIME                    = Long.getLong("WAIT_TIME", 10_000L);
  public static final double GRID_WIDTH                   = 600d;
  public static final double GRID_HEIGHT                  = 420d;
  public static final String BUILD_TOOL_OUTPUT_DIRECTORY  = "target/";
  public static final String TEST_SCREEN_CAPTURE_FILE     = "test_screen_capture" + EXPORT_IMAGE_FILE_TYPE;

  public static final String CLASSPATH_RESOURCES_PATH_JPG = ".*org" + FILE_SEPARATOR + "alienlabs" + FILE_SEPARATOR + "adaloveslace" + FILE_SEPARATOR + ".*functionaltest" + FILE_SEPARATOR + ".*util" + FILE_SEPARATOR + ".*.jpg";
  public static final String CLASSPATH_RESOURCES_PATH     = "org" + FILE_SEPARATOR + "alienlabs" + FILE_SEPARATOR + "adaloveslace" + FILE_SEPARATOR + "test" + FILE_SEPARATOR;

  public static final String SNOWFLAKE                    = "snowflake_small";
  public static final String SNOWFLAKE_IMAGE              = "snowflake_small.jpg";

  public static final String COLOR_WHEEL                  = "color wheel";
  public static final String COLOR_WHEEL_IMAGE            = "color wheel.jpg";

  public static final double FIRST_SNOWFLAKE_PIXEL_X      = 150d;

  public static final double FIRST_SNOWFLAKE_PIXEL_Y      = 450d;

  public static final double SECOND_SNOWFLAKE_PIXEL_X     = 310d;

  public static final double SECOND_SNOWFLAKE_PIXEL_Y     = 140d;

  public static final double OTHER_SNOWFLAKE_PIXEL_X      = 15d;

  public static final double OTHER_SNOWFLAKE_PIXEL_Y      = 5d;

  // Pixel sample positions used for "grid line present vs absent" assertions.
  // Aligned with CRISS_CROSS spacing (25px) so we land on a line intersection.
  public static final double GRAY_PIXEL_X                 = 100d;
  public static final double GRAY_PIXEL_Y                 = 75d;
  public static final Color  SNOWFLAKE_DOT_COLOR          = Color.valueOf("0x9df6feff");

  public Color foundColorOnGrid;

  private static final Logger logger = LoggerFactory.getLogger(AppFunctionalTestParent.class);

  /**
   * Jenkins agents are often slow; TestFX defaults to ~60s for setup and can time out between tests
   * after long runs. Override with {@code -Dtestfx.setup.timeout.ms} / {@code -Dtestfx.launch.timeout.ms}.
   */
  @BeforeAll
  static void configureTestFxTimeoutsForCi() {
    long setupMs = Long.getLong("testfx.setup.timeout.ms", 120_000L);
    long launchMs = Long.getLong("testfx.launch.timeout.ms", 120_000L);
    FxToolkit.toolkitContext().setSetupTimeoutInMillis(setupMs);
    FxToolkit.toolkitContext().setLaunchTimeoutInMillis(launchMs);
    logger.info("TestFX timeouts: setup={}ms launch={}ms", setupMs, launchMs);
  }

  /**
   * Init method called before each test
   *
   * @param primaryStage The injected window (stage)
   */
  public void start(Stage primaryStage) {
    // Functional tests may run multiple invocations within the same JVM.
    // Reset spinner internal static state to keep the spinner update logic deterministic.
    org.alienlabs.adaloveslace.view.component.spinner.RotationSpinner.resetNumberOfUpdates();
    org.alienlabs.adaloveslace.view.component.spinner.ZoomSpinner.resetNumberOfUpdates();

    this.app = new App();
    this.app.setPrimaryStage(primaryStage);

    this.primaryStage = primaryStage;
    Locale locale = new Locale("en", "EN");
    App.resourceBundle = ResourceBundle.getBundle("AdaLovesLace", locale);
    Diagram diagram = new Diagram(this.app);
    this.app.setDiagram(diagram);
    this.app.setResizes(new WindowResizeEvents(this.app));
    this.app.setWindowRepositionEvents(new WindowRepositionEvents(this.app));

    // The grid dots are twice as big as in the production code in order to facilitate tests
    this.app.showMainWindow(
            this.app.getResizes().getMainWindowWidth(),
            this.app.getResizes().getMainWindowHeight(),
            this.app.getResizes().getGridWidth(),
            this.app.getResizes().getGridHeight(),
            this.primaryStage,
            diagram);
    this.app.setOptionalDotGrid(this.app.getMainWindow().getOptionalDotGrid());

    this.toolboxWindow = this.app.showToolboxWindow(this.app, this, CLASSPATH_RESOURCES_PATH_JPG);
    // Preferences can persist huge coordinates from dev machines; Xvfb is often ~1280×768.
    final double safeMainX = 20d;
    final double safeMainY = 20d;
    this.primaryStage.setX(safeMainX);
    this.primaryStage.setY(safeMainY);
    this.primaryStage.setWidth(580d);
    this.primaryStage.setHeight(480d);
    this.app.getToolboxStage().setWidth(600d);
    this.app.getToolboxStage().setX(safeMainX + 580d + 15d);
    this.app.getToolboxStage().setY(safeMainY);
    this.app.getToolboxStage().setHeight(560d);

    // GeometryWindow et StateWindow sont maintenant intégrées dans ToolboxWindow
    // this.geometryWindow = this.app.showGeometryWindow(this.app);
    // this.app.getGeometryStage().setX(720d);
    // this.app.getGeometryStage().setY(50d);
    // this.app.getGeometryStage().setHeight(500d);

    // this.stateWindow = this.app.showStateWindow(this.app);
    // this.app.getStateStage().setX(720d);
    // this.app.getStateStage().setY(770d);
    // this.app.getStateStage().setHeight(300d);
  }

  // Snapshot/colors must be read on the FX thread; avoid WaitForAsyncUtils.waitFor(asyncFx) here (it interacts badly
  // with the glass robot and tight FX polling in functional tests).
  protected Color getColor(Point2D pointToMoveTo) {
    // Important: this method can be called from inside a `Platform.runLater` predicate
    // (see waitForCondition(...)). Never block the FX thread waiting on a CompletableFuture,
    // or we'd deadlock (the runLater callback can't execute while the FX thread is blocked).
    if (Platform.isFxApplicationThread()) {
      copyCanvas(pointToMoveTo);
      return this.foundColorOnGrid;
    }

    CompletableFuture<Color> done = new CompletableFuture<>();
    Platform.runLater(() -> {
      try {
        copyCanvas(pointToMoveTo);
        done.complete(this.foundColorOnGrid);
      } catch (Throwable t) {
        done.completeExceptionally(t);
      }
    });
    try {
      return done.get(15, TimeUnit.SECONDS);
    } catch (TimeoutException e) {
      throw new AssertionError("Timed out while reading canvas color", e);
    } catch (ExecutionException e) {
      throw new AssertionError("Failed to read canvas color", e.getCause());
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new AssertionError("Interrupted while reading canvas color");
    }
  }

  // Click on the grid with the snowflake selected in order to draw a snowflake on the grid
  protected void drawASnowflake(FxRobot robot) {
    // Avoid fragile pixel coordinate mapping: draw directly on the FX thread.
    robot.interact(() -> app.getOptionalDotGrid().getDiagram().drawKnot(FIRST_SNOWFLAKE_PIXEL_X, FIRST_SNOWFLAKE_PIXEL_Y));
  }

  protected void selectASnowflake(FxRobot robot) {
      clickSelectButton(robot);
      // Select the first visible knot node (single-knot case for most tests).
      var knots = app.getOptionalDotGrid().getDiagram().getCurrentStep().getAllVisibleKnots();
      if (!knots.isEmpty()) {
        robot.moveTo(knots.get(0).getImageView());
        WaitForAsyncUtils.waitForFxEvents();
        robot.clickOn(knots.get(0).getImageView());
      }
  }

  // Click on the grid with the color wheel selected in order to draw a color wheel on the grid
  protected void drawFirstColorWheel(FxRobot robot) {
    robot.interact(() -> app.getOptionalDotGrid().getDiagram().drawKnot(FIRST_SNOWFLAKE_PIXEL_X, FIRST_SNOWFLAKE_PIXEL_Y));
  }

  // Click on the grid with the second snowflake selected in order to draw a snowflake on the grid elsewhere
  protected void drawSecondSnowflake(FxRobot robot) {
    robot.interact(() -> app.getOptionalDotGrid().getDiagram().drawKnot(SECOND_SNOWFLAKE_PIXEL_X, SECOND_SNOWFLAKE_PIXEL_Y));
  }

  // Click on the grid with the third (not duplicated) snowflake selected in order to draw a snowflake on the grid elsewhere
  protected void drawOtherSnowflake(FxRobot robot) {
    robot.interact(() -> app.getOptionalDotGrid().getDiagram().drawKnot(OTHER_SNOWFLAKE_PIXEL_X, OTHER_SNOWFLAKE_PIXEL_Y));
  }

  // Click on the grid where the snowflake is in order to select it
  protected void clickSelectButton(FxRobot robot) {
    robot.clickOn("#selectionButton");
  }

  protected void incrementSpinner(FxRobot robot, Spinner<Integer> spinner) {
    robot.moveTo("#" + spinner.getId() + " .increment-arrow-button");
    WaitForAsyncUtils.waitForFxEvents();
    robot.clickOn("#" + spinner.getId() + " .increment-arrow-button");
    WaitForAsyncUtils.waitForFxEvents();
  }

  protected void decrementSpinner(FxRobot robot, Spinner<Integer> spinner) {
    robot.moveTo("#" + spinner.getId() + " .decrement-arrow-button");
    WaitForAsyncUtils.waitForFxEvents();
    robot.clickOn("#" + spinner.getId() + " .decrement-arrow-button");
    WaitForAsyncUtils.waitForFxEvents();
  }

  protected void setSpinnerValue(FxRobot robot, Spinner<Integer> spinner, int value) {
      // Use model-level update for deterministic functional tests.
      // Typing into the text field is slower/flakier and can accumulate FX queue work.
      robot.interact(() -> spinner.getValueFactory().setValue(value));
      WaitForAsyncUtils.waitForFxEvents();
  }
  /**
   * Multi-select two pattern knots: first click selects, Ctrl+second click adds (matches MainWindow behaviour).
   * Uses the same offsets as single-select helpers so clicks land on snapped knot images, not only the draw points.
   */
  protected void selectTwoSnowflakes(FxRobot robot) {
    var knots = app.getOptionalDotGrid().getDiagram().getCurrentStep().getAllVisibleKnots();
    if (knots.size() < 2) {
      return;
    }

    // Pick knots by X coordinate proximity to the expected constants.
    var first = knots.stream().min(java.util.Comparator.comparingDouble(k -> Math.abs(k.getX() - FIRST_SNOWFLAKE_PIXEL_X))).orElseThrow();
    var second = knots.stream().min(java.util.Comparator.comparingDouble(k -> Math.abs(k.getX() - SECOND_SNOWFLAKE_PIXEL_X))).orElseThrow();

    robot.moveTo(first.getImageView());
    WaitForAsyncUtils.waitForFxEvents();
    robot.clickOn(first.getImageView());
    WaitForAsyncUtils.waitForFxEvents();
    robot.press(KeyCode.CONTROL);
    if (!second.equals(first)) {
      robot.moveTo(second.getImageView());
      WaitForAsyncUtils.waitForFxEvents();
      robot.clickOn(second.getImageView());
    }
    robot.release(KeyCode.CONTROL);
    WaitForAsyncUtils.waitForFxEvents();
  }
  /** Multi-select helper: keep index for call-site readability (unused). */
  protected void selectSnowflake(FxRobot robot, double snowflakeX, double snowflakeY, int index) {
      var knots = app.getOptionalDotGrid().getDiagram().getCurrentStep().getAllVisibleKnots();
      if (knots.isEmpty()) {
        return;
      }

      org.alienlabs.adaloveslace.domain.Knot selected = knots.stream()
              .min(java.util.Comparator.comparingDouble(k -> Math.abs(k.getX() - snowflakeX) + Math.abs(k.getY() - snowflakeY)))
              .orElseThrow();

      robot.press(KeyCode.CONTROL);
      robot.moveTo(selected.getImageView());
      WaitForAsyncUtils.waitForFxEvents();
      robot.clickOn(selected.getImageView());
      robot.release(KeyCode.CONTROL);
  }

  protected void selectSecondSnowflake(FxRobot robot) {
    var knots = app.getOptionalDotGrid().getDiagram().getCurrentStep().getAllVisibleKnots();
    if (knots.size() < 2) {
      return;
    }
    var second = knots.stream().min(java.util.Comparator.comparingDouble(k -> Math.abs(k.getX() - SECOND_SNOWFLAKE_PIXEL_X))).orElseThrow();
    robot.moveTo(second.getImageView());
    WaitForAsyncUtils.waitForFxEvents();
    robot.clickOn(second.getImageView());
  }

  // Click on the snowflake in the toolbox to select its pattern
  protected void selectAndClickOnSnowflakePatternButton(FxRobot robot) {
    // Avoid TestFX visibility checks: the snowflake lives in a tall toolbox that is often clipped in Xvfb.
    robot.interact(() -> toolboxWindow.getSnowflakeButton().fire());
  }

  protected void clickOnButton(FxRobot robot, ToggleButton button) {
    // Click the concrete node (toolbox is a separate Stage; ID lookup is easy to mis-target).
    robot.clickOn(button);
  }

  protected void clickOnButton(FxRobot robot, UndoKnotButton button) {
    robot.interact(UndoKnotButton::undoKnot);
  }

  /** Prefer this over {@code robot.clickOn("#drawingButton")} when the toolbox is clipped in Xvfb. */
  protected void enterDrawingMode(FxRobot robot) {
    robot.interact(() -> DrawingButton.onSetDrawModeAction(app));
  }


  /**
   * Evaluate the condition on the JavaFX thread via {@link Platform#runLater} and wait without calling
   * {@link WaitForAsyncUtils#waitForFxEvents} in a tight loop (that drained input events and caused click storms).
   */
  public boolean waitForCondition(java.util.function.BooleanSupplier condition, long timeoutMs, long pollIntervalMs) {
    long deadline = System.currentTimeMillis() + timeoutMs;
    long pause = Math.max(10L, pollIntervalMs);
    long perPollWaitMs = Math.min(5_000L, Math.max(250L, pause * 3));
    while (System.currentTimeMillis() < deadline) {
      CompletableFuture<Boolean> done = new CompletableFuture<>();
      Platform.runLater(() -> {
        try {
          done.complete(condition.getAsBoolean());
        } catch (Throwable t) {
          done.completeExceptionally(t);
        }
      });
      try {
        Boolean ok = done.get(perPollWaitMs, TimeUnit.MILLISECONDS);
        if (Boolean.TRUE.equals(ok)) {
          return true;
        }
      } catch (TimeoutException e) {
        logger.debug("Condition check still pending on FX thread; will retry");
      } catch (ExecutionException e) {
        logger.error("Error while waiting for condition", e.getCause());
        return false;
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return false;
      }
      try {
        Thread.sleep(pause);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return false;
      }
    }
    return false;
  }

  /**
   * Wait for a condition to be met with default polling interval
   * @param condition The condition to check
   * @param timeoutMs Maximum time to wait in milliseconds
   * @return true if condition was met, false if timeout
   */
  public boolean waitForCondition(java.util.function.BooleanSupplier condition, long timeoutMs) {
    return waitForCondition(condition, timeoutMs, 100); // Default 100ms polling
  }

  /**
   * Wait for a condition to be met with more sophisticated error handling
   * @param condition The condition to check
   * @param timeoutMs Maximum time to wait in milliseconds
   * @param pollIntervalMs Interval between checks in milliseconds
   * @param description Description of what we're waiting for (for logging)
   * @return true if condition was met, false if timeout
   */
  public boolean waitForCondition(java.util.function.BooleanSupplier condition, long timeoutMs, long pollIntervalMs, String description) {
    logger.info("Waiting for condition: {}", description);
    boolean result = waitForCondition(condition, timeoutMs, pollIntervalMs);
    if (result) {
      logger.info("Condition satisfied: {}", description);
    } else {
      logger.warn("Condition timeout after {} ms: {}", timeoutMs, description);
    }
    return result;
  }

  /**
   * Wait for a condition with default timeout and description
   * @param condition The condition to check
   * @param description Description of what we're waiting for (for logging)
   * @return true if condition was met, false if timeout
   */
  public boolean waitForCondition(java.util.function.BooleanSupplier condition, String description) {
    return waitForCondition(condition, WAIT_TIME, 100, description);
  }

  /**
   * Assert that a condition is met within the timeout period
   * @param condition The condition to check
   * @param timeoutMs Maximum time to wait in milliseconds
   * @param description Description of what we're waiting for (for logging)
   * @throws AssertionError if condition is not met within timeout
   */
  public void assertCondition(java.util.function.BooleanSupplier condition, long timeoutMs, String description) {
    if (!waitForCondition(condition, timeoutMs, 100, description)) {
      throw new AssertionError("Condition not met within " + timeoutMs + "ms: " + description);
    }
  }

  /**
   * Assert that a condition is met within the default timeout period
   * @param condition The condition to check
   * @param description Description of what we're waiting for (for logging)
   * @throws AssertionError if condition is not met within timeout
   */
  public void assertCondition(java.util.function.BooleanSupplier condition, String description) {
    assertCondition(condition, WAIT_TIME, description);
  }

  private void copyCanvas(Point2D pointToMoveTo) {
    // Ensure grid nodes are up-to-date before snapshotting pixels.
    // (Functional tests toggle grid types and rely on the rendered result.)
    if (this.app != null && this.app.getOptionalDotGrid() != null) {
      this.app.getOptionalDotGrid().layoutChildren();
    }

    WritableImage snapshot = new ImageUtil(this.app).buildWritableImageWithTechnicalElements(
      BUILD_TOOL_OUTPUT_DIRECTORY + TEST_SCREEN_CAPTURE_FILE);

    PixelReader pr = snapshot.getPixelReader();
    // `ImageUtil` snapshots `app.getMovablePane()` using a viewport whose origin depends on the movablePane
    // layout position. So we must convert the input point into movablePane local coordinates (screenToLocal),
    // then subtract the viewport min, before indexing the snapshot's pixels.
    final int snapshotW = (int) snapshot.getWidth();
    final int snapshotH = (int) snapshot.getHeight();

    double viewportMinX = app.getMovablePane().getLayoutX();
    double viewportMinY = app.getMovablePane().getLayoutY();

    Point2D movableLocal = app.getMovablePane().screenToLocal(pointToMoveTo.getX(), pointToMoveTo.getY());
    int x = (int) Math.round(movableLocal.getX() - viewportMinX);
    int y = (int) Math.round(movableLocal.getY() - viewportMinY);

    // Fallback for call-sites that pass "local" (not screen) coordinates: try stage-relative and raw values.
    if (x < 0 || y < 0 || x >= snapshotW || y >= snapshotH) {
      int xStageRel = (int) Math.round(pointToMoveTo.getX() - this.primaryStage.getX());
      int yStageRel = (int) Math.round(pointToMoveTo.getY() - this.primaryStage.getY());
      if (xStageRel >= 0 && yStageRel >= 0 && xStageRel < snapshotW && yStageRel < snapshotH) {
        x = xStageRel;
        y = yStageRel;
      } else {
        int xRaw = (int) Math.round(pointToMoveTo.getX());
        int yRaw = (int) Math.round(pointToMoveTo.getY());
        x = xRaw;
        y = yRaw;
      }
    }

    // Keep indexing safe even if the test provides an out-of-bounds point.
    x = Math.max(0, Math.min(x, snapshotW - 1));
    y = Math.max(0, Math.min(y, snapshotH - 1));

    // Sampling strategy:
    // - Prefer the exact (x,y) pixel when it's not "background-like" and is not fully transparent.
    //   This makes rotation/zoom tests sensitive to local visual changes.
    // - Otherwise, fall back to a neighborhood search for the darkest non-transparent pixel.
    //   This makes knot/pattern detection resilient when (x,y) lands on an anti-aliased edge.
    final int radius = 3; // 7x7
    final int centerArgb = pr.getArgb(x, y);
    final int centerA = (centerArgb >>> 24) & 0xFF;
    final double centerLum = luminance(centerArgb);
    final double backgroundLumThreshold = 245d; // close to pure white (255)

    int bestArgb = centerArgb;
    boolean useCenter = centerA > 0 && centerLum < backgroundLumThreshold;

    if (!useCenter) {
      double bestLum = Double.POSITIVE_INFINITY;
      for (int dx = -radius; dx <= radius; dx++) {
        for (int dy = -radius; dy <= radius; dy++) {
          int nx = x + dx;
          int ny = y + dy;
          if (nx < 0 || ny < 0 || nx >= snapshotW || ny >= snapshotH) {
            continue;
          }
          int argb = pr.getArgb(nx, ny);
          int a = (argb >>> 24) & 0xFF;
          if (a <= 0) {
            continue; // ignore fully transparent pixels (can have misleading RGB)
          }
          double lum = luminance(argb);
          if (lum >= backgroundLumThreshold) {
            continue; // ignore background-like white pixels
          }
          if (lum < bestLum) {
            bestLum = lum;
            bestArgb = argb;
          }
        }
      }
    }

    int a = (bestArgb >>> 24) & 0xFF;
    int r = (bestArgb >>> 16) & 0xFF;
    int g = (bestArgb >>> 8) & 0xFF;
    int b = (bestArgb) & 0xFF;
    this.foundColorOnGrid = Color.rgb(r, g, b, a / 255.0);
    logger.info("# argb: {} at ({}, {}), with stage at ({}, {})", this.foundColorOnGrid, x, y, this.primaryStage.getX(), this.primaryStage.getY());
  }

  private static double luminance(int argb) {
    int r = (argb >>> 16) & 0xFF;
    int g = (argb >>> 8) & 0xFF;
    int b = (argb) & 0xFF;
    // Relative luminance (sRGB). Lower = darker.
    return 0.2126d * r + 0.7152d * g + 0.0722d * b;
  }

  protected Point2D newPointOnGrid(double pixelX, double pixelY) {
    return new Point2D(this.primaryStage.getX() + pixelX,
      this.primaryStage.getY() + pixelY);
  }

  /**
   * Scene point computed from `app.getMovablePane()` local coordinates.
   * <p>
   * TestFX `robot.clickOn(Point2D)` expects scene coordinates, so we use `localToScene(...)` instead of
   * `localToScreen(...)` (which can double-apply stage translation and miss the grid cell).
   */
  protected Point2D newPointOnMovablePane(double localX, double localY) {
    if (this.app == null || this.app.getMovablePane() == null) {
      return newPointOnGrid(localX, localY);
    }
    Point2D scenePoint = this.app.getMovablePane().localToScene(localX, localY);
    return new Point2D(scenePoint.getX(), scenePoint.getY());
  }

  protected Point2D newPointOnGridForFirstNonGridNode() {
    ImageView imageView = app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots().iterator().next().getImageView();
    return new Point2D(this.primaryStage.getX() + imageView.getBoundsInParent().getCenterX(),
      this.primaryStage.getY() + imageView.getBoundsInParent().getCenterY() + 10d);
  }

  // @see https://stackoverflow.com/questions/23741574/how-to-get-the-absolute-rotation-of-a-node-in-javafx
  protected double getSnowFlakeRotationAngle() {
    if (!app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().isEmpty()) {
      return app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots()
              .stream().findFirst().orElseThrow().getImageView().getRotate();
    }
    return app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots()
            .stream().findFirst().orElseThrow().getImageView().getRotate();
  }

  protected double getSnowFlakeZoomFactor() {
    if (!app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().isEmpty()) {
      return app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots()
              .stream().findFirst().orElseThrow().getImageView().getScaleX();
    }
    return app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots()
            .stream().findFirst().orElseThrow().getImageView().getScaleX();
  }

  protected void initDrawAndSelectSnowFlake(FxRobot robot) {
    selectAndClickOnSnowflakePatternButton(robot);
    drawASnowflake(robot);
    // Model-level selection: spinner listeners depend on currentStep.getSelectedKnots().
    robot.interact(() -> {
      var step = app.getOptionalDotGrid().getDiagram().getCurrentStep();
      if (!step.getDisplayedKnots().isEmpty()) {
        var knot = step.getDisplayedKnots().get(0);
        var newDisplayed = new java.util.ArrayList<>(step.getDisplayedKnots());
        newDisplayed.remove(knot);
        step.setDisplayedKnots(newDisplayed);
        step.setSelectedKnots(java.util.List.of(knot));
        app.getOptionalDotGrid().getDiagram().setCurrentKnot(knot);
        // Ensure the scene reflects the new step state.
        app.getOptionalDotGrid().layoutChildren();
      }
    });
    WaitForAsyncUtils.waitForFxEvents();
  }

  /** Knot scale is read on the FX thread inside {@link #assertCondition}. */
  protected void assertZoomFactorNear(double expectedZoom, String description) {
    assertCondition(
        () -> Math.abs(getSnowFlakeZoomFactor() - expectedZoom) < 0.02d,
        description);
  }

  /** Knot rotation is read on the FX thread inside {@link #assertCondition}. */
  protected void assertRotationNear(double expectedDegrees, String description) {
    assertCondition(
        () -> Math.abs(getSnowFlakeRotationAngle() - expectedDegrees) < 0.05d,
        description);
  }

  protected void drawSnowFlake(FxRobot robot, double x, double y) {
    robot.interact(() -> app.getOptionalDotGrid().getDiagram().drawKnot(x, y));
  }

  protected void duplicateKnots(FxRobot robot) {
    robot.clickOn("#duplicationButton");
  }

  protected void selectDeleteMode(FxRobot robot) {
    robot.clickOn("#deletionButton");
  }

  protected void selectSecondKnotWithControlKeyPressed(FxRobot robot) {
    Point2D snowflakeOnTheGrid = newPointOnGrid(SECOND_SNOWFLAKE_PIXEL_X + 30d, SECOND_SNOWFLAKE_PIXEL_Y + 30d);
    Point2D screenPoint = app.getMovablePane().getChildren().stream()
            .filter(ImageView.class::isInstance).sorted(Comparator.comparing(Node::getLayoutX))
            .toList().get(2).getScene().getWindow().getScene().getRoot().localToScreen(snowflakeOnTheGrid);
    double screenX = screenPoint.getX();
    double screenY = screenPoint.getY();

    MouseEvent enterWithControl = new MouseEvent(
            MouseEvent.MOUSE_ENTERED,
            snowflakeOnTheGrid.getX(),
            snowflakeOnTheGrid.getY(),
            screenX,
            screenY,
            MouseButton.PRIMARY,
            1,
            false,  // shift
            true,   // control
            false,  // alt
            false,  // meta
            false, false, false, false, false, false,
            null
    );

    app.getPrimaryStage().fireEvent(enterWithControl);
  }

  protected void unselectControlKey(FxRobot robot) {
    robot.release(KeyCode.CONTROL);
  }

}

package org.alienlabs.adaloveslace.functionaltest;

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
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid.UndoKnotButton;
import org.alienlabs.adaloveslace.view.window.GeometryWindow;
import org.alienlabs.adaloveslace.view.window.StateWindow;
import org.alienlabs.adaloveslace.view.window.ToolboxWindow;
import org.alienlabs.adaloveslace.view.window.event.WindowRepositionEvents;
import org.alienlabs.adaloveslace.view.window.event.WindowResizeEvents;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.robot.Motion;

import java.util.Comparator;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.alienlabs.adaloveslace.App.EXPORT_IMAGE_FILE_TYPE;
import static org.alienlabs.adaloveslace.util.FileUtil.FILE_SEPARATOR;

@ExtendWith(ApplicationExtension.class)
public class AppFunctionalTestParent {

  public Stage primaryStage;
  public GeometryWindow geometryWindow;
  public ToolboxWindow toolboxWindow;
  public StateWindow stateWindow;
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

  public static final double GRAY_PIXEL_X                 = 98d;
  public static final double GRAY_PIXEL_Y                 = 67d;
  public static final Color  SNOWFLAKE_DOT_COLOR          = Color.valueOf("0x9df6feff");

  public Color foundColorOnGrid;

  private static final Logger logger = LoggerFactory.getLogger(AppFunctionalTestParent.class);

  /**
   * Init method called before each test
   *
   * @param primaryStage The injected window (stage)
   */
  public void start(Stage primaryStage) {
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
    this.app.getToolboxStage().setX(1500d);
    this.app.getToolboxStage().setY(50d);
    this.app.getToolboxStage().setHeight(900d);

    this.geometryWindow = this.app.showGeometryWindow(this.app);
    this.app.getGeometryStage().setX(720d);
    this.app.getGeometryStage().setY(50d);
    this.app.getGeometryStage().setHeight(500d);

    this.stateWindow = this.app.showStateWindow(this.app);
    this.app.getStateStage().setX(720d);
    this.app.getStateStage().setY(770d);
    this.app.getStateStage().setHeight(300d);
  }

  // This is in order to have time to copy the image to the canvas, otherwise the image is always white and we don't
  // have access to the UI thread for the copy without "Platform.runLater()"
  protected Color getColor(Point2D pointToMoveTo) {
    copyCanvas(pointToMoveTo);
    return this.foundColorOnGrid;
  }

  // Click on the grid with the snowflake selected in order to draw a snowflake on the grid
  protected void drawASnowflake(FxRobot robot) {
    Point2D snowflakeOnTheGrid = newPointOnGrid(FIRST_SNOWFLAKE_PIXEL_X, FIRST_SNOWFLAKE_PIXEL_Y);
    robot.clickOn(snowflakeOnTheGrid);
  }

  protected void selectASnowflake(FxRobot robot) {
      clickSelectButton(robot);
      Point2D snowflakeOnTheGrid = newPointOnGrid(FIRST_SNOWFLAKE_PIXEL_X, FIRST_SNOWFLAKE_PIXEL_Y);
      robot.clickOn(snowflakeOnTheGrid, Motion.DIRECT, MouseButton.PRIMARY);
  }

  // Click on the grid with the color wheel selected in order to draw a color wheel on the grid
  protected void drawFirstColorWheel(FxRobot robot) {
    Point2D colorWheelOnTheGrid = newPointOnGrid(FIRST_SNOWFLAKE_PIXEL_X, FIRST_SNOWFLAKE_PIXEL_Y);
    robot.clickOn(colorWheelOnTheGrid);
  }

  // Click on the grid with the second snowflake selected in order to draw a snowflake on the grid elsewhere
  protected void drawSecondSnowflake(FxRobot robot) {
    Point2D snowflakeOnTheGrid = newPointOnGrid(SECOND_SNOWFLAKE_PIXEL_X, SECOND_SNOWFLAKE_PIXEL_Y);
    robot.clickOn(snowflakeOnTheGrid);
  }

  // Click on the grid with the third (not duplicated) snowflake selected in order to draw a snowflake on the grid elsewhere
  protected void drawOtherSnowflake(FxRobot robot) {
    Point2D snowflakeOnTheGrid = newPointOnGrid(OTHER_SNOWFLAKE_PIXEL_X, OTHER_SNOWFLAKE_PIXEL_Y);
    robot.clickOn(snowflakeOnTheGrid, Motion.DIRECT, MouseButton.PRIMARY);
  }

  // Click on the grid where the snowflake is in order to select it
  protected void clickSelectButton(FxRobot robot) {
    robot.clickOn(geometryWindow.getSelectionButton(), Motion.DIRECT, MouseButton.PRIMARY);
  }

  protected void incrementSpinner(FxRobot robot, Spinner<Integer> spinner) {
    robot.clickOn("#" + spinner.getId() + " .increment-arrow-button");
  }

  protected void decrementSpinner(FxRobot robot, Spinner<Integer> spinner) {
    robot.clickOn("#" + spinner.getId() + " .decrement-arrow-button");
  }

  protected void setSpinnerValue(FxRobot robot, Spinner<Integer> spinner, int value) {
      robot.clickOn("#" + spinner.getId() + " .text-field")
              .eraseText(3)
              .write(String.valueOf(value))
              .type(KeyCode.ENTER);
  }
  protected void selectTwoSnowflakes(FxRobot robot) {
      robot.press(KeyCode.CONTROL)
              .moveTo(this.primaryStage.getX() + FIRST_SNOWFLAKE_PIXEL_X, this.primaryStage.getY() + FIRST_SNOWFLAKE_PIXEL_Y)
              .clickOn(MouseButton.PRIMARY)
              .moveTo(this.primaryStage.getX() + SECOND_SNOWFLAKE_PIXEL_X, this.primaryStage.getY() + SECOND_SNOWFLAKE_PIXEL_Y)
              .clickOn(MouseButton.PRIMARY)
              .release(KeyCode.CONTROL);

  }
  protected void selectSnowflake(double snowflakeX, double snowflakeY, int index) {
      Point2D snowflakeOnTheGrid = newPointOnGrid(snowflakeX + 25d, snowflakeY + 25d);
      Point2D screenPoint = app.getMovablePane().getChildren().stream().filter(ImageView.class::isInstance).toList().get(index).localToScreen(snowflakeOnTheGrid);
      double screenX = screenPoint.getX();
      double screenY = screenPoint.getY();

    MouseEvent enterWithControl = new MouseEvent(
            MouseEvent.MOUSE_ENTERED,
            snowflakeOnTheGrid.getX(),
            snowflakeOnTheGrid.getY(),
            screenX,
            screenY,
            MouseButton.PRIMARY,
            0,
            false,  // shift
            true,   // control
            false,  // alt
            false,  // meta
            false, false, false, false, false, false,
            null
    );

    app.getPrimaryStage().fireEvent(enterWithControl);
  }

  protected void selectSecondSnowflake(FxRobot robot) {
    Point2D snowflakeOnTheGrid = newPointOnGrid(SECOND_SNOWFLAKE_PIXEL_X + 25d, SECOND_SNOWFLAKE_PIXEL_Y + 25d);
    robot.moveTo(snowflakeOnTheGrid).press(MouseButton.PRIMARY).release(MouseButton.PRIMARY);
  }

  // Click on the snowflake in the toolbox to select its pattern
  protected void selectAndClickOnSnowflakePatternButton(FxRobot robot) {
    clickOnButton(robot, toolboxWindow.getSnowflakeButton());
  }

  protected void clickOnButton(FxRobot robot, ToggleButton button) {
    robot.clickOn("#" + button.getId());
  }

  protected void clickOnButton(FxRobot robot, UndoKnotButton button) {
    robot.clickOn("#" + button.getId());
  }


  /**
   * Wait for a condition to be met with polling using CompletableFuture
   * @param condition The con;dition to check
   * @param timeoutMs Maximum time to wait in milliseconds
   * @param pollIntervalMs Interval between checks in milliseconds
   * @return true if condition was met, false if timeout
   */
  public boolean waitForCondition(java.util.function.BooleanSupplier condition, long timeoutMs, long pollIntervalMs) {
    CompletableFuture<Boolean> future = new CompletableFuture<>();
    
    // Create a scheduled task that checks the condition periodically
    CompletableFuture.runAsync(() -> {
      long startTime = System.currentTimeMillis();
      while (System.currentTimeMillis() - startTime < timeoutMs && !future.isDone()) {
        try {
          if (condition.getAsBoolean()) {
            future.complete(true);
            return;
          }
          Thread.sleep(pollIntervalMs);
        } catch (InterruptedException e) {
          logger.error("Interrupted while waiting for condition!", e);
          future.completeExceptionally(e);
          return;
        }
      }
      // Timeout reached
      future.complete(false);
    });

    try {
      return future.get(timeoutMs + 1000, TimeUnit.MILLISECONDS); // Add buffer for timeout
    } catch (TimeoutException e) {
      logger.error("Condition check timed out after {} ms", timeoutMs, e);
      return false;
    } catch (Exception e) {
      logger.error("Error while waiting for condition", e);
      return false;
    }
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
    logger.debug("Waiting for condition: {}", description);
    boolean result = waitForCondition(condition, timeoutMs, pollIntervalMs);
    if (result) {
      logger.debug("Condition satisfied: {}", description);
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
    WritableImage snapshot = new ImageUtil(this.app).buildWritableImageWithTechnicalElements(
      BUILD_TOOL_OUTPUT_DIRECTORY + TEST_SCREEN_CAPTURE_FILE);

    PixelReader pr = snapshot.getPixelReader();
    int x = Double.valueOf(pointToMoveTo.getX() - this.primaryStage.getX()).intValue();
    int y = Double.valueOf(pointToMoveTo.getY() - this.primaryStage.getY()).intValue();

    this.foundColorOnGrid = pr.getColor(x, y);
    logger.debug("# argb: {} at ({}, {}), with stage at ({}, {})", this.foundColorOnGrid, x, y, this.primaryStage.getX(), this.primaryStage.getY());
  }

  protected Point2D newPointOnGrid(double pixelX, double pixelY) {
    return new Point2D(this.primaryStage.getX() + pixelX,
      this.primaryStage.getY() + pixelY);
  }

  protected Point2D newPointOnGridForFirstNonGridNode() {
    ImageView imageView = app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots().iterator().next().getImageView();
    return new Point2D(this.primaryStage.getX() + imageView.getBoundsInParent().getCenterX(),
      this.primaryStage.getY() + imageView.getBoundsInParent().getCenterY() + 10d);
  }

  // @see https://stackoverflow.com/questions/23741574/how-to-get-the-absolute-rotation-of-a-node-in-javafx
  protected double getSnowFlakeRotationAngle() {
    return app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().
            stream().findFirst().get().getImageView().
            getRotate();
  }

  protected double getSnowFlakeZoomFactor() {
    return app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().
      stream().findFirst().get().getImageView().
            getScaleX();
  }

  protected void initDrawAndSelectSnowFlake(FxRobot robot) {
    selectAndClickOnSnowflakePatternButton(robot);
    drawASnowflake(robot);
    selectASnowflake(robot);
  }

  protected void drawSnowFlake(FxRobot robot, double x, double y) {
    Point2D snowflakeOnTheGrid = newPointOnGrid(x, y);
    robot.clickOn(snowflakeOnTheGrid, Motion.DIRECT, MouseButton.PRIMARY);
  }

  protected void duplicateKnots(FxRobot robot) {
    robot.clickOn("#duplicationButton");
  }

  protected void selectDeleteMode(FxRobot robot) {
    robot.clickOn(this.geometryWindow.getDeletionButton(), Motion.DIRECT, MouseButton.PRIMARY);
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

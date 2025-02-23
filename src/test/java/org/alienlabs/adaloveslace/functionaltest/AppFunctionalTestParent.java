package org.alienlabs.adaloveslace.functionaltest;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Spinner;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Diagram;
import org.alienlabs.adaloveslace.util.ImageUtil;
import org.alienlabs.adaloveslace.view.window.GeometryWindow;
import org.alienlabs.adaloveslace.view.window.StateWindow;
import org.alienlabs.adaloveslace.view.window.ToolboxWindow;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.robot.Motion;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.alienlabs.adaloveslace.App.*;
import static org.alienlabs.adaloveslace.util.FileUtil.PATH_SEPARATOR;

@ExtendWith(ApplicationExtension.class)
public class AppFunctionalTestParent {

  public Stage primaryStage;
  public GeometryWindow geometryWindow;
  public ToolboxWindow toolboxWindow;
  public StateWindow stateWindow;
  public App app;

  // For tests:
  public static final long   SLEEP_TIME                   = Long.getLong("SLEEP_TIME", 2_500L);
  public static final long   WAIT_TIME                    = Long.getLong("WAIT_TIME", 5_000L);
  public static final double GRID_WIDTH                   = 600d;
  public static final double GRID_HEIGHT                  = 420d;
  public static final String BUILD_TOOL_OUTPUT_DIRECTORY  = "target/";
  public static final String TEST_SCREEN_CAPTURE_FILE     = "test_screen_capture" + EXPORT_IMAGE_FILE_TYPE;

  public static final String CLASSPATH_RESOURCES_PATH_JPG = ".*org" + PATH_SEPARATOR + "alienlabs" + PATH_SEPARATOR + "adaloveslace" + PATH_SEPARATOR + ".*test" + PATH_SEPARATOR + ".*.jpg";
  public static final String CLASSPATH_RESOURCES_PATH     = "org" + PATH_SEPARATOR + "alienlabs" + PATH_SEPARATOR + "adaloveslace" + PATH_SEPARATOR + "test" + PATH_SEPARATOR;

  public static final String SNOWFLAKE                    = "snowflake_small";
  public static final String SNOWFLAKE_IMAGE              = "snowflake_small.jpg";

  public static final String COLOR_WHEEL                  = "color wheel";
  public static final String COLOR_WHEEL_IMAGE            = "color wheel.jpg";

  public static final double FIRST_SNOWFLAKE_PIXEL_X      = 215d;

  public static final double FIRST_SNOWFLAKE_PIXEL_Y      = 145d;

  public static final double SECOND_SNOWFLAKE_PIXEL_X     = 315d;

  public static final double SECOND_SNOWFLAKE_PIXEL_Y     = 145d;

  public static final double OTHER_SNOWFLAKE_PIXEL_X      = 115d;

  public static final double OTHER_SNOWFLAKE_PIXEL_Y      = 75d;

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

    Locale locale = new Locale("en", "EN");
    App.resourceBundle = ResourceBundle.getBundle("AdaLovesLace", locale);
    Diagram diagram = new Diagram(this.app);
    this.app.setDiagram(diagram);
    this.primaryStage = primaryStage;

    // The grid dots are twice as big as in the production code in order to facilitate tests
    this.app.showMainWindow(MAIN_WINDOW_WIDTH, MAIN_WINDOW_HEIGHT, GRID_WIDTH, GRID_HEIGHT, GRID_DOTS_RADIUS * 2d, this.primaryStage, diagram);
    this.app.setOptionalDotGrid(this.app.getMainWindow().getOptionalDotGrid());

    this.toolboxWindow = this.app.showToolboxWindow(this.app, this, CLASSPATH_RESOURCES_PATH_JPG);
    this.app.getToolboxStage().setX(1150d);
    this.app.getToolboxStage().setY(50d);
    this.app.getToolboxStage().setHeight(600d);

    this.geometryWindow = this.app.showGeometryWindow(this.app);
    this.app.getGeometryStage().setX(720d);
    this.app.getGeometryStage().setY(50d);

    this.stateWindow = this.app.showStateWindow(this.app);
    this.app.getStateStage().setX(720d);
    this.app.getStateStage().setY(770d);
    this.app.getStateStage().setHeight(300d);
  }

  // This is in order to have time to copy the image to the canvas, otherwise the image is always white and we don't
  // have access to the UI thread for the copy without "Platform.runLater()"
  protected Color getColor(Point2D pointToMoveTo) {
    synchronizeTask(() -> copyCanvas(pointToMoveTo));
    return this.foundColorOnGrid;
  }

  // Click on the grid with the snowflake selected in order to draw a snowflake on the grid
  protected void drawASnowflake(FxRobot robot) {
    Point2D snowflakeOnTheGrid = newPointOnGrid(FIRST_SNOWFLAKE_PIXEL_X, FIRST_SNOWFLAKE_PIXEL_Y);
    robot.clickOn(snowflakeOnTheGrid, Motion.DIRECT, MouseButton.PRIMARY);
  }

  // Click on the grid with the color wheel selected in order to draw a color wheel on the grid
  protected void drawFirstColorWheel(FxRobot robot) {
    Point2D colorWheelOnTheGrid = newPointOnGrid(FIRST_SNOWFLAKE_PIXEL_X, FIRST_SNOWFLAKE_PIXEL_Y);
    robot.clickOn(colorWheelOnTheGrid, Motion.DIRECT, MouseButton.PRIMARY);
  }

  // Click on the grid with the second snowflake selected in order to draw a snowflake on the grid elsewhere
  protected void drawSecondSnowflake(FxRobot robot) {
    Point2D snowflakeOnTheGrid = newPointOnGrid(SECOND_SNOWFLAKE_PIXEL_X, SECOND_SNOWFLAKE_PIXEL_Y);
    robot.clickOn(snowflakeOnTheGrid, Motion.DIRECT, MouseButton.PRIMARY);
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
    robot.clickOn(geometryWindow.getGeometryStage().getScene().getWindow().getX() +
                    spinner.getLayoutX() + 50,
            geometryWindow.getGeometryStage().getScene().getWindow().getY() +
                    spinner.getLayoutY() + 35, Motion.DIRECT, MouseButton.PRIMARY);
  }

  protected void decrementSpinner(FxRobot robot, Spinner<Integer> spinner) {
    robot.clickOn(geometryWindow.getGeometryStage().getScene().getWindow().getX() +
            spinner.getLayoutX() + 50,
            geometryWindow.getGeometryStage().getScene().getWindow().getY() +
                    spinner.getLayoutY() + 75, Motion.DIRECT, MouseButton.PRIMARY);
  }

  protected void setSpinnerValue(Spinner<Integer> spinner, int value) {
    spinner.getValueFactory().setValue(value);
  }

  protected void selectFirstSnowflake(FxRobot robot) {
    Point2D snowflakeOnTheGrid = newPointOnGrid(FIRST_SNOWFLAKE_PIXEL_X + 20d, FIRST_SNOWFLAKE_PIXEL_Y + 20d);
    robot.clickOn(snowflakeOnTheGrid, Motion.DIRECT, MouseButton.PRIMARY);
  }

  protected void selectFirstColorWheel(FxRobot robot) {
    Point2D colorWheelOnTheGrid = newPointOnGrid(FIRST_SNOWFLAKE_PIXEL_X + 20d, FIRST_SNOWFLAKE_PIXEL_Y + 20d);
    robot.clickOn(colorWheelOnTheGrid, Motion.DIRECT, MouseButton.PRIMARY);
  }

  protected void selectSecondSnowflake(FxRobot robot) {
    Point2D snowflakeOnTheGrid = newPointOnGrid(SECOND_SNOWFLAKE_PIXEL_X + 20d, SECOND_SNOWFLAKE_PIXEL_Y + 20d);
    robot.clickOn(snowflakeOnTheGrid, Motion.DIRECT, MouseButton.PRIMARY);
  }

  // Click on the snowflake in the toolbox to select its pattern
  protected void selectAndClickOnSnowflakePatternButton(FxRobot robot) {
    clickOnButton(robot, toolboxWindow.getSnowflakeButton());
  }

  // Click on the color wheel in the toolbox to select its pattern
  protected void selectAndClickOnColorWheelPatternButton(FxRobot robot) {
    clickOnButton(robot, toolboxWindow.getColorWheelButton());
  }

  protected void clickOnButton(FxRobot robot, Node button) {
    robot.clickOn(button, Motion.DIRECT, MouseButton.PRIMARY);
  }

  public void synchronizeTask(Runnable runnable) {
    final CountDownLatch lock  = new CountDownLatch(1);
    Platform.runLater(() -> {
      runnable.run();
      lock.countDown();
    });

    // We block the main thread to let the runnable (JavaFX application thread) work
    this.sleepMainThread();

    try {
      // And when the runnable has returned we can continue,
      // but we must let it as much time as it needs to complete, lest the assertion which comes after will be wrong
      lock.await(WAIT_TIME, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      logger.error("Interrupted!", e);
    }
  }

  public void synchronizeLongTask(Runnable runnable) {
    final CountDownLatch lock  = new CountDownLatch(1);
    Platform.runLater(() -> {
      runnable.run();
      lock.countDown();
    });

    // We block the main thread to let the runnable (JavaFX application thread) work
    this.sleepMainThread();
    this.sleepMainThread();
    this.sleepMainThread();
    this.sleepMainThread();
    this.sleepMainThread();

    try {
      // And when the runnable has returned we can continue,
      // but we must let it as much time as it needs to complete, lest the assertion which comes after will be wrong
      lock.await(WAIT_TIME, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      logger.error("Interrupted!", e);
    }
  }

  public void sleepMainThread() {
    try {
      Thread.sleep(SLEEP_TIME);
    } catch (InterruptedException e) {
      logger.error("Interrupted!", e);
    }
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
    synchronizeTask(() -> selectAndClickOnSnowflakePatternButton(robot));
    synchronizeTask(() -> drawASnowflake(robot));
    synchronizeTask(() -> clickSelectButton(robot));
    synchronizeTask(() -> selectFirstSnowflake(robot));
  }

  protected void drawSnowFlake(FxRobot robot, double x, double y) {
    Point2D snowflakeOnTheGrid = newPointOnGrid(x, y);
    robot.clickOn(snowflakeOnTheGrid, Motion.DIRECT, MouseButton.PRIMARY);
  }

  protected void duplicateKnots(FxRobot robot) {
    this.sleepMainThread();
    robot.clickOn(this.geometryWindow.getDuplicationButton(), Motion.DIRECT, MouseButton.PRIMARY);
  }

  protected void selectDeleteMode(FxRobot robot) {
    robot.clickOn(this.geometryWindow.getDeletionButton(), Motion.DIRECT, MouseButton.PRIMARY);
  }

  protected void selectSecondKnotWithControlKeyPressed(FxRobot robot) {
    Point2D snowflakeOnTheGrid = newPointOnGrid(SECOND_SNOWFLAKE_PIXEL_X + 10d, SECOND_SNOWFLAKE_PIXEL_Y + 10d);
    robot.press(KeyCode.CONTROL);
    robot.clickOn(snowflakeOnTheGrid, Motion.DIRECT, MouseButton.PRIMARY);
  }

  protected void unselectControlKey(FxRobot robot) {
    robot.release(KeyCode.CONTROL);
  }

}

package org.alienlabs.adaloveslace.functionaltest;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Diagram;
import org.alienlabs.adaloveslace.util.ImageUtil;
import org.alienlabs.adaloveslace.view.window.GeometryWindow;
import org.alienlabs.adaloveslace.view.window.ToolboxWindow;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.robot.Motion;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;
import static org.alienlabs.adaloveslace.App.EXPORT_IMAGE_FILE_TYPE;
import static org.alienlabs.adaloveslace.App.GRID_DOTS_RADIUS;
import static org.alienlabs.adaloveslace.util.FileUtil.PATH_SEPARATOR;

@ExtendWith(ApplicationExtension.class)
public class AppFunctionalTestParent {

  public Stage primaryStage;
  public GeometryWindow geometryWindow;
  public ToolboxWindow toolboxWindow;
  public App app;

  // For tests:
  public static final long   SLEEP_BETWEEN_ACTIONS_TIME   = Long.getLong("SLEEP_BETWEEN_ACTIONS_TIME", 1_000L);
  public static final double GRID_WIDTH           = 600d;
  public static final double GRID_HEIGHT          = 420d;
  public static final String BUILD_TOOL_OUTPUT_DIRECTORY  = "target/";
  public static final String TEST_SCREEN_CAPTURE_FILE     = "test_screen_capture" + EXPORT_IMAGE_FILE_TYPE;

  public static final String CLASSPATH_RESOURCES_PATH_JPG = ".*org" + PATH_SEPARATOR + "alienlabs" + PATH_SEPARATOR + "adaloveslace" + PATH_SEPARATOR + ".*test" + PATH_SEPARATOR + ".*.jpg";
  public static final String CLASSPATH_RESOURCES_PATH     = "org" + PATH_SEPARATOR + "alienlabs" + PATH_SEPARATOR + "adaloveslace" + PATH_SEPARATOR + "test" + PATH_SEPARATOR;

  public static final String SNOWFLAKE            = "snowflake_small";
  public static final String SNOWFLAKE_IMAGE      = "snowflake_small.jpg";

  public static final double SNOWFLAKE_PIXEL_X    = 415d;

  public static final double SNOWFLAKE_PIXEL_Y    = 145d;

  public static final double GRAY_PIXEL_X         = 98d;
  public static final double GRAY_PIXEL_Y         = 67d;
  public static final Color  SNOWFLAKE_DOT_COLOR  = Color.valueOf("0x9df6feff");

  public Color foundColorOnGrid;

  /**
   * Countdown latch
   */
  protected CountDownLatch lock = new CountDownLatch(1);

  private static final Logger logger = LoggerFactory.getLogger(AppFunctionalTestParent.class);

  /**
   * Init method called before each test
   *
   * @param primaryStage The injected window (stage)
   */
  @Start
  public void start(Stage primaryStage) {

    this.app = new App();
    this.app.setPrimaryStage(primaryStage);

    Locale locale = new Locale("en", "EN");
    App.resourceBundle = ResourceBundle.getBundle("AdaLovesLace", locale);
    this.app.setDiagram(new Diagram());
    this.primaryStage = primaryStage;

    // The grid dots are twice as big as in the production code in order to facilitate tests
    this.app.showMainWindow(640d, 480d, GRID_WIDTH, GRID_HEIGHT, GRID_DOTS_RADIUS * 2d, this.primaryStage);

    this.toolboxWindow = this.app.showToolboxWindow(this.app, this, CLASSPATH_RESOURCES_PATH_JPG);
    this.app.getToolboxStage().setX(1600d);
    this.app.getToolboxStage().setY(50d);
    this.app.getToolboxStage().setHeight(600d);

    this.geometryWindow = this.app.showGeometryWindow(this.app);
    this.app.getGeometryStage().setX(1100d);
    this.app.getGeometryStage().setY(50d);
  }

  // This is in order to have time to copy the image to the canvas, otherwise the image is always white and we don't
  // have access to the UI thread for the copy without "Platform.runLater()"
  protected Color getColor(Point2D pointToMoveTo) {
    lock = new CountDownLatch(1);
    copyCanvas(pointToMoveTo);

    try {
      lock.await(SLEEP_BETWEEN_ACTIONS_TIME, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      logger.error("Interrupted!", e);
    }

    return this.foundColorOnGrid;
  }

  // Click on the grid with the snowflake selected in order to draw a snowflake on the grid
  protected void drawSnowflake(FxRobot robot) {
    Point2D snowflakeOnTheGrid = newPointOnGrid(SNOWFLAKE_PIXEL_X, SNOWFLAKE_PIXEL_Y);
    robot.clickOn(snowflakeOnTheGrid, Motion.DEFAULT, MouseButton.PRIMARY);
  }

  // Click on the grid where the snowflake is in order to select it
  protected void selectSnowflake(FxRobot robot) {
    lock = new CountDownLatch(1);

    Platform.runLater(() -> {
      robot.clickOn(geometryWindow.getSelectionButton(), Motion.DEFAULT, MouseButton.PRIMARY);
      lock.countDown();
    });

    try {
      lock.await(SLEEP_BETWEEN_ACTIONS_TIME, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      logger.error("Interrupted!", e);
    }

    Point2D snowflakeOnTheGrid = newPointOnGrid(SNOWFLAKE_PIXEL_X + 10d, SNOWFLAKE_PIXEL_Y + 10d);
    robot.clickOn(snowflakeOnTheGrid, Motion.DEFAULT, MouseButton.PRIMARY);
  }

  // Click on the snowflake in the toolbox to select its pattern
  protected void selectAndClickOnSnowflake(FxRobot robot) {
    clickOnButton(robot, toolboxWindow.getSnowflakeButton());
  }

  private void clickOnButton(FxRobot robot, Node button) {
    robot.clickOn(button, Motion.DEFAULT, MouseButton.PRIMARY);

    // No choice to sleep because we want to have time for the action to perform
    try {
      sleep(SLEEP_BETWEEN_ACTIONS_TIME);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private void copyCanvas(Point2D pointToMoveTo) {
    Platform.runLater(() -> {
      WritableImage snapshot = new ImageUtil(this.app).buildWritableImageWithTechnicalElements(
        BUILD_TOOL_OUTPUT_DIRECTORY + TEST_SCREEN_CAPTURE_FILE);

      PixelReader pr = snapshot.getPixelReader();
      int x = Double.valueOf(pointToMoveTo.getX() - this.primaryStage.getX()).intValue();
      int y = Double.valueOf(pointToMoveTo.getY() - this.primaryStage.getY()).intValue();

      this.foundColorOnGrid = pr.getColor(x, y);
      logger.info("# argb: {} at ({}, {}), with stage at ({}, {})", this.foundColorOnGrid, x, y, this.primaryStage.getX(), this.primaryStage.getY());

      lock.countDown();
    });
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

}

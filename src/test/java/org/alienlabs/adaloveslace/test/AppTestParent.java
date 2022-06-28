package org.alienlabs.adaloveslace.test;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Point2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Diagram;
import org.alienlabs.adaloveslace.view.window.GeometryWindow;
import org.alienlabs.adaloveslace.view.window.ToolboxWindow;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.alienlabs.adaloveslace.App.GRID_DOTS_RADIUS;
import static org.alienlabs.adaloveslace.App.TOOLBOX_BUTTON_ID;
import static org.alienlabs.adaloveslace.util.FileUtil.PATH_SEPARATOR;

@ExtendWith(ApplicationExtension.class)
public class AppTestParent {
  public Stage primaryStage;
  public GeometryWindow geometryWindow;
  public ToolboxWindow toolboxWindow;
  public App app;

  // For tests:
  public static final double GRID_WIDTH           = 600d;
  public static final double GRID_HEIGHT          = 420d;
  public static final String BUILD_TOOL_OUTPUT_DIRECTORY  = "target/";

  public static final String CLASSPATH_RESOURCES_PATH_JPG = ".*test" + PATH_SEPARATOR + ".*.jpg";

  public static final String MANDALA              = "mandala_small";
  public static final String SNOWFLAKE            = "snowflake_small";
  public static final String MANDALA_BUTTON       = TOOLBOX_BUTTON_ID + "1";
  public static final String SNOWFLAKE_BUTTON     = TOOLBOX_BUTTON_ID + "2";

  public static final double  GRAY_PIXEL_X        = 98d;
  public static final double  GRAY_PIXEL_Y        = 67d;
  public static final Color   SNOWFLAKE_DOT_COLOR = Color.valueOf("0x9bf4ffff");

  public Color foundColorOnGrid;

  /**
   * Countdown latch
   */
  public CountDownLatch lock = new CountDownLatch(1);

  private static final Logger logger = LoggerFactory.getLogger(AppTestParent.class);

  /**
   * Init method called before each test
   *
   * @param primaryStage The injected window (stage)
   */
  @Start
  public void start(Stage primaryStage) {
    this.app = new App();
    this.app.setDiagram(new Diagram());

    this.geometryWindow = this.app.showGeometryWindow(this.app);
    this.app.getGeometryStage().setX(1000d);
    this.app.getGeometryStage().setY(50d);

    this.toolboxWindow = this.app.showToolboxWindow(this.app, this, CLASSPATH_RESOURCES_PATH_JPG);
    this.app.getToolboxStage().setX(750d);
    this.app.getToolboxStage().setY(50d);

    this.primaryStage = primaryStage;

    // The grid dots are twice as big as in the production code in order to facilitate tests
    this.app.showMainWindow(640d, 480d, GRID_WIDTH, GRID_HEIGHT, GRID_DOTS_RADIUS * 2d, this.primaryStage);
  }

  // This is in order to have time to copy the image to the canvas, otherwise the image is always white and we don't
  // have access to the UI thread for the copy without "Platform.runLater()"
  protected Color getColor(Point2D pointToMoveTo) {
    lock = new CountDownLatch(1);
    copyCanvas(pointToMoveTo);

    try {
      lock.await(5_000, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      logger.error("Interrupted!", e);
    }

    return this.foundColorOnGrid;
  }

  private void copyCanvas(Point2D pointToMoveTo) {
    Platform.runLater(() -> {
      WritableImage wi = new WritableImage(Double.valueOf(this.primaryStage.getX() + app.getRoot().getLayoutX() + GRID_WIDTH).intValue(),
        Double.valueOf(this.primaryStage.getY() + app.getRoot().getLayoutY() + GRID_HEIGHT).intValue());
      WritableImage snapshot = app.getRoot().snapshot(new SnapshotParameters(), wi);

      File output = new File(BUILD_TOOL_OUTPUT_DIRECTORY + "test_screen_capture.png");
      try {
        ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", output);
      } catch (IOException e) {
        logger.error("Problem writing root group image file!", e);
      }

      logger.info("Snapshot done!");

      PixelReader pr = snapshot.getPixelReader();
      this.foundColorOnGrid = pr.getColor(Double.valueOf(pointToMoveTo.getX()).intValue(), Double.valueOf(pointToMoveTo.getY()).intValue());
      logger.info("# argb: {}", this.foundColorOnGrid);

      lock.countDown();
    });
  }

  protected Point2D newPointOnGrid(double pixelX, double pixelY) {
    return new Point2D(this.primaryStage.getX() + pixelX,
      this.primaryStage.getY() + pixelY);
  }

}

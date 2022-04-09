package org.alienlabs.adaloveslace.test;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Diagram;
import org.alienlabs.adaloveslace.view.ToolboxWindow;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.alienlabs.adaloveslace.App.TOOLBOX_BUTTON_ID;
import static org.alienlabs.adaloveslace.util.FileUtil.PATH_SEPARATOR;

@ExtendWith(ApplicationExtension.class)
public class AppTestParent {

  public Stage primaryStage;
  public ToolboxWindow toolboxWindow;
  public App app;

  // For tests:
  public static final String CLASSPATH_RESOURCES_PATH_JPG = ".*test" + PATH_SEPARATOR + ".*.jpg";

  public static final String MANDALA              = "mandala_small";
  public static final String SNOWFLAKE            = "snowflake_small";
  public static final String MANDALA_BUTTON       = TOOLBOX_BUTTON_ID + "1";
  public static final String SNOWFLAKE_BUTTON     = TOOLBOX_BUTTON_ID + "2";

  public static final double  GRAY_PIXEL_X        = 78d;
  public static final long    GRAY_PIXEL_Y        = 92l;
  public static final Color   SNOWFLAKE_DOT_COLOR = Color.valueOf("0xcececeff");


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

    this.toolboxWindow = this.app.showToolboxWindow(this.app, this, CLASSPATH_RESOURCES_PATH_JPG);

    this.primaryStage = primaryStage;
    this.app.showMainWindow(640d, 480d, 600d, 420d, this.primaryStage);
  }

  // This is in order to have time to copy the image to the canvas, otherwise the image is always white and we don't
  // have access to the UI thread for the copy without "Platform.runLater()"
  public Color getColor(Canvas canvas, Point2D pointToMoveTo) {
    lock = new CountDownLatch(1);
    copyCanvas(canvas, pointToMoveTo);

    try {
      lock.await(5_000, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      logger.error("Interrupted!", e);
    }

    return this.foundColorOnGrid;
  }

  private void copyCanvas(Canvas canvas, Point2D pointToMoveTo) {
    Platform.runLater(() -> {
      Image snapshot = canvas.snapshot(null, null);
      logger.info("Snapshot done!");

      PixelReader pr = snapshot.getPixelReader();
      this.foundColorOnGrid = pr.getColor(Double.valueOf(pointToMoveTo.getX()).intValue(), Double.valueOf(pointToMoveTo.getY()).intValue());
      logger.info("# argb: {}", this.foundColorOnGrid);

      lock.countDown();
    });
  }

}

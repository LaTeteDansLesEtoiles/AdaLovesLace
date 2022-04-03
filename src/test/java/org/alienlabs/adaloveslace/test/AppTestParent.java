package org.alienlabs.adaloveslace.test;

import javafx.stage.Stage;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Diagram;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.File;

import static org.alienlabs.adaloveslace.App.TOOLBOX_BUTTON_ID;
import static org.alienlabs.adaloveslace.util.FileUtil.PATH_SEPARATOR;

@ExtendWith(ApplicationExtension.class)
public class AppTestParent {

  Stage primaryStage;
  App app;

  // For tests:
  public static final String CLASSPATH_RESOURCES_PATH_JPG = ".*test" + PATH_SEPARATOR + ".*.jpg";
  public static final String CLASSPATH_RESOURCES_PATH_PNG = ".*test" + PATH_SEPARATOR +  ".*.png";

  static final String MANDALA           = "mandala_small.jpg";
  static final String SNOWFLAKE         = "snowflake_small.jpg";
  static final String MANDALA_BUTTON    = TOOLBOX_BUTTON_ID + "1";
  static final String SNOWFLAKE_BUTTON  = TOOLBOX_BUTTON_ID + "2";

  /**
   * Init method called before each test
   *
   * @param primaryStage The injected window (stage)
   */
  @Start
  void start(Stage primaryStage) {
    this.app = new App();
    this.app.setDiagram(new Diagram());

    String ps = File.separator;
    this.app.showToolboxWindow(this.app, this, CLASSPATH_RESOURCES_PATH_JPG);

    this.primaryStage = primaryStage;
    this.app.showMainWindow(this, this.primaryStage, CLASSPATH_RESOURCES_PATH_PNG);
  }

}

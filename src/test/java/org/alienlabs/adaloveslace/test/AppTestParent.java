package org.alienlabs.adaloveslace.test;

import javafx.stage.Stage;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Diagram;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.File;

@ExtendWith(ApplicationExtension.class)
public class AppTestParent {

  Stage primaryStage;
  App app;

  static final String MANDALA    = "mandala_small.jpg";
  static final String SNOWFLAKE  = "snowflake_small.jpg";

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
    this.app.showToolboxWindow(this.app, this, ".*test" + ps + ".*.jpg");

    this.primaryStage = primaryStage;
    this.app.showMainWindow(this.primaryStage);
  }

}

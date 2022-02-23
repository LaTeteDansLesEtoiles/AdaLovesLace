package org.alienlabs.adaloveslace;

import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(ApplicationExtension.class)
public class AppTest {

  private Stage primaryStage;

  public AppTest() {
  }

  /**
   * Init method called before each test
   *
   * @param primaryStage The injected window (stage)
   */
  @Start
  private void start(Stage primaryStage) {
    App app = new App();
    this.primaryStage = primaryStage;
    app.showMainWindow(primaryStage);
  }

  @Test
  void testShowMainWindow() {
    assertEquals("Ada Loves Lace", primaryStage.getTitle());
  }
}

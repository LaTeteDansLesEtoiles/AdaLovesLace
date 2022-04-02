package org.alienlabs.adaloveslace.test;

import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.Start;

import static org.alienlabs.adaloveslace.App.MAIN_WINDOW_TITLE;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MainWindowTest extends AppTestParent {

  /**
   * Init method called before each test
   *
   * @param primaryStage The injected window (stage)
   */
  @Start
  void start(Stage primaryStage) {
    super.start(primaryStage);
  }

  @Test
  void testShowMainWindow() {
    assertEquals(MAIN_WINDOW_TITLE, super.primaryStage.getTitle());
  }

}

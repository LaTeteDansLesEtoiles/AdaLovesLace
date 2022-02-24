package org.alienlabs.adaloveslace.test;

import javafx.stage.Stage;
import org.alienlabs.adaloveslace.App;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.control.LabeledMatchers;

import static org.alienlabs.adaloveslace.App.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(ApplicationExtension.class)
public class AppTest {

  public static final String SNOWFLAKE = "snowflake_small.jpg";
  private Stage primaryStage;
  private App app;

  public AppTest() {
  }

  /**
   * Init method called before each test
   *
   * @param primaryStage The injected window (stage)
   */
  @Start
  private void start(Stage primaryStage) {
    app = new App();
    this.primaryStage = primaryStage;
    app.showMainWindow(this.primaryStage);
    app.showToolboxWindow();
  }

  @Test
  void testShowMainWindow() {
    assertEquals(MAIN_WINDOW_TITLE, primaryStage.getTitle());
  }

  @Test
  void testShowToolboxWindow() {
    assertEquals(TOOLBOX_TITLE, app.getToolboxStage().getTitle());
  }

  /**
   * Checks if 1st toolbox button contains image "snowflake_small.jpg"
   *
   * @param robot The injected FxRobot
   */
  @Test
  void should_contain_button_with_text(FxRobot robot) {
    FxAssert.verifyThat(TOOLBOX_BUTTON_ID + "1", LabeledMatchers.hasText(SNOWFLAKE));
  }

}

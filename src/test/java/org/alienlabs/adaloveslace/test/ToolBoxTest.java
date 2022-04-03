package org.alienlabs.adaloveslace.test;

import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.control.LabeledMatchers;

import static org.alienlabs.adaloveslace.App.TOOLBOX_TITLE;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ToolBoxTest extends AppTestParent {

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
  void testShowToolboxWindow() {
    assertEquals(TOOLBOX_TITLE, super.app.getToolboxStage().getTitle());
  }

  /**
   * Checks if 1st toolbox button contains image "mandala_small.jpg"
   *
   * @param robot The injected FxRobot
   */
  @Test
  void should_contain_1st_button_with_text(FxRobot robot) {
    FxAssert.verifyThat(MANDALA_BUTTON, LabeledMatchers.hasText(MANDALA));
  }

  /**
   * Checks if 2nd toolbox button contains image "snowflake_small.jpg"
   *
   * @param robot The injected FxRobot
   */
  @Test
  void should_contain_2nd_button_with_text(FxRobot robot) {
    FxAssert.verifyThat(SNOWFLAKE_BUTTON, LabeledMatchers.hasText(SNOWFLAKE));
  }

}

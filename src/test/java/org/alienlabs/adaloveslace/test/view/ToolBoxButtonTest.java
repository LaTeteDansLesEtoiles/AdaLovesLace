package org.alienlabs.adaloveslace.test.view;

import javafx.stage.Stage;
import org.alienlabs.adaloveslace.test.AppTestParent;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.LabeledMatchers;

import static org.alienlabs.adaloveslace.view.component.button.QuitButton.QUIT_APP;
import static org.alienlabs.adaloveslace.view.component.button.ShowHideGridButton.SHOW_HIDE_GRID_BUTTON_NAME;

class ToolBoxButtonTest extends AppTestParent {

  /**
   * Init method called before each test
   *
   * @param primaryStage The injected window (stage)
   */
  @Start
  public void start(Stage primaryStage) {
    super.start(primaryStage);
  }

  /**
   * Checks if 1st pattern toolbox button contains image "mandala_small.jpg"
   *
   * @param robot The injected FxRobot
   */
  @Test
  void should_contain_1st_pattern_button_with_text(FxRobot robot) {
    FxAssert.verifyThat(MANDALA_BUTTON, LabeledMatchers.hasText(MANDALA));
  }

  /**
   * Checks if 2nd pattern toolbox button contains image "snowflake_small.jpg"
   *
   * @param robot The injected FxRobot
   */
  @Test
  void should_contain_2nd_pattern_button_with_text(FxRobot robot) {
    FxAssert.verifyThat(SNOWFLAKE_BUTTON, LabeledMatchers.hasText(SNOWFLAKE));
  }

  /**
   * Checks if "show / hide grid" toolbox button contains right text
   *
   * @param robot The injected FxRobot
   */
  @Test
  void should_contain_show_hide_grid_button_with_text(FxRobot robot) {
    FxAssert.verifyThat(SHOW_HIDE_GRID_BUTTON_NAME, NodeMatchers.isVisible());
  }

  /**
   * Checks if "Quit" toolbox button contains right text
   *
   * @param robot The injected FxRobot
   */
  @Test
  void should_contain_quit_button_with_text(FxRobot robot) {
    FxAssert.verifyThat(QUIT_APP, NodeMatchers.isVisible());
  }

}

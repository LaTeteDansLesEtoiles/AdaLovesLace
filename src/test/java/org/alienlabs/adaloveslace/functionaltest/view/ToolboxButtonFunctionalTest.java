package org.alienlabs.adaloveslace.functionaltest.view;

import javafx.stage.Stage;
import org.alienlabs.adaloveslace.functionaltest.AppFunctionalTestParent;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.base.NodeMatchers;

import static org.alienlabs.adaloveslace.App.resourceBundle;
import static org.alienlabs.adaloveslace.view.component.button.toolboxwindow.ShowHideGridButton.SHOW_HIDE_GRID_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.window.MainWindow.*;

class ToolboxButtonFunctionalTest extends AppFunctionalTestParent {

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
   * Checks if 1st pattern toolbox button contains image "snowflake_small.jpg"
   *
   */
  @Test
  void should_contain_snowflake_pattern_button_with_text() {
    FxAssert.verifyThat(SNOWFLAKE, NodeMatchers.isVisible());
  }

  /**
   * Checks if "show / hide grid" toolbox button contains right text
   *
   */
  @Test
  void should_contain_show_hide_grid_button_with_text() {
    FxAssert.verifyThat(resourceBundle.getString(SHOW_HIDE_GRID_BUTTON_NAME), NodeMatchers.isVisible());
  }

  /**
   * Checks if "Quit" toolbox button contains right text
   *
   */
  @Test
  void should_contain_quit_button_with_text() {
    FxAssert.verifyThat(resourceBundle.getString(QUIT_APP), NodeMatchers.isVisible());
  }

  /**
   * Checks if "Undo knot" toolbox button contains right text
   *
   */
  @Test
  void should_contain_undo_knot_button_with_text() {
    FxAssert.verifyThat(resourceBundle.getString(UNDO_KNOT), NodeMatchers.isVisible());
  }

  /**
   * Checks if "Redo knot" toolbox button contains right text
   *
   */
  @Test
  void should_contain_redo_knot_button_with_text() {
    FxAssert.verifyThat(resourceBundle.getString(REDO_KNOT), NodeMatchers.isVisible());
  }

  /**
   * Checks if "Reset diagram" toolbox button contains right text
   *
   */
  @Test
  void should_contain_reset_diagram_button_with_text() {
    FxAssert.verifyThat(resourceBundle.getString(RESET_DIAGRAM), NodeMatchers.isVisible());
  }

}

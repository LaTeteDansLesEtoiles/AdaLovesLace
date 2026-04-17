package org.alienlabs.adaloveslace.functionaltest.view.component.button.toolboxwindow.grid;

import javafx.stage.Stage;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.enumeration.MouseMode;
import org.alienlabs.adaloveslace.domain.enumeration.PatternOrTextMode;
import org.alienlabs.adaloveslace.functionaltest.AppFunctionalTestParent;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid.CreatePatternButton;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Exercises toolbox grid controls that drive diagram mode / pattern-or-text state through the real UI.
 */
@Tag("functional")
class ToolboxGridModeButtonsFunctionalTest extends AppFunctionalTestParent {

  @Override
  @Start
  public void start(Stage primaryStage) {
    super.start(primaryStage);
  }

  @Test
  void create_pattern_button_sets_create_pattern_mode(FxRobot robot) {
    robot.clickOn(App.resourceBundle.getString(CreatePatternButton.CREATE_PATTERN_BUTTON));
    WaitForAsyncUtils.waitForFxEvents();

    robot.interact(() ->
        assertEquals(MouseMode.CREATE_PATTERN, app.getOptionalDotGrid().getDiagram().getCurrentMode()));
  }

  @Test
  void text_button_selects_text_mode(FxRobot robot) {
    robot.clickOn(App.resourceBundle.getString(App.TEXT_BUTTON_NAME));
    WaitForAsyncUtils.waitForFxEvents();

    robot.interact(() ->
        assertEquals(PatternOrTextMode.TEXT, app.getOptionalDotGrid().getCurrentPatternOrTextModeProperty().get()));
  }
}

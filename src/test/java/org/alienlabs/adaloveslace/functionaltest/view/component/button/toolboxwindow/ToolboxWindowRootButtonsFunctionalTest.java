package org.alienlabs.adaloveslace.functionaltest.view.component.button.toolboxwindow;

import javafx.stage.Stage;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.functionaltest.AppFunctionalTestParent;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.ShareButton;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.util.WaitForAsyncUtils;

/**
 * Root-level toolbox controls in {@link org.alienlabs.adaloveslace.view.component.button.toolboxwindow}
 * (not {@code grid} or {@code file} subpackages).
 */
@Tag("functional")
class ToolboxWindowRootButtonsFunctionalTest extends AppFunctionalTestParent {

  @Override
  @Start
  public void start(Stage primaryStage) {
    super.start(primaryStage);
  }

  @Test
  void share_diagram_toolbox_button_is_visible() {
    FxAssert.verifyThat(App.resourceBundle.getString(ShareButton.SHARE_BUTTON_NAME), NodeMatchers.isVisible());
  }

  @Test
  void share_toolbox_click_opens_dialog_then_cancel_closes(FxRobot robot) {
    robot.clickOn(App.resourceBundle.getString(ShareButton.SHARE_BUTTON_NAME));
    WaitForAsyncUtils.waitForFxEvents();
    robot.clickOn("Cancel");
    WaitForAsyncUtils.waitForFxEvents();
  }
}

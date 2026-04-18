package org.alienlabs.adaloveslace.functionaltest.view.component.button.toolboxwindow;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.function.Predicate;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.functionaltest.AppFunctionalTestParent;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.ShareButton;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.base.NodeMatchers;
import org.alienlabs.adaloveslace.testutil.FxAwait;

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
  void share_toolbox_click_opens_dialog_then_cancel_closes(FxRobot robot) throws Exception {
    // Click the concrete node on the toolbox stage (text lookup can target the wrong window when several are shown).
    Predicate<Node> isShare = n -> n instanceof ShareButton;
    ShareButton share = robot.from(app.getToolboxStage().getScene().getRoot())
        .lookup(isShare)
        .queryAs(ShareButton.class);
    robot.clickOn(share);
    FxAwait.syncFx();
    Window dialogWindow = null;
    for (Window w : robot.listWindows()) {
      if (w.isShowing() && w != app.getPrimaryStage() && w != app.getToolboxStage()) {
        dialogWindow = w;
        break;
      }
    }
    assertNotNull(dialogWindow, "Share dialog window should open");
    robot.targetWindow(dialogWindow);
    // Under GTK/Xvfb the CANCEL button label is not always the literal "Cancel" for TestFX text lookup.
    robot.press(KeyCode.ESCAPE).release(KeyCode.ESCAPE);
    FxAwait.syncFx();
  }
}

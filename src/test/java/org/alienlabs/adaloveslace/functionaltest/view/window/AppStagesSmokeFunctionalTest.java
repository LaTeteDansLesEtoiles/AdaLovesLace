package org.alienlabs.adaloveslace.functionaltest.view.window;

import javafx.stage.Stage;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.functionaltest.AppFunctionalTestParent;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.Start;

import static org.alienlabs.adaloveslace.App.resourceBundle;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("functional")
class AppStagesSmokeFunctionalTest extends AppFunctionalTestParent {

  @Override
  @Start
  public void start(Stage primaryStage) {
    super.start(primaryStage);
  }

  @Test
  void primary_and_toolbox_stages_show_expected_titles() {
    assertTrue(app.getPrimaryStage().isShowing());
    assertTrue(app.getToolboxStage().isShowing());
    assertEquals(resourceBundle.getString(App.MAIN_WINDOW_TITLE), app.getPrimaryStage().getTitle());
    assertEquals(resourceBundle.getString(App.TOOLBOX_TITLE), app.getToolboxStage().getTitle());
  }
}

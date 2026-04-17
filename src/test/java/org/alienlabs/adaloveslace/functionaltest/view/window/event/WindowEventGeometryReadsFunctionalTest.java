package org.alienlabs.adaloveslace.functionaltest.view.window.event;

import javafx.stage.Stage;
import org.alienlabs.adaloveslace.functionaltest.AppFunctionalTestParent;
import org.alienlabs.adaloveslace.view.window.event.WindowRepositionEvents;
import org.alienlabs.adaloveslace.view.window.event.WindowResizeEvents;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Smoke-checks window geometry helpers on a fully bootstrapped app (real stages and prefs wiring).
 */
@Tag("functional")
class WindowEventGeometryReadsFunctionalTest extends AppFunctionalTestParent {

  @Override
  @Start
  public void start(Stage primaryStage) {
    super.start(primaryStage);
  }

  @Test
  void resize_and_reposition_helpers_report_sane_geometry(FxRobot robot) {
    WaitForAsyncUtils.waitForFxEvents();
    robot.interact(() -> {
      WindowResizeEvents resize = app.getResizes();
      assertTrue(resize.getMainWindowWidth() > 10d);
      assertTrue(resize.getMainWindowHeight() > 10d);
      assertTrue(resize.getGridWidth() > 10d);
      assertTrue(resize.getGridHeight() > 10d);
      assertTrue(resize.getToolboxWindowWidth() > 10d);

      WindowRepositionEvents repos = app.getWindowRepositionEvents();
      assertTrue(Double.isFinite(repos.getMainWindowX()));
      assertTrue(Double.isFinite(repos.getMainWindowY()));
      assertTrue(Double.isFinite(repos.getToolboxWindowX()));
      assertTrue(Double.isFinite(repos.getToolboxWindowY()));
    });
  }

}

package org.alienlabs.adaloveslace.functionaltest.util;

import javafx.stage.Stage;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.util.SplashStartupTasks;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Ensures bundled resources used during splash still resolve when read from the JavaFX thread
 * (classpath + toolkit initialized the way TestFX does).
 */
@Tag("functional")
@ExtendWith(ApplicationExtension.class)
class ClasspathBundledResourcesFunctionalTest {

  @Start
  void start(Stage stage) {
    stage.setWidth(10);
    stage.setHeight(10);
    stage.show();
  }

  @Test
  void splash_bundled_css_and_font_readable_on_fx_thread(FxRobot robot) {
    robot.interact(() -> assertDoesNotThrow(() -> SplashStartupTasks.verifyBundledResources(App.class)));
  }
}

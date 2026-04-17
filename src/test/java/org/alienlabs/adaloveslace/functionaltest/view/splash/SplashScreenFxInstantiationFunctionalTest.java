package org.alienlabs.adaloveslace.functionaltest.view.splash;

import javafx.stage.Stage;
import org.alienlabs.adaloveslace.view.splash.SplashScreen;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Loads {@link SplashScreen} on the JavaFX thread to cover splash package initialization in CI.
 */
@Tag("functional")
@ExtendWith(ApplicationExtension.class)
class SplashScreenFxInstantiationFunctionalTest {

  @Start
  void start(Stage stage) {
    stage.setWidth(20);
    stage.setHeight(20);
    stage.show();
  }

  @Test
  void splash_screen_can_be_constructed_on_fx_thread(FxRobot robot) {
    robot.interact(() -> assertNotNull(new SplashScreen()));
  }
}

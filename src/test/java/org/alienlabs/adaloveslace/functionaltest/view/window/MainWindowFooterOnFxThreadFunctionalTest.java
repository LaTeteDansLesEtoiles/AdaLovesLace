package org.alienlabs.adaloveslace.functionaltest.view.window;

import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.view.window.MainWindow;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Builds {@link MainWindow#createFooter} on the FX thread (closer to real menu/footer wiring).
 */
@Tag("functional")
@ExtendWith(ApplicationExtension.class)
class MainWindowFooterOnFxThreadFunctionalTest {

  @Start
  void start(Stage stage) {
    stage.setWidth(40);
    stage.setHeight(40);
    stage.show();
  }

  @Test
  void create_footer_on_fx_thread_contains_runtime_strings(FxRobot robot) {
    robot.interact(() -> {
      TilePane footer = new MainWindow().createFooter("25.0.2", "25");
      assertEquals(100d, footer.getPrefHeight(), 1e-9);
      assertFalse(footer.getChildren().isEmpty());
      long labelCount = footer.getChildren().stream().filter(n -> n instanceof Label).count();
      assertTrue(labelCount >= 3);
      assertTrue(footer.getChildren().stream().anyMatch(n ->
          n instanceof Label lbl && lbl.getText().contains("JavaFX")));
    });
  }
}

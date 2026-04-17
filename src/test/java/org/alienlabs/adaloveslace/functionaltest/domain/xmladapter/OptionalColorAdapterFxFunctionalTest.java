package org.alienlabs.adaloveslace.functionaltest.domain.xmladapter;

import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.domain.xmladapter.OptionalColorAdapter;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Ensures {@link OptionalColorAdapter} still round-trips JavaFX {@link Color} when invoked on the FX thread.
 */
@Tag("functional")
@ExtendWith(ApplicationExtension.class)
class OptionalColorAdapterFxFunctionalTest {

  @Start
  void start(Stage stage) {
    stage.setWidth(10);
    stage.setHeight(10);
    stage.show();
  }

  @Test
  void color_adapter_round_trips_on_fx_thread(FxRobot robot) {
    robot.interact(() -> {
      OptionalColorAdapter adapter = new OptionalColorAdapter();
      Optional<Color> original = Optional.of(Color.color(0.2, 0.4, 0.6, 0.8));
      String wire = adapter.marshal(original);
      assertEquals(original, adapter.unmarshal(wire));
    });
  }
}

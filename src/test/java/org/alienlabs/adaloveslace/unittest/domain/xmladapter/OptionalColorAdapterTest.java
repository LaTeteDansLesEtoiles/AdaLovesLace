package org.alienlabs.adaloveslace.unittest.domain.xmladapter;

import javafx.scene.paint.Color;
import org.alienlabs.adaloveslace.domain.xmladapter.OptionalColorAdapter;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("unit")
class OptionalColorAdapterTest {

  private final OptionalColorAdapter adapter = new OptionalColorAdapter();

  @Test
  void unmarshal_null_or_blank_is_empty() {
    assertTrue(adapter.unmarshal(null).isEmpty());
    assertTrue(adapter.unmarshal("").isEmpty());
  }

  @ParameterizedTest
  @ValueSource(strings = {"#FF0000FF", "#00ff00ff", "rgb(10,20,30)"})
  void unmarshal_then_marshal_round_trip(String web) {
    Optional<Color> c = adapter.unmarshal(web);
    assertTrue(c.isPresent());
    Optional<Color> again = adapter.unmarshal(adapter.marshal(c));
    assertEquals(c.get(), again.get());
  }

  @Test
  void marshal_empty_optional_is_empty_string() {
    assertEquals("", adapter.marshal(Optional.empty()));
  }
}

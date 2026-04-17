package org.alienlabs.adaloveslace.unittest.domain.xmladapter;

import org.alienlabs.adaloveslace.domain.xmladapter.OptionalStringAdapter;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("unit")
class OptionalStringAdapterTest {

  private final OptionalStringAdapter adapter = new OptionalStringAdapter();

  @Test
  void unmarshal_null_is_empty() {
    assertTrue(adapter.unmarshal(null).isEmpty());
  }

  @Test
  void unmarshal_empty_string_is_empty() {
    assertTrue(adapter.unmarshal("").isEmpty());
  }

  @Test
  void unmarshal_non_empty_wraps_value() {
    assertEquals(Optional.of("lace"), adapter.unmarshal("lace"));
  }

  @Test
  void marshal_empty_returns_null() {
    assertNull(adapter.marshal(Optional.empty()));
  }

  @Test
  void marshal_present_returns_value() {
    assertEquals("x", adapter.marshal(Optional.of("x")));
  }
}

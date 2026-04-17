package org.alienlabs.adaloveslace.unittest.domain.xmladapter;

import org.alienlabs.adaloveslace.domain.Pattern;
import org.alienlabs.adaloveslace.domain.xmladapter.OptionalPatternAdapter;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("unit")
class OptionalPatternAdapterTest {

  private final OptionalPatternAdapter adapter = new OptionalPatternAdapter();

  @Test
  void unmarshal_null_is_empty() {
    assertTrue(adapter.unmarshal(null).isEmpty());
  }

  @Test
  void unmarshal_value_is_present() {
    Pattern p = new Pattern();
    p.setFilename("k.jpg");
    p.setAbsoluteFilename("/tmp/k.jpg");
    assertEquals(Optional.of(p), adapter.unmarshal(p));
  }

  @Test
  void marshal_empty_returns_null() {
    assertNull(adapter.marshal(Optional.empty()));
  }

  @Test
  void marshal_present_returns_pattern() {
    Pattern p = new Pattern();
    p.setFilename("p.png");
    p.setAbsoluteFilename("/x/p.png");
    assertEquals(p, adapter.marshal(Optional.of(p)));
  }
}

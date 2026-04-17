package org.alienlabs.adaloveslace.unittest.domain.enumeration;

import org.alienlabs.adaloveslace.domain.enumeration.SubTechnique;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("unit")
class SubTechniqueEnumerationTest {

  @ParameterizedTest
  @EnumSource(SubTechnique.class)
  void each_constant_has_non_blank_message_key(SubTechnique subTechnique) {
    assertNotNull(subTechnique.getMessageKey());
    assertFalse(subTechnique.getMessageKey().isBlank());
  }

  @Test
  void message_keys_are_unique_across_constants() {
    Set<String> keys = new HashSet<>();
    for (SubTechnique s : SubTechnique.values()) {
      keys.add(s.getMessageKey());
    }
    assertEquals(SubTechnique.values().length, keys.size());
  }

  @Test
  void value_of_round_trips_name() {
    Arrays.stream(SubTechnique.values()).forEach(s ->
        assertEquals(s, SubTechnique.valueOf(s.name())));
  }
}

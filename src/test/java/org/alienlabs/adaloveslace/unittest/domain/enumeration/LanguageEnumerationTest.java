package org.alienlabs.adaloveslace.unittest.domain.enumeration;

import org.alienlabs.adaloveslace.domain.enumeration.Language;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("unit")
class LanguageEnumerationTest {

  @ParameterizedTest
  @EnumSource(Language.class)
  void each_language_has_non_blank_display_value(Language language) {
    assertNotNull(language.getValue());
    assertFalse(language.getValue().isBlank());
  }

  @Test
  void display_values_are_distinct() {
    Set<String> values = new HashSet<>();
    for (Language l : Language.values()) {
      values.add(l.getValue());
    }
    assertEquals(Language.values().length, values.size());
  }
}

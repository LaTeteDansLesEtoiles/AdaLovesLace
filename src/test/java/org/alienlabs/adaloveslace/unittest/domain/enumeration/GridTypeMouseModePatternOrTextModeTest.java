package org.alienlabs.adaloveslace.unittest.domain.enumeration;

import org.alienlabs.adaloveslace.domain.enumeration.GridType;
import org.alienlabs.adaloveslace.domain.enumeration.MouseMode;
import org.alienlabs.adaloveslace.domain.enumeration.PatternOrTextMode;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("unit")
class GridTypeMouseModePatternOrTextModeTest {

  @ParameterizedTest
  @EnumSource(GridType.class)
  void grid_type_value_of_matches_name(GridType gridType) {
    assertEquals(gridType, GridType.valueOf(gridType.name()));
  }

  @ParameterizedTest
  @EnumSource(MouseMode.class)
  void mouse_mode_value_of_matches_name(MouseMode mode) {
    assertEquals(mode, MouseMode.valueOf(mode.name()));
  }

  @ParameterizedTest
  @EnumSource(PatternOrTextMode.class)
  void pattern_or_text_mode_value_of_matches_name(PatternOrTextMode mode) {
    assertEquals(mode, PatternOrTextMode.valueOf(mode.name()));
  }
}

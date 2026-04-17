package org.alienlabs.adaloveslace.unittest.domain.enumeration;

import org.alienlabs.adaloveslace.domain.enumeration.SubTechnique;
import org.alienlabs.adaloveslace.domain.enumeration.Technique;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("unit")
class TechniqueEnumerationTest {

  @ParameterizedTest
  @EnumSource(Technique.class)
  void each_technique_has_non_blank_message_key(Technique technique) {
    assertNotNull(technique.getMessageKey());
    assertFalse(technique.getMessageKey().isBlank());
  }

  @ParameterizedTest
  @EnumSource(Technique.class)
  void each_technique_exposes_at_least_one_sub_technique(Technique technique) {
    assertFalse(technique.getSubTechniques().isEmpty());
  }

  @Test
  void sub_techniques_listed_under_techniques_cover_all_except_granny_square() {
    Set<SubTechnique> covered = EnumSet.noneOf(SubTechnique.class);
    for (Technique t : Technique.values()) {
      covered.addAll(t.getSubTechniques());
    }
    EnumSet<SubTechnique> all = EnumSet.allOf(SubTechnique.class);
    all.removeAll(covered);
    assertEquals(EnumSet.of(SubTechnique.GRANNY_SQUARE), all,
        "If this fails, update Technique/SubTechnique wiring or adjust this expectation.");
  }

  @Test
  void no_sub_technique_is_assigned_to_more_than_one_technique() {
    Set<SubTechnique> seen = new HashSet<>();
    for (Technique t : Technique.values()) {
      for (SubTechnique s : t.getSubTechniques()) {
        assertTrue(seen.add(s), "Duplicate assignment for " + s);
      }
    }
  }
}

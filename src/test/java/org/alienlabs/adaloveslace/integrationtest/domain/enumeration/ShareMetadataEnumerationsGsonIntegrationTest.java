package org.alienlabs.adaloveslace.integrationtest.domain.enumeration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.alienlabs.adaloveslace.domain.dto.DiagramDTO;
import org.alienlabs.adaloveslace.domain.enumeration.Language;
import org.alienlabs.adaloveslace.domain.enumeration.SubTechnique;
import org.alienlabs.adaloveslace.domain.enumeration.Technique;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies network {@link DiagramDTO} metadata enums round-trip through Gson (same annotations as production API payloads).
 */
@Tag("integration")
class ShareMetadataEnumerationsGsonIntegrationTest {

  private static Gson gson() {
    return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
  }

  @ParameterizedTest
  @EnumSource(Technique.class)
  void gson_round_trip_preserves_technique_sub_technique_and_language(Technique technique) {
    SubTechnique sub = technique.getSubTechniques().get(0);
    Language language = Language.NON_RELEVANT;

    DiagramDTO original = new DiagramDTO()
        .uuid(UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"))
        .name("gson-enum")
        .technique(technique)
        .subTechnique(sub)
        .language(language);

    String json = gson().toJson(original);
    DiagramDTO copy = gson().fromJson(json, DiagramDTO.class);

    assertEquals(technique, copy.getTechnique());
    assertEquals(sub, copy.getSubTechnique());
    assertEquals(language, copy.getLanguage());
  }

  @Test
  void gson_serializes_enum_constants_as_names_in_json() {
    DiagramDTO dto = new DiagramDTO()
        .uuid(UUID.fromString("11111111-2222-3333-4444-555555555555"))
        .technique(Technique.CROCHET)
        .subTechnique(SubTechnique.TAPESTRY)
        .language(Language.FRENCH);

    String json = gson().toJson(dto);
    assertTrue(json.contains("\"technique\":\"CROCHET\""));
    assertTrue(json.contains("\"subTechnique\":\"TAPESTRY\""));
    assertTrue(json.contains("\"language\":\"FRENCH\""));
  }
}

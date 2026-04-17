package org.alienlabs.adaloveslace.unittest.domain.dto;

import org.alienlabs.adaloveslace.domain.dto.DiagramDTO;
import org.alienlabs.adaloveslace.domain.enumeration.Language;
import org.alienlabs.adaloveslace.domain.enumeration.SubTechnique;
import org.alienlabs.adaloveslace.domain.enumeration.Technique;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("unit")
class DiagramDTOFluentApiUnitTest {

  @Test
  void fluent_chain_populates_all_network_payload_fields() {
    UUID id = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
    UUID clientId = UUID.fromString("11111111-2222-3333-4444-555555555555");
    UUID secret = UUID.fromString("66666666-7777-8888-9999-aaaaaaaaaaaa");
    List<String> previews = List.of("a.png", "b.png");

    DiagramDTO dto = new DiagramDTO()
        .uuid(id)
        .name("diagram-name")
        .showcase("show")
        .previewContentType("image/png")
        .technique(Technique.LACE)
        .subTechnique(SubTechnique.TATTING_LACE)
        .language(Language.FRENCH)
        .diagram("lace-bytes")
        .diagramContentType("application/lace")
        .username("ada")
        .clientId(clientId)
        .clientSecret(secret)
        .diagramPreview("preview-payload")
        .previews(previews);

    assertEquals(id, dto.getUuid());
    assertEquals("diagram-name", dto.getName());
    assertEquals("show", dto.getShowcase());
    assertEquals("image/png", dto.getPreviewContentType());
    assertEquals(Technique.LACE, dto.getTechnique());
    assertEquals(SubTechnique.TATTING_LACE, dto.getSubTechnique());
    assertEquals(Language.FRENCH, dto.getLanguage());
    assertEquals("lace-bytes", dto.getDiagram());
    assertEquals("application/lace", dto.getDiagramContentType());
    assertEquals("ada", dto.getUsername());
    assertEquals(clientId, dto.getClientId());
    assertEquals(secret, dto.getClientSecret());
    assertEquals("preview-payload", dto.getDiagramPreview());
    assertEquals(previews, dto.getPreviews());
  }

  @Test
  void equals_and_hashCode_use_uuid_only() {
    UUID id = UUID.fromString("22222222-3333-4444-5555-666666666666");
    DiagramDTO a = new DiagramDTO().uuid(id).name("one");
    DiagramDTO b = new DiagramDTO().uuid(id).name("other");
    assertEquals(a, b);
    assertEquals(a.hashCode(), b.hashCode());
    assertNotEquals(a, new DiagramDTO().uuid(UUID.randomUUID()));
  }

  @Test
  void toString_contains_core_fields() {
    DiagramDTO dto = new DiagramDTO()
        .uuid(UUID.fromString("33333333-4444-5555-6666-777777777777"))
        .name("n")
        .technique(Technique.CROCHET)
        .subTechnique(SubTechnique.CROCHET)
        .language(Language.English)
        .diagramContentType("application/lace")
        .previewContentType("image/jpeg");
    String s = dto.toString();
    assertTrue(s.contains("n"));
    assertTrue(s.contains("CROCHET"));
    assertTrue(s.contains("application/lace"));
  }
}

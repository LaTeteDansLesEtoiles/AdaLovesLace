package org.alienlabs.adaloveslace.unittest.domain;

import org.alienlabs.adaloveslace.domain.Picture;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Exercises the {@link Picture} serialisable value object. Picture is a plain JAXB/GSON bean with both
 * setter/getter pairs and fluent helpers — nothing here depends on JavaFX, so a pure unit test pins behaviour
 * and brings the class over the jacoco per-class minimum without paying the cost of an FX runtime.
 */
@Tag("unit")
class PictureUnitTest {

  @Test
  void defaultInstanceHasNullFields() {
    Picture picture = new Picture();

    assertNull(picture.getPicture());
    assertNull(picture.getPictureContentType());
    assertNull(picture.getShowcase());
    assertNull(picture.getPreview());
  }

  @Test
  void fluentSettersReturnSameInstanceAndUpdateAllFields() {
    Picture picture = new Picture();

    Picture chained = picture
        .picture("snowflake.png")
        .pictureContentType("image/png")
        .showcase("showcase-value")
        .preview(42L);

    assertSame(picture, chained, "Fluent setters must return the same instance so builders chain cleanly");
    assertEquals("snowflake.png", picture.getPicture());
    assertEquals("image/png", picture.getPictureContentType());
    assertEquals("showcase-value", picture.getShowcase());
    assertEquals(42L, picture.getPreview());
  }

  @Test
  void imperativeSettersOverwriteFluentValues() {
    Picture picture = new Picture().picture("first.png").preview(1L);

    picture.setPicture("second.png");
    picture.setPictureContentType("image/jpeg");
    picture.setShowcase("new-showcase");
    picture.setPreview(99L);

    assertEquals("second.png", picture.getPicture());
    assertEquals("image/jpeg", picture.getPictureContentType());
    assertEquals("new-showcase", picture.getShowcase());
    assertEquals(99L, picture.getPreview());
  }

  @Test
  void toStringMentionsPublishedMetadataButNotRawBinaryField() {
    Picture picture = new Picture()
        .picture("payload.png")
        .pictureContentType("image/png")
        .showcase("display-title");

    String rendered = picture.toString();

    assertNotNull(rendered);
    assertTrue(rendered.contains("image/png"),
        "toString must expose the content type so logs carry the MIME of the previewed image");
    assertTrue(rendered.contains("display-title"),
        "toString must expose the showcase caption that users recognise in saved diagrams");
  }
}

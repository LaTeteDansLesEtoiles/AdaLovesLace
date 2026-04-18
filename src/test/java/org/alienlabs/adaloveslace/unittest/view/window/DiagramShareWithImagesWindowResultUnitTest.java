package org.alienlabs.adaloveslace.unittest.view.window;

import javafx.scene.image.Image;
import org.alienlabs.adaloveslace.domain.enumeration.SubTechnique;
import org.alienlabs.adaloveslace.domain.enumeration.Technique;
import org.alienlabs.adaloveslace.view.window.DiagramShareWithImagesWindow;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit-level coverage for {@link DiagramShareWithImagesWindow.Result}. The nested class is a public record-like
 * payload returned by the share dialog; its fields are consumed downstream by {@link org.alienlabs.adaloveslace.util.ShareUtil}
 * and must faithfully carry every field the dialog collected from the user. Testing it in isolation avoids the
 * TestFX round-trip and keeps the class above the jacoco per-class minimum even when the share dialog is not
 * opened interactively.
 */
@Tag("unit")
class DiagramShareWithImagesWindowResultUnitTest {

  @Test
  void constructorStoresEveryFieldExactlyAsProvided() {
    List<Image> images = Collections.emptyList();

    DiagramShareWithImagesWindow.Result result = new DiagramShareWithImagesWindow.Result(
        "ada",
        "bobbin-lace",
        "A swatch for testing",
        "client-id-1234",
        "client-secret-5678",
        Technique.LACE,
        SubTechnique.TATTING_LACE,
        images);

    assertEquals("ada", result.username);
    assertEquals("bobbin-lace", result.name);
    assertEquals("A swatch for testing", result.description);
    assertEquals("client-id-1234", result.clientId);
    assertEquals("client-secret-5678", result.clientSecret);
    assertSame(Technique.LACE, result.technique);
    assertSame(SubTechnique.TATTING_LACE, result.subTechnique);
    assertSame(images, result.images, "Images list must be stored by reference; the caller owns the collection");
  }

  @Test
  void acceptsNullOptionalFieldsForEmptyDialogInput() {
    DiagramShareWithImagesWindow.Result result = new DiagramShareWithImagesWindow.Result(
        null, null, null, null, null, null, null, null);

    assertNull(result.username);
    assertNull(result.name);
    assertNull(result.description);
    assertNull(result.clientId);
    assertNull(result.clientSecret);
    assertNull(result.technique);
    assertNull(result.subTechnique);
    assertNull(result.images);
  }

  @Test
  void imagesListIsReturnedAsIsForPluralSharePayloads() {
    List<Image> images = Collections.emptyList();
    DiagramShareWithImagesWindow.Result result = new DiagramShareWithImagesWindow.Result(
        "ada", "n", "d", "c", "s", Technique.LACE, SubTechnique.TATTING_LACE, images);

    assertNotNull(result.images);
    assertTrue(result.images.isEmpty(),
        "Empty images collection propagates into the outbound payload without mutation");
  }
}

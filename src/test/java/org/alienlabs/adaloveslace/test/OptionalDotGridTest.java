package org.alienlabs.adaloveslace.test;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.alienlabs.adaloveslace.business.model.Diagram;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.business.model.Pattern;
import org.alienlabs.adaloveslace.util.FileUtil;
import org.alienlabs.adaloveslace.view.component.OptionalDotGrid;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.FileInputStream;
import java.io.IOException;

import static org.alienlabs.adaloveslace.functionaltest.AppFunctionalTestParent.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OptionalDotGridTest {

  private ImageView imageView;

  @ParameterizedTest(name = "Check optional dot grid #{index} - Fixed zoom")
  @CsvSource({"1,0", "2.05,7", "4,20"})
  void zoom_knot_factor(String expectedZoomFactor, String actualZoomFactor) {
    // Given
    Knot knot = new Knot(0d, 0d, buildPattern(), imageView);
    knot.setZoomFactor(Integer.parseInt(actualZoomFactor));

    // When
    double zoomFactor = new OptionalDotGrid(new Diagram(), null).zoomAndFlipKnot(knot, null);

    // Then
    assertEquals(Double.valueOf(expectedZoomFactor), zoomFactor);
  }

  @ParameterizedTest(name = "Check optional dot grid #{index} - Zoom in known range")
  @CsvSource({"1, 1, 1.5", "-1, 0.6, 1", "-5, 0.2, 0.3", "-15, 0.01, 0.1"})
  void zoom_knot_with_zoom_factor(String initialZoomFactor, String minZoomFactor, String maxZoomFactor) {
    // Given
    Knot knot = new Knot(0d, 0d, buildPattern(), imageView);
    knot.setZoomFactor(Integer.parseInt(initialZoomFactor));

    // When
    double zoomFactor = new OptionalDotGrid(new Diagram(), null).zoomAndFlipKnot(knot, null);

    // Then
    assertTrue(zoomFactor > Double.parseDouble(minZoomFactor));
    assertTrue(zoomFactor < Double.parseDouble(maxZoomFactor));
  }

  private Pattern buildPattern() {
    Pattern pattern = new Pattern();

    pattern.setAbsoluteFilename(CLASSPATH_RESOURCES_PATH + SNOWFLAKE_IMAGE);

    try (FileInputStream fis = new FileInputStream(new FileUtil().getResources(this, java.util.regex.Pattern.compile(CLASSPATH_RESOURCES_PATH_JPG)).get(0))) {
      Image image = new Image(fis);
      imageView = new ImageView(image);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return pattern;
  }

}

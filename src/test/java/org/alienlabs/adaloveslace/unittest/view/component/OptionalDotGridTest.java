package org.alienlabs.adaloveslace.unittest.view.component;

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

class OptionalDotGridTest {

  private ImageView imageView;

  @ParameterizedTest(name = "Check optional dot grid #{index} - Fixed value zoom")
  @CsvSource({"1,0", "1.7,7", "3,20", "1.3,3", "1.0,0", "1.1,1", "0.9,-1", "1.2,2", "0.8,-2", "1.3,3", "0.7,-3"})
  void zoom_knot_factor(String expectedZoomFactor, String settedZoomFactor) {
    // Given
    Knot knot = new Knot(0d, 0d, buildPattern(), imageView);
    knot.setZoomFactor(Integer.parseInt(settedZoomFactor));

    // When
    double actualZoomFactor = new OptionalDotGrid(null, new Diagram(null), null).zoomAndFlipKnot(knot);

    // Then
    assertEquals(Double.valueOf(expectedZoomFactor), actualZoomFactor);
  }

  private Pattern buildPattern() {
    Pattern pattern = new Pattern();

    pattern.setAbsoluteFilename(CLASSPATH_RESOURCES_PATH + COLOR_WHEEL_IMAGE);

    try (FileInputStream fis = new FileInputStream(new FileUtil().getResources(this, java.util.regex.Pattern.compile(CLASSPATH_RESOURCES_PATH_JPG)).get(0))) {
      Image image = new Image(fis);
      imageView = new ImageView(image);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return pattern;
  }

}

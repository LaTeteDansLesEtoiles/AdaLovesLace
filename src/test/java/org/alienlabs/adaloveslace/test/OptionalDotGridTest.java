package org.alienlabs.adaloveslace.test;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.alienlabs.adaloveslace.business.model.Diagram;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.business.model.Pattern;
import org.alienlabs.adaloveslace.util.FileUtil;
import org.alienlabs.adaloveslace.view.component.OptionalDotGrid;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.alienlabs.adaloveslace.functionaltest.AppFunctionalTestParent.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OptionalDotGridTest {

  private ImageView imageView;

  @Test
  void zoom_knot_factor_0() {
    // Init
    Knot knot = new Knot(0d, 0d, buildPattern(), imageView);
    knot.setZoomFactor(0);

    // Run
    double zoomFactor = new OptionalDotGrid(new Diagram(), null).zoomAndFlipKnot(knot, null);

    // Verify
    assertEquals(1d, zoomFactor);
  }

  @Test
  void zoom_knot_with_zoom_factor_1() {
    // Init
    Knot knot = new Knot(0d, 0d, buildPattern(), imageView);
    knot.setZoomFactor(1);

    // Run
    double zoomFactor = new OptionalDotGrid(new Diagram(), null).zoomAndFlipKnot(knot, null);

    // Verify
    assertTrue(zoomFactor > 1d);
    assertTrue(zoomFactor < 1.5d);
  }


  @Test
  void zoom_knot_with_zoom_factor_minus_1() {
    // Init
    Knot knot = new Knot(0d, 0d, buildPattern(), imageView);
    knot.setZoomFactor(-1);

    // Run
    double zoomFactor = new OptionalDotGrid(new Diagram(), null).zoomAndFlipKnot(knot, null);

    // Verify
    assertTrue(zoomFactor < 1d);
    assertTrue(zoomFactor > 0.6d);
  }

  @Test
  void zoom_knot_factor_7() {
    // Init
    Knot knot = new Knot(0d, 0d, buildPattern(), imageView);
    knot.setZoomFactor(7);

    // Run
    double zoomFactor = new OptionalDotGrid(new Diagram(), null).zoomAndFlipKnot(knot, null);

    // Verify
    assertEquals(2.05d, zoomFactor);
  }

  @Test
  void zoom_knot_factor_20() {
    // Init
    Knot knot = new Knot(0d, 0d, buildPattern(), imageView);
    knot.setZoomFactor(20);

    // Run
    double zoomFactor = new OptionalDotGrid(new Diagram(), null).zoomAndFlipKnot(knot, null);

    // Verify
    assertEquals(4d, zoomFactor);
  }

  @Test
  void zoom_knot_factor_minus_5() {
    // Init
    Knot knot = new Knot(0d, 0d, buildPattern(), imageView);
    knot.setZoomFactor(-5);

    // Run
    double zoomFactor = new OptionalDotGrid(new Diagram(), null).zoomAndFlipKnot(knot, null);

    // Verify
    assertTrue(zoomFactor < 0.3d);
    assertTrue(zoomFactor > 0.2);
  }

  @Test
  void zoom_knot_factor_minus_15() {
    // Init
    Knot knot = new Knot(0d, 0d, buildPattern(), imageView);
    knot.setZoomFactor(-15);

    // Run
    double zoomFactor = new OptionalDotGrid(new Diagram(), null).zoomAndFlipKnot(knot, null);

    // Verify
    assertTrue(zoomFactor < 0.1d);
    assertTrue(zoomFactor > 0.01d);
  }

  private Pattern buildPattern() {
    Pattern pattern = new Pattern();

    pattern.setAbsoluteFilename(CLASSPATH_RESOURCES_PATH + SNOWFLAKE_IMAGE);

    try (FileInputStream fis = new FileInputStream(new FileUtil().getResources(this, java.util.regex.Pattern.compile(CLASSPATH_RESOURCES_PATH_JPG)).get(0))) {
      Image image = new Image(fis);
      imageView = new ImageView(image);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return pattern;
  }

}

package org.alienlabs.adaloveslace.unittest.view.window.event;

import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import org.alienlabs.adaloveslace.view.window.event.GridEvents;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

@Tag("unit")
class GridEventsDrawingPreviewImageViewTest {

  @AfterEach
  void clearDrawingPreview() {
    GridEvents.setCurrentImageView(null);
  }

  @Test
  void set_current_image_view_is_reflected_by_getter() {
    ImageView iv = new ImageView(new WritableImage(2, 2));
    GridEvents.setCurrentImageView(iv);
    assertSame(iv, GridEvents.getDrawingPreviewImageView());
  }

  @Test
  void clear_current_image_view_returns_null_preview() {
    GridEvents.setCurrentImageView(new ImageView(new WritableImage(2, 2)));
    GridEvents.setCurrentImageView(null);
    assertNull(GridEvents.getDrawingPreviewImageView());
  }
}

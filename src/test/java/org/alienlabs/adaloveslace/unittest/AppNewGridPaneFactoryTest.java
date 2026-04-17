package org.alienlabs.adaloveslace.unittest;

import javafx.geometry.Pos;
import org.alienlabs.adaloveslace.App;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("unit")
class AppNewGridPaneFactoryTest {

  @Test
  void newGridPane_matches_toolbox_layout_convention() {
    App app = new App();
    var grid = app.newGridPane();
    assertEquals(5d, grid.getHgap());
    assertEquals(5d, grid.getVgap());
    assertEquals(Pos.TOP_CENTER, grid.getAlignment());
    assertEquals(10d, grid.getPadding().getTop());
  }
}

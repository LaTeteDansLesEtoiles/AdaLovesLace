package org.alienlabs.adaloveslace.unittest.view.window;

import org.alienlabs.adaloveslace.view.window.MainWindow;
import org.alienlabs.adaloveslace.view.window.ToolboxWindow;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("unit")
class MainWindowFooterAndConstantsUnitTest {

  @Test
  void menu_string_constants_are_non_blank() {
    assertFalse(MainWindow.SAVE_FILE.isBlank());
    assertFalse(MainWindow.LOAD_FILE.isBlank());
    assertEquals(25d, MainWindow.NEW_KNOT_GAP, 1e-9);
  }

  @Test
  void toolbox_spinner_bounds_are_consistent() {
    assertTrue(ToolboxWindow.ZOOM_SPINNER_MIN_VALUE < ToolboxWindow.ZOOM_SPINNER_MAX_VALUE);
    assertTrue(ToolboxWindow.ROTATION_SPINNER_MIN_VALUE < ToolboxWindow.ROTATION_SPINNER_MAX_VALUE);
    assertEquals(14, ToolboxWindow.MAX_PATTERNS_WITHOUT_SCROLL);
    assertEquals(30d, ToolboxWindow.GEOMETRY_BUTTONS_HEIGHT, 1e-9);
  }
}

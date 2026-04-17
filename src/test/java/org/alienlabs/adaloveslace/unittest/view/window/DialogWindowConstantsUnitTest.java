package org.alienlabs.adaloveslace.unittest.view.window;

import org.alienlabs.adaloveslace.view.window.CreatePatternWindow;
import org.alienlabs.adaloveslace.view.window.FileAlreadyExistsWindow;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Tag("unit")
class DialogWindowConstantsUnitTest {

  @Test
  void create_pattern_window_width_matches_file_exists_dialog() {
    assertEquals(CreatePatternWindow.CREATE_PATTERN_WINDOW_WIDTH,
        FileAlreadyExistsWindow.CREATE_PATTERN_WINDOW_WIDTH,
        1e-9);
  }

  @Test
  void dialog_bundle_keys_are_stable_identifiers() {
    for (String key : new String[] {
        CreatePatternWindow.CREATE_PATTERN_WINDOW_TITLE,
        FileAlreadyExistsWindow.FILE_ALREADY_EXISTS_WINDOW_TITLE,
        FileAlreadyExistsWindow.CANCEL_BUTTON_TEXT
    }) {
      assertFalse(key.isBlank());
    }
  }
}

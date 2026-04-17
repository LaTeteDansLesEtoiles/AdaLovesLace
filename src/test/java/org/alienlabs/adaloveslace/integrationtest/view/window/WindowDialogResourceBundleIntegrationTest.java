package org.alienlabs.adaloveslace.integrationtest.view.window;

import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.view.window.CreatePatternWindow;
import org.alienlabs.adaloveslace.view.window.FileAlreadyExistsWindow;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Ensures dialog window resource keys resolve through the same {@link App#resourceBundle} used in production.
 */
@Tag("integration")
class WindowDialogResourceBundleIntegrationTest {

  @Test
  void create_pattern_alert_strings_resolve() {
    assertFalse(App.resourceBundle.getString(CreatePatternWindow.CREATE_PATTERN_WINDOW_TITLE).isBlank());
    assertFalse(App.resourceBundle.getString(CreatePatternWindow.CREATE_PATTERN_HEADER_TEXT).isBlank());
    assertFalse(App.resourceBundle.getString(CreatePatternWindow.CANCEL_BUTTON_TEXT).isBlank());
  }

  @Test
  void file_exists_alert_strings_resolve() {
    assertFalse(App.resourceBundle.getString(FileAlreadyExistsWindow.FILE_ALREADY_EXISTS_WINDOW_TITLE).isBlank());
    assertFalse(App.resourceBundle.getString(FileAlreadyExistsWindow.SAVE_BUTTON_TEXT).isBlank());
  }
}

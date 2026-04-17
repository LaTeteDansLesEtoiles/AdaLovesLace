package org.alienlabs.adaloveslace.unittest;

import org.alienlabs.adaloveslace.App;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.alienlabs.adaloveslace.view.window.MainWindow.SAVE_FILE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("unit")
class AppConstantsAndBundleKeysTest {

  @Test
  void lace_extension_and_project_name_are_stable_contracts() {
    assertEquals(".lace", App.LACE_FILE_EXTENSION);
    assertEquals("adaloveslace", App.PROJECT_NAME);
    assertTrue(App.LACE_FILE_MIME_TYPE.contains("lace"));
  }

  @Test
  void default_bundle_resolves_core_window_and_file_menu_keys() {
    assertFalse(App.resourceBundle.getString(App.MAIN_WINDOW_TITLE).isBlank());
    assertFalse(App.resourceBundle.getString(App.TOOLBOX_TITLE).isBlank());
    assertFalse(App.resourceBundle.getString(SAVE_FILE).isBlank());
  }

  @Test
  void default_grid_dimensions_match_public_constants() {
    App app = new App();
    assertEquals(App.DEFAULT_GRID_WIDTH, app.getGridWidth());
    assertEquals(App.DEFAULT_GRID_HEIGHT, app.getGridHeight());
  }
}

package org.alienlabs.adaloveslace.unittest.view.component.button.toolboxwindow.grid;

import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.enumeration.GridType;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid.AddKnotButton;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid.CreatePatternButton;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid.ShowHideGridButton;
import org.alienlabs.adaloveslace.view.window.MainWindow;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Ensures toolbox grid controls stay wired to {@code AdaLovesLace*.properties} keys used in code.
 */
@Tag("unit")
class ToolboxGridButtonBundleKeysTest {

  private static final Locale LOCALE = Locale.of("en", "EN");

  @Test
  void resource_bundle_defines_toolbox_grid_button_labels_and_reset_dialog_strings() {
    ResourceBundle bundle = ResourceBundle.getBundle("AdaLovesLace", LOCALE);

    assertNonBlank(bundle, ShowHideGridButton.SHOW_HIDE_GRID_BUTTON_NAME);
    assertNonBlank(bundle, CreatePatternButton.CREATE_PATTERN_BUTTON);
    assertNonBlank(bundle, "CREATE_PATTERN_BUTTON_TOOLTIP");

    assertNonBlank(bundle, MainWindow.UNDO_KNOT);
    assertNonBlank(bundle, MainWindow.REDO_KNOT);
    assertNonBlank(bundle, MainWindow.RESET_DIAGRAM);

    assertNonBlank(bundle, App.TEXT_BUTTON_NAME);
    assertNonBlank(bundle, App.ADD_KNOT_BUTTON_NAME);
    assertNonBlank(bundle, App.ADD_KNOT_WITH_SIZE_DIALOG_TITLE);
    assertNonBlank(bundle, App.COLOR_BUTTON_NAME);
    assertNonBlank(bundle, App.RESET_ALL_BUTTON_NAME);

    assertNonBlank(bundle, "ResetDiagramWindowTitle");
    assertNonBlank(bundle, "ResetDiagramHeaderText");
    assertNonBlank(bundle, "ResetDiagramContentText");
    assertNonBlank(bundle, "ResetDiagramButtonText");
    assertNonBlank(bundle, "CancelResetDiagramButtonText");

    assertNonBlank(bundle, "ResetAllWindowTitle");
    assertNonBlank(bundle, "ResetAllHeaderText");
    assertNonBlank(bundle, "ResetAllContentText");
    assertNonBlank(bundle, "ResetAllButtonText");
    assertNonBlank(bundle, "CancelResetAll");

    assertNonBlank(bundle, "BackInBlack");

    for (GridType gridType : GridType.values()) {
      assertNonBlank(bundle, gridType.name());
    }
  }

  @Test
  void add_knot_file_filter_constants_are_non_blank() {
    assertFalse(AddKnotButton.KNOT_FILES.isBlank());
    assertFalse(AddKnotButton.KNOT_FILES_FILTER1.isBlank());
    assertFalse(AddKnotButton.KNOT_FILES_FILTER2.isBlank());
    assertFalse(AddKnotButton.KNOT_FILES_FILTER3.isBlank());
  }

  private static void assertNonBlank(ResourceBundle bundle, String key) {
    String value = bundle.getString(key);
    assertFalse(value.isBlank(), "Bundle key must be non-blank: " + key);
  }
}

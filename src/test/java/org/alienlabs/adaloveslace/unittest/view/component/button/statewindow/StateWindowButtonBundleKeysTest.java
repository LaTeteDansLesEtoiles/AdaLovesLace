package org.alienlabs.adaloveslace.unittest.view.component.button.statewindow;

import org.alienlabs.adaloveslace.view.component.button.statewindow.InvisibleButton;
import org.alienlabs.adaloveslace.view.component.button.statewindow.SelectableButton;
import org.alienlabs.adaloveslace.view.component.button.statewindow.UnselectableButton;
import org.alienlabs.adaloveslace.view.component.button.statewindow.VisibleButton;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertFalse;

@Tag("unit")
class StateWindowButtonBundleKeysTest {

  private static final Locale LOCALE = Locale.of("en", "EN");

  @Test
  void resource_bundle_defines_state_button_labels_and_tooltips() {
    ResourceBundle bundle = ResourceBundle.getBundle("AdaLovesLace", LOCALE);

    assertNonBlank(bundle, SelectableButton.SELECTABLE_BUTTON_NAME);
    assertNonBlank(bundle, UnselectableButton.UNSELECTABLE_BUTTON_NAME);
    assertNonBlank(bundle, InvisibleButton.INVISIBLE_BUTTON_NAME);
    assertNonBlank(bundle, VisibleButton.VISIBLE_BUTTON_NAME);

    assertNonBlank(bundle, "SELECTABLE_BUTTON_TOOLTIP");
    assertNonBlank(bundle, "UNSELECTABLE_BUTTON_TOOLTIP");
    assertNonBlank(bundle, "INVISIBLE_BUTTON_TOOLTIP");
    assertNonBlank(bundle, "VISIBLE_BUTTON_TOOLTIP");
  }

  private static void assertNonBlank(ResourceBundle bundle, String key) {
    String value = bundle.getString(key);
    assertFalse(value.isBlank(), "Bundle key must be non-blank: " + key);
  }
}

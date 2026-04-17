package org.alienlabs.adaloveslace.unittest.view.component.button.toolboxwindow;

import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.ShareButton;
import org.alienlabs.adaloveslace.view.window.DiagramShareWithImagesWindow;
import org.alienlabs.adaloveslace.view.window.MainWindow;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Bundle keys for root-level toolbox buttons ({@link org.alienlabs.adaloveslace.view.component.button.toolboxwindow})
 * and the share dialog opened from {@link ShareButton}.
 */
@Tag("unit")
class ToolboxWindowRootButtonBundleKeysTest {

  private static final Locale LOCALE = Locale.of("en", "EN");

  @Test
  void share_button_name_key_matches_public_constant() {
    assertEquals("SHARE_BUTTON_NAME", ShareButton.SHARE_BUTTON_NAME);
  }

  @Test
  void resource_bundle_defines_quit_confirmation_and_share_entry_strings() {
    ResourceBundle bundle = ResourceBundle.getBundle("AdaLovesLace", LOCALE);

    assertNonBlank(bundle, ShareButton.SHARE_BUTTON_NAME);
    assertNonBlank(bundle, MainWindow.QUIT_APP);

    assertNonBlank(bundle, "QuitWindowTitle");
    assertNonBlank(bundle, "QuitHeaderText");
    assertNonBlank(bundle, "QuitContentText");
    assertNonBlank(bundle, "QuitButtonText");
    assertNonBlank(bundle, "SaveAndQuitButtonText");
    assertNonBlank(bundle, "CancelQuitButtonText");
  }

  @Test
  void resource_bundle_defines_share_dialog_strings() {
    ResourceBundle bundle = ResourceBundle.getBundle("AdaLovesLace", LOCALE);

    assertNonBlank(bundle, DiagramShareWithImagesWindow.SHARE_DIAGRAM_WINDOW_TITLE);
    assertNonBlank(bundle, DiagramShareWithImagesWindow.SHARE_BUTTON_TEXT);
    assertNonBlank(bundle, DiagramShareWithImagesWindow.USERNAME_PROMPT);
    assertNonBlank(bundle, DiagramShareWithImagesWindow.FILENAME_PROMPT);
    assertNonBlank(bundle, DiagramShareWithImagesWindow.DESCRIPTION_PROMPT);
    assertNonBlank(bundle, DiagramShareWithImagesWindow.CLIENT_ID_PROMPT);
    assertNonBlank(bundle, DiagramShareWithImagesWindow.CLIENT_SECRET_PROMPT);
    assertNonBlank(bundle, DiagramShareWithImagesWindow.HIDE_CREDENTIALS);
    assertNonBlank(bundle, DiagramShareWithImagesWindow.SHOW_CREDENTIALS);
    assertNonBlank(bundle, DiagramShareWithImagesWindow.USERNAME_LABEL);
    assertNonBlank(bundle, DiagramShareWithImagesWindow.FILENAME_LABEL);
    assertNonBlank(bundle, DiagramShareWithImagesWindow.DESCRIPTION_LABEL);
    assertNonBlank(bundle, DiagramShareWithImagesWindow.CLIENT_ID_LABEL);
    assertNonBlank(bundle, DiagramShareWithImagesWindow.CLIENT_SECRET_LABEL);
    assertNonBlank(bundle, DiagramShareWithImagesWindow.CHOOSE_TECHNIQUE_LABEL);
    assertNonBlank(bundle, DiagramShareWithImagesWindow.CHOOSE_SUB_TECHNIQUE_LABEL);
    assertNonBlank(bundle, DiagramShareWithImagesWindow.TECHNIQUE_LABEL);
    assertNonBlank(bundle, DiagramShareWithImagesWindow.SUB_TECHNIQUE_LABEL);
    assertNonBlank(bundle, DiagramShareWithImagesWindow.LOAD_IMAGE);
    assertNonBlank(bundle, DiagramShareWithImagesWindow.CHOOSE_IMAGE);
    assertNonBlank(bundle, DiagramShareWithImagesWindow.ADDITIONAL_IMAGES_LABEL);
  }

  private static void assertNonBlank(ResourceBundle bundle, String key) {
    String value = bundle.getString(key);
    assertFalse(value.isBlank(), "Bundle key must be non-blank: " + key);
  }
}

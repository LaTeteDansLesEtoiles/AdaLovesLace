package org.alienlabs.adaloveslace.unittest.util;

import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.util.SplashStartupTasks;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Splash status labels must stay aligned with {@link SplashStartupTasks#phases()} message keys.
 */
@Tag("unit")
class SplashStartupPhaseBundleKeysTest {

  private static final Locale LOCALE = Locale.of("en", "EN");

  @Test
  void resource_bundle_defines_all_splash_startup_phase_keys() {
    ResourceBundle bundle = ResourceBundle.getBundle("AdaLovesLace", LOCALE);
    SplashStartupTasks tasks = SplashStartupTasks.forTesting(App.class, new java.io.File("unused"));
    for (SplashStartupTasks.SplashPhase phase : tasks.phases()) {
      String key = phase.messageKey();
      String value = bundle.getString(key);
      assertFalse(value.isBlank(), "Splash phase bundle key must be non-blank: " + key);
    }
  }
}

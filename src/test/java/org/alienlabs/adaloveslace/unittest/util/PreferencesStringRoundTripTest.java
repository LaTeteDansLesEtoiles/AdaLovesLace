package org.alienlabs.adaloveslace.unittest.util;

import org.alienlabs.adaloveslace.util.Preferences;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@Tag("unit")
class PreferencesStringRoundTripTest {

  @Test
  void set_and_get_string_value_round_trip() {
    String key = "adaloveslace_unit_test_" + UUID.randomUUID();
    Preferences p = new Preferences();
    try {
      p.setStringValue(key, "hello-util");
      assertEquals("hello-util", p.getStringValue(key));
    } finally {
      p.setStringValue(key, null);
    }
    assertEquals("", p.getStringValue(key));
  }

  @Test
  void set_path_round_trip_and_clear() throws Exception {
    String key = "adaloveslace_unit_path_" + UUID.randomUUID();
    java.nio.file.Path temp = java.nio.file.Files.createTempFile("prefs-path-", ".txt");
    Preferences p = new Preferences();
    try {
      p.setPathWithFileValue(temp.toFile(), key);
      assertEquals(temp.toAbsolutePath().normalize(), p.getPathWithFileValue(key).toPath().normalize());
    } finally {
      p.setPathWithFileValue(null, key);
    }
    assertNull(p.getPathWithFileValue(key));
    java.nio.file.Files.deleteIfExists(temp);
  }
}

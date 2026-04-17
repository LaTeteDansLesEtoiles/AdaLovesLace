package org.alienlabs.adaloveslace.unittest.util;

import org.alienlabs.adaloveslace.util.SystemInfo;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@Tag("unit")
class SystemInfoTest {

  @Test
  void java_version_property_is_readable() {
    assertNotNull(SystemInfo.javaVersion());
    assertFalse(SystemInfo.javaVersion().isBlank());
  }

  @Test
  void javafx_version_matches_system_property_when_set() {
    String v = SystemInfo.javafxVersion();
    String prop = System.getProperty(SystemInfo.JAVAFX_VERSION);
    if (prop == null) {
      assertNull(v);
    } else {
      assertFalse(v.isBlank());
      assertEquals(prop, v);
    }
  }
}

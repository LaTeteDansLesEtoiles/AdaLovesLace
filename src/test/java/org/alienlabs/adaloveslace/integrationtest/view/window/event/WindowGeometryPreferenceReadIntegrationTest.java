package org.alienlabs.adaloveslace.integrationtest.view.window.event;

import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.util.Preferences;
import org.alienlabs.adaloveslace.view.window.event.WindowRepositionEvents;
import org.alienlabs.adaloveslace.view.window.event.WindowResizeEvents;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Reads window geometry from {@link Preferences} through {@link WindowResizeEvents} and
 * {@link WindowRepositionEvents} (production preference key names).
 */
@Tag("integration")
class WindowGeometryPreferenceReadIntegrationTest {

  private final Map<String, String> previous = new LinkedHashMap<>();

  @AfterEach
  void restorePreferences() {
    Preferences p = new Preferences();
    for (Map.Entry<String, String> e : previous.entrySet()) {
      if (e.getValue() == null || e.getValue().isEmpty()) {
        p.setStringValue(e.getKey(), null);
      } else {
        p.setStringValue(e.getKey(), e.getValue());
      }
    }
    previous.clear();
  }

  private void remember(String key, Preferences p) {
    if (!previous.containsKey(key)) {
      previous.put(key, p.getStringValue(key));
    }
  }

  @Test
  void resize_getters_parse_stored_strings_with_offsets() {
    Preferences p = new Preferences();
    remember(WindowResizeEvents.MAIN_WINDOW_WIDTH, p);
    remember(WindowResizeEvents.GRID_WIDTH, p);
    remember(WindowResizeEvents.MAIN_WINDOW_HEIGHT, p);
    remember(WindowResizeEvents.GRID_HEIGHT, p);
    remember(WindowResizeEvents.TOOLBOX_WINDOW_WIDTH, p);
    remember(WindowResizeEvents.TOOLBOX_WINDOW_HEIGHT, p);

    p.setStringValue(WindowResizeEvents.MAIN_WINDOW_WIDTH, "900");
    p.setStringValue(WindowResizeEvents.GRID_WIDTH, "880");
    p.setStringValue(WindowResizeEvents.MAIN_WINDOW_HEIGHT, "700");
    p.setStringValue(WindowResizeEvents.GRID_HEIGHT, "600");
    p.setStringValue(WindowResizeEvents.TOOLBOX_WINDOW_WIDTH, "420");
    p.setStringValue(WindowResizeEvents.TOOLBOX_WINDOW_HEIGHT, "900");

    WindowResizeEvents resize = new WindowResizeEvents(new App());
    assertEquals(900d, resize.getMainWindowWidth(), 1e-6);
    assertEquals(880d, resize.getGridWidth(), 1e-6);
    assertEquals(700d - 32d, resize.getMainWindowHeight(), 1e-6);
    assertEquals(600d - 32d, resize.getGridHeight(), 1e-6);
    assertEquals(420d, resize.getToolboxWindowWidth(), 1e-6);
    assertEquals(900d - 32d, resize.getToolboxWindowHeight(), 1e-6);
  }

  @Test
  void reposition_getters_parse_stored_strings_with_offsets() {
    Preferences p = new Preferences();
    remember(WindowRepositionEvents.MAIN_WINDOW_X, p);
    remember(WindowRepositionEvents.MAIN_WINDOW_Y, p);
    remember(WindowRepositionEvents.TOOLBOX_WINDOW_X, p);
    remember(WindowRepositionEvents.TOOLBOX_WINDOW_Y, p);

    p.setStringValue(WindowRepositionEvents.MAIN_WINDOW_X, "120");
    p.setStringValue(WindowRepositionEvents.MAIN_WINDOW_Y, "80");
    p.setStringValue(WindowRepositionEvents.TOOLBOX_WINDOW_X, "1500");
    p.setStringValue(WindowRepositionEvents.TOOLBOX_WINDOW_Y, "40");

    WindowRepositionEvents repos = new WindowRepositionEvents(new App());
    assertEquals(120d, repos.getMainWindowX(), 1e-6);
    assertEquals(80d - 16d, repos.getMainWindowY(), 1e-6);
    assertEquals(1500d, repos.getToolboxWindowX(), 1e-6);
    assertEquals(40d - 16d, repos.getToolboxWindowY(), 1e-6);
  }
}

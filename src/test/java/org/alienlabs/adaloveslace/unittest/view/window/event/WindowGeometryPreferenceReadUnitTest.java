package org.alienlabs.adaloveslace.unittest.view.window.event;

import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.util.Preferences;
import org.alienlabs.adaloveslace.view.window.event.WindowRepositionEvents;
import org.alienlabs.adaloveslace.view.window.event.WindowResizeEvents;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.alienlabs.adaloveslace.App.DEFAULT_GRID_HEIGHT;
import static org.alienlabs.adaloveslace.App.DEFAULT_GRID_WIDTH;
import static org.alienlabs.adaloveslace.App.DEFAULT_MAIN_WINDOW_HEIGHT;
import static org.alienlabs.adaloveslace.App.DEFAULT_MAIN_WINDOW_WIDTH;
import static org.alienlabs.adaloveslace.App.DEFAULT_MAIN_WINDOW_X;
import static org.alienlabs.adaloveslace.App.DEFAULT_MAIN_WINDOW_Y;
import static org.alienlabs.adaloveslace.view.window.ToolboxWindow.DEFAULT_TOOLBOX_WINDOW_WIDTH;
import static org.alienlabs.adaloveslace.view.window.ToolboxWindow.DEFAULT_TOOLBOX_WINDOW_X;
import static org.alienlabs.adaloveslace.view.window.ToolboxWindow.DEFAULT_TOOLBOX_WINDOW_Y;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Pure-Java exercise of the geometry getters in {@link WindowResizeEvents} and
 * {@link WindowRepositionEvents}: covers the parse-with-offset branch and the empty-string
 * default branch without spinning the JavaFX toolkit. Backs the JaCoCo {@code PACKAGE}
 * minimum on {@code org.alienlabs.adaloveslace.view.window.event} when only unit tests run.
 */
@Tag("unit")
class WindowGeometryPreferenceReadUnitTest {

  private final Map<String, String> previous = new LinkedHashMap<>();

  private void remember(String key, Preferences p) {
    if (!previous.containsKey(key)) {
      previous.put(key, p.getStringValue(key));
    }
  }

  private void clear(Preferences p, List<String> keys) {
    for (String k : keys) {
      remember(k, p);
      p.setStringValue(k, null);
    }
  }

  @BeforeEach
  void clearAllKeys() {
    Preferences p = new Preferences();
    clear(p, List.of(
        WindowResizeEvents.MAIN_WINDOW_WIDTH,
        WindowResizeEvents.MAIN_WINDOW_HEIGHT,
        WindowResizeEvents.GRID_WIDTH,
        WindowResizeEvents.GRID_HEIGHT,
        WindowResizeEvents.TOOLBOX_WINDOW_WIDTH,
        WindowResizeEvents.TOOLBOX_WINDOW_HEIGHT,
        WindowRepositionEvents.MAIN_WINDOW_X,
        WindowRepositionEvents.MAIN_WINDOW_Y,
        WindowRepositionEvents.TOOLBOX_WINDOW_X,
        WindowRepositionEvents.TOOLBOX_WINDOW_Y
    ));
  }

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

  @Test
  void resize_getters_return_defaults_when_preferences_are_unset() {
    WindowResizeEvents resize = new WindowResizeEvents(new App());
    // No -32 offset must be applied when falling back to defaults.
    assertEquals(DEFAULT_MAIN_WINDOW_WIDTH, resize.getMainWindowWidth(), 1e-6);
    assertEquals(DEFAULT_MAIN_WINDOW_HEIGHT, resize.getMainWindowHeight(), 1e-6);
    assertEquals(DEFAULT_GRID_WIDTH, resize.getGridWidth(), 1e-6);
    assertEquals(DEFAULT_GRID_HEIGHT, resize.getGridHeight(), 1e-6);
    assertEquals(DEFAULT_TOOLBOX_WINDOW_WIDTH, resize.getToolboxWindowWidth(), 1e-6);
    // getToolboxWindowHeight defaults to literal 0d in production code (not exposed as a constant).
    assertEquals(0d, resize.getToolboxWindowHeight(), 1e-6);
  }

  @Test
  void reposition_getters_return_defaults_when_preferences_are_unset() {
    WindowRepositionEvents repos = new WindowRepositionEvents(new App());
    assertEquals(DEFAULT_MAIN_WINDOW_X, repos.getMainWindowX(), 1e-6);
    assertEquals(DEFAULT_MAIN_WINDOW_Y, repos.getMainWindowY(), 1e-6);
    assertEquals(DEFAULT_TOOLBOX_WINDOW_X, repos.getToolboxWindowX(), 1e-6);
    assertEquals(DEFAULT_TOOLBOX_WINDOW_Y, repos.getToolboxWindowY(), 1e-6);
  }

  @Test
  void resize_getters_parse_stored_strings_with_offsets() {
    Preferences p = new Preferences();
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

package org.alienlabs.adaloveslace.unittest.view.window.event;

import org.alienlabs.adaloveslace.view.window.event.WindowRepositionEvents;
import org.alienlabs.adaloveslace.view.window.event.WindowResizeEvents;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("unit")
class WindowEventPreferenceKeyConstantsTest {

  @Test
  void window_resize_preference_keys_are_distinct_and_non_blank() {
    List<String> keys = List.of(
        WindowResizeEvents.MAIN_WINDOW_WIDTH,
        WindowResizeEvents.GRID_WIDTH,
        WindowResizeEvents.MAIN_WINDOW_HEIGHT,
        WindowResizeEvents.GRID_HEIGHT,
        WindowResizeEvents.TOOLBOX_WINDOW_WIDTH,
        WindowResizeEvents.TOOLBOX_WINDOW_HEIGHT,
        WindowResizeEvents.GEOMETRY_WINDOW_WIDTH,
        WindowResizeEvents.GEOMETRY_WINDOW_HEIGHT,
        WindowResizeEvents.STATE_WINDOW_WIDTH,
        WindowResizeEvents.STATE_WINDOW_HEIGHT
    );
    assertTrue(keys.stream().noneMatch(String::isBlank));
    assertEquals(keys.size(), new HashSet<>(keys).size(), "Resize preference keys must stay unique for prefs wiring");
  }

  @Test
  void window_reposition_preference_keys_are_distinct_and_non_blank() {
    List<String> keys = List.of(
        WindowRepositionEvents.MAIN_WINDOW_X,
        WindowRepositionEvents.MAIN_WINDOW_Y,
        WindowRepositionEvents.TOOLBOX_WINDOW_X,
        WindowRepositionEvents.TOOLBOX_WINDOW_Y
    );
    assertTrue(keys.stream().noneMatch(String::isBlank));
    Set<String> unique = new HashSet<>(keys);
    assertEquals(keys.size(), unique.size(), "Reposition preference keys must stay unique for prefs wiring");
  }
}

package org.alienlabs.adaloveslace.unittest.view.component.button.geometrywindow.move;

import org.alienlabs.adaloveslace.view.component.button.geometrywindow.move.MoveToolboxNodeIds;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("unit")
class MoveToolboxNodeIdsTest {

  @Test
  void move_toolbox_ids_are_distinct_and_non_blank() {
    List<String> ids = List.of(
        MoveToolboxNodeIds.UP_LEFT,
        MoveToolboxNodeIds.UP,
        MoveToolboxNodeIds.UP_RIGHT,
        MoveToolboxNodeIds.LEFT,
        MoveToolboxNodeIds.FAST_MODE,
        MoveToolboxNodeIds.RIGHT,
        MoveToolboxNodeIds.DOWN_LEFT,
        MoveToolboxNodeIds.DOWN,
        MoveToolboxNodeIds.DOWN_RIGHT
    );
    Set<String> unique = new HashSet<>(ids);
    assertTrue(ids.stream().noneMatch(String::isBlank));
    assertEquals(ids.size(), unique.size(), "Each move control id must be unique for stable TestFX lookup");
  }
}

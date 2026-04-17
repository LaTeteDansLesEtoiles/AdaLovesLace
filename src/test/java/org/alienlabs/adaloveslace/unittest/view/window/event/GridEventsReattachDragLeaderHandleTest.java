package org.alienlabs.adaloveslace.unittest.view.window.event;

import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.alienlabs.adaloveslace.domain.Knot;
import org.alienlabs.adaloveslace.view.window.event.GridEvents;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

@Tag("unit")
class GridEventsReattachDragLeaderHandleTest {

  @Test
  void reattachDragLeaderHandle_movesHandleOntoDragCopyAtSameIndex() {
    WritableImage img = new WritableImage(2, 2);
    Circle handle = new Circle(2, Color.BLUE);
    Knot leader = new Knot(1, 2, Optional.empty(), Optional.of("a"), Optional.of(Color.RED), new ImageView(img));
    leader.setHandle(handle);
    Knot follower = new Knot(10, 20, Optional.empty(), Optional.of("b"), Optional.of(Color.BLUE), new ImageView(img));

    List<Knot> selectedBefore = List.of(leader, follower);
    List<Knot> dragCopies = new ArrayList<>();
    dragCopies.add(new Knot(1, 2, Optional.empty(), Optional.of("a"), Optional.of(Color.RED), new ImageView(img)));
    dragCopies.add(new Knot(10, 20, Optional.empty(), Optional.of("b"), Optional.of(Color.BLUE), new ImageView(img)));

    GridEvents.reattachDragLeaderHandle(selectedBefore, leader, dragCopies);

    assertSame(handle, dragCopies.get(0).getHandle());
    assertNull(leader.getHandle());
    assertNull(dragCopies.get(1).getHandle());
  }
}

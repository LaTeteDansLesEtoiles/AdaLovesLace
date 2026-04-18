package org.alienlabs.adaloveslace.unittest.view.window.event;

import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.alienlabs.adaloveslace.domain.Knot;
import org.alienlabs.adaloveslace.domain.Pattern;
import org.alienlabs.adaloveslace.view.window.event.GridEvents;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

@Tag("unit")
class GridEventsReattachDragLeaderHandleTest {

  @Test
  void reattachDragLeaderHandle_movesHandleOntoDragCopyAtSameIndex() {
    WritableImage img = new WritableImage(2, 2);
    Circle handle = new Circle(2, Color.BLUE);
    Knot leader = new Knot(1, 2, Optional.<Pattern>empty(), Optional.of("a"), Optional.of(Color.RED), new ImageView(img));
    leader.setHandle(handle);
    Knot follower = new Knot(10, 20, Optional.<Pattern>empty(), Optional.of("b"), Optional.of(Color.BLUE), new ImageView(img));

    List<Knot> selectedBefore = List.of(leader, follower);
    List<Knot> dragCopies = new ArrayList<>();
    dragCopies.add(new Knot(1, 2, Optional.<Pattern>empty(), Optional.of("a"), Optional.of(Color.RED), new ImageView(img)));
    dragCopies.add(new Knot(10, 20, Optional.<Pattern>empty(), Optional.of("b"), Optional.of(Color.BLUE), new ImageView(img)));

    GridEvents.reattachDragLeaderHandle(selectedBefore, leader, dragCopies);

    assertSame(handle, dragCopies.get(0).getHandle());
    assertNull(leader.getHandle());
    assertNull(dragCopies.get(1).getHandle());
  }

  @Test
  void reattachDragLeaderHandle_isNoOp_whenEventSourceKnotIsNull() {
    List<Knot> selectedBefore = List.of(new Knot());
    List<Knot> dragCopies = new ArrayList<>();
    dragCopies.add(new Knot());

    assertDoesNotThrow(() -> GridEvents.reattachDragLeaderHandle(selectedBefore, null, dragCopies));
    assertNull(dragCopies.get(0).getHandle());
  }

  @Test
  void reattachDragLeaderHandle_isNoOp_whenSelectedBeforeIsNull() {
    List<Knot> dragCopies = new ArrayList<>();
    dragCopies.add(new Knot());

    assertDoesNotThrow(() -> GridEvents.reattachDragLeaderHandle(null, new Knot(), dragCopies));
    assertNull(dragCopies.get(0).getHandle());
  }

  @Test
  void reattachDragLeaderHandle_isNoOp_whenDragKnotsIsNull() {
    Knot leader = new Knot();
    leader.setHandle(new Circle(2, Color.BLUE));
    List<Knot> selectedBefore = List.of(leader);

    assertDoesNotThrow(() -> GridEvents.reattachDragLeaderHandle(selectedBefore, leader, null));
    // Leader handle must be untouched when there is no target list to receive it.
    assertSame(leader.getHandle(), leader.getHandle());
  }

  @Test
  void reattachDragLeaderHandle_isNoOp_whenLeaderNotFoundInSelectedBefore() {
    WritableImage img = new WritableImage(2, 2);
    Knot leader = new Knot(1, 2, Optional.<Pattern>empty(), Optional.of("a"), Optional.of(Color.RED), new ImageView(img));
    Circle handle = new Circle(2, Color.BLUE);
    leader.setHandle(handle);

    Knot stranger = new Knot(3, 4, Optional.<Pattern>empty(), Optional.of("x"), Optional.of(Color.BLUE), new ImageView(img));
    // Leader is NOT present in selectedBefore -> indexOf returns -1 -> nothing happens.
    List<Knot> selectedBefore = List.of(stranger);

    List<Knot> dragCopies = new ArrayList<>();
    dragCopies.add(new Knot(3, 4, Optional.<Pattern>empty(), Optional.of("x"), Optional.of(Color.BLUE), new ImageView(img)));

    GridEvents.reattachDragLeaderHandle(selectedBefore, leader, dragCopies);

    assertSame(handle, leader.getHandle(), "Leader should keep its handle when it is not in the selection list");
    assertNull(dragCopies.get(0).getHandle());
  }

  @Test
  void reattachDragLeaderHandle_isNoOp_whenLeaderHandleIsAlreadyNull() {
    WritableImage img = new WritableImage(2, 2);
    Knot leader = new Knot(1, 2, Optional.<Pattern>empty(), Optional.of("a"), Optional.of(Color.RED), new ImageView(img));
    // No handle attached to leader.

    List<Knot> selectedBefore = List.of(leader);
    List<Knot> dragCopies = new ArrayList<>();
    dragCopies.add(new Knot(1, 2, Optional.<Pattern>empty(), Optional.of("a"), Optional.of(Color.RED), new ImageView(img)));

    GridEvents.reattachDragLeaderHandle(selectedBefore, leader, dragCopies);

    assertNull(dragCopies.get(0).getHandle(), "Nothing to hand off when leader has no handle");
  }
}

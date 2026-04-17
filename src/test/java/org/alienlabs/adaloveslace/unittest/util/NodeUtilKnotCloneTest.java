package org.alienlabs.adaloveslace.unittest.util;

import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.Diagram;
import org.alienlabs.adaloveslace.domain.Knot;
import org.alienlabs.adaloveslace.util.NodeUtil;
import org.alienlabs.adaloveslace.view.component.grid.OptionalDotGrid;
import org.alienlabs.adaloveslace.view.window.MainWindow;
import org.alienlabs.adaloveslace.view.window.event.GridEvents;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("unit")
class NodeUtilKnotCloneTest {

  private App app;
  private Diagram diagram;

  @BeforeEach
  void beforeEach() {
    app = new App();
    app.setMainWindow(new MainWindow());
    app.setMovablePane(new Pane());
    diagram = new Diagram(app);
    app.setOptionalDotGrid(new OptionalDotGrid(app, diagram, app.getMovablePane()));
    app.setDiagram(diagram);
    GridEvents.app = app;
  }

  @Test
  void copyKnot_sharesImageView_withSource() {
    WritableImage wi = new WritableImage(4, 4);
    ImageView iv = new ImageView(wi);
    Knot k = new Knot(1, 1, Optional.empty(), Optional.of("x"), Optional.of(Color.RED), iv);
    NodeUtil nu = new NodeUtil();
    Knot shallow = nu.copyKnot(k);
    assertSame(k.getImageView(), shallow.getImageView());
  }

  @Test
  void copyKnotCloningImageView_usesDistinctImageView_andClearsDecorations() {
    WritableImage wi = new WritableImage(4, 4);
    ImageView iv = new ImageView(wi);
    Knot k = new Knot(1, 1, Optional.empty(), Optional.of("x"), Optional.of(Color.RED), iv);
    NodeUtil nu = new NodeUtil();
    Knot clone = nu.copyKnotCloningImageView(k);
    assertNotSame(k.getImageView(), clone.getImageView());
    assertSame(wi, clone.getImageView().getImage());
    assertNull(clone.getHandle());
    assertNull(clone.getHovered());
    assertNull(clone.getSelection());
  }

  @Test
  void duplicateSelectedKnotsAsNewStep_twoSelectedKnots_havePairwiseDistinctImageViews() {
    WritableImage img1 = new WritableImage(4, 4);
    WritableImage img2 = new WritableImage(4, 4);
    Knot k1 = new Knot(10, 10, Optional.empty(), Optional.of("a"), Optional.of(Color.RED), new ImageView(img1));
    Knot k2 = new Knot(50, 10, Optional.empty(), Optional.of("b"), Optional.of(Color.BLUE), new ImageView(img2));
    List<Knot> displayed = new ArrayList<>();
    List<Knot> selected = new ArrayList<>(List.of(k1, k2));
    Diagram.newStep(displayed, selected, false);

    NodeUtil.duplicateSelectedKnotsAsNewStep(app, false);

    List<Knot> after = diagram.getCurrentStep().getSelectedKnots();
    assertTrue(after.size() >= 2, "expected at least two selected knots after duplicate step");
    java.util.Set<ImageView> views = new java.util.HashSet<>();
    for (Knot k : after) {
      assertNotNull(k.getImageView());
      assertTrue(views.add(k.getImageView()), "each selected knot must own a distinct ImageView");
    }
  }

  @Test
  void duplicateSelectedKnotsAsNewStep_transfersLeaderHandleOntoClone_forDragMatching() {
    WritableImage img1 = new WritableImage(4, 4);
    WritableImage img2 = new WritableImage(4, 4);
    Circle leaderHandle = new Circle(3, Color.BLUE);
    Knot k1 = new Knot(10, 10, Optional.empty(), Optional.of("a"), Optional.of(Color.RED), new ImageView(img1));
    k1.setHandle(leaderHandle);
    Knot k2 = new Knot(50, 10, Optional.empty(), Optional.of("b"), Optional.of(Color.BLUE), new ImageView(img2));
    List<Knot> displayed = new ArrayList<>();
    List<Knot> selected = new ArrayList<>(List.of(k1, k2));
    Diagram.newStep(displayed, selected, false);

    NodeUtil.duplicateSelectedKnotsAsNewStep(app, false);

    List<Knot> after = diagram.getCurrentStep().getSelectedKnots();
    long withHandle = after.stream().filter(k -> k.getHandle() != null).count();
    assertEquals(1, withHandle, "exactly one selected knot should keep the drag handle Circle");
    Knot leader = after.stream().filter(k -> k.getHandle() != null).findFirst().orElseThrow();
    assertSame(leaderHandle, leader.getHandle(), "clone must reuse the same Circle for MOUSE_DRAGGED matching");
  }

  @Test
  void duplicateSelectedKnotsAsNewStep_appendsExactlyOneDiagramStep_forSingleUndoSlicePerDragPress() {
    WritableImage img = new WritableImage(4, 4);
    Knot selected = new Knot(10, 10, Optional.empty(), Optional.of("a"), Optional.of(Color.RED), new ImageView(img));
    Diagram.newStep(new ArrayList<>(), new ArrayList<>(List.of(selected)), false);

    int stepsBeforeDuplicate = diagram.getAllSteps().size();

    NodeUtil.duplicateSelectedKnotsAsNewStep(app, false);

    assertEquals(stepsBeforeDuplicate + 1, diagram.getAllSteps().size(),
        "drag press must commit exactly one undo step; drop relayout must not add another");
  }
}

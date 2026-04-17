package org.alienlabs.adaloveslace.unittest.view.component.grid;

import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.Diagram;
import org.alienlabs.adaloveslace.domain.Knot;
import org.alienlabs.adaloveslace.view.component.grid.OptionalDotGrid;
import org.alienlabs.adaloveslace.view.window.MainWindow;
import org.alienlabs.adaloveslace.view.window.event.GridEvents;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("unit")
class OptionalDotGridRetainRasterTest {

  private App app;
  private Pane movablePane;
  private Diagram diagram;
  private OptionalDotGrid grid;

  @BeforeEach
  void beforeEach() {
    app = new App();
    app.setMainWindow(new MainWindow());
    movablePane = new Pane();
    app.setMovablePane(movablePane);
    diagram = new Diagram(app);
    grid = new OptionalDotGrid(app, diagram, movablePane);
    app.setOptionalDotGrid(grid);
    app.setDiagram(diagram);
    GridEvents.app = app;
  }

  @Test
  void retainOnlyDisplayedKnotRastersOnMovablePane_keepsDisplayed_removesSelectedAndOrphans() {
    WritableImage imgD = new WritableImage(2, 2);
    WritableImage imgS = new WritableImage(2, 2);
    WritableImage imgO = new WritableImage(2, 2);
    ImageView ivD = new ImageView(imgD);
    ImageView ivS = new ImageView(imgS);
    ImageView orphan = new ImageView(imgO);

    Knot displayed = new Knot(5, 5, Optional.empty(), Optional.of("d"), Optional.of(Color.GREEN), ivD);
    Knot selected = new Knot(50, 50, Optional.empty(), Optional.of("s"), Optional.of(Color.RED), ivS);

    movablePane.getChildren().addAll(ivD, ivS, orphan);

    List<Knot> displayedList = new ArrayList<>(List.of(displayed));
    List<Knot> selectedList = new ArrayList<>(List.of(selected));
    Diagram.newStep(displayedList, selectedList, false);

    grid.retainOnlyDisplayedKnotRastersOnMovablePane();

    assertTrue(movablePane.getChildren().contains(ivD), "displayed knot raster must stay on the pane");
    assertFalse(movablePane.getChildren().contains(ivS), "selected knot raster at origin must be removed before drag moves");
    assertFalse(movablePane.getChildren().contains(orphan), "stale ImageView not owned by displayed knots must be removed");
  }

  @Test
  void retainOnlyDisplayedKnotRastersOnMovablePane_preservesDrawingPreviewImageView() {
    WritableImage imgD = new WritableImage(2, 2);
    WritableImage imgDraw = new WritableImage(2, 2);
    ImageView ivD = new ImageView(imgD);
    ImageView preview = new ImageView(imgDraw);
    GridEvents.setCurrentImageView(preview);

    Knot displayed = new Knot(1, 1, Optional.empty(), Optional.of("d"), Optional.of(Color.GREEN), ivD);
    movablePane.getChildren().addAll(ivD, preview);

    List<Knot> displayedList = new ArrayList<>(List.of(displayed));
    Diagram.newStep(displayedList, new ArrayList<>(), false);

    grid.retainOnlyDisplayedKnotRastersOnMovablePane();

    assertTrue(movablePane.getChildren().contains(ivD));
    assertTrue(movablePane.getChildren().contains(preview), "drawing preview ghost must not be stripped");
    GridEvents.setCurrentImageView(null);
  }
}

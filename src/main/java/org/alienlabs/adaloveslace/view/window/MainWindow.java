package org.alienlabs.adaloveslace.view.window;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Diagram;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.util.Events;
import org.alienlabs.adaloveslace.util.NodeUtil;
import org.alienlabs.adaloveslace.view.component.grid.OptionalDotGrid;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.ParentGridStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.alienlabs.adaloveslace.App.resourceBundle;
import static org.alienlabs.adaloveslace.business.model.Diagram.newStep;

public class MainWindow {

  public static final double NEW_KNOT_GAP  = 15d;
  public static final String LANGUAGE = "Language";
  public static final String TOOL = "Tool";
  public static final String EDIT = "Edit";
  public static final String FILE = "File";

  private OptionalDotGrid optionalDotGrid;
  private TilePane footer;

  public static final String SAVE_FILE      = "Save";

  public static final String SAVE_FILE_AS   = "SaveAs";

  public static final String FRENCH         = "Français";

  public static final String ENGLISH        = "English";

  public static final String LOAD_FILE      = "Load";

  public static final String EXPORT_IMAGE   = "ExportAnImage";

  public static final String QUIT_APP       = "Quit";

  public static final String UNDO_KNOT      = "UndoKnot";

  public static final String REDO_KNOT      = "RedoKnot";

  public static final String RESET_DIAGRAM  = "ResetDiagram";

  public static final String MOUSE_CLICKED  = "MOUSE_CLICKED";

  public static final KeyCodeCombination SAVE_AS_KEY_COMBINATION = new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN);

  private StackPane grid;

  private static final Logger logger        = LoggerFactory.getLogger(MainWindow.class);

  public MainWindow() {
    // Just to be able to unit test code using the UI without effectively instantiating the UI
  }

  public TilePane createFooter(String javafxVersion, String javaVersion) {
    footer = new TilePane(Orientation.VERTICAL);
    footer.setPrefHeight(100);
    footer.setPrefWidth(Double.MAX_VALUE);
    footer.getStyleClass().add("footer");

    footer.getChildren().addAll(new Label(resourceBundle.getString("Pitch")));
    footer.getChildren().addAll(new Label(resourceBundle.getString("Copyright")));
    footer.getChildren().addAll(new Label(resourceBundle.getString("License")));
    footer.getChildren().addAll(new Label("JavaFX " + javafxVersion + resourceBundle.getString("RunningWith") + javaVersion));
    footer.setAlignment(Pos.BOTTOM_CENTER);

    return footer;
  }

  public StackPane createGrid(App app, final double width, final double height, final Diagram diagram, final Pane canvas) {
    if (width == 0d || height == 0d) {
      this.optionalDotGrid = new OptionalDotGrid(app, diagram, canvas);
    } else {
      this.optionalDotGrid = new OptionalDotGrid(app, width, height, diagram, canvas);
    }

    grid = new StackPane(this.optionalDotGrid);
    app.setGridStrategy(new ParentGridStrategy(app, this.getOptionalDotGrid().getGridPane()));

    if (width != 0d && height != 0d) {
      grid.setPrefWidth(width);
      grid.setPrefHeight(height);
    }

    grid.setAlignment(Pos.TOP_LEFT);

    return grid;
  }

  public void onMainWindowClicked(final App app, final Pane movablePane) {
    movablePane.addEventHandler(MouseEvent.MOUSE_CLICKED, Events.getMouseClickEventHandler(app));
    movablePane.addEventHandler(MouseEvent.MOUSE_MOVED, Events.getGridHoverEventHandler(app));
    movablePane.setOnMouseExited(Events.getGridHoverExitEventHandler(app));
    movablePane.setOnMousePressed(Events.getMouseRightClickEventHandler(app));
    movablePane.setOnMouseDragged(Events.getGridDraggedEventHandler(app));
  }

  public void onClickWithSelectionMode(App app) {
    Iterator<Knot> it = optionalDotGrid.getDiagram().getCurrentStep().getAllVisibleKnots().iterator();
    boolean hasClickedOnAGivenKnot = false;
    List<Knot> displayedKnots = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots());
    List<Knot> selectedKnots = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots());

    // We iterate on the Knots as long as they are still Knots left to iterate
    // And we stop at the first clicked Knot
    while (it.hasNext()) {
      Knot knot = it.next();

      hasClickedOnAGivenKnot = new NodeUtil().isMouseOverKnot(knot);

      if (hasClickedOnAGivenKnot && (knot.getSelection() == null)) {
        logger.debug("Clicked Knot {} in order to select it",
                knot.getPattern().isPresent() ?
                        knot.getPattern().get().getFilename() :
                        knot.getText().get());

        // If the "Control" key is pressed, we are in multi-selection mode
        if (!app.getCurrentlyActiveKeys().containsKey(KeyCode.CONTROL)) {
          Knot copiedKnot = new NodeUtil().copyKnot(knot);
          removeNodeAndDecorationsForNowDisplayedKnots(app, selectedKnots);
          displayedKnots.addAll(new ArrayList<>(selectedKnots));
          displayedKnots.remove(knot);
          selectedKnots.clear();
          selectedKnots.add(copiedKnot);

          app.getOptionalDotGrid().getDiagram().setCurrentKnot(copiedKnot);
          newStep(displayedKnots, selectedKnots, true);
        } else {
          Knot copiedKnot = new NodeUtil().copyKnot(knot);
          selectedKnots.add(copiedKnot);
          displayedKnots.remove(knot);

          app.getOptionalDotGrid().getDiagram().setCurrentKnot(copiedKnot);
          hideHandlesForNotSelectedKnots(app, displayedKnots);
          newStep(displayedKnots, selectedKnots, true);
        }

        break;
      } else if (hasClickedOnAGivenKnot) {
        logger.debug("Clicked Knot displayed {}, pattern {} in order to unselect it",
          app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots().contains(knot),
                knot.getPattern().isPresent() ? knot.getPattern().get().getFilename() : knot.getText().get());
        logger.debug("Clicked Knot selected {}, pattern {} in order to unselect it",
          app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().contains(knot),
                knot.getPattern().isPresent() ? knot.getPattern().get().getFilename() : knot.getText().get());

        // If the "Control" key is pressed, we are in multi-selection mode
        if (!app.getCurrentlyActiveKeys().containsKey(KeyCode.CONTROL)) {
          Knot copiedKnot = new NodeUtil().copyKnot(knot);
          displayedKnots.addAll(new ArrayList<>(selectedKnots));
          selectedKnots.clear();
          selectedKnots.add(copiedKnot);
          displayedKnots.remove(knot);

          app.getOptionalDotGrid().getDiagram().setCurrentKnot(copiedKnot);
          hideHandlesForNotSelectedKnots(app, displayedKnots);
          newStep(displayedKnots, selectedKnots, true);

          break;
        } else {
          Knot copiedKnot = new NodeUtil().copyKnot(knot);
          copiedKnot.setSelection(null);
          selectedKnots.remove(knot);
          displayedKnots.remove(knot);
          displayedKnots.add(copiedKnot);

          app.getOptionalDotGrid().getDiagram().setCurrentKnot(copiedKnot);
          hideHandlesForNotSelectedKnots(app, displayedKnots);
          newStep(displayedKnots, selectedKnots, true);
          break;
        }
      }
    }

    // If we have clicked elsewhere, we deselect all knots
    if (!hasClickedOnAGivenKnot) {
      displayedKnots.addAll(selectedKnots.stream().map(knot -> new NodeUtil().copyKnot(knot)).toList());
      removeNodeAndDecorationsForNowDisplayedKnots(app, displayedKnots);
      selectedKnots.clear();

      newStep(displayedKnots, selectedKnots, true);
    }
  }

  private static void removeNodeAndDecorationsForNowDisplayedKnots(App app, List<Knot> nowDisplayedKnots) {
    nowDisplayedKnots.forEach(k -> {
      app.getMovablePane().getChildren().remove(k.getSelection());
      k.setSelection(null);
      app.getMovablePane().getChildren().remove(k.getHovered());
      k.setHovered(null);
      app.getMovablePane().getChildren().remove(k.getHandle());
      k.setHandle(null);
      app.getMovablePane().getChildren().remove(k.getImageView());
    });
  }

  private void hideHandlesForNotSelectedKnots(App app, List<Knot> displayedKnots) {
    for (Knot knot : displayedKnots) {
      if (knot.getHandle() != null) {
        app.getMovablePane().getChildren().remove(knot.getHandle());
        knot.setHandle(null);
      }
    }
  }

  public void onClickWithDeletionMode(App app, Diagram diagram) {
    for (Knot knot : app.getOptionalDotGrid().getDiagram().getCurrentStep().getAllVisibleKnots()) {
      if (!knot.isSelectable()) {
        continue;
      }

      if (new NodeUtil().isMouseOverKnot(knot)) {
        removeKnotIfClicked(app, diagram, knot);
        break;
      }
    }
  }

  private void removeKnotIfClicked(App app, Diagram diagram, Knot knot) {
    app.getMovablePane().getChildren().remove(knot.getImageView());
    app.getMovablePane().getChildren().remove(knot.getHovered());
    app.getMovablePane().getChildren().remove(knot.getHandle());
    app.getMovablePane().getChildren().remove(knot.getSelection());
    app.getOptionalDotGrid().getDiagram().deleteKnotDecorationsFromFollowingSteps(app, knot);

    List<Knot> displayedKnotsToFilterOut = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots());
    displayedKnotsToFilterOut.remove(knot);
    List<Knot> selectedKnotsToFilterOut = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots());
    selectedKnotsToFilterOut.remove(knot);

    newStep(displayedKnotsToFilterOut, selectedKnotsToFilterOut, true);

    logger.debug("Removing Knot {}, current index = {}", knot, diagram.getCurrentStepIndex());
  }

  @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
    value = "EI_EXPOSE_REP",
    justification = "Copying a CanvasWithOptionalDotGrid, which is a stage, would mean working with another window")
  public OptionalDotGrid getOptionalDotGrid() {
    return this.optionalDotGrid;
  }

  public StackPane getGrid() {
    return this.grid;
  }

  public void setOptionalDotGrid(OptionalDotGrid optionalDotGrid) {
    this.optionalDotGrid = optionalDotGrid;
  }

}

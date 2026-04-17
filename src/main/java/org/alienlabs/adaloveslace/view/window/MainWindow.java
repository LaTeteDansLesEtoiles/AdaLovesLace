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
import org.alienlabs.adaloveslace.domain.Diagram;
import org.alienlabs.adaloveslace.domain.Knot;
import org.alienlabs.adaloveslace.util.NodeUtil;
import org.alienlabs.adaloveslace.view.component.grid.OptionalDotGrid;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.ParentGridStrategy;
import org.alienlabs.adaloveslace.view.window.event.GridEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.alienlabs.adaloveslace.App.resourceBundle;
import static org.alienlabs.adaloveslace.domain.Diagram.newStep;
import static org.alienlabs.adaloveslace.domain.Knot.NEW_TEXT;

public class MainWindow {

  public static final double NEW_KNOT_GAP  = 25d;

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

  public static final String SELECT_ALL      = "SelectAll";

  public static final String RESET_DIAGRAM  = "ResetDiagram";

  public static final String MOUSE_CLICKED  = "MOUSE_CLICKED";

  public static final KeyCodeCombination SAVE_AS_KEY_COMBINATION = new KeyCodeCombination(KeyCode.T, KeyCombination.CONTROL_DOWN);

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

  public void addGridEvents(final App app, final Pane movablePane) {
    movablePane.addEventHandler(MouseEvent.MOUSE_CLICKED, GridEvents.getMouseClickEventHandler(app));
    movablePane.addEventHandler(MouseEvent.MOUSE_MOVED, GridEvents.getGridHoverEventHandler(app));
    movablePane.setOnMouseExited(GridEvents.getGridHoverExitEventHandler(app));
    movablePane.setOnMousePressed(GridEvents.getMouseRightClickEventHandler(app));
    movablePane.setOnMouseDragged(GridEvents.getGridDraggedEventHandler(app));
    movablePane.setOnMouseReleased(GridEvents.getGridDragReleasedEventHandler(app));
  }

  public void onClickWithSelectionMode(App app, javafx.scene.input.MouseEvent event) {
    Iterator<Knot> it = optionalDotGrid.getDiagram().getCurrentStep().getAllVisibleKnots().iterator();
    boolean hasClickedOnAGivenKnot = false;
    List<Knot> displayedKnots = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots());
    List<Knot> selectedKnots = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots());
    NodeUtil nodeUtil = new NodeUtil();
    
    // Check if Control is pressed directly from the MouseEvent
    boolean isControlDown = event.isControlDown();
    
    logger.info("Multi-selection check: isControlDown={}, currentlyActiveKeys contains CONTROL={}", 
            isControlDown, app.getCurrentlyActiveKeys().containsKey(KeyCode.CONTROL));

    // We iterate on the Knots as long as they are still Knots left to iterate
    // And we stop at the first clicked Knot
    while (it.hasNext()) {
      Knot knot = it.next();

      // Headless TestFX doesn't always update the `isHover()` state before the click.
      // Use the click position against the knot image view bounds instead of hover-only detection.
      javafx.geometry.Point2D clickLocal =
          knot.getImageView().sceneToLocal(event.getSceneX(), event.getSceneY());
      boolean clickedByBounds = knot.getImageView().getBoundsInLocal().contains(clickLocal);
      hasClickedOnAGivenKnot = clickedByBounds || nodeUtil.isMouseOverKnot(knot);
      
      // Check if the knot is already selected by testing membership in selectedKnots
      boolean isAlreadySelected = selectedKnots.stream()
              .anyMatch(k -> k.getImageView() == knot.getImageView());

      if (hasClickedOnAGivenKnot && !isAlreadySelected) {
        logger.info("Clicked Knot {} in order to select it",
                knot.getPattern().isPresent() ?
                        knot.getPattern().get().getFilename() :
                        knot.getText().get());

        // If the "Control" key is pressed, we are in multi-selection mode
        if (!isControlDown) {
          Knot copiedKnot = knot;
          removeNodeAndDecorationsForNowDisplayedKnots(app, selectedKnots);

          displayedKnots.addAll(new ArrayList<>(selectedKnots));
          displayedKnots.remove(knot);
          selectedKnots.clear();
          configureSingleSelectedKnot(app, selectedKnots, copiedKnot);

          newStep(displayedKnots, selectedKnots, true);
        } else {
          // Multi-selection mode: add the knot to the existing selection
          logger.info("Multi-selection: adding knot to selection. Current selectedKnots size: {}", selectedKnots.size());
          
          // Keep current selected knots and add the clicked one (no visual copy in selection mode)
          List<Knot> newSelectedKnots = new ArrayList<>(selectedKnots);
          for (Knot alreadySelected : selectedKnots) {
            displayedKnots.remove(alreadySelected);
          }
          
          Knot copiedKnot = knot;
          
          // Remove the original knot from displayedKnots before adding the copy
          displayedKnots.remove(knot);
          
          // Add the copy of the new knot to selectedKnots
          newSelectedKnots.add(copiedKnot);
          logger.info("Multi-selection: after adding, newSelectedKnots size: {}", newSelectedKnots.size());

          // Do not modify the current step; create a new step with all selected knots
          app.getOptionalDotGrid().getDiagram().setCurrentKnot(copiedKnot);
          hideHandlesForNotSelectedKnots(app, displayedKnots);
          newStep(displayedKnots, newSelectedKnots, true);
        }

        break;
      } else if (hasClickedOnAGivenKnot) {
        logger.info("Clicked Knot displayed {}, pattern {} in order to unselect it",
          app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots().contains(knot),
                knot.getPattern().isPresent() ? knot.getPattern().get().getFilename() : knot.getText().get());
        logger.info("Clicked Knot selected {}, pattern {} in order to unselect it",
          app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().contains(knot),
                knot.getPattern().isPresent() ? knot.getPattern().get().getFilename() : knot.getText().get());

        // If the "Control" key is pressed, we are in multi-selection mode
        if (!isControlDown) {
          Knot copiedKnot = knot;
          displayedKnots.addAll(new ArrayList<>(selectedKnots));
          displayedKnots.remove(knot);
          configureSingleSelectedKnot(app, selectedKnots, copiedKnot);

          hideHandlesForNotSelectedKnots(app, displayedKnots);
          newStep(displayedKnots, selectedKnots, true);

          break;
        } else {
          // Multi-selection mode: unselect this knot but keep the others selected
          
          // Keep all selected knots except the clicked one
          List<Knot> newSelectedKnots = new ArrayList<>();
          for (Knot alreadySelected : selectedKnots) {
            // Do not copy the knot we want to unselect
            if (alreadySelected.getImageView() == knot.getImageView()) {
              continue; // Skip this knot; it will be unselected
            }
            newSelectedKnots.add(alreadySelected);
            
            // Remove selected knots from displayedKnots if present
            displayedKnots.remove(alreadySelected);
          }
          
          Knot copiedKnot = knot;
          copiedKnot.setSelection(null);
          
          displayedKnots.remove(knot);
          displayedKnots.add(copiedKnot);

          // Use the newSelectedKnots list (with the knot removed)
          // If other knots remain selected, keep the last one as currentKnot
          if (!newSelectedKnots.isEmpty()) {
            app.getOptionalDotGrid().getDiagram().setCurrentKnot(newSelectedKnots.get(newSelectedKnots.size() - 1));
          } else {
            app.getOptionalDotGrid().getDiagram().setCurrentKnot(copiedKnot);
          }
          hideHandlesForNotSelectedKnots(app, displayedKnots);
          newStep(displayedKnots, newSelectedKnots, true);
          break;
        }
      }
    }

    // If we have clicked elsewhere, we deselect all knots
    if (!hasClickedOnAGivenKnot) {
      displayedKnots.addAll(selectedKnots);
      removeNodeAndDecorationsForNowDisplayedKnots(app, displayedKnots);
      selectedKnots.clear();
      GridEvents.setCurrentImageView(null);

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

  private static void configureSingleSelectedKnot(App app, List<Knot> selectedKnots, Knot knot) {
    selectedKnots.clear();
    selectedKnots.add(knot);

    List<Knot> selectedKnotsOfStep = new ArrayList<>();
    selectedKnotsOfStep.add(knot);
    app.getOptionalDotGrid().getDiagram().getCurrentStep().setSelectedKnots(selectedKnotsOfStep);
    app.getOptionalDotGrid().getDiagram().setCurrentKnot(knot);
    if (!NEW_TEXT.toString().contentEquals(knot.getText().get())) {
      knot.setTypedText(new StringBuilder(knot.getText().get()));
    }
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

    logger.info("Removing Knot {}, current index = {}", knot, diagram.getCurrentStepIndex());
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

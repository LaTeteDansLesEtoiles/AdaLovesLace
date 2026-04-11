package org.alienlabs.adaloveslace.view.component.button.geometrywindow;

import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.Knot;
import org.alienlabs.adaloveslace.domain.enumeration.MouseMode;
import org.alienlabs.adaloveslace.util.NodeUtil;
import org.alienlabs.adaloveslace.view.window.event.GridEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.alienlabs.adaloveslace.App.TOOLTIPS_DURATION;
import static org.alienlabs.adaloveslace.App.resourceBundle;
import static org.alienlabs.adaloveslace.domain.Diagram.newStep;
import static org.alienlabs.adaloveslace.view.window.MainWindow.NEW_KNOT_GAP;
import static org.alienlabs.adaloveslace.view.window.ToolboxWindow.GEOMETRY_BUTTONS_HEIGHT;

public class DuplicationButton extends ToggleButton {

  public static final String DUPLICATION_BUTTON_NAME  = "DUPLICATION_BUTTON_NAME";

  private static final Logger logger                  = LoggerFactory.getLogger(DuplicationButton.class);

  public DuplicationButton(App app, String buttonLabel) {
    super(buttonLabel);
    // Ensure TestFX robot clicks trigger the same behavior.
    this.setOnAction(_ -> onSetDuplicationModeAction(app));
    this.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);
    this.setId("duplicationButton");

    final Tooltip tooltip = new Tooltip();
    tooltip.setText(resourceBundle.getString("DUPLICATION_BUTTON_TOOLTIP"));
    tooltip.setShowDuration(TOOLTIPS_DURATION);
    this.setTooltip(tooltip);
  }

  public static void onSetDuplicationModeAction(final App app) {
    logger.info("Duplicating");
    app.getOptionalDotGrid().getDiagram().setCurrentMode(MouseMode.DUPLICATION);

    List<Knot> displayedKnots = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots());
    List<Knot> selectedKnots = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots());
    List<Knot> selectedKnotsCopy = new ArrayList<>();

    for (Knot knot : selectedKnots) {
      Knot copiedKnot = new NodeUtil().copyKnotCloningImageView(knot);
      copiedKnot.setX(knot.getX() + NEW_KNOT_GAP);
      copiedKnot.setY(knot.getY() + NEW_KNOT_GAP);
      copiedKnot.getImageView().setLayoutX(knot.getX() + NEW_KNOT_GAP);
      copiedKnot.getImageView().setLayoutY(knot.getY() + NEW_KNOT_GAP);

      displayedKnots.add(knot);
      selectedKnotsCopy.add(copiedKnot);
    }

    newStep(displayedKnots, selectedKnotsCopy, true);
    GridEvents.removeEventsFromGrid(app);

    app.getToolboxWindow().getDrawingButton()     .setSelected(false);
    app.getToolboxWindow().getSelectionButton()   .setSelected(false);
    app.getToolboxWindow().getDeletionButton()    .setSelected(false);
    app.getToolboxWindow().getDuplicationButton() .setSelected(true);
  }

}

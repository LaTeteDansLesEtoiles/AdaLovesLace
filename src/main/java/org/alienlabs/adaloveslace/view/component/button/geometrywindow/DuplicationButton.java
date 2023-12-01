package org.alienlabs.adaloveslace.view.component.button.geometrywindow;

import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.business.model.MouseMode;
import org.alienlabs.adaloveslace.util.NodeUtil;
import org.alienlabs.adaloveslace.view.window.GeometryWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.alienlabs.adaloveslace.App.TOOLTIPS_DURATION;
import static org.alienlabs.adaloveslace.App.resourceBundle;
import static org.alienlabs.adaloveslace.business.model.Diagram.newStep;
import static org.alienlabs.adaloveslace.view.window.GeometryWindow.GEOMETRY_BUTTONS_HEIGHT;
import static org.alienlabs.adaloveslace.view.window.MainWindow.NEW_KNOT_GAP;

public class DuplicationButton extends ToggleButton {

  public static final String DUPLICATION_BUTTON_NAME  = "DUPLICATION_BUTTON_NAME";

  private static final Logger logger                  = LoggerFactory.getLogger(DuplicationButton.class);

  public DuplicationButton(App app, GeometryWindow window, String buttonLabel) {
    super(buttonLabel);
    this.setOnMouseClicked(event -> onSetDuplicationModeAction(app, window));
    this.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);

    final Tooltip tooltip = new Tooltip();
    tooltip.setText(resourceBundle.getString("DUPLICATION_BUTTON_TOOLTIP"));
    tooltip.setShowDuration(TOOLTIPS_DURATION);
    this.setTooltip(tooltip);
  }

  public static void onSetDuplicationModeAction(final App app, final GeometryWindow window) {
    logger.debug("Duplicating");
    app.getOptionalDotGrid().getDiagram().setCurrentMode(MouseMode.DUPLICATION);

    List<Knot> displayedKnots = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots());
    List<Knot> selectedKnots = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots());
    List<Knot> selectedKnotsCopy = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots());

    for (Knot knot : selectedKnots) {
      Knot copiedKnot = new NodeUtil().copyKnotCloningImageView(knot);
      copiedKnot.setX(knot.getX() + (NEW_KNOT_GAP * selectedKnots.size()));
      copiedKnot.setY(knot.getY() + (NEW_KNOT_GAP * selectedKnots.size()));
      copiedKnot.getImageView().setX(knot.getX() + (NEW_KNOT_GAP * selectedKnots.size()));
      copiedKnot.getImageView().setY(knot.getY() + (NEW_KNOT_GAP * selectedKnots.size()));

      displayedKnots.remove(knot);
      displayedKnots.remove(copiedKnot);

      selectedKnotsCopy.add(copiedKnot);
    }

    newStep(displayedKnots, selectedKnotsCopy, true);

    window.getDrawingButton()     .setSelected(false);
    window.getSelectionButton()   .setSelected(false);
    window.getDeletionButton()    .setSelected(false);
    window.getDuplicationButton() .setSelected(true);
  }

}

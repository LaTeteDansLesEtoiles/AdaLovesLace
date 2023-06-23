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

import java.util.Set;
import java.util.TreeSet;

import static org.alienlabs.adaloveslace.business.model.Diagram.newStep;
import static org.alienlabs.adaloveslace.view.window.GeometryWindow.GEOMETRY_BUTTONS_HEIGHT;
import static org.alienlabs.adaloveslace.view.window.MainWindow.NEW_KNOT_GAP;

public class DuplicationButton extends ToggleButton {

  public static final String DUPLICATION_BUTTON_NAME  = "DUPLICATION_BUTTON_NAME";
  public static final String BUTTON_TOOLTIP           = "Select this button then click on a\nknot in the canvas to copy it\n";

  private static final Logger logger                  = LoggerFactory.getLogger(DuplicationButton.class);

  public DuplicationButton(App app, GeometryWindow window, String buttonLabel) {
    super(buttonLabel);
    this.setOnMouseClicked(event -> onSetDuplicationModeAction(app, window));
    this.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);

    final Tooltip tooltip = new Tooltip();
    tooltip.setText(BUTTON_TOOLTIP);
    this.setTooltip(tooltip);
  }

  public static void onSetDuplicationModeAction(final App app, final GeometryWindow window) {
    logger.debug("Duplicating");
    app.getOptionalDotGrid().getDiagram().setCurrentMode(MouseMode.DUPLICATION);

    Set<Knot> displayedKnots = new TreeSet<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots());
    Set<Knot> selectedKnots = new TreeSet<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots());
    Set<Knot> selectedKnotsCopy = new TreeSet<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots());

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

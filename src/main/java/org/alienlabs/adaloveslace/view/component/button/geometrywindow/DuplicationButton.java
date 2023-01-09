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

import java.util.HashSet;
import java.util.Set;

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
    logger.info("Duplicating");
    app.getOptionalDotGrid().getDiagram().setCurrentMode(MouseMode.DUPLICATION);

    Set<Knot> copiedKnots = new HashSet<>();
    Set<Knot> newDisplayedKnots = new HashSet<>();

    for (Knot knot : app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots()) {
      Knot copiedKnot = new NodeUtil().copyKnotCloningImageView(knot);
      copiedKnot.setX(knot.getX() + NEW_KNOT_GAP);
      copiedKnot.setY(knot.getY() + NEW_KNOT_GAP);

      copiedKnots.add(copiedKnot);
    }

    newDisplayedKnots.addAll(app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots());
    newDisplayedKnots.addAll(app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots());

    app.getDiagram().addKnotsToStep(newDisplayedKnots, copiedKnots);
    app.getOptionalDotGrid().layoutChildren();

    window.getDrawingButton()     .setSelected(false);
    window.getSelectionButton()   .setSelected(false);
    window.getDeletionButton()    .setSelected(false);
    window.getDuplicationButton() .setSelected(true);
  }

}

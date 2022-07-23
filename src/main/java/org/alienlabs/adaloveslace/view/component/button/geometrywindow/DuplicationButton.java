package org.alienlabs.adaloveslace.view.component.button.geometrywindow;

import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.business.model.MouseMode;
import org.alienlabs.adaloveslace.view.window.GeometryWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.alienlabs.adaloveslace.view.window.GeometryWindow.GEOMETRY_BUTTONS_HEIGHT;

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
    logger.info("Setting duplication mode");
    app.getOptionalDotGrid().getDiagram().setCurrentMode(MouseMode.DUPLICATION);

    // We use a list of copied knots in order not to
    // concurrently modify the list of knots already present
    // on the canvas
    List<Knot> copiedKnots = new ArrayList<>();
    for (Knot knot : app.getDiagram().getKnots()) {
      if (app.getOptionalDotGrid().getAllSelectedKnots().contains(knot)) {
        Knot copiedKnot = app.getMainWindow().duplicateKnot(knot.getX(), knot.getY(), knot);
        copiedKnots.add(copiedKnot);
        app.getOptionalDotGrid().circleSelectedKnot(copiedKnot);
      }
    }

    app.getOptionalDotGrid().clearSelections();
    app.getOptionalDotGrid().getAllSelectedKnots().addAll(copiedKnots);
    copiedKnots.stream().forEach(knot -> app.getOptionalDotGrid().getDiagram().addKnot(knot));
    app.getOptionalDotGrid().layoutChildren();

    window.getDrawingButton()     .setSelected(false);
    window.getSelectionButton()   .setSelected(false);
    window.getDeletionButton()    .setSelected(false);
    window.getDuplicationButton() .setSelected(true);
  }

}

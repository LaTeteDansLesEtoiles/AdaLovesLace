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
import java.util.Set;
import java.util.TreeSet;

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

    List<Knot> allElements = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots());
    allElements.removeAll(app.getOptionalDotGrid().getAllSelectedKnots());

    // We use a list of copied knots in order not to
    // concurrently modify the list of knots already present
    // on the canvas
    Set<Knot> copiedKnots = new TreeSet<>();

    for (Knot knot : app.getOptionalDotGrid().getAllSelectedKnots()) {
      Knot copiedKnot = app.getMainWindow().duplicateKnot(app, knot.getX(), knot.getY(), knot);
      knot.getImageView().toBack();
      copiedKnots.add(copiedKnot);
      allElements.add(knot);
      app.getOptionalDotGrid().getDiagram().deleteNodesFromCurrentStep(app, knot);
      app.getOptionalDotGrid().getDiagram().deleteNodesFromCurrentStep(app, copiedKnot);
    }

    app.getOptionalDotGrid().getAllSelectedKnots().clear();
    app.getOptionalDotGrid().getAllSelectedKnots().addAll(app.getDiagram().addKnotWithStep(app, copiedKnots.stream().toList()));
    app.getOptionalDotGrid().layoutChildren();

    app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots().addAll(allElements);

    window.getDrawingButton()     .setSelected(false);
    window.getSelectionButton()   .setSelected(false);
    window.getDeletionButton()    .setSelected(false);
    window.getDuplicationButton() .setSelected(true);
  }

}

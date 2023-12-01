package org.alienlabs.adaloveslace.view.component.button.geometrywindow;

import javafx.scene.control.Tooltip;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.business.model.MouseMode;
import org.alienlabs.adaloveslace.util.NodeUtil;
import org.alienlabs.adaloveslace.view.component.button.ImageButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.alienlabs.adaloveslace.App.TOOLTIPS_DURATION;
import static org.alienlabs.adaloveslace.App.resourceBundle;
import static org.alienlabs.adaloveslace.business.model.Diagram.newStep;
import static org.alienlabs.adaloveslace.view.window.GeometryWindow.GEOMETRY_BUTTONS_HEIGHT;

public class HorizontalFlippingButton extends ImageButton {

  public static final String HORIZONTAL_FLIPPING_BUTTON_NAME    = "HORIZONTAL_FLIPPING_BUTTON_NAME";

  private static final Logger logger                            = LoggerFactory.getLogger(HorizontalFlippingButton.class);

  public HorizontalFlippingButton(App app, String buttonLabel) {
    super(buttonLabel);
    this.setOnMouseClicked(event -> onFlipHorizontallyAction(app));
    this.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);

    final Tooltip tooltip = new Tooltip();
    tooltip.setText(resourceBundle.getString("HORIZONTAL_FLIPPING_BUTTON_TOOLTIP"));
    tooltip.setShowDuration(TOOLTIPS_DURATION);
    this.setTooltip(tooltip);

    buildButtonImage("flip_horizontally.png");
  }

  public static void onFlipHorizontallyAction(final App app) {
    logger.debug("Flipping horizontally");
    app.getOptionalDotGrid().getDiagram().setCurrentMode(MouseMode.MIRROR);

    List<Knot> displayedKnots = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots());
    List<Knot> selectedKnots = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots());
    List<Knot> selectedKnotsCopy = new ArrayList<>();

    for (Knot knot : selectedKnots) {
        Knot copy = new NodeUtil().copyKnot(knot);
        copy.setFlippedHorizontally(!knot.isFlippedHorizontally());
        selectedKnotsCopy.add(copy);
    }

    displayedKnots.removeAll(selectedKnots);

    newStep(displayedKnots, selectedKnotsCopy, true);
  }

}

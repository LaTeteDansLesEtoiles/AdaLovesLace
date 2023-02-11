package org.alienlabs.adaloveslace.view.component.button.geometrywindow;

import javafx.scene.control.Tooltip;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.business.model.MouseMode;
import org.alienlabs.adaloveslace.util.NodeUtil;
import org.alienlabs.adaloveslace.view.component.button.ImageButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.TreeSet;

import static org.alienlabs.adaloveslace.view.window.GeometryWindow.GEOMETRY_BUTTONS_HEIGHT;

public class VerticalFlippingButton extends ImageButton {

  public static final String VERTICAL_FLIPPING_BUTTON_NAME      = "VERTICAL_FLIPPING_BUTTON_NAME";
  public static final String BUTTON_TOOLTIP                     = "Flip horizontally\ncurrent selection\n";

  private static final Logger logger                            = LoggerFactory.getLogger(VerticalFlippingButton.class);

  public VerticalFlippingButton(App app, String buttonLabel) {
    super(buttonLabel);
    this.setOnMouseClicked(event -> onFlipVerticallyAction(app));
    this.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);

    final Tooltip tooltip = new Tooltip();
    tooltip.setText(BUTTON_TOOLTIP);
    this.setTooltip(tooltip);

    buildButtonImage("flip_vertically.png");
  }

  public static void onFlipVerticallyAction(final App app) {
    logger.info("Flipping vertically");
    app.getOptionalDotGrid().getDiagram().setCurrentMode(MouseMode.MIRROR);

    Set<Knot> newSelectedKnots = new TreeSet<>();
    Set<Knot> newDisplayedKnots = new TreeSet<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots());

    for (Knot knot : app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots()) {
      Knot copy = new NodeUtil().copyKnot(knot);
      copy.setFlippedVertically(!knot.isFlippedVertically());
      newSelectedKnots.add(copy);
      newDisplayedKnots.add(copy);
      newDisplayedKnots.remove(knot);
    }

    app.getOptionalDotGrid().getDiagram().addKnotsToStep(newDisplayedKnots, newSelectedKnots);

    app.getOptionalDotGrid().layoutChildren();
  }

}

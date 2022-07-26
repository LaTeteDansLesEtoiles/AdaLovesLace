package org.alienlabs.adaloveslace.view.component.button.geometrywindow;

import javafx.scene.control.Tooltip;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.util.NodeUtil;
import org.alienlabs.adaloveslace.view.component.button.ImageButton;
import org.alienlabs.adaloveslace.view.window.GeometryWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.alienlabs.adaloveslace.view.window.GeometryWindow.GEOMETRY_BUTTONS_HEIGHT;

public class HorizontalFlippingButton extends ImageButton {

  public static final String HORIZONTAL_FLIPPING_BUTTON_NAME    = "HORIZONTAL_FLIPPING_BUTTON_NAME";
  public static final String BUTTON_TOOLTIP                     = "Flip horizontally\ncurrent selection\n";

  private static final Logger logger                            = LoggerFactory.getLogger(HorizontalFlippingButton.class);

  public HorizontalFlippingButton(App app, GeometryWindow window, String buttonLabel) {
    super(buttonLabel);
    this.setOnMouseClicked(event -> onFlipHorizontallyAction(app, window));
    this.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);

    final Tooltip tooltip = new Tooltip();
    tooltip.setText(BUTTON_TOOLTIP);
    this.setTooltip(tooltip);

    buildButtonImage("flip_horizontally.png");
  }

  public static void onFlipHorizontallyAction(final App app, final GeometryWindow window) {
    logger.info("Flipping horizontally");

    List<Knot> knots = new ArrayList<>();

    for (Knot knot : app.getOptionalDotGrid().getAllSelectedKnots()) {
      Knot copy = new NodeUtil().copyKnot(knot);
      copy.setFlippedHorizontally(!knot.isFlippedHorizontally());
      knots.add(copy);
    }

    app.getDiagram().addKnotWithStepFiltering(app, knots, app.getOptionalDotGrid().getAllSelectedKnots());
    app.getOptionalDotGrid().layoutChildren();
  }

}

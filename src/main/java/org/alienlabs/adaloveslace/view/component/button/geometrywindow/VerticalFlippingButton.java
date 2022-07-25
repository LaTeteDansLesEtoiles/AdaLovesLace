package org.alienlabs.adaloveslace.view.component.button.geometrywindow;

import javafx.scene.control.Tooltip;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.view.component.button.ImageButton;
import org.alienlabs.adaloveslace.view.window.GeometryWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.alienlabs.adaloveslace.view.window.GeometryWindow.GEOMETRY_BUTTONS_HEIGHT;

public class VerticalFlippingButton extends ImageButton {

  public static final String VERTICAL_FLIPPING_BUTTON_NAME      = "VERTICAL_FLIPPING_BUTTON_NAME";
  public static final String BUTTON_TOOLTIP                     = "Flip horizontally\ncurrent selection\n";

  private static final Logger logger                            = LoggerFactory.getLogger(VerticalFlippingButton.class);

  public VerticalFlippingButton(App app, GeometryWindow window, String buttonLabel) {
    super(buttonLabel);
    this.setOnMouseClicked(event -> onFlipVerticallyAction(app, window));
    this.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);

    final Tooltip tooltip = new Tooltip();
    tooltip.setText(BUTTON_TOOLTIP);
    this.setTooltip(tooltip);

    buildButtonImage("flip_vertically.png");
  }

  public static void onFlipVerticallyAction(final App app, final GeometryWindow window) {
    logger.info("Flipping vertically");

    for (Knot knot : app.getOptionalDotGrid().getAllSelectedKnots()) {
      knot.setFlippedVertically(!knot.isFlippedVertically());
    }

    app.getOptionalDotGrid().layoutChildren();
  }

}

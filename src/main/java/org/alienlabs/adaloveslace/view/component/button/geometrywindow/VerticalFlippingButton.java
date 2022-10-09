package org.alienlabs.adaloveslace.view.component.button.geometrywindow;

import javafx.scene.control.Tooltip;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.business.model.MouseMode;
import org.alienlabs.adaloveslace.util.NodeUtil;
import org.alienlabs.adaloveslace.view.component.button.ImageButton;
import org.alienlabs.adaloveslace.view.window.GeometryWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.TreeSet;

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
    app.getOptionalDotGrid().getDiagram().setCurrentMode(MouseMode.MIRROR);

    Set<Knot> knots = new TreeSet<>();

    app.getDiagram().addKnotsToStep(
      app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots(),
      app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots());

    for (Knot knot : app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots()) {
      Knot copy = new NodeUtil().copyKnot(knot);
      knot.setVisible(false);
      copy.setFlippedVertically(!knot.isFlippedVertically());
      knots.add(copy);
    }

    app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().clear();
    app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().addAll(knots);

    app.getOptionalDotGrid().layoutChildren();
  }

}

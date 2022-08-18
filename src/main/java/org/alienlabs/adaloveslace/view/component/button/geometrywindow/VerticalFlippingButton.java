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

import java.util.ArrayList;
import java.util.List;
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

    List<Knot> allElements = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots());
    allElements.removeAll(app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots());

    Set<Knot> knots = new TreeSet<>();

    for (Knot knot : app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots()) {
      Knot copy = new NodeUtil().copyKnot(knot);
      copy.setFlippedVertically(!knot.isFlippedVertically());
      knots.add(copy);
      app.getOptionalDotGrid().getDiagram().deleteNodesFromCurrentStep(app, knot);
      app.getOptionalDotGrid().getDiagram().deleteNodesFromCurrentStep(app, copy);
    }

    app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().clear();
    app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().addAll(app.getDiagram().addKnotsToStep(app, knots));
    app.getOptionalDotGrid().layoutChildren();

    app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots().addAll(allElements);
  }

}

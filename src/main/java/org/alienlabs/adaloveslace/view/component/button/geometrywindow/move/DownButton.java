package org.alienlabs.adaloveslace.view.component.button.geometrywindow.move;

import javafx.scene.control.Button;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.business.model.MouseMode;
import org.alienlabs.adaloveslace.util.NodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.alienlabs.adaloveslace.business.model.Diagram.newStep;
import static org.alienlabs.adaloveslace.view.window.GeometryWindow.GEOMETRY_BUTTONS_HEIGHT;

public class DownButton extends Button {

  private static final Logger logger        = LoggerFactory.getLogger(DownButton.class);

  public DownButton(App app) {
    this.setOnMouseClicked(event -> onMoveKnotDownAction(app));
    this.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);
  }

  public static void onMoveKnotDownAction(App app) {
    app.getOptionalDotGrid().getDiagram().setCurrentMode(MouseMode.MOVE);

    List<Knot> displayedKnots = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots());
    List<Knot> selectedKnots = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots());
    List<Knot> copiedKnots = new ArrayList<>();
    List<Knot> toRemoveKnots = new ArrayList<>();

    for (Knot knot : selectedKnots) {
      knot.setY(knot.getY() + FastMoveModeButton.getMoveSpeed());
      Knot copiedKnot = new NodeUtil().copyKnot(knot);

      if (copiedKnot.getHandle() != null) {
        copiedKnot.getHandle().setLayoutY(copiedKnot.getHandle().getLayoutY() + FastMoveModeButton.getMoveSpeed());
      }

      toRemoveKnots.add(knot);
      app.getOptionalDotGrid().getRoot().getChildren().remove(knot.getImageView());
      copiedKnots.add(copiedKnot);

      logger.debug("Moving down knot {}", knot);
    }

    selectedKnots.removeAll(toRemoveKnots);
    displayedKnots.removeAll(toRemoveKnots);
    selectedKnots.addAll(copiedKnots);
    newStep(displayedKnots, selectedKnots, true);
  }

}

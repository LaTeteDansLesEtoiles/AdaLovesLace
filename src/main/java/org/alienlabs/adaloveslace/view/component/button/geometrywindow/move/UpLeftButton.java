package org.alienlabs.adaloveslace.view.component.button.geometrywindow.move;

import javafx.scene.control.Button;
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

public class UpLeftButton extends Button {

  private static final Logger logger      = LoggerFactory.getLogger(UpLeftButton.class);

  public UpLeftButton(App app, GeometryWindow window) {
    this.setOnMouseClicked(event -> onMoveKnotUpLeftAction(app, window));
    this.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);
  }

  public static void onMoveKnotUpLeftAction(App app, GeometryWindow window) {
    app.getOptionalDotGrid().getDiagram().setCurrentMode(MouseMode.MOVE);

    List<Knot> allElements = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots());
    allElements.removeAll(app.getOptionalDotGrid().getAllSelectedKnots());

    Set<Knot> knots = new TreeSet<>();

    for (Knot knot : app.getOptionalDotGrid().getAllSelectedKnots()) {
      knot.setX(knot.getX() - FastMoveModeButton.getMoveSpeed());
      knot.setY(knot.getY() - FastMoveModeButton.getMoveSpeed());
      knots.add(knot);
      logger.debug("Moving up left knot {}", knot);
    }

    app.getOptionalDotGrid().getAllSelectedKnots().clear();
    app.getOptionalDotGrid().getAllSelectedKnots().addAll(app.getDiagram().addKnotWithStep(app, knots.stream().toList()));
    app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots().addAll(allElements);
    app.getOptionalDotGrid().layoutChildren();
  }

}

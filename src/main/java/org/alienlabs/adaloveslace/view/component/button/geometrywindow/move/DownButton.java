package org.alienlabs.adaloveslace.view.component.button.geometrywindow.move;

import javafx.scene.control.Button;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.util.MoveKnotVectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.alienlabs.adaloveslace.view.window.ToolboxWindow.GEOMETRY_BUTTONS_HEIGHT;

public class DownButton extends Button {

  private static final Logger logger        = LoggerFactory.getLogger(DownButton.class);

  public DownButton(App app) {
    this.setOnMouseClicked(event -> onMoveKnotDownAction(app));
    this.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);
  }

  public static void onMoveKnotDownAction(App app) {
    double speed = FastMoveModeButton.getMoveSpeed();
    MoveKnotActionUtil.moveSelectedKnots(
        app,
        MoveKnotVectors.Direction.DOWN.deltaX(speed),
        MoveKnotVectors.Direction.DOWN.deltaY(speed),
        "down",
        logger);
  }

}

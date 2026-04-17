package org.alienlabs.adaloveslace.view.component.button.geometrywindow.move;

import javafx.scene.control.Button;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.util.MoveKnotVectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.alienlabs.adaloveslace.view.window.ToolboxWindow.GEOMETRY_BUTTONS_HEIGHT;

public class UpRightButton extends Button {

  private static final Logger logger = LoggerFactory.getLogger(UpRightButton.class);

  public UpRightButton(App app) {
    this.setOnMouseClicked(event -> onMoveKnotUpRightAction(app));
    this.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);
  }

  public static void onMoveKnotUpRightAction(App app) {
    double speed = FastMoveModeButton.getMoveSpeed();
    MoveKnotActionUtil.moveSelectedKnots(
        app,
        MoveKnotVectors.Direction.UP_RIGHT.deltaX(speed),
        MoveKnotVectors.Direction.UP_RIGHT.deltaY(speed),
        "up right",
        logger);
  }

}

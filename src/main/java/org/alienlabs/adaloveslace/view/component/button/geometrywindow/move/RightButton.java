package org.alienlabs.adaloveslace.view.component.button.geometrywindow.move;

import javafx.scene.control.Button;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.util.MoveKnotVectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.alienlabs.adaloveslace.view.window.ToolboxWindow.GEOMETRY_BUTTONS_HEIGHT;

public class RightButton extends Button {

  private static final Logger logger          = LoggerFactory.getLogger(RightButton.class);

  public RightButton(App app) {
    this.setOnMouseClicked(event -> onMoveKnotRightAction(app));
    this.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);
  }

  public static void onMoveKnotRightAction(App app) {
    double speed = FastMoveModeButton.getMoveSpeed();
    MoveKnotActionUtil.moveSelectedKnots(
        app,
        MoveKnotVectors.Direction.RIGHT.deltaX(speed),
        MoveKnotVectors.Direction.RIGHT.deltaY(speed),
        "right",
        logger);
  }

}

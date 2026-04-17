package org.alienlabs.adaloveslace.view.component.button.geometrywindow.move;

import javafx.scene.control.Button;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.util.MoveKnotVectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.alienlabs.adaloveslace.view.window.ToolboxWindow.GEOMETRY_BUTTONS_HEIGHT;

public class UpLeftButton extends Button {

  private static final Logger logger      = LoggerFactory.getLogger(UpLeftButton.class);

  public UpLeftButton(App app) {
    this.setOnMouseClicked(event -> onMoveKnotUpLeftAction(app));
    this.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);
  }

  public static void onMoveKnotUpLeftAction(App app) {
    double speed = FastMoveModeButton.getMoveSpeed();
    MoveKnotActionUtil.moveSelectedKnots(
        app,
        MoveKnotVectors.Direction.UP_LEFT.deltaX(speed),
        MoveKnotVectors.Direction.UP_LEFT.deltaY(speed),
        "up left",
        logger);
  }

}

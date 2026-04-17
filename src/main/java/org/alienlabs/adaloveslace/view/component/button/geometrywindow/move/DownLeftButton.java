package org.alienlabs.adaloveslace.view.component.button.geometrywindow.move;

import javafx.scene.control.Button;
import org.alienlabs.adaloveslace.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.alienlabs.adaloveslace.view.window.ToolboxWindow.GEOMETRY_BUTTONS_HEIGHT;

public class DownLeftButton extends Button {

  private static final Logger logger        = LoggerFactory.getLogger(DownLeftButton.class);

  public DownLeftButton(App app) {
    this.setOnMouseClicked(event -> onMoveKnotDownLeftAction(app));
    this.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);
  }

  public static void onMoveKnotDownLeftAction(App app) {
    double delta = FastMoveModeButton.getMoveSpeed();
    MoveKnotActionUtil.moveSelectedKnots(app, -delta, delta, "down left", logger);
  }

}

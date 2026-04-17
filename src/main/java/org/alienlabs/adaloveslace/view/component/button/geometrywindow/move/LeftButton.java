package org.alienlabs.adaloveslace.view.component.button.geometrywindow.move;

import javafx.scene.control.Button;
import org.alienlabs.adaloveslace.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.alienlabs.adaloveslace.view.window.ToolboxWindow.GEOMETRY_BUTTONS_HEIGHT;

public class LeftButton extends Button {

  private static final Logger logger        = LoggerFactory.getLogger(LeftButton.class);

  public LeftButton(App app) {
    this.setOnMouseClicked(event -> onMoveKnotLeftAction(app));
    this.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);
  }

  public static void onMoveKnotLeftAction(App app) {
    MoveKnotActionUtil.moveSelectedKnots(app, -FastMoveModeButton.getMoveSpeed(), 0d, "left", logger);
  }

}

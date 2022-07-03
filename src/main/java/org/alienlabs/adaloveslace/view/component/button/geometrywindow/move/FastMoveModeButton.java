package org.alienlabs.adaloveslace.view.component.button.geometrywindow.move;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.ToggleButton;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.view.window.GeometryWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.alienlabs.adaloveslace.view.window.GeometryWindow.GEOMETRY_BUTTONS_HEIGHT;

public class FastMoveModeButton extends ToggleButton {

  private static SimpleBooleanProperty isFastMode;

  public static final boolean DEFAULT_FAST_MODE         = false;

  private static final Logger logger                    = LoggerFactory.getLogger(FastMoveModeButton.class);

  public FastMoveModeButton(App app, GeometryWindow window) {
    isFastMode = new SimpleBooleanProperty(DEFAULT_FAST_MODE);

    this.setOnMouseClicked(event -> onSwitchSlowModeAction(app, window));
    this.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);
    this.setMaxHeight(GEOMETRY_BUTTONS_HEIGHT);
  }

  public static void onSwitchSlowModeAction(App app, GeometryWindow window) {
    isFastMode.set(!isFastMode.get());
    logger.info("Setting fast move mode: {}", isFastMode.get());
  }

  public static double getMoveSpeed() {
    return isFastMode.get() ? 5d : 1d;
  }

}

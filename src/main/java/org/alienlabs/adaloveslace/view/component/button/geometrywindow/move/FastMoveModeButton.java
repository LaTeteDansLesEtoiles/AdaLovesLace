package org.alienlabs.adaloveslace.view.component.button.geometrywindow.move;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FastMoveModeButton extends ToggleButton {

  public static final String BUTTON_TOOLTIP             = "Set \"fast knot move\" mode on or off (f)\n";
  public static final double FAST_MODE_SPEED            = 5d;
  public static final double SLOW_MODE_SPEED            = 1d;

  private static SimpleBooleanProperty isFastMode;
  private static final boolean DEFAULT_FAST_MODE        = false;

  private static FastMoveModeButton instance;

  private static final Logger logger                    = LoggerFactory.getLogger(FastMoveModeButton.class);

  public FastMoveModeButton() {
    FastMoveModeButton.isFastMode = new SimpleBooleanProperty(DEFAULT_FAST_MODE);
    FastMoveModeButton.instance = this;
    this.setOnMouseClicked(event -> onSwitchFastModeAction());

    final Tooltip tooltip = new Tooltip();
    tooltip.setText(BUTTON_TOOLTIP);
    this.setTooltip(tooltip);
  }

  public static void onSwitchFastModeAction() {
    isFastMode.set(!isFastMode.get());
    instance.setSelected(isFastMode.get());
    logger.debug("Setting fast move mode: {}", isFastMode.get());
  }

  public static double getMoveSpeed() {
    return isFastMode.get() ? FAST_MODE_SPEED : SLOW_MODE_SPEED;
  }

}

package org.alienlabs.adaloveslace.view.component.button.geometrywindow.move;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.alienlabs.adaloveslace.App.TOOLTIPS_DURATION;
import static org.alienlabs.adaloveslace.App.resourceBundle;

public class FastMoveModeButton extends ToggleButton {

  public static final double FAST_MODE_SPEED            = 5d;
  public static final double SLOW_MODE_SPEED            = 1d;

  private static SimpleBooleanProperty isFastMode;
  private static final boolean DEFAULT_FAST_MODE        = false;

  private static FastMoveModeButton instance;

  private static final Logger logger                    = LoggerFactory.getLogger(FastMoveModeButton.class);

  public FastMoveModeButton() {
    if (FastMoveModeButton.isFastMode == null) {
      FastMoveModeButton.isFastMode = new SimpleBooleanProperty(DEFAULT_FAST_MODE);
    }
    FastMoveModeButton.instance = this;
    this.setOnMouseClicked(event -> onSwitchFastModeAction());

    final Tooltip tooltip = new Tooltip();
    tooltip.setText(resourceBundle.getString("FAST_MODE_BUTTON_TOOLTIP"));
    tooltip.setShowDuration(TOOLTIPS_DURATION);
    this.setTooltip(tooltip);
  }

  public static void onSwitchFastModeAction() {
    if (isFastMode == null) {
      isFastMode = new SimpleBooleanProperty(DEFAULT_FAST_MODE);
    }
    isFastMode.set(!isFastMode.get());
    if (instance != null) {
      instance.setSelected(isFastMode.get());
    }
    logger.info("Setting fast move mode: {}", isFastMode.get());
  }

  public static double getMoveSpeed() {
    if (isFastMode == null) {
      return SLOW_MODE_SPEED;
    }
    return isFastMode.get() ? FAST_MODE_SPEED : SLOW_MODE_SPEED;
  }

  /**
   * Clears static UI state so a new toolbox instance starts from the default slow mode.
   * Intended for tests; production does not call this.
   */
  public static void resetStateForTests() {
    isFastMode = null;
    instance = null;
  }

}

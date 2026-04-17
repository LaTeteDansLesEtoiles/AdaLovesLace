package org.alienlabs.adaloveslace.view.component.button.statewindow;

import javafx.scene.control.Tooltip;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.view.component.button.ImageButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.alienlabs.adaloveslace.App.TOOLTIPS_DURATION;
import static org.alienlabs.adaloveslace.App.resourceBundle;
import static org.alienlabs.adaloveslace.view.window.ToolboxWindow.STATE_BUTTONS_HEIGHT;

public class VisibleButton extends ImageButton {

  public static final String VISIBLE_BUTTON_NAME    = "VISIBLE_BUTTON_NAME";

  private static final Logger logger                = LoggerFactory.getLogger(VisibleButton.class);

  public VisibleButton(App app, String buttonLabel) {
    super(buttonLabel);
    this.setOnMouseClicked(event -> onSetVisibleAction(app));
    this.setPrefHeight(STATE_BUTTONS_HEIGHT);

    final Tooltip tooltip = new Tooltip();
    tooltip.setText(resourceBundle.getString("VISIBLE_BUTTON_TOOLTIP"));
    tooltip.setShowDuration(TOOLTIPS_DURATION);
    this.setTooltip(tooltip);
    buildButtonImage("visible.png");
  }

  public static void onSetVisibleAction(App app) {
    StateVisibilityActionUtil.switchToDrawingAndClearDecorations(app, logger);
  }

}

package org.alienlabs.adaloveslace.view.component.button.geometrywindow;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;

import static org.alienlabs.adaloveslace.App.TOOLTIPS_DURATION;
import static org.alienlabs.adaloveslace.App.resourceBundle;
import static org.alienlabs.adaloveslace.view.window.GeometryWindow.GEOMETRY_BUTTONS_HEIGHT;

public class RotationButton extends Button {

  public static final String ROTATION_BUTTON_NAME     = "ROTATION_BUTTON_NAME";

  public RotationButton(String buttonLabel) {
    super(buttonLabel);
    this.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);

    final Tooltip tooltip = new Tooltip();
    tooltip.setText(resourceBundle.getString("ROTATION_BUTTON_TOOLTIP"));
    tooltip.setShowDuration(TOOLTIPS_DURATION);
    this.setTooltip(tooltip);
  }

}

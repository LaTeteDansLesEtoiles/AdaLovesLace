package org.alienlabs.adaloveslace.view.component.button.geometrywindow;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;

import static org.alienlabs.adaloveslace.App.TOOLTIPS_DURATION;
import static org.alienlabs.adaloveslace.App.resourceBundle;
import static org.alienlabs.adaloveslace.view.window.GeometryWindow.GEOMETRY_BUTTONS_HEIGHT;

public class ZoomButton extends Button {

  public static final String ZOOM_BUTTON_NAME    = "ZOOM_BUTTON_NAME";

  public ZoomButton(String buttonLabel) {
    super(buttonLabel);
    this.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);

    final Tooltip tooltip = new Tooltip();
    tooltip.setText(resourceBundle.getString("ZOOM_BUTTON_TOOLTIP"));
    tooltip.setShowDuration(TOOLTIPS_DURATION);
    this.setTooltip(tooltip);
  }

}

package org.alienlabs.adaloveslace.view.component.button.geometrywindow;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.view.window.GeometryWindow;

import static org.alienlabs.adaloveslace.view.window.GeometryWindow.GEOMETRY_BUTTONS_HEIGHT;

public class ZoomButton extends Button {

  public static final String ZOOM_BUTTON_NAME    = " Zoom";
  public static final String BUTTON_TOOLTIP      = "Use the fields above to zoom in or out\nthe currently selected knot\n";

  public ZoomButton(App app, GeometryWindow window, String buttonLabel) {
    super(buttonLabel);
    this.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);
    this.setMaxHeight(GEOMETRY_BUTTONS_HEIGHT);

    final Tooltip tooltip = new Tooltip();
    tooltip.setText(BUTTON_TOOLTIP);
    this.setTooltip(tooltip);
  }

}

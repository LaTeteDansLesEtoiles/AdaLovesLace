package org.alienlabs.adaloveslace.view.component.button.geometrywindow;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.view.window.GeometryWindow;

import static org.alienlabs.adaloveslace.view.window.GeometryWindow.GEOMETRY_BUTTONS_HEIGHT;

public class RotationButton extends Button {

  public static final String ROTATION_BUTTON_NAME     = "Rotate";
  public static final String BUTTON_TOOLTIP           = "Use the fields above to rotate\nthe currently selected knot\n";

  public RotationButton(App app, GeometryWindow window, String buttonLabel) {
    super(buttonLabel);
    this.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);

    final Tooltip tooltip = new Tooltip();
    tooltip.setText(BUTTON_TOOLTIP);
    this.setTooltip(tooltip);
  }

}

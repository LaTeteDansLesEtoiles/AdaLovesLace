package org.alienlabs.adaloveslace.view.component.button.geometrywindow;

import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.MouseMode;
import org.alienlabs.adaloveslace.view.window.GeometryWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.alienlabs.adaloveslace.view.window.GeometryWindow.GEOMETRY_BUTTONS_HEIGHT;

public class DeletionButton extends ToggleButton {

  public static final String DELETION_BUTTON_NAME     = "Delete ";
  public static final String BUTTON_TOOLTIP           = "Select this button then click on a\nknot in the canvas to delete it\n";

  private static final Logger logger                  = LoggerFactory.getLogger(DeletionButton.class);

  public DeletionButton(App app, GeometryWindow window, String buttonLabel) {
    super(buttonLabel);
    this.setOnMouseClicked(event -> onSetSelectionModeAction(app, window));
    this.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);

    final Tooltip tooltip = new Tooltip();
    tooltip.setText(BUTTON_TOOLTIP);
    this.setTooltip(tooltip);
  }

  public static void onSetSelectionModeAction(App app, GeometryWindow window) {
    logger.info("Setting deletion mode");
    app.getOptionalDotGrid().getDiagram().setCurrentMode(MouseMode.DELETION);

    window.getDrawingButton()     .setSelected(false);
    window.getSelectionButton()   .setSelected(false);
    window.getDeletionButton()    .setSelected(true);
    window.getDuplicationButton() .setSelected(false);
  }

}

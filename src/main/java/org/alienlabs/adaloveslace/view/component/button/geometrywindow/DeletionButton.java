package org.alienlabs.adaloveslace.view.component.button.geometrywindow;

import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.enumeration.MouseMode;
import org.alienlabs.adaloveslace.view.window.event.GridEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.alienlabs.adaloveslace.App.TOOLTIPS_DURATION;
import static org.alienlabs.adaloveslace.App.resourceBundle;
import static org.alienlabs.adaloveslace.view.window.ToolboxWindow.GEOMETRY_BUTTONS_HEIGHT;

public class DeletionButton extends ToggleButton {

  public static final String DELETION_BUTTON_NAME     = "DELETION_BUTTON_NAME";

  private static final Logger logger                  = LoggerFactory.getLogger(DeletionButton.class);

  public DeletionButton(App app, String buttonLabel) {
    super(buttonLabel);
    this.setOnMouseClicked(event -> onSetDeletionModeAction(app));
    this.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);

    final Tooltip tooltip = new Tooltip();
    tooltip.setText(resourceBundle.getString("DELETION_BUTTON_TOOLTIP"));
    tooltip.setShowDuration(TOOLTIPS_DURATION);
    this.setTooltip(tooltip);
  }

  public static void onSetDeletionModeAction(App app) {
    logger.info("Setting deletion mode");
    app.getOptionalDotGrid().getDiagram().setCurrentMode(MouseMode.DELETION);

    // Les boutons sont maintenant dans ToolboxWindow
    // window.getDrawingButton()     .setSelected(false);
    // window.getSelectionButton()   .setSelected(false);
    // window.getDeletionButton()    .setSelected(true);
    // window.getDuplicationButton() .setSelected(false);

    GridEvents.removeEventsFromGrid(app);
    app.getMovablePane().addEventHandler(MouseEvent.MOUSE_MOVED, GridEvents.getGridHoverEventHandler(app));
  }

}

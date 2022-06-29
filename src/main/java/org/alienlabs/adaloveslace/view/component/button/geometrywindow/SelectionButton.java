package org.alienlabs.adaloveslace.view.component.button.geometrywindow;

import javafx.scene.control.ToggleButton;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.MouseMode;
import org.alienlabs.adaloveslace.view.window.GeometryWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.alienlabs.adaloveslace.view.window.GeometryWindow.GEOMETRY_BUTTONS_HEIGHT;

public class SelectionButton extends ToggleButton {

  public static final String SELECTION_BUTTON_NAME    = "Select ";

  private static final Logger logger                  = LoggerFactory.getLogger(SelectionButton.class);

  public SelectionButton(App app, GeometryWindow window, String buttonLabel) {
    super(buttonLabel);
    this.setOnMouseClicked(event -> onSetSelectionModeAction(app, window));
    this.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);
    this.setMaxHeight(GEOMETRY_BUTTONS_HEIGHT);
  }

  public static void onSetSelectionModeAction(App app, GeometryWindow window) {
    logger.info("Setting selection mode");
    app.getOptionalDotGrid().getDiagram().setCurrentMode(MouseMode.SELECTION);

    window.getDrawingButton().setSelected(false);
    window.getSelectionButton().setSelected(true);
    window.getRotationButton().setSelected(false);
    window.getZoomButton().setSelected(false);
  }

}

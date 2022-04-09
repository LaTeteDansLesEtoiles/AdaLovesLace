package org.alienlabs.adaloveslace.view;

import javafx.scene.control.Button;
import org.alienlabs.adaloveslace.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShowHideGridButton extends Button {

  private static final Logger logger = LoggerFactory.getLogger(ShowHideGridButton.class);

  public ShowHideGridButton(String buttonLabel, App app) {
    super(buttonLabel);
    this.setId("showHideGridButton");

    this.setOnMouseClicked(event -> {
      String eType = event.getEventType().toString();
      final boolean currentShowHideGridState = app.getCanvasWithOptionalDotGrid().isShowHideGridProperty().get();
      app.getCanvasWithOptionalDotGrid().isShowHideGridProperty().set(!currentShowHideGridState);
      logger.info("Event type -> {}, show: {}", eType, app.getCanvasWithOptionalDotGrid().isShowHideGridProperty().get());
    });
  }

}

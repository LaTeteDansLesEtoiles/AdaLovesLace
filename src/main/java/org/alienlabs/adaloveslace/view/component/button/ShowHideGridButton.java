package org.alienlabs.adaloveslace.view.component.button;

import javafx.scene.control.Button;
import org.alienlabs.adaloveslace.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShowHideGridButton extends Button {

  public static final String SHOW_HIDE_GRID_BUTTON_NAME = "Show / Hide grid";

  private static final Logger logger = LoggerFactory.getLogger(ShowHideGridButton.class);

  public ShowHideGridButton(String buttonLabel, App app) {
    super(buttonLabel);
    this.setOnMouseClicked(event -> showHideGrid(app));
  }

  public static void showHideGrid(App app) {
    final boolean currentShowHideGridState = app.getCanvasWithOptionalDotGrid().isShowHideGridProperty().get();
    app.getCanvasWithOptionalDotGrid().isShowHideGridProperty().set(!currentShowHideGridState);
    logger.info("Event show / hide grid: {}", app.getCanvasWithOptionalDotGrid().isShowHideGridProperty().get());
  }

}

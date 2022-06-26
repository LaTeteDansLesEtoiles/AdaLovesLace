package org.alienlabs.adaloveslace.view.component.button.toolboxwindow;

import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.view.component.button.ImageButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShowHideGridButton extends ImageButton {

  public static final String SHOW_HIDE_GRID_BUTTON_NAME = "Show / hide grid";

  private static final Logger logger = LoggerFactory.getLogger(ShowHideGridButton.class);

  public ShowHideGridButton(String buttonLabel, App app) {
    super(buttonLabel);
    this.setOnMouseClicked(event -> showHideGrid(app));
    buildButtonImage("show_hide_grid.png");
  }

  public static void showHideGrid(App app) {
    final boolean currentShowHideGridState = app.getOptionalDotGrid().isShowHideGridProperty().get();
    app.getOptionalDotGrid().isShowHideGridProperty().set(!currentShowHideGridState);
    logger.info("Event show / hide grid: {}", !currentShowHideGridState);
  }

}

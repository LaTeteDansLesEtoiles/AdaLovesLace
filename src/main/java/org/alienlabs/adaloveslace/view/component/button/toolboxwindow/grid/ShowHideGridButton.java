package org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid;

import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.view.component.button.ImageButton;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.ParentGridStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShowHideGridButton extends ImageButton {

  private static App app;

  public static final String SHOW_HIDE_GRID_BUTTON_NAME = "ShowHideGrid";

  private static final Logger logger = LoggerFactory.getLogger(ShowHideGridButton.class);

  public ShowHideGridButton(String buttonLabel, App app) {
    super(buttonLabel);
    ShowHideGridButton.app = app;
    this.setOnMouseClicked(_ -> showHideGrid());
    buildButtonImage("show_hide_grid.png");
  }

  public static void showHideGrid() {
    app.getGridStrategy().switchGridType();
    ParentGridStrategy.setGridHasBeenDrawn(false);

    logger.debug("Event switch grid");
    app.getOptionalDotGrid().layoutChildren();
  }

}

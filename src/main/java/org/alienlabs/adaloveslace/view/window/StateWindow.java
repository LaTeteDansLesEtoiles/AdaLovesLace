package org.alienlabs.adaloveslace.view.window;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.view.component.button.statewindow.InvisibleButton;
import org.alienlabs.adaloveslace.view.component.button.statewindow.SelectableButton;
import org.alienlabs.adaloveslace.view.component.button.statewindow.UnselectableButton;
import org.alienlabs.adaloveslace.view.component.button.statewindow.VisibleButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.alienlabs.adaloveslace.App.*;
import static org.alienlabs.adaloveslace.view.component.button.statewindow.InvisibleButton.INVISIBLE_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.component.button.statewindow.SelectableButton.SELECTABLE_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.component.button.statewindow.UnselectableButton.UNSELECTABLE_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.component.button.statewindow.VisibleButton.VISIBLE_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.window.ToolboxWindow.DEFAULT_TOOLBOX_WINDOW_WIDTH;
import static org.alienlabs.adaloveslace.view.window.ToolboxWindow.DEFAULT_TOOLBOX_WINDOW_X;

public class StateWindow {

  public static final double STATE_WINDOW_GAP                = 10d;
  public static final double DEFAULT_STATE_WINDOW_Y = DEFAULT_MAIN_WINDOW_Y + DEFAULT_MAIN_WINDOW_HEIGHT + STATE_WINDOW_GAP;
  public static final double DEFAULT_STATE_WINDOW_WIDTH      = 400d;
  public static final double DEFAULT_STATE_WINDOW_HEIGHT     = 240d;
  public static final double GAP_BETWEEN_BUTTONS             = 5d;

  public static final double STATE_BUTTONS_HEIGHT            = 35d;
  public static final double DEFAULT_STATE_WINDOW_X          = DEFAULT_TOOLBOX_WINDOW_X + DEFAULT_TOOLBOX_WINDOW_WIDTH;

  private UnselectableButton unselectableButton;
  private SelectableButton selectableButton;
  private InvisibleButton invisibleButton;
  private VisibleButton visibleButton;

  private static final Logger logger = LoggerFactory.getLogger(StateWindow.class);
  private Stage stateStage;

  public void createStateStage(App app, Stage stateStage, Pane parent) {
    Scene stateScene = new Scene(parent, app.getResizes().getStateWindowWidth(), app.getResizes().getStateWindowHeight());
    stateScene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

    this.stateStage = stateStage;
    stateStage.setTitle(resourceBundle.getString(STATE_TITLE));
    stateStage.setOnCloseRequest(windowEvent -> {
      logger.debug("You shall not close the state window directly!");
      windowEvent.consume();
    });
    stateStage.setX(app.getWindowRepositionEvents().getStateWindowX());
    stateStage.setY(app.getWindowRepositionEvents().getStateWindowY());
    stateStage.setScene(stateScene);
    stateStage.show();
  }

  public void createStateButtons(App app, GridPane parent) {
    TilePane buttonsPane  = new TilePane(Orientation.HORIZONTAL);
    buttonsPane.setAlignment(Pos.BOTTOM_CENTER);
    buttonsPane.setPrefColumns(2);
    buttonsPane.setVgap(GAP_BETWEEN_BUTTONS);

    this.invisibleButton = new InvisibleButton(app, resourceBundle.getString(INVISIBLE_BUTTON_NAME));
    this.visibleButton = new VisibleButton(app, resourceBundle.getString(VISIBLE_BUTTON_NAME));
    this.unselectableButton = new UnselectableButton(app, resourceBundle.getString(UNSELECTABLE_BUTTON_NAME));
    this.selectableButton = new SelectableButton(app, resourceBundle.getString(SELECTABLE_BUTTON_NAME));

    buttonsPane.getChildren().addAll(
      this.unselectableButton, this.selectableButton,
      this.invisibleButton, this.visibleButton, app.getSlider()
    );

    parent.add(buttonsPane, 0, 0);
  }

  public UnselectableButton getUnselectableButton() {
    return this.unselectableButton;
  }

  public SelectableButton getSelectableButton() {
    return this.selectableButton;
  }

  public InvisibleButton getInvisibleButton() {
    return this.invisibleButton;
  }

  public VisibleButton getVisibleButton() {
    return this.visibleButton;
  }

  public Stage getStateStage() {
    return this.stateStage;
  }

}

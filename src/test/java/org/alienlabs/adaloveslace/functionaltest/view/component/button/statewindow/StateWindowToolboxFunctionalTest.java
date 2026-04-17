package org.alienlabs.adaloveslace.functionaltest.view.component.button.statewindow;

import javafx.stage.Stage;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.enumeration.MouseMode;
import org.alienlabs.adaloveslace.functionaltest.AppFunctionalTestParent;
import org.alienlabs.adaloveslace.view.component.button.statewindow.InvisibleButton;
import org.alienlabs.adaloveslace.view.component.button.statewindow.SelectableButton;
import org.alienlabs.adaloveslace.view.component.button.statewindow.UnselectableButton;
import org.alienlabs.adaloveslace.view.component.button.statewindow.VisibleButton;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("functional")
class StateWindowToolboxFunctionalTest extends AppFunctionalTestParent {

  @Override
  @Start
  public void start(Stage primaryStage) {
    super.start(primaryStage);
  }

  @BeforeEach
  void stabilizeDiagramMode(FxRobot robot) {
    robot.interact(() -> {
      if (app != null && app.getOptionalDotGrid() != null && app.getOptionalDotGrid().getDiagram() != null) {
        app.getOptionalDotGrid().getDiagram().setCurrentMode(MouseMode.DRAWING);
      }
    });
  }

  @Test
  void clicking_invisible_toolbox_button_returns_to_drawing_mode(FxRobot robot) {
    initDrawAndSelectSnowFlake(robot);
    WaitForAsyncUtils.waitForFxEvents();

    robot.interact(() -> app.getOptionalDotGrid().getDiagram().setCurrentMode(MouseMode.SELECTION));
    robot.clickOn(App.resourceBundle.getString(InvisibleButton.INVISIBLE_BUTTON_NAME));
    WaitForAsyncUtils.waitForFxEvents();

    robot.interact(() ->
        assertEquals(MouseMode.DRAWING, app.getOptionalDotGrid().getDiagram().getCurrentMode()));
  }

  @Test
  void clicking_visible_toolbox_button_returns_to_drawing_mode(FxRobot robot) {
    initDrawAndSelectSnowFlake(robot);
    WaitForAsyncUtils.waitForFxEvents();

    robot.interact(() -> app.getOptionalDotGrid().getDiagram().setCurrentMode(MouseMode.SELECTION));
    robot.clickOn(App.resourceBundle.getString(VisibleButton.VISIBLE_BUTTON_NAME));
    WaitForAsyncUtils.waitForFxEvents();

    robot.interact(() ->
        assertEquals(MouseMode.DRAWING, app.getOptionalDotGrid().getDiagram().getCurrentMode()));
  }

  @Test
  void unselectable_then_selectable_round_trip_updates_model(FxRobot robot) {
    initDrawAndSelectSnowFlake(robot);
    WaitForAsyncUtils.waitForFxEvents();

    robot.clickOn(App.resourceBundle.getString(UnselectableButton.UNSELECTABLE_BUTTON_NAME));
    WaitForAsyncUtils.waitForFxEvents();

    robot.interact(() -> {
      var step = app.getOptionalDotGrid().getDiagram().getCurrentStep();
      assertTrue(step.getSelectedKnots().isEmpty());
      assertEquals(1, step.getDisplayedKnots().size());
      assertFalse(step.getDisplayedKnots().get(0).isSelectable());
    });

    robot.clickOn(App.resourceBundle.getString(SelectableButton.SELECTABLE_BUTTON_NAME));
    WaitForAsyncUtils.waitForFxEvents();

    robot.interact(() ->
        assertTrue(app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots().get(0).isSelectable()));
  }

  @Test
  void clicking_visible_after_unselectable_keeps_drawing_mode(FxRobot robot) {
    initDrawAndSelectSnowFlake(robot);
    WaitForAsyncUtils.waitForFxEvents();

    robot.clickOn(App.resourceBundle.getString(UnselectableButton.UNSELECTABLE_BUTTON_NAME));
    WaitForAsyncUtils.waitForFxEvents();

    robot.interact(() -> app.getOptionalDotGrid().getDiagram().setCurrentMode(MouseMode.MOVE));
    robot.clickOn(App.resourceBundle.getString(VisibleButton.VISIBLE_BUTTON_NAME));
    WaitForAsyncUtils.waitForFxEvents();

    robot.interact(() ->
        assertEquals(MouseMode.DRAWING, app.getOptionalDotGrid().getDiagram().getCurrentMode()));
  }
}

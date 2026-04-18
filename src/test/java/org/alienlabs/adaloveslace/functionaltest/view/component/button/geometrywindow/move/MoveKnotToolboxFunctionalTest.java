package org.alienlabs.adaloveslace.functionaltest.view.component.button.geometrywindow.move;

import javafx.stage.Stage;
import org.alienlabs.adaloveslace.domain.enumeration.MouseMode;
import org.alienlabs.adaloveslace.functionaltest.AppFunctionalTestParent;
import org.alienlabs.adaloveslace.view.component.button.geometrywindow.move.FastMoveModeButton;
import org.alienlabs.adaloveslace.view.component.button.geometrywindow.move.LeftButton;
import org.alienlabs.adaloveslace.view.component.button.geometrywindow.move.RightButton;
import org.alienlabs.adaloveslace.view.component.grid.OptionalDotGrid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("functional")
class MoveKnotToolboxFunctionalTest extends AppFunctionalTestParent {

  @Override
  @Start
  public void start(Stage primaryStage) {
    super.start(primaryStage);
  }

  @BeforeEach
  void stabilizeMoveUiState(FxRobot robot) {
    robot.interact(() -> {
      OptionalDotGrid.moveKnotPause.stop();
      FastMoveModeButton.resetStateForTests();
      if (app != null && app.getOptionalDotGrid() != null && app.getOptionalDotGrid().getDiagram() != null) {
        app.getOptionalDotGrid().getDiagram().setCurrentMode(MouseMode.DRAWING);
      }
    });
  }

  @Test
  void should_shift_selected_knot_horizontally_when_clicking_move_right_then_left(FxRobot robot) {
    initDrawAndSelectSnowFlake(robot);
    WaitForAsyncUtils.waitForFxEvents();

    double xBefore = app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().get(0).getX();

    // The move buttons use setOnMouseClicked, so ButtonBase#fire (ActionEvent) does not invoke them.
    // Invoke the static action directly on the FX thread to bypass Xvfb/ScrollPane visibility issues.
    robot.interact(() -> RightButton.onMoveKnotRightAction(app));
    WaitForAsyncUtils.waitForFxEvents();
    double xAfterRight = app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().get(0).getX();
    assertEquals(xBefore + FastMoveModeButton.SLOW_MODE_SPEED, xAfterRight, 1e-3);

    robot.interact(() -> LeftButton.onMoveKnotLeftAction(app));
    WaitForAsyncUtils.waitForFxEvents();
    double xAfterLeft = app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().get(0).getX();
    assertEquals(xBefore, xAfterLeft, 1e-3);
  }

  @Test
  void should_use_fast_step_when_fast_move_toggle_is_selected(FxRobot robot) {
    initDrawAndSelectSnowFlake(robot);
    WaitForAsyncUtils.waitForFxEvents();

    robot.interact(FastMoveModeButton::onSwitchFastModeAction);
    WaitForAsyncUtils.waitForFxEvents();
    assertTrue(FastMoveModeButton.getMoveSpeed() >= FastMoveModeButton.FAST_MODE_SPEED - 1e-6);

    double x0 = app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().get(0).getX();
    robot.interact(() -> RightButton.onMoveKnotRightAction(app));
    WaitForAsyncUtils.waitForFxEvents();
    double x1 = app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().get(0).getX();
    assertEquals(x0 + FastMoveModeButton.FAST_MODE_SPEED, x1, 1e-2);
  }
}

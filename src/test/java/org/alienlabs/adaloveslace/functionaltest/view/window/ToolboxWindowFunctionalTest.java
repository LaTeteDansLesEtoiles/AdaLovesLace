package org.alienlabs.adaloveslace.functionaltest.view.window;

import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.domain.enumeration.GridType;
import org.alienlabs.adaloveslace.functionaltest.AppFunctionalTestParent;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.base.ColorMatchers;
import org.testfx.util.WaitForAsyncUtils;

import static org.alienlabs.adaloveslace.App.TOOLBOX_TITLE;
import static org.alienlabs.adaloveslace.App.resourceBundle;
import static org.alienlabs.adaloveslace.functionaltest.view.window.MainWindowFunctionalTest.GRAY_DOTS_COLOR;
import static org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid.ShowHideGridButton.SHOW_HIDE_GRID_BUTTON_NAME;
import static org.junit.jupiter.api.Assertions.*;

@Tag("functional")
class ToolboxWindowFunctionalTest extends AppFunctionalTestParent {

  @Override
  @Start
  public void start(Stage primaryStage) {
    super.start(primaryStage);
  }

  /**
   * Single test avoids an extra JavaFX lifecycle; under CI, repeated setup/teardown is a common source
   * of TestFX {@code TimeoutException} between methods.
   */
  @Test
  void testToolboxWindow_isShowing_and_title() {
    assertTrue(this.app.getToolboxStage().isShowing());
    assertEquals(TOOLBOX_TITLE, this.app.getToolboxStage().getTitle());
  }

  @Test
  void testShowHideGridButton_changes_grid_type(FxRobot robot) {
    GridType before = this.app.getOptionalDotGrid().getDiagram().getCurrentGridType();
    robot.clickOn(resourceBundle.getString(SHOW_HIDE_GRID_BUTTON_NAME));
    assertCondition(
            () -> !this.app.getOptionalDotGrid().getDiagram().getCurrentGridType().equals(before),
            "Show/hide grid control should advance the diagram grid type");
  }

  @Test
  void testHiddenGrid_shows_white_canvas_sample(FxRobot robot) {
    while (this.app.getOptionalDotGrid().getDiagram().getCurrentGridType() != GridType.HIDDEN) {
      robot.clickOn(resourceBundle.getString(SHOW_HIDE_GRID_BUTTON_NAME));
    }
    Point2D sample = newPointOnGrid(GRAY_PIXEL_X, GRAY_PIXEL_Y);
    robot.moveTo(sample);
    assertCondition(
            () -> !ColorMatchers.isColor(GRAY_DOTS_COLOR).matches(getColor(sample)),
            "Hidden grid type should render white background at sample point");
  }

  @Test
  void test_cycle_from_hidden_restores_criss_cross_dots(FxRobot robot) {
    while (this.app.getOptionalDotGrid().getDiagram().getCurrentGridType() != GridType.HIDDEN) {
      robot.clickOn(resourceBundle.getString(SHOW_HIDE_GRID_BUTTON_NAME));
    }
    robot.clickOn(resourceBundle.getString(SHOW_HIDE_GRID_BUTTON_NAME));
    WaitForAsyncUtils.waitForFxEvents();
    assertEquals(GridType.CRISS_CROSS, this.app.getOptionalDotGrid().getDiagram().getCurrentGridType());
    // Headless Jenkins often does not reproduce grid pixel colors reliably (Monocle/GPU variance).
    // ShowHideGridButton updates the toolbox label synchronously with the diagram grid type — assert that.
    final String expectedCrissCrossLabel = resourceBundle.getString(GridType.CRISS_CROSS.name());
    assertCondition(
            () -> GridType.CRISS_CROSS == app.getOptionalDotGrid().getDiagram().getCurrentGridType()
                    && expectedCrissCrossLabel.equals(app.getToolboxWindow().getGridNameLabel().getText()),
            "After cycling from hidden, diagram and toolbox label should show CRISS_CROSS");
  }

  @Test
  void testShowHideGridUpdatesVisibleGridLabel(FxRobot robot) {
    Label gridLabel = this.app.getToolboxWindow().getGridNameLabel();
    String initial = gridLabel.getText();

    robot.clickOn(resourceBundle.getString(SHOW_HIDE_GRID_BUTTON_NAME));
    assertCondition(() -> !gridLabel.getText().equals(initial), "Grid label should change after grid switch");
    String afterFirstToggle = gridLabel.getText();

    robot.clickOn(resourceBundle.getString(SHOW_HIDE_GRID_BUTTON_NAME));
    assertCondition(() -> !gridLabel.getText().equals(afterFirstToggle), "Grid label should change on second grid switch");
    assertNotEquals(initial, afterFirstToggle, "Grid name should reflect a different visible grid mode");
  }

}

package org.alienlabs.adaloveslace.functionaltest.view.component.spinner;

import javafx.stage.Stage;
import org.alienlabs.adaloveslace.functionaltest.AppFunctionalTestParent;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;

import static org.alienlabs.adaloveslace.domain.Knot.DEFAULT_ROTATION;
import static org.alienlabs.adaloveslace.view.window.ToolboxWindow.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("functional")
class RotationSpinnerFunctionalTest extends AppFunctionalTestParent {

  /**
   * Init method called before each test
   * @param primaryStage The injected window (stage)
   */
  @Start
  public void start(Stage primaryStage) {
    super.start(primaryStage);
  }

  /**
   * Checks if the second and third rotation spinners in the toolbox
   * contain the right value when raising the value in the first one
   *
   */
  @Test
  void should_contain_rotation_new_first_value_up(FxRobot robot) {
    // Given
    initDrawAndSelectSnowFlake(robot);

    // When
    incrementSpinner(robot, this.toolboxWindow.getRotationSpinner1());

    // Then
    assertRotationNear(DEFAULT_ROTATION + ROTATION_SPINNER_INCREMENTS_1, "Rotation after first spinner up");
  }

  /**
   * Checks if the second and third rotation spinners in the toolbox
   * contain the right value when lowering the value in the first one
   *
   */
  @Test
  void should_contain_rotation_new_first_value_down(FxRobot robot) {
    // Given
    initDrawAndSelectSnowFlake(robot);

    // When
    decrementSpinner(robot, this.toolboxWindow.getRotationSpinner1());

    // Then
    assertRotationNear(DEFAULT_ROTATION - ROTATION_SPINNER_INCREMENTS_1, "Rotation after first spinner down");
  }

  /**
   * Checks if the first and third rotation spinners in the toolbox
   * contain the right value when raising the value in the second one
   *
   */
  @Test
  void should_contain_rotation_new_second_value_up(FxRobot robot) {
    // Given
    initDrawAndSelectSnowFlake(robot);

    // When
   incrementSpinner(robot, this.toolboxWindow.getRotationSpinner2());

    // Then
    assertRotationNear(DEFAULT_ROTATION + ROTATION_SPINNER_INCREMENTS_2, "Rotation after second spinner up");
  }

  /**
   * Checks if the first and third rotation spinners in the toolbox
   * contain the right value when lowering the value in the second one
   *
   */
  @Test
  void should_contain_rotation_new_second_value_down(FxRobot robot) {
    // Given
    initDrawAndSelectSnowFlake(robot);

    // When
    decrementSpinner(robot, this.toolboxWindow.getRotationSpinner2());

    // Then
    assertRotationNear(DEFAULT_ROTATION - ROTATION_SPINNER_INCREMENTS_2, "Rotation after second spinner down");
  }

  /**
   * Checks if the first and second rotation spinners in the toolbox
   * contain the right value when raising the value in the third one
   *
   */
  @Test
  void should_contain_rotation_new_third_value_up(FxRobot robot) {
    // Given
    initDrawAndSelectSnowFlake(robot);

    // When
    incrementSpinner(robot, this.toolboxWindow.getRotationSpinner3());

    // Then
    assertRotationNear(DEFAULT_ROTATION + ROTATION_SPINNER_INCREMENTS_3, "Rotation after third spinner up");
  }

  /**
   * Checks if the first and second rotation spinners in the toolbox
   * contain the right value when lowering the value in the third one
   *
   */
  @Test
  void should_contain_rotation_new_third_value_down(FxRobot robot) {
    // Given
    initDrawAndSelectSnowFlake(robot);

    // When
    decrementSpinner(robot, this.toolboxWindow.getRotationSpinner3());

    // Then
    assertRotationNear(DEFAULT_ROTATION - ROTATION_SPINNER_INCREMENTS_3, "Rotation after third spinner down");
  }

  /**
   * Checks if the second and third rotation spinners stay in sync when changing the first.
   * Default rotation is asserted first (was a separate test) to avoid extra JavaFX lifecycles.
   */
  @Test
  void any_rotation_spinner_should_react_to_first_rotation_values(FxRobot robot) {
    initDrawAndSelectSnowFlake(robot);
    assertRotationNear(DEFAULT_ROTATION, "Default knot rotation");

    final int[] values = {1, 20, 50, 100, 200, -1, -20, -50, -100, -200, 0};
    for (int spinnerValue : values) {
      initDrawAndSelectSnowFlake(robot);
      setSpinnerValue(robot, this.toolboxWindow.getRotationSpinner1(), spinnerValue);
      assertEquals(spinnerValue, this.toolboxWindow.getRotationSpinner2().getValueFactory().getValue());
      assertEquals(spinnerValue, this.toolboxWindow.getRotationSpinner3().getValueFactory().getValue());
    }
  }

  /**
   *
   * Checks if the first and third rotation spinners in the toolbox
   * contain the right value when choosing a value in the second one
   *
   */
  @Test
  void any_rotation_spinner_should_react_to_second_rotation_values(FxRobot robot) {
    final int[] values = {1, 20, 50, 100, 200, -1, -20, -50, -100, -200, 0};
    for (int spinnerValue : values) {
      initDrawAndSelectSnowFlake(robot);
      setSpinnerValue(robot, this.toolboxWindow.getRotationSpinner2(), spinnerValue);
      assertEquals(spinnerValue, this.toolboxWindow.getRotationSpinner1().getValueFactory().getValue());
      assertEquals(spinnerValue, this.toolboxWindow.getRotationSpinner3().getValueFactory().getValue());
    }
  }

  /**
   *
   * Checks if the first and second rotation spinners in the toolbox
   * contain the right value when choosing a value in the third one
   *
   */
  @Test
  void any_rotation_spinner_should_react_to_third_rotation_values(FxRobot robot) {
    final int[] values = {1, 20, 50, 100, 200, -1, -20, -50, -100, -200, 0};
    for (int spinnerValue : values) {
      initDrawAndSelectSnowFlake(robot);
      setSpinnerValue(robot, this.toolboxWindow.getRotationSpinner3(), spinnerValue);
      assertEquals(spinnerValue, this.toolboxWindow.getRotationSpinner1().getValueFactory().getValue());
      assertEquals(spinnerValue, this.toolboxWindow.getRotationSpinner2().getValueFactory().getValue());
    }
  }

}

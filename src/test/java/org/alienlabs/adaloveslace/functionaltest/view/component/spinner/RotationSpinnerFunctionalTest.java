package org.alienlabs.adaloveslace.functionaltest.view.component.spinner;

import javafx.stage.Stage;
import org.alienlabs.adaloveslace.functionaltest.AppFunctionalTestParent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;

import static org.alienlabs.adaloveslace.business.model.Knot.DEFAULT_ROTATION;
import static org.alienlabs.adaloveslace.view.window.GeometryWindow.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
   * Checks if the three rotation spinners in the toolbox contain the right default value
   *
   */
  @Test
  void should_contain_rotation_default_value(FxRobot robot) {
    // Given
    initDrawAndSelectSnowFlake(robot);

    // Then
    this.sleepMainThread();
    assertRotationAnglesEqual(DEFAULT_ROTATION, this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().
        stream().findFirst().get().getRotationAngle());
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
    synchronizeTask(() -> incrementSpinner(this.geometryWindow.getRotationSpinner1()));

    // Then
    synchronizeTask(() -> assertRotationAnglesEqual(
            DEFAULT_ROTATION + ROTATION_SPINNER_INCREMENTS_1, getSnowFlakeRotationAngle()));
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
    synchronizeTask(() ->  decrementSpinner(this.geometryWindow.getRotationSpinner1()));

    // Then
    assertRotationAnglesEqual(DEFAULT_ROTATION - ROTATION_SPINNER_INCREMENTS_1, getSnowFlakeRotationAngle());
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
   synchronizeTask(() -> incrementSpinner(this.geometryWindow.getRotationSpinner2()));

    // Then
    assertRotationAnglesEqual(DEFAULT_ROTATION + ROTATION_SPINNER_INCREMENTS_2, getSnowFlakeRotationAngle());
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
    synchronizeTask(() -> decrementSpinner(this.geometryWindow.getRotationSpinner2()));

    // Then
    assertRotationAnglesEqual(DEFAULT_ROTATION - ROTATION_SPINNER_INCREMENTS_2, getSnowFlakeRotationAngle());
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
    synchronizeTask(() -> incrementSpinner(this.geometryWindow.getRotationSpinner3()));

    // Then
    assertRotationAnglesEqual(DEFAULT_ROTATION + ROTATION_SPINNER_INCREMENTS_3, getSnowFlakeRotationAngle());
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
    synchronizeTask(() -> decrementSpinner(this.geometryWindow.getRotationSpinner3()));

    // Then
    assertRotationAnglesEqual(DEFAULT_ROTATION - ROTATION_SPINNER_INCREMENTS_3, getSnowFlakeRotationAngle());
  }

  /**
   *
   * Checks if the second and third zoom spinners in the toolbox
   * contain the right value when choosing a value in the first one
   *
   */
  @ParameterizedTest(name = "Check changing first rotation value #{index}")
  @CsvSource({"1", "20", "50", "100", "200", "-1", "-20", "-50", "-100", "-200", "0"})
  void any_rotation_spinner_should_react_to_the_first_rotation_value_change(int spinnerValue, FxRobot robot) {
    // Given
    initDrawAndSelectSnowFlake(robot);

    // When
    synchronizeTask(() -> setSpinnerValue(this.geometryWindow.getRotationSpinner1(), spinnerValue));

    // Then
    assertEquals(spinnerValue, this.geometryWindow.getRotationSpinner2().getValueFactory().getValue());
    assertEquals(spinnerValue, this.geometryWindow.getRotationSpinner3().getValueFactory().getValue());
  }

  /**
   *
   * Checks if the first and third rotation spinners in the toolbox
   * contain the right value when choosing a value in the second one
   *
   */
  @ParameterizedTest(name = "Check changing second rotation value #{index}")
  @CsvSource({"1", "20", "50", "100", "200", "-1", "-20", "-50", "-100", "-200", "0"})
  void any_rotation_spinner_should_react_to_the_second_rotation_value_change(int spinnerValue, FxRobot robot) {
    // Given
    initDrawAndSelectSnowFlake(robot);

    // When
    synchronizeTask(() -> setSpinnerValue(this.geometryWindow.getRotationSpinner2(), spinnerValue));

    // Then
    assertEquals(spinnerValue, this.geometryWindow.getRotationSpinner1().getValueFactory().getValue());
    assertEquals(spinnerValue, this.geometryWindow.getRotationSpinner3().getValueFactory().getValue());
  }

  /**
   *
   * Checks if the first and second rotation spinners in the toolbox
   * contain the right value when choosing a value in the third one
   *
   */
  @ParameterizedTest(name = "Check changing third zoom value #{index}")
  @CsvSource({"1", "20", "50", "100", "200", "-1", "-20", "-50", "-100", "-200", "0"})
  void any_rotation_spinner_should_react_to_third_rotation_value_change(int spinnerValue, FxRobot robot) {
    // Given
    initDrawAndSelectSnowFlake(robot);
    // When
    synchronizeTask(() -> setSpinnerValue(this.geometryWindow.getRotationSpinner3(), spinnerValue));

    // Then
    assertEquals(spinnerValue, this.geometryWindow.getRotationSpinner1().getValueFactory().getValue());
    assertEquals(spinnerValue, this.geometryWindow.getRotationSpinner2().getValueFactory().getValue());
  }

  private void assertRotationAnglesEqual(int defaultRotation, double snowFlakeRotationAngle) {
    assertEquals(defaultRotation, snowFlakeRotationAngle);
  }

}

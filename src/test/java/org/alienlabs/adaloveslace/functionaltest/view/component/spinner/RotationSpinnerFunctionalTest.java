package org.alienlabs.adaloveslace.functionaltest.view.component.spinner;

import javafx.stage.Stage;
import org.alienlabs.adaloveslace.functionaltest.AppFunctionalTestParent;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;
import org.testfx.framework.junit5.Stop;

import static org.alienlabs.adaloveslace.business.model.Knot.DEFAULT_ROTATION;
import static org.alienlabs.adaloveslace.view.window.GeometryWindow.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RotationSpinnerFunctionalTest extends AppFunctionalTestParent {

  private static final Logger logger          = LoggerFactory.getLogger(RotationSpinnerFunctionalTest.class);

  /**
   * Init method called before each test
   *
   * @param primaryStage The injected window (stage)
   */
  @Start
  public void start(Stage primaryStage) {
    super.start(primaryStage);
  }

  @Stop
  public void stop() {
    super.stop();
  }

  /**
   * Checks if the three rotation spinners in the toolbox contain the right default value
   *
   */
  @Test
  void should_contain_rotation_default_value(FxRobot robot) {
    // Given
    drawAndSelectSnowFlake(robot);

    // Then
    synchronizeAssertTask(() -> assertRotationAnglesEqual(DEFAULT_ROTATION, this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().
        stream().findFirst().get().getRotationAngle()));
  }

  /**
   * Checks if the second and third rotation spinners in the toolbox
   * contain the right value when raising the value in the first one
   *
   */
  @Test
  void should_contain_rotation_new_first_value_up(FxRobot robot) {
    // Given
    drawAndSelectSnowFlake(robot);

    // When
    synchronizeTaskAndAssert(() -> incrementSpinner(this.geometryWindow.getRotationSpinner1()),
      () -> assertRotationAnglesEqual(DEFAULT_ROTATION + ROTATION_SPINNER_INCREMENTS_1, getSnowFlakeRotationAngle()));
  }

  /**
   * Checks if the second and third rotation spinners in the toolbox
   * contain the right value when lowering the value in the first one
   *
   */
  @Test
  void should_contain_rotation_new_first_value_down(FxRobot robot) {
    // Given
    drawAndSelectSnowFlake(robot);

    // When
    synchronizeTaskAndAssert(() ->  decrementSpinner(this.geometryWindow.getRotationSpinner1()),
      () -> assertRotationAnglesEqual(DEFAULT_ROTATION - ROTATION_SPINNER_INCREMENTS_1, getSnowFlakeRotationAngle()));
  }

  /**
   * Checks if the first and third rotation spinners in the toolbox
   * contain the right value when raising the value in the second one
   *
   */
  @Test
  void should_contain_rotation_new_second_value_up(FxRobot robot) {
    // Given
    drawAndSelectSnowFlake(robot);

    // When
    synchronizeTaskAndAssert(() -> incrementSpinner(this.geometryWindow.getRotationSpinner2()),
      () -> assertRotationAnglesEqual(DEFAULT_ROTATION + ROTATION_SPINNER_INCREMENTS_2, getSnowFlakeRotationAngle()));
  }

  /**
   * Checks if the first and third rotation spinners in the toolbox
   * contain the right value when lowering the value in the second one
   *
   */
  @Test
  void should_contain_rotation_new_second_value_down(FxRobot robot) {
    // Given
    drawAndSelectSnowFlake(robot);

    // When
    synchronizeTaskAndAssert(() -> decrementSpinner(this.geometryWindow.getRotationSpinner2()),
      () -> assertRotationAnglesEqual(DEFAULT_ROTATION - ROTATION_SPINNER_INCREMENTS_2, getSnowFlakeRotationAngle()));
  }

  /**
   * Checks if the first and second rotation spinners in the toolbox
   * contain the right value when raising the value in the third one
   *
   */
  @Test
  void should_contain_rotation_new_third_value_up(FxRobot robot) {
    // Given
    drawAndSelectSnowFlake(robot);

    // When
    synchronizeTaskAndAssert(() -> incrementSpinner(this.geometryWindow.getRotationSpinner3()),
      () -> assertRotationAnglesEqual(DEFAULT_ROTATION + ROTATION_SPINNER_INCREMENTS_3, getSnowFlakeRotationAngle()));
  }

  /**
   * Checks if the first and second rotation spinners in the toolbox
   * contain the right value when lowering the value in the third one
   *
   */
  @Test
  void should_contain_rotation_new_third_value_down(FxRobot robot) {
    // Given
    drawAndSelectSnowFlake(robot);

    // When
    synchronizeTaskAndAssert(() -> decrementSpinner(this.geometryWindow.getRotationSpinner3()),
      () -> assertRotationAnglesEqual(DEFAULT_ROTATION - ROTATION_SPINNER_INCREMENTS_3, getSnowFlakeRotationAngle()));
  }

  private void assertRotationAnglesEqual(int defaultRotation, double snowFlakeRotationAngle) {
    assertEquals(defaultRotation, snowFlakeRotationAngle);
  }

}

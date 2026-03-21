package org.alienlabs.adaloveslace.functionaltest.view.component.spinner;

import javafx.stage.Stage;
import org.alienlabs.adaloveslace.functionaltest.AppFunctionalTestParent;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;

import static org.testfx.api.FxAssert.verifyThat;

@Tag("functional")
class ZoomSpinnerFunctionalTest extends AppFunctionalTestParent {

  /**
   * Init method called before each test.
   * @param primaryStage The injected window (stage)
   */
  @Override
  @Start
  public void start(Stage primaryStage) {
    super.start(primaryStage);
  }

  /**
   * Checks if the drawn Pattern has the right zoom factor values when providing values in the first zoom spinner.
   * Also covers default zoom (was a separate test) to avoid an extra JavaFX lifecycle after long loop tests.
   *
   */
  @Test
  void should_react_to_first_zoom_values(FxRobot robot) {
    initDrawAndSelectSnowFlake(robot);
    assertZoomFactorNear(1d, "Zoom factor to be properly initialized to 1.0");

    final int[] values = {1, 3, -1, -2, -3, 0};
    final double[] expected = {1.1d, 1.3d, 0.9d, 0.8d, 0.7d, 1d};
    for (int i = 0; i < values.length; i++) {
      initDrawAndSelectSnowFlake(robot);
      setSpinnerValue(robot, this.toolboxWindow.getZoomSpinner1(), values[i]);
      assertZoomFactorNear(expected[i], "Zoom factor should match spinner " + values[i]);
    }
  }


  /**
   * Checks if the drawn Pattern has the right zoom factor values when providing values in the second zoom spinner.
   *
   */
  @Test
  void should_react_to_second_zoom_values(FxRobot robot) {
    final int[] values = {1, 3, -1, -2, -3, 0};
    final double[] expected = {1.1d, 1.3d, 0.9d, 0.8d, 0.7d, 1d};
    for (int i = 0; i < values.length; i++) {
      initDrawAndSelectSnowFlake(robot);
      setSpinnerValue(robot, this.toolboxWindow.getZoomSpinner2(), values[i]);
      assertZoomFactorNear(expected[i], "Zoom factor should match spinner " + values[i]);
    }
  }

  /**
   * Checks if the drawn Pattern has the right zoom factor values when providing values in the third zoom spinner.
   *
   */
  @Test
  void should_react_to_third_zoom_values(FxRobot robot) {
    final int[] values = {1, 3, -1, -2, -3, 0};
    final double[] expected = {1.1d, 1.3d, 0.9d, 0.8d, 0.7d, 1d};
    for (int i = 0; i < values.length; i++) {
      initDrawAndSelectSnowFlake(robot);
      setSpinnerValue(robot, this.toolboxWindow.getZoomSpinner3(), values[i]);
      assertZoomFactorNear(expected[i], "Zoom factor should match spinner " + values[i]);
    }
  }

  /**
   *
   * Checks if the second and third zoom spinners in the toolbox
   * contain the right value when choosing a value in the first one
   *
   */
  @Test
  void any_zoom_spinner_should_react_to_first_zoom_values(FxRobot robot) {
    final int[] values = {1, 2, 5, 10, 20, -1, -2, -5, -10, -20, 0};
    for (int spinnerValue : values) {
      initDrawAndSelectSnowFlake(robot);
      setSpinnerValue(robot, this.toolboxWindow.getZoomSpinner1(), spinnerValue);
      verifyThat(this.toolboxWindow.getZoomSpinner2().getValueFactory().getValue(), org.hamcrest.Matchers.is(spinnerValue));
      verifyThat(this.toolboxWindow.getZoomSpinner3().getValueFactory().getValue(), org.hamcrest.Matchers.is(spinnerValue));
    }
  }

  /**
   *
   * Checks if the first and third zoom spinners in the toolbox
   * contain the right value when choosing a value in the second one
   *
   */
  @Test
  void any_zoom_spinner_should_react_to_second_zoom_values(FxRobot robot) {
    final int[] values = {1, 2, 5, 10, 20, -1, -2, -5, -10, -20, 0};
    for (int spinnerValue : values) {
      initDrawAndSelectSnowFlake(robot);
      setSpinnerValue(robot, this.toolboxWindow.getZoomSpinner2(), spinnerValue);
      verifyThat(this.toolboxWindow.getZoomSpinner1().getValueFactory().getValue(), org.hamcrest.Matchers.is(spinnerValue));
      verifyThat(this.toolboxWindow.getZoomSpinner3().getValueFactory().getValue(), org.hamcrest.Matchers.is(spinnerValue));
    }
  }


  /**
   *
   * Checks if the first and second zoom spinners in the toolbox
   * contain the right value when choosing a value in the third one
   *
   */
  @Test
  void any_zoom_spinner_should_react_to_third_zoom_values(FxRobot robot) {
    final int[] values = {1, 2, 5, 10, 20, -1, -2, -5, -10, -20, 0};
    for (int spinnerValue : values) {
      initDrawAndSelectSnowFlake(robot);
      setSpinnerValue(robot, this.toolboxWindow.getZoomSpinner3(), spinnerValue);
      verifyThat(this.toolboxWindow.getZoomSpinner1().getValueFactory().getValue(), org.hamcrest.Matchers.is(spinnerValue));
      verifyThat(this.toolboxWindow.getZoomSpinner2().getValueFactory().getValue(), org.hamcrest.Matchers.is(spinnerValue));
    }
  }

  /**
   * Checks if the second and third zoom spinners in the toolbox
   * contain the right value when raising the value in the first one
   *
   */
  @Test
  void should_contain_zoom_new_first_value_up(FxRobot robot) {
    // Given
    initDrawAndSelectSnowFlake(robot);

    // When
    incrementSpinner(robot, this.toolboxWindow.getZoomSpinner1());

    // Then
    assertZoomFactorNear(1.1d, "Zoom after first spinner increment");
  }

  /**
   * Checks if the second and third zoom spinners in the toolbox
   * contain the right value when lowering the value in the first one
   *
   */
  @Test
  void should_contain_zoom_new_first_value_down(FxRobot robot) {
    // Given
    initDrawAndSelectSnowFlake(robot);

    // When
    decrementSpinner(robot, this.toolboxWindow.getZoomSpinner1());

    // Then
    assertZoomFactorNear(0.9d, "Zoom after first spinner decrement");
  }

  /**
   * Checks if the first and third zoom spinners in the toolbox
   * contain the right value when raising the value in the second one
   *
   */
  @Test
  void should_contain_zoom_new_second_value_up(FxRobot robot) {
    // Given
    initDrawAndSelectSnowFlake(robot);

    // When
    incrementSpinner(robot, this.toolboxWindow.getZoomSpinner2());

    // Then
    assertZoomFactorNear(1.2d, "Zoom after second spinner increment");
  }

  /**
   * Checks if the first and third zoom spinners in the toolbox
   * contain the right value when lowering the value in the second one
   *
   */
  @Test
  void should_contain_zoom_new_second_value_down(FxRobot robot) {
    // Given
    initDrawAndSelectSnowFlake(robot);

    // When
    decrementSpinner(robot, this.toolboxWindow.getZoomSpinner2());

    // Then
    assertZoomFactorNear(0.8d, "Zoom after second spinner decrement");
  }

  /**
   * Checks if the first and second zoom spinners in the toolbox
   * contain the right value when raising the value in the third one
   *
   */
  @Test
  void should_contain_zoom_new_third_value_up(FxRobot robot) {
    // Given
    initDrawAndSelectSnowFlake(robot);

    // When
    incrementSpinner(robot, this.toolboxWindow.getZoomSpinner3());

    // Then
    assertZoomFactorNear(1.3d, "Zoom after third spinner increment");
  }

  /**
   * Checks if the first and second zoom spinners in the toolbox
   * contain the right value when lowering the value in the third one
   *
   */
  @Test
  void should_contain_zoom_new_third_value_down(FxRobot robot) {
    // Given
    initDrawAndSelectSnowFlake(robot);

    // When
    decrementSpinner(robot, this.toolboxWindow.getZoomSpinner3());

    // Then
    assertZoomFactorNear(0.7d, "Zoom after third spinner decrement");
  }

}

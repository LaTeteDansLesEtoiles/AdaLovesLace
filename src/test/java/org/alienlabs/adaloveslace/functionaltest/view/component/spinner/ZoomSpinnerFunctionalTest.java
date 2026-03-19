package org.alienlabs.adaloveslace.functionaltest.view.component.spinner;

import javafx.stage.Stage;
import org.alienlabs.adaloveslace.functionaltest.AppFunctionalTestParent;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
   * Checks if the three zoom spinners in the toolbox contain the right default value
   *
   */
  @Test
  void should_contain_zoom_default_value(FxRobot robot) {
    // Given
    initDrawAndSelectSnowFlake(robot);

    // Then
    // Wait for the zoom factor to be properly initialized
    assertCondition(() -> {
      try {
        double zoomFactor = getSnowFlakeZoomFactor();
        return Math.abs(zoomFactor - 1.0) < 0.001; // Allow for small floating point differences
      } catch (Exception e) {
        return false;
      }
    }, "Zoom factor to be properly initialized to 1.0");
    assertZoomFactorEquals(1d); // Spinner contains 0
  }

  /**
   * Checks if the drawn Pattern has the right zoom factor values when providing values in the first zoom spinner.
   *
   */
  @ParameterizedTest(name = "Check changing first zoom value #{index}")
  @CsvSource({"1, 1.1", "3, 1.3", "-1, 0.9", "-2, 0.8", "-3, 0.7", "0, 1"})
  void should_react_to_first_zoom_value(int spinnerValue, double expectedZoomFactor) {
    // Given
      FxRobot robot = new FxRobot();
      initDrawAndSelectSnowFlake(robot);

    // When
    setSpinnerValue(robot, this.toolboxWindow.getZoomSpinner1(), spinnerValue);

    // Then
    assertZoomFactorEquals(expectedZoomFactor);
  }


  /**
   * Checks if the drawn Pattern has the right zoom factor values when providing values in the second zoom spinner.
   *
   */
  @ParameterizedTest(name = "Check changing second zoom value #{index}")
  @CsvSource({"1, 1.1", "3, 1.3", "-1, 0.9", "-2, 0.8", "-3, 0.7", "0, 1"})
  void should_react_to_second_zoom_value(int spinnerValue, double expectedZoomFactor) {
    // Given
      FxRobot robot = new FxRobot();
      initDrawAndSelectSnowFlake(robot);

    // When
    setSpinnerValue(robot, this.toolboxWindow.getZoomSpinner2(), spinnerValue);

    // Then
    assertZoomFactorEquals(expectedZoomFactor);
  }

  /**
   * Checks if the drawn Pattern has the right zoom factor values when providing values in the third zoom spinner.
   *
   */
  @ParameterizedTest(name = "Check changing third zoom value #{index}")
  @CsvSource({"1, 1.1", "3, 1.3", "-1, 0.9", "-2, 0.8", "-3, 0.7", "0, 1"})
  void should_react_to_third_zoom_value(int spinnerValue, double expectedZoomFactor) {
    // Given
      FxRobot robot = new FxRobot();
      initDrawAndSelectSnowFlake(robot);

    // When
    setSpinnerValue(robot, this.toolboxWindow.getZoomSpinner3(), spinnerValue);

    // Then
    assertZoomFactorEquals(expectedZoomFactor);
  }

  /**
   *
   * Checks if the second and third zoom spinners in the toolbox
   * contain the right value when choosing a value in the first one
   *
   */
  @ParameterizedTest(name = "Check changing first zoom value #{index}")
  @CsvSource({"1", "2", "5", "10", "20", "-1", "-2", "-5", "-10", "-20", "0"})
  void any_zoom_spinner_should_react_to_the_first_zoom_value_change(int spinnerValue) {
    // Given
      FxRobot robot = new FxRobot();
      initDrawAndSelectSnowFlake(robot);

    // When
    setSpinnerValue(robot, this.toolboxWindow.getZoomSpinner1(), spinnerValue);

    // Then
    verifyThat(this.toolboxWindow.getZoomSpinner2().getValueFactory().getValue(), org.hamcrest.Matchers.is(spinnerValue));
    verifyThat(this.toolboxWindow.getZoomSpinner3().getValueFactory().getValue(), org.hamcrest.Matchers.is(spinnerValue));
  }

  /**
   *
   * Checks if the first and third zoom spinners in the toolbox
   * contain the right value when choosing a value in the second one
   *
   */
  @ParameterizedTest(name = "Check changing second zoom value #{index}")
  @CsvSource({"1", "2", "5", "10", "20", "-1", "-2", "-5", "-10", "-20", "0"})
  void any_zoom_spinner_should_react_to_the_second_zoom_value_change(int spinnerValue) {
    // Given
      FxRobot robot = new FxRobot();
      initDrawAndSelectSnowFlake(robot);

    // When
    setSpinnerValue(robot, this.toolboxWindow.getZoomSpinner2(), spinnerValue);

    // Then
    verifyThat(this.toolboxWindow.getZoomSpinner1().getValueFactory().getValue(), org.hamcrest.Matchers.is(spinnerValue));
    verifyThat(this.toolboxWindow.getZoomSpinner3().getValueFactory().getValue(), org.hamcrest.Matchers.is(spinnerValue));
  }


  /**
   *
   * Checks if the first and second zoom spinners in the toolbox
   * contain the right value when choosing a value in the third one
   *
   */
  @ParameterizedTest(name = "Check changing third zoom value #{index}")
  @CsvSource({"1", "2", "5", "10", "20", "-1", "-2", "-5", "-10", "-20", "0"})
  void any_zoom_spinner_should_react_to_third_zoom_value_change(int spinnerValue) {
    // Given
      FxRobot robot = new FxRobot();
      initDrawAndSelectSnowFlake(robot);

    // When
    setSpinnerValue(robot, this.toolboxWindow.getZoomSpinner3(), spinnerValue);

    // Then
    verifyThat(this.toolboxWindow.getZoomSpinner1().getValueFactory().getValue(), org.hamcrest.Matchers.is(spinnerValue));
    verifyThat(this.toolboxWindow.getZoomSpinner2().getValueFactory().getValue(), org.hamcrest.Matchers.is(spinnerValue));
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
    assertZoomFactorEquals(1.1d);
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
    assertZoomFactorEquals(0.9d);
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
    assertZoomFactorEquals(1.2d);
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
    assertZoomFactorEquals(0.8d);
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
    assertZoomFactorEquals(1.3d);
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
    assertZoomFactorEquals(0.7d);
  }

  private void assertZoomFactorEquals(double expectedZoom) {
    // For zoom factor assertions, we need to use a custom matcher since it's not a UI element
    // We'll keep the JUnit assertion for this specific case as it's testing business logic
    org.junit.jupiter.api.Assertions.assertEquals(expectedZoom, getSnowFlakeZoomFactor());
  }

}

package org.alienlabs.adaloveslace.functionaltest.view.component.spinner;

import javafx.stage.Stage;
import org.alienlabs.adaloveslace.functionaltest.AppFunctionalTestParent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ZoomSpinnerFunctionalTest extends AppFunctionalTestParent {

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

  /**
   * Checks if the three zoom spinners in the toolbox contain the right default value
   *
   */
  @Test
  void should_contain_zoom_default_value(FxRobot robot) {
    // Given
    initDrawAndSelectSnowFlake(robot);

    // Then
    assertZoomFactorEquals(1d); // Spinner contains 0
  }

  /**
   * Checks if the drawn Pattern has the right zoom factor values when providing values in the first zoom spinner.
   *
   */
  @ParameterizedTest(name = "Check changing first zoom value #{index}")
  @CsvSource({"1, 1.1", "2, 1.2", "3, 1.3", "-1, 0.95", "-2, 0.9", "-3, 0.85", "0, 1"})
  void should_react_to_first_zoom_value(int spinnerValue, double expectedZoomFactor) {
    // Given
    initDrawAndSelectSnowFlake(new FxRobot());

    // When
    synchronizeTask(() -> setSpinnerValue(this.geometryWindow.getZoomSpinner1(), spinnerValue));

    // Then
    assertZoomFactorEquals(expectedZoomFactor);
  }


  /**
   * Checks if the drawn Pattern has the right zoom factor values when providing values in the second zoom spinner.
   *
   */
  @ParameterizedTest(name = "Check changing second zoom value #{index}")
  @CsvSource({"1, 1.1", "2, 1.2", "3, 1.3", "-1, 0.95", "-2, 0.9", "-3, 0.85", "0, 1"})
  void should_react_to_second_zoom_value(int spinnerValue, double expectedZoomFactor) {
    // Given
    initDrawAndSelectSnowFlake(new FxRobot());

    // When
    synchronizeTask(() -> setSpinnerValue(this.geometryWindow.getZoomSpinner2(), spinnerValue));

    // Then
    assertZoomFactorEquals(expectedZoomFactor);
  }

  /**
   * Checks if the drawn Pattern has the right zoom factor values when providing values in the third zoom spinner.
   *
   */
  @ParameterizedTest(name = "Check changing third zoom value #{index}")
  @CsvSource({"1, 1.1", "2, 1.2", "3, 1.3", "-1, 0.95", "-2, 0.9", "-3, 0.85", "0, 1"})
  void should_react_to_third_zoom_value(int spinnerValue, double expectedZoomFactor) {
    // Given
    initDrawAndSelectSnowFlake(new FxRobot());

    // When
    synchronizeTask(() -> setSpinnerValue(this.geometryWindow.getZoomSpinner3(), spinnerValue));

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
    initDrawAndSelectSnowFlake(new FxRobot());

    // When
    synchronizeTask(() -> setSpinnerValue(this.geometryWindow.getZoomSpinner1(), spinnerValue));

    // Then
    assertEquals(spinnerValue, this.geometryWindow.getZoomSpinner2().getValueFactory().getValue());
    assertEquals(spinnerValue, this.geometryWindow.getZoomSpinner3().getValueFactory().getValue());
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
    initDrawAndSelectSnowFlake(new FxRobot());

    // When
    synchronizeTask(() -> setSpinnerValue(this.geometryWindow.getZoomSpinner2(), spinnerValue));

    // Then
    assertEquals(spinnerValue, this.geometryWindow.getZoomSpinner1().getValueFactory().getValue());
    assertEquals(spinnerValue, this.geometryWindow.getZoomSpinner3().getValueFactory().getValue());
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
    initDrawAndSelectSnowFlake(new FxRobot());

    // When
    synchronizeTask(() -> setSpinnerValue(this.geometryWindow.getZoomSpinner3(), spinnerValue));

    // Then
    assertEquals(spinnerValue, this.geometryWindow.getZoomSpinner1().getValueFactory().getValue());
    assertEquals(spinnerValue, this.geometryWindow.getZoomSpinner2().getValueFactory().getValue());
  }

  private void assertZoomFactorEquals(double expectedZoom) {
    assertEquals(expectedZoom, getSnowFlakeZoomFactor());
  }

}

package org.alienlabs.adaloveslace.functionaltest.view.component.spinner;

import javafx.stage.Stage;
import org.alienlabs.adaloveslace.functionaltest.AppFunctionalTestParent;
import org.junit.jupiter.api.Test;
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
    drawAndSelectSnowFlake(robot);

    // Then
    assertZoomFactorsEqual(1d); // Spinner contains 0
  }

  /**
   * Checks if the second and third zoom spinners in the toolbox
   * contain the right value when raising the value in the first one
   *
   */
  @Test
  void should_contain_zoom_new_first_value_up(FxRobot robot) {
    // Given
    drawAndSelectSnowFlake(robot);

    // When
    synchronizeTask(() -> incrementSpinner(this.geometryWindow.getZoomSpinner1()));

    // Then
    assertZoomFactorsEqual(1.1); // Spinner contains 1
  }

  /**
   * Checks if the second and third zoom spinners in the toolbox
   * contain the right value when lowering the value in the first one
   *
   */
  @Test
  void should_contain_zoom_new_first_value_down(FxRobot robot) {
    // Given
    drawAndSelectSnowFlake(robot);

    // When
    synchronizeTask(() -> decrementSpinner(this.geometryWindow.getZoomSpinner1()));

    // Then
    assertZoomFactorsEqual(0.95d); // Spinner contains -1
  }

  /**
   * Checks if the first and third zoom spinners in the toolbox
   * contain the right value when raising the value in the second one
   *
   */
  @Test
  void should_contain_zoom_new_second_value_up(FxRobot robot) {
    // Given
    drawAndSelectSnowFlake(robot);

    // When
    synchronizeTask(() -> incrementSpinner(this.geometryWindow.getZoomSpinner2()));

    // Then
    assertZoomFactorsEqual(1.2d); // Spinner contains 2
  }

  /**
   * Checks if the first and third zoom spinners in the toolbox
   * contain the right value when lowering the value in the second one
   *
   */
  @Test
  void should_contain_zoom_new_second_value_down(FxRobot robot) {
    // Given
    drawAndSelectSnowFlake(robot);

    // When
    synchronizeTask(() -> decrementSpinner(this.geometryWindow.getZoomSpinner2()));

    // Then
    assertZoomFactorsEqual(0.9d); // Spinner contains -2
  }

  /**
   * Checks if the first and second zoom spinners in the toolbox
   * contain the right value when raising the value in the third one
   *
   */
  @Test
  void should_contain_zoom_new_third_value_up(FxRobot robot) {
    // Given
    drawAndSelectSnowFlake(robot);

    // When
    synchronizeTask(() -> incrementSpinner(this.geometryWindow.getZoomSpinner3()));

    // Then
    assertZoomFactorsEqual(1.3); // Spinner contains 3
  }

  /**
   * Checks if the first and second zoom spinners in the toolbox
   * contain the right value when lowering the value in the third one
   *
   */
  @Test
  void should_contain_zoom_new_third_value_down(FxRobot robot) {
    // Given
    drawAndSelectSnowFlake(robot);

    // When
    synchronizeTask(() -> decrementSpinner(this.geometryWindow.getZoomSpinner3()));

    // Then
    assertZoomFactorsEqual(0.85); // Spinner contains -3
  }

  private void assertZoomFactorsEqual(double expectedZoom) {
    assertEquals(expectedZoom, getSnowFlakeZoomFactor());
  }

}

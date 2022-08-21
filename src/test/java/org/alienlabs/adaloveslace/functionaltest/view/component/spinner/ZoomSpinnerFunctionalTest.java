package org.alienlabs.adaloveslace.functionaltest.view.component.spinner;

import javafx.stage.Stage;
import org.alienlabs.adaloveslace.functionaltest.AppFunctionalTestParent;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;

import static org.alienlabs.adaloveslace.business.model.Knot.DEFAULT_ZOOM;
import static org.alienlabs.adaloveslace.view.window.GeometryWindow.*;
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
    // Init
    synchronizeTask(() -> selectAndClickOnSnowflakeButton(robot));

    // Run
    synchronizeTask(() -> drawSnowflake(robot));
    synchronizeTask(() -> clickSelectButton(robot));
    synchronizeTask(() -> selectSnowflake(robot));

    // Verify
    assertIncrementWasReflected(DEFAULT_ZOOM, getSnowFlakeComputedZoomFactor());
  }

  /**
   * Checks if the second and third zoom spinners in the toolbox
   * contain the right value when raising the value in the first one
   *
   */
  @Test
  void should_contain_zoom_new_first_value_up(FxRobot robot) {
    // Init
    synchronizeTask(() -> selectAndClickOnSnowflakeButton(robot));
    synchronizeTask(() -> drawSnowflake(robot));
    synchronizeTask(() -> clickSelectButton(robot));
    synchronizeTask(() -> selectSnowflake(robot));

    // Run
    synchronizeTask(() -> incrementSpinner(this.geometryWindow.getZoomSpinner1()));

    // Verify
    assertIncrementWasReflected(DEFAULT_ZOOM + ZOOM_SPINNER_INCREMENTS_1, computeZoomFactorOfSelectedKnot());
  }

  /**
   * Checks if the second and third zoom spinners in the toolbox
   * contain the right value when lowering the value in the first one
   *
   */
  @Test
  void should_contain_zoom_new_first_value_down(FxRobot robot) {
    // Init
    synchronizeTask(() -> selectAndClickOnSnowflakeButton(robot));
    synchronizeTask(() -> drawSnowflake(robot));
    synchronizeTask(() -> clickSelectButton(robot));
    synchronizeTask(() -> selectSnowflake(robot));

    // Run
    synchronizeTask(() -> decrementSpinner(this.geometryWindow.getZoomSpinner1()));

    // Verify
    assertIncrementWasReflected(DEFAULT_ZOOM - ZOOM_SPINNER_INCREMENTS_1, getSnowFlakeComputedZoomFactor());
  }

  /**
   * Checks if the first and third zoom spinners in the toolbox
   * contain the right value when raising the value in the second one
   *
   */
  @Test
  void should_contain_zoom_new_second_value_up(FxRobot robot) {
    // Init
    synchronizeTask(() -> selectAndClickOnSnowflakeButton(robot));
    synchronizeTask(() -> drawSnowflake(robot));
    synchronizeTask(() -> clickSelectButton(robot));
    synchronizeTask(() -> selectSnowflake(robot));

    // Run
    synchronizeTask(() -> incrementSpinner(this.geometryWindow.getZoomSpinner2()));

    // Verify
    assertIncrementWasReflected(DEFAULT_ZOOM + ZOOM_SPINNER_INCREMENTS_2, getSnowFlakeComputedZoomFactor());
  }

  /**
   * Checks if the first and third zoom spinners in the toolbox
   * contain the right value when lowering the value in the second one
   *
   */
  @Test
  void should_contain_zoom_new_second_value_down(FxRobot robot) {
    // Init
    synchronizeTask(() -> selectAndClickOnSnowflakeButton(robot));
    synchronizeTask(() -> drawSnowflake(robot));
    synchronizeTask(() -> clickSelectButton(robot));
    synchronizeTask(() -> selectSnowflake(robot));

    // Run
    synchronizeTask(() -> decrementSpinner(this.geometryWindow.getZoomSpinner2()));

    // Verify
    assertDecrementWasReflected(DEFAULT_ZOOM - ZOOM_SPINNER_INCREMENTS_2, getSnowFlakeComputedZoomFactor());
  }

  /**
   * Checks if the first and second zoom spinners in the toolbox
   * contain the right value when raising the value in the third one
   *
   */
  @Test
  void should_contain_zoom_new_third_value_up(FxRobot robot) {
    // Init
    synchronizeTask(() -> selectAndClickOnSnowflakeButton(robot));
    synchronizeTask(() -> drawSnowflake(robot));
    synchronizeTask(() -> clickSelectButton(robot));
    synchronizeTask(() -> selectSnowflake(robot));

    // Run
    synchronizeTask(() -> incrementSpinner(this.geometryWindow.getZoomSpinner3()));

    // Verify
    assertIncrementWasReflected(DEFAULT_ZOOM + ZOOM_SPINNER_INCREMENTS_3, getSnowFlakeComputedZoomFactor());
  }

  /**
   * Checks if the first and second zoom spinners in the toolbox
   * contain the right value when lowering the value in the third one
   *
   */
  @Test
  void should_contain_zoom_new_third_value_down(FxRobot robot) {
    // Init
    synchronizeTask(() -> selectAndClickOnSnowflakeButton(robot));
    synchronizeTask(() -> drawSnowflake(robot));
    synchronizeTask(() -> clickSelectButton(robot));
    synchronizeTask(() -> selectSnowflake(robot));

    // Run
    synchronizeTask(() -> decrementSpinner(this.geometryWindow.getZoomSpinner3()));

    // Verify
    assertDecrementWasReflected(DEFAULT_ZOOM - ZOOM_SPINNER_INCREMENTS_3, getSnowFlakeComputedZoomFactor());
  }

  private void assertIncrementWasReflected(int increment, double actual) {
    assertEquals(computeNewZoomFactor(increment), actual);
  }

  private void assertDecrementWasReflected(int decrement, double actual) {
    assertEquals(computeNewZoomFactor(decrement), actual);
  }

  private double computeZoomFactorOfSelectedKnot() {
    return app.getOptionalDotGrid().computeZoomFactor(this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().
      stream().findFirst().get());
  }

  private double computeNewZoomFactor(int DEFAULT_ZOOM) {
    return app.getOptionalDotGrid().computeZoomFactor(DEFAULT_ZOOM);
  }

  private double getSnowFlakeComputedZoomFactor() {
    return this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().
      stream().findFirst().get().getImageView().getScaleX();
  }

}

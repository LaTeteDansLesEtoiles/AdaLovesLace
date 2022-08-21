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
    assertEquals(DEFAULT_ZOOM, getSnowFlakeZoomFactor());
    assertEquals(DEFAULT_ZOOM, this.geometryWindow.getZoomSpinner1().getValue());
    assertEquals(DEFAULT_ZOOM, this.geometryWindow.getZoomSpinner2().getValue());
    assertEquals(DEFAULT_ZOOM, this.geometryWindow.getZoomSpinner3().getValue());
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
    synchronizeTask(() -> this.geometryWindow.getZoomSpinner1().getValueFactory().setValue(DEFAULT_ZOOM + ZOOM_SPINNER_INCREMENTS_1));

    // Verify
    assertEquals(DEFAULT_ZOOM + ZOOM_SPINNER_INCREMENTS_1, getSnowFlakeZoomFactor());
    assertEquals(DEFAULT_ZOOM + ZOOM_SPINNER_INCREMENTS_1, this.geometryWindow.getZoomSpinner2().getValue());
    assertEquals(DEFAULT_ZOOM + ZOOM_SPINNER_INCREMENTS_1, this.geometryWindow.getZoomSpinner3().getValue());
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
    synchronizeTask(() -> this.geometryWindow.getZoomSpinner1().getValueFactory().setValue(DEFAULT_ZOOM - ZOOM_SPINNER_INCREMENTS_1));

    // Verify
    assertEquals(DEFAULT_ZOOM - ZOOM_SPINNER_INCREMENTS_1, getSnowFlakeZoomFactor());
    assertEquals(DEFAULT_ZOOM - ZOOM_SPINNER_INCREMENTS_1, this.geometryWindow.getZoomSpinner2().getValue());
    assertEquals(DEFAULT_ZOOM - ZOOM_SPINNER_INCREMENTS_1, this.geometryWindow.getZoomSpinner3().getValue());
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
    synchronizeTask(() -> this.geometryWindow.getZoomSpinner2().getValueFactory().setValue(DEFAULT_ZOOM + ZOOM_SPINNER_INCREMENTS_2));

    // Verify
    assertEquals(DEFAULT_ZOOM + ZOOM_SPINNER_INCREMENTS_2, getSnowFlakeZoomFactor());
    assertEquals(DEFAULT_ZOOM + ZOOM_SPINNER_INCREMENTS_2, this.geometryWindow.getZoomSpinner1().getValue());
    assertEquals(DEFAULT_ZOOM + ZOOM_SPINNER_INCREMENTS_2, this.geometryWindow.getZoomSpinner3().getValue());
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
    synchronizeTask(() -> this.geometryWindow.getZoomSpinner2().getValueFactory().setValue(DEFAULT_ZOOM - ZOOM_SPINNER_INCREMENTS_2));

    // Verify
    assertEquals(DEFAULT_ZOOM - ZOOM_SPINNER_INCREMENTS_2, getSnowFlakeZoomFactor());
    assertEquals(DEFAULT_ZOOM - ZOOM_SPINNER_INCREMENTS_2, this.geometryWindow.getZoomSpinner1().getValue());
    assertEquals(DEFAULT_ZOOM - ZOOM_SPINNER_INCREMENTS_2, this.geometryWindow.getZoomSpinner3().getValue());
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
    synchronizeTask(() -> this.geometryWindow.getZoomSpinner3().getValueFactory().setValue(DEFAULT_ZOOM + ZOOM_SPINNER_INCREMENTS_3));

    // Verify
    assertEquals(DEFAULT_ZOOM + ZOOM_SPINNER_INCREMENTS_3, getSnowFlakeZoomFactor());
    assertEquals(DEFAULT_ZOOM + ZOOM_SPINNER_INCREMENTS_3, this.geometryWindow.getZoomSpinner1().getValue());
    assertEquals(DEFAULT_ZOOM + ZOOM_SPINNER_INCREMENTS_3, this.geometryWindow.getZoomSpinner2().getValue());
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
    synchronizeTask(() -> this.geometryWindow.getZoomSpinner3().getValueFactory().setValue(DEFAULT_ZOOM - ZOOM_SPINNER_INCREMENTS_3));

    // Verify
    assertEquals(DEFAULT_ZOOM - ZOOM_SPINNER_INCREMENTS_3, getSnowFlakeZoomFactor());
    assertEquals(DEFAULT_ZOOM - ZOOM_SPINNER_INCREMENTS_3, this.geometryWindow.getZoomSpinner1().getValue());
    assertEquals(DEFAULT_ZOOM - ZOOM_SPINNER_INCREMENTS_3, this.geometryWindow.getZoomSpinner2().getValue());
  }

  private double getSnowFlakeZoomFactor() {
    return this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().
      stream().findFirst().get().getZoomFactor();
  }

}

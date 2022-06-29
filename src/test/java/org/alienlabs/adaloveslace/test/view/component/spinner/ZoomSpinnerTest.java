package org.alienlabs.adaloveslace.test.view.component.spinner;

import javafx.geometry.Point2D;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.test.AppTestParent;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;

import static org.alienlabs.adaloveslace.view.window.GeometryWindow.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ZoomSpinnerTest extends AppTestParent {

  private static final double SPINNER_1_UP_X     = 250d;
  private static final double SPINNER_1_UP_Y     = 180d;
  private static final double SPINNER_1_DOWN_X   = 250d;
  private static final double SPINNER_1_DOWN_Y   = 220d;

  private static final double SPINNER_2_UP_X     = 250d;
  private static final double SPINNER_2_UP_Y     = 245d;
  private static final double SPINNER_2_DOWN_X   = 250d;
  private static final double SPINNER_2_DOWN_Y   = 285d;

  private static final double SPINNER_3_UP_X     = 250d;
  private static final double SPINNER_3_UP_Y     = 310d;
  private static final double SPINNER_3_DOWN_X   = 250d;
  private static final double SPINNER_3_DOWN_Y   = 345d;

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
    selectAndClickOnSnowflake(robot);

    // Run
    drawSnowflake(robot);

    // Verify
    assertEquals(ZOOM_SPINNER_DEFAULT_VALUE,
      this.app.getOptionalDotGrid().getDiagram().getCurrentKnot().getZoomFactor());
    assertEquals(ZOOM_SPINNER_DEFAULT_VALUE,
      this.geometryWindow.getZoomSpinner1().getValue());
    assertEquals(ZOOM_SPINNER_DEFAULT_VALUE,
      this.geometryWindow.getZoomSpinner2().getValue());
    assertEquals(ZOOM_SPINNER_DEFAULT_VALUE,
      this.geometryWindow.getZoomSpinner3().getValue());
  }

  /**
   * Checks if the second and third zoom spinners in the toolbox
   * contain the right value when raising the value in the first one
   *
   */
  @Test
  void should_contain_zoom_new_first_value_up(FxRobot robot) {
    // Init
    selectAndClickOnSnowflake(robot);
    drawSnowflake(robot);

    Point2D pointToMoveTo = newSpinnerPoint(SPINNER_1_UP_X, SPINNER_1_UP_Y);

    // Run
    robot.moveTo(pointToMoveTo);
    robot.clickOn(pointToMoveTo);

    // Verify
    try {
      Thread.sleep(SLEEP_TIME);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    assertEquals(ZOOM_SPINNER_DEFAULT_VALUE + ZOOM_SPINNER_INCREMENTS_1,
      this.app.getOptionalDotGrid().getDiagram().getCurrentKnot().getZoomFactor());
    assertEquals(ZOOM_SPINNER_DEFAULT_VALUE + ZOOM_SPINNER_INCREMENTS_1,
      this.geometryWindow.getZoomSpinner1().getValue());
    assertEquals(ZOOM_SPINNER_DEFAULT_VALUE + ZOOM_SPINNER_INCREMENTS_1,
      this.geometryWindow.getZoomSpinner2().getValue());
    assertEquals(ZOOM_SPINNER_DEFAULT_VALUE + ZOOM_SPINNER_INCREMENTS_1,
      this.geometryWindow.getZoomSpinner3().getValue());
  }

  /**
   * Checks if the second and third zoom spinners in the toolbox
   * contain the right value when lowering the value in the first one
   *
   */
  @Test
  void should_contain_zoom_new_first_value_down(FxRobot robot) {
    // Init
    selectAndClickOnSnowflake(robot);
    drawSnowflake(robot);

    Point2D pointToMoveTo = newSpinnerPoint(SPINNER_1_DOWN_X, SPINNER_1_DOWN_Y);

    // Run
    robot.moveTo(pointToMoveTo);
    robot.clickOn(pointToMoveTo);

    // Verify
    try {
      Thread.sleep(SLEEP_TIME);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    assertEquals(ZOOM_SPINNER_DEFAULT_VALUE - ZOOM_SPINNER_INCREMENTS_1,
      this.app.getOptionalDotGrid().getDiagram().getCurrentKnot().getZoomFactor());
    assertEquals(ZOOM_SPINNER_DEFAULT_VALUE - ZOOM_SPINNER_INCREMENTS_1,
      this.geometryWindow.getZoomSpinner1().getValue());
    assertEquals(ZOOM_SPINNER_DEFAULT_VALUE - ZOOM_SPINNER_INCREMENTS_1,
      this.geometryWindow.getZoomSpinner2().getValue());
    assertEquals(ZOOM_SPINNER_DEFAULT_VALUE - ZOOM_SPINNER_INCREMENTS_1,
      this.geometryWindow.getZoomSpinner3().getValue());
  }

  /**
   * Checks if the first and third zoom spinners in the toolbox
   * contain the right value when raising the value in the second one
   *
   */
  @Test
  void should_contain_zoom_new_second_value_up(FxRobot robot) {
    // Init
    selectAndClickOnSnowflake(robot);
    drawSnowflake(robot);

    Point2D pointToMoveTo = newSpinnerPoint(SPINNER_2_UP_X, SPINNER_2_UP_Y);

    // Run
    robot.moveTo(pointToMoveTo);
    robot.clickOn(pointToMoveTo);

    // Verify
    try {
      Thread.sleep(SLEEP_TIME);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    assertEquals(ZOOM_SPINNER_DEFAULT_VALUE + ZOOM_SPINNER_INCREMENTS_2,
      this.app.getOptionalDotGrid().getDiagram().getCurrentKnot().getZoomFactor());
    assertEquals(ZOOM_SPINNER_DEFAULT_VALUE + ZOOM_SPINNER_INCREMENTS_2,
      this.geometryWindow.getZoomSpinner1().getValue());
    assertEquals(ZOOM_SPINNER_DEFAULT_VALUE + ZOOM_SPINNER_INCREMENTS_2,
      this.geometryWindow.getZoomSpinner2().getValue());
    assertEquals(ZOOM_SPINNER_DEFAULT_VALUE + ZOOM_SPINNER_INCREMENTS_2,
      this.geometryWindow.getZoomSpinner3().getValue());
  }

  /**
   * Checks if the first and third zoom spinners in the toolbox
   * contain the right value when lowering the value in the second one
   *
   */
  @Test
  void should_contain_zoom_new_second_value_down(FxRobot robot) {
    // Init
    selectAndClickOnSnowflake(robot);
    drawSnowflake(robot);

    Point2D pointToMoveTo = newSpinnerPoint(SPINNER_2_DOWN_X, SPINNER_2_DOWN_Y);

    // Run
    robot.moveTo(pointToMoveTo);
    robot.clickOn(pointToMoveTo);

    // Verify
    try {
      Thread.sleep(SLEEP_TIME);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    assertEquals(ZOOM_SPINNER_DEFAULT_VALUE - ZOOM_SPINNER_INCREMENTS_2,
      this.app.getOptionalDotGrid().getDiagram().getCurrentKnot().getZoomFactor());
    assertEquals(ZOOM_SPINNER_DEFAULT_VALUE - ZOOM_SPINNER_INCREMENTS_2,
      this.geometryWindow.getZoomSpinner1().getValue());
    assertEquals(ZOOM_SPINNER_DEFAULT_VALUE - ZOOM_SPINNER_INCREMENTS_2,
      this.geometryWindow.getZoomSpinner2().getValue());
    assertEquals(ZOOM_SPINNER_DEFAULT_VALUE - ZOOM_SPINNER_INCREMENTS_2,
      this.geometryWindow.getZoomSpinner3().getValue());
  }

  /**
   * Checks if the first and second zoom spinners in the toolbox
   * contain the right value when raising the value in the third one
   *
   */
  @Test
  void should_contain_zoom_new_third_value_up(FxRobot robot) {
    // Init
    selectAndClickOnSnowflake(robot);
    drawSnowflake(robot);

    Point2D pointToMoveTo = newSpinnerPoint(SPINNER_3_UP_X, SPINNER_3_UP_Y);

    // Run
    robot.moveTo(pointToMoveTo);
    robot.clickOn(pointToMoveTo);

    // Verify
    try {
      Thread.sleep(SLEEP_TIME);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    assertEquals(ZOOM_SPINNER_DEFAULT_VALUE + ZOOM_SPINNER_INCREMENTS_3,
      this.app.getOptionalDotGrid().getDiagram().getCurrentKnot().getZoomFactor());
    assertEquals(ZOOM_SPINNER_DEFAULT_VALUE + ZOOM_SPINNER_INCREMENTS_3,
      this.geometryWindow.getZoomSpinner1().getValue());
    assertEquals(ZOOM_SPINNER_DEFAULT_VALUE + ZOOM_SPINNER_INCREMENTS_3,
      this.geometryWindow.getZoomSpinner2().getValue());
    assertEquals(ZOOM_SPINNER_DEFAULT_VALUE + ZOOM_SPINNER_INCREMENTS_3,
      this.geometryWindow.getZoomSpinner3().getValue());
  }

  /**
   * Checks if the first and second zoom spinners in the toolbox
   * contain the right value when lowering the value in the third one
   *
   */
  @Test
  void should_contain_zoom_new_third_value_down(FxRobot robot) {
    // Init
    selectAndClickOnSnowflake(robot);
    drawSnowflake(robot);

    Point2D pointToMoveTo = newSpinnerPoint(SPINNER_3_DOWN_X, SPINNER_3_DOWN_Y);

    // Run
    robot.moveTo(pointToMoveTo);
    robot.clickOn(pointToMoveTo);

    // Verify
    try {
      Thread.sleep(SLEEP_TIME);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    assertEquals(ZOOM_SPINNER_DEFAULT_VALUE - ZOOM_SPINNER_INCREMENTS_3,
      this.app.getOptionalDotGrid().getDiagram().getCurrentKnot().getZoomFactor());
    assertEquals(ZOOM_SPINNER_DEFAULT_VALUE - ZOOM_SPINNER_INCREMENTS_3,
      this.geometryWindow.getZoomSpinner1().getValue());
    assertEquals(ZOOM_SPINNER_DEFAULT_VALUE - ZOOM_SPINNER_INCREMENTS_3,
      this.geometryWindow.getZoomSpinner2().getValue());
    assertEquals(ZOOM_SPINNER_DEFAULT_VALUE - ZOOM_SPINNER_INCREMENTS_3,
      this.geometryWindow.getZoomSpinner3().getValue());
  }

}

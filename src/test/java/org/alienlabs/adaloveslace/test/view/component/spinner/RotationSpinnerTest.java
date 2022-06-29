package org.alienlabs.adaloveslace.test.view.component.spinner;

import javafx.geometry.Point2D;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.test.AppTestParent;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;

import static org.alienlabs.adaloveslace.view.window.GeometryWindow.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RotationSpinnerTest extends AppTestParent {

  private static final double SPINNER_UP_Y    = 18d;
  private static final double SPINNER_DOWN_Y  = 20d;

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
   * Checks if the three rotation spinners in the toolbox contain the right default value
   *
   */
  @Test
  void should_contain_rotation_default_value(FxRobot robot) {
    // Init
    selectAndClickOnSnowflake(robot);

    // Run
    drawSnowflake(robot);

    // Verify
    assertEquals(ROTATION_SPINNER_DEFAULT_VALUE,
      this.app.getOptionalDotGrid().getDiagram().getCurrentKnot().getImageView().getRotate());
    assertEquals(ROTATION_SPINNER_DEFAULT_VALUE,
      this.geometryWindow.getRotationSpinner1().getValue());
    assertEquals(ROTATION_SPINNER_DEFAULT_VALUE,
      this.geometryWindow.getRotationSpinner2().getValue());
    assertEquals(ROTATION_SPINNER_DEFAULT_VALUE,
      this.geometryWindow.getRotationSpinner3().getValue());
  }

  /**
   * Checks if the second and third rotation spinners in the toolbox
   * contain the right value when raising the value in the first one
   *
   */
  @Test
  void should_contain_rotation_new_first_value_up(FxRobot robot) {
    // Init
    selectAndClickOnSnowflake(robot);
    drawSnowflake(robot);

    Point2D pointToMoveTo = newSpinnerPoint(this.geometryWindow.getRotationSpinner1().getLayoutX() +
        this.geometryWindow.getRotationSpinner1().getWidth() / 2d,
      this.geometryWindow.getRotationSpinner1().getLayoutY() + this.geometryWindow.getRotationSpinner1().getHeight() - SPINNER_UP_Y);

    // Run
    robot.moveTo(pointToMoveTo);
    robot.clickOn(pointToMoveTo);

    // Verify
    try {
      Thread.sleep(SLEEP_BETWEEN_ACTIONS_TIME);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    assertEquals(ROTATION_SPINNER_DEFAULT_VALUE + ROTATION_SPINNER_INCREMENTS_1,
      this.app.getOptionalDotGrid().getDiagram().getCurrentKnot().getImageView().getRotate());
    assertEquals(ROTATION_SPINNER_DEFAULT_VALUE + ROTATION_SPINNER_INCREMENTS_1,
      this.geometryWindow.getRotationSpinner1().getValue());
    assertEquals(ROTATION_SPINNER_DEFAULT_VALUE + ROTATION_SPINNER_INCREMENTS_1,
      this.geometryWindow.getRotationSpinner2().getValue());
    assertEquals(ROTATION_SPINNER_DEFAULT_VALUE + ROTATION_SPINNER_INCREMENTS_1,
      this.geometryWindow.getRotationSpinner3().getValue());
  }

  /**
   * Checks if the second and third rotation spinners in the toolbox
   * contain the right value when lowering the value in the first one
   *
   */
  @Test
  void should_contain_rotation_new_first_value_down(FxRobot robot) {
    // Init
    selectAndClickOnSnowflake(robot);
    drawSnowflake(robot);

    Point2D pointToMoveTo = newSpinnerPoint(this.geometryWindow.getRotationSpinner1().getLayoutX() +
        this.geometryWindow.getRotationSpinner1().getWidth() / 2d,
      this.geometryWindow.getRotationSpinner1().getLayoutY() + this.geometryWindow.getRotationSpinner1().getHeight() + SPINNER_DOWN_Y);

    // Run
    robot.moveTo(pointToMoveTo);
    robot.clickOn(pointToMoveTo);

    // Verify
    try {
      Thread.sleep(SLEEP_BETWEEN_ACTIONS_TIME);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    assertEquals(ROTATION_SPINNER_DEFAULT_VALUE - ROTATION_SPINNER_INCREMENTS_1,
      this.app.getOptionalDotGrid().getDiagram().getCurrentKnot().getImageView().getRotate());
    assertEquals(ROTATION_SPINNER_DEFAULT_VALUE - ROTATION_SPINNER_INCREMENTS_1,
      this.geometryWindow.getRotationSpinner1().getValue());
    assertEquals(ROTATION_SPINNER_DEFAULT_VALUE - ROTATION_SPINNER_INCREMENTS_1,
      this.geometryWindow.getRotationSpinner2().getValue());
    assertEquals(ROTATION_SPINNER_DEFAULT_VALUE - ROTATION_SPINNER_INCREMENTS_1,
      this.geometryWindow.getRotationSpinner3().getValue());
  }

  /**
   * Checks if the first and third rotation spinners in the toolbox
   * contain the right value when raising the value in the second one
   *
   */
  @Test
  void should_contain_rotation_new_second_value_up(FxRobot robot) {
    // Init
    selectAndClickOnSnowflake(robot);
    drawSnowflake(robot);

    Point2D pointToMoveTo = newSpinnerPoint(this.geometryWindow.getRotationSpinner2().getLayoutX() +
        this.geometryWindow.getRotationSpinner2().getWidth() / 2d,
      this.geometryWindow.getRotationSpinner2().getLayoutY() + this.geometryWindow.getRotationSpinner2().getHeight() - SPINNER_UP_Y);

    // Run
    robot.moveTo(pointToMoveTo);
    robot.clickOn(pointToMoveTo);

    // Verify
    try {
      Thread.sleep(SLEEP_BETWEEN_ACTIONS_TIME);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    assertEquals(ROTATION_SPINNER_DEFAULT_VALUE + ROTATION_SPINNER_INCREMENTS_2,
      this.app.getOptionalDotGrid().getDiagram().getCurrentKnot().getImageView().getRotate());
    assertEquals(ROTATION_SPINNER_DEFAULT_VALUE + ROTATION_SPINNER_INCREMENTS_2,
      this.geometryWindow.getRotationSpinner1().getValue());
    assertEquals(ROTATION_SPINNER_DEFAULT_VALUE + ROTATION_SPINNER_INCREMENTS_2,
      this.geometryWindow.getRotationSpinner2().getValue());
    assertEquals(ROTATION_SPINNER_DEFAULT_VALUE + ROTATION_SPINNER_INCREMENTS_2,
      this.geometryWindow.getRotationSpinner3().getValue());
  }

  /**
   * Checks if the first and third rotation spinners in the toolbox
   * contain the right value when lowering the value in the second one
   *
   */
  @Test
  void should_contain_rotation_new_second_value_down(FxRobot robot) {
    // Init
    selectAndClickOnSnowflake(robot);
    drawSnowflake(robot);

    Point2D pointToMoveTo = newSpinnerPoint(this.geometryWindow.getRotationSpinner2().getLayoutX() +
        this.geometryWindow.getRotationSpinner2().getWidth() / 2d,
      this.geometryWindow.getRotationSpinner2().getLayoutY() + this.geometryWindow.getRotationSpinner2().getHeight() + SPINNER_DOWN_Y);

    // Run
    robot.moveTo(pointToMoveTo);
    robot.clickOn(pointToMoveTo);

    // Verify
    try {
      Thread.sleep(SLEEP_BETWEEN_ACTIONS_TIME);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    assertEquals(ROTATION_SPINNER_DEFAULT_VALUE - ROTATION_SPINNER_INCREMENTS_2,
      this.app.getOptionalDotGrid().getDiagram().getCurrentKnot().getImageView().getRotate());
    assertEquals(ROTATION_SPINNER_DEFAULT_VALUE - ROTATION_SPINNER_INCREMENTS_2,
      this.geometryWindow.getRotationSpinner1().getValue());
    assertEquals(ROTATION_SPINNER_DEFAULT_VALUE - ROTATION_SPINNER_INCREMENTS_2,
      this.geometryWindow.getRotationSpinner2().getValue());
    assertEquals(ROTATION_SPINNER_DEFAULT_VALUE - ROTATION_SPINNER_INCREMENTS_2,
      this.geometryWindow.getRotationSpinner3().getValue());
  }

  /**
   * Checks if the first and second rotation spinners in the toolbox
   * contain the right value when raising the value in the third one
   *
   */
  @Test
  void should_contain_rotation_new_third_value_up(FxRobot robot) {
    // Init
    selectAndClickOnSnowflake(robot);
    drawSnowflake(robot);

    Point2D pointToMoveTo = newSpinnerPoint(this.geometryWindow.getRotationSpinner3().getLayoutX() +
        this.geometryWindow.getRotationSpinner3().getWidth() / 2d,
      this.geometryWindow.getRotationSpinner3().getLayoutY() + this.geometryWindow.getRotationSpinner3().getHeight() - SPINNER_UP_Y);

    // Run
    robot.moveTo(pointToMoveTo);
    robot.clickOn(pointToMoveTo);

    // Verify
    try {
      Thread.sleep(SLEEP_BETWEEN_ACTIONS_TIME);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    assertEquals(ROTATION_SPINNER_DEFAULT_VALUE + ROTATION_SPINNER_INCREMENTS_3,
      this.app.getOptionalDotGrid().getDiagram().getCurrentKnot().getImageView().getRotate());
    assertEquals(ROTATION_SPINNER_DEFAULT_VALUE + ROTATION_SPINNER_INCREMENTS_3,
      this.geometryWindow.getRotationSpinner1().getValue());
    assertEquals(ROTATION_SPINNER_DEFAULT_VALUE + ROTATION_SPINNER_INCREMENTS_3,
      this.geometryWindow.getRotationSpinner2().getValue());
    assertEquals(ROTATION_SPINNER_DEFAULT_VALUE + ROTATION_SPINNER_INCREMENTS_3,
      this.geometryWindow.getRotationSpinner3().getValue());
  }

  /**
   * Checks if the first and second rotation spinners in the toolbox
   * contain the right value when lowering the value in the third one
   *
   */
  @Test
  void should_contain_rotation_new_third_value_down(FxRobot robot) {
    // Init
    selectAndClickOnSnowflake(robot);
    drawSnowflake(robot);

    Point2D pointToMoveTo = newSpinnerPoint(this.geometryWindow.getRotationSpinner3().getLayoutX() +
        this.geometryWindow.getRotationSpinner3().getWidth() / 2d,
      this.geometryWindow.getRotationSpinner3().getLayoutY() + this.geometryWindow.getRotationSpinner3().getHeight() + SPINNER_DOWN_Y);

    // Run
    robot.moveTo(pointToMoveTo);
    robot.clickOn(pointToMoveTo);

    // Verify
    try {
      Thread.sleep(SLEEP_BETWEEN_ACTIONS_TIME);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    assertEquals(ROTATION_SPINNER_DEFAULT_VALUE - ROTATION_SPINNER_INCREMENTS_3,
      this.app.getOptionalDotGrid().getDiagram().getCurrentKnot().getImageView().getRotate());
    assertEquals(ROTATION_SPINNER_DEFAULT_VALUE - ROTATION_SPINNER_INCREMENTS_3,
      this.geometryWindow.getRotationSpinner1().getValue());
    assertEquals(ROTATION_SPINNER_DEFAULT_VALUE - ROTATION_SPINNER_INCREMENTS_3,
      this.geometryWindow.getRotationSpinner2().getValue());
    assertEquals(ROTATION_SPINNER_DEFAULT_VALUE - ROTATION_SPINNER_INCREMENTS_3,
      this.geometryWindow.getRotationSpinner3().getValue());
  }

}

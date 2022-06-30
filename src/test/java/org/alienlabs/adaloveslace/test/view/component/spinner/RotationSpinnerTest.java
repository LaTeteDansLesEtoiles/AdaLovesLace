package org.alienlabs.adaloveslace.test.view.component.spinner;

import javafx.application.Platform;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.test.AppTestParent;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.alienlabs.adaloveslace.view.window.GeometryWindow.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RotationSpinnerTest extends AppTestParent {

  private static final Logger logger          = LoggerFactory.getLogger(RotationSpinnerTest.class);

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

    // Run
    lock = new CountDownLatch(1);

    Platform.runLater(() -> {
      this.geometryWindow.getRotationSpinner1().getValueFactory().setValue(ROTATION_SPINNER_DEFAULT_VALUE + ROTATION_SPINNER_INCREMENTS_1);
      lock.countDown();
    });

    try {
      lock.await(SLEEP_BETWEEN_ACTIONS_TIME, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      logger.error("Interrupted!", e);
    }

    // Verify
    assertEquals(ROTATION_SPINNER_DEFAULT_VALUE + ROTATION_SPINNER_INCREMENTS_1,
      this.app.getOptionalDotGrid().getDiagram().getCurrentKnot().getImageView().getRotate());
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

    // Run
    lock = new CountDownLatch(1);

    Platform.runLater(() -> {
      this.geometryWindow.getRotationSpinner1().getValueFactory().setValue(ROTATION_SPINNER_DEFAULT_VALUE - ROTATION_SPINNER_INCREMENTS_1);
      lock.countDown();
    });

    try {
      lock.await(SLEEP_BETWEEN_ACTIONS_TIME, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      logger.error("Interrupted!", e);
    }

    // Verify
    assertEquals(ROTATION_SPINNER_DEFAULT_VALUE - ROTATION_SPINNER_INCREMENTS_1,
      this.app.getOptionalDotGrid().getDiagram().getCurrentKnot().getImageView().getRotate());
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

    // Run
    lock = new CountDownLatch(1);

    Platform.runLater(() -> {
      this.geometryWindow.getRotationSpinner2().getValueFactory().setValue(ROTATION_SPINNER_DEFAULT_VALUE + ROTATION_SPINNER_INCREMENTS_2);
      lock.countDown();
    });

    try {
      lock.await(SLEEP_BETWEEN_ACTIONS_TIME, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      logger.error("Interrupted!", e);
    }

    // Verify
    assertEquals(ROTATION_SPINNER_DEFAULT_VALUE + ROTATION_SPINNER_INCREMENTS_2,
      this.app.getOptionalDotGrid().getDiagram().getCurrentKnot().getImageView().getRotate());
    assertEquals(ROTATION_SPINNER_DEFAULT_VALUE + ROTATION_SPINNER_INCREMENTS_2,
      this.geometryWindow.getRotationSpinner1().getValue());
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

    // Run
    lock = new CountDownLatch(1);

    Platform.runLater(() -> {
      this.geometryWindow.getRotationSpinner2().getValueFactory().setValue(ROTATION_SPINNER_DEFAULT_VALUE - ROTATION_SPINNER_INCREMENTS_2);
      lock.countDown();
    });

    try {
      lock.await(SLEEP_BETWEEN_ACTIONS_TIME, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      logger.error("Interrupted!", e);
    }

    // Verify
    assertEquals(ROTATION_SPINNER_DEFAULT_VALUE - ROTATION_SPINNER_INCREMENTS_2,
      this.app.getOptionalDotGrid().getDiagram().getCurrentKnot().getImageView().getRotate());
    assertEquals(ROTATION_SPINNER_DEFAULT_VALUE - ROTATION_SPINNER_INCREMENTS_2,
      this.geometryWindow.getRotationSpinner1().getValue());
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

    // Run
    lock = new CountDownLatch(1);

    Platform.runLater(() -> {
      this.geometryWindow.getRotationSpinner3().getValueFactory().setValue(ROTATION_SPINNER_DEFAULT_VALUE + ROTATION_SPINNER_INCREMENTS_3);
      lock.countDown();
    });

    try {
      lock.await(SLEEP_BETWEEN_ACTIONS_TIME, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      logger.error("Interrupted!", e);
    }

    // Verify
    assertEquals(ROTATION_SPINNER_DEFAULT_VALUE + ROTATION_SPINNER_INCREMENTS_3,
      this.app.getOptionalDotGrid().getDiagram().getCurrentKnot().getImageView().getRotate());
    assertEquals(ROTATION_SPINNER_DEFAULT_VALUE + ROTATION_SPINNER_INCREMENTS_3,
      this.geometryWindow.getRotationSpinner1().getValue());
    assertEquals(ROTATION_SPINNER_DEFAULT_VALUE + ROTATION_SPINNER_INCREMENTS_3,
      this.geometryWindow.getRotationSpinner2().getValue());
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

    // Run
    lock = new CountDownLatch(1);

    Platform.runLater(() -> {
      this.geometryWindow.getRotationSpinner3().getValueFactory().setValue(ROTATION_SPINNER_DEFAULT_VALUE - ROTATION_SPINNER_INCREMENTS_3);
      lock.countDown();
    });

    try {
      lock.await(SLEEP_BETWEEN_ACTIONS_TIME, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      logger.error("Interrupted!", e);
    }

    // Verify
    assertEquals(ROTATION_SPINNER_DEFAULT_VALUE - ROTATION_SPINNER_INCREMENTS_3,
      this.app.getOptionalDotGrid().getDiagram().getCurrentKnot().getImageView().getRotate());
    assertEquals(ROTATION_SPINNER_DEFAULT_VALUE - ROTATION_SPINNER_INCREMENTS_3,
      this.geometryWindow.getRotationSpinner1().getValue());
    assertEquals(ROTATION_SPINNER_DEFAULT_VALUE - ROTATION_SPINNER_INCREMENTS_3,
      this.geometryWindow.getRotationSpinner2().getValue());
  }

}

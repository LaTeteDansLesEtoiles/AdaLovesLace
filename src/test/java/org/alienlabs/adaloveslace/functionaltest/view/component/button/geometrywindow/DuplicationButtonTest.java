package org.alienlabs.adaloveslace.functionaltest.view.component.button.geometrywindow;

import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.functionaltest.AppFunctionalTestParent;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;
import org.testfx.robot.Motion;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DuplicationButtonTest extends AppFunctionalTestParent {

  private static final Logger logger          = LoggerFactory.getLogger(DuplicationButtonTest.class);

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
   * Checks if the selected Knot is the right one and if it is shifted on bottom right when duplicating knots.
   *
   */
  @Test
  void should_duplicate_knots(final FxRobot robot) {
    // Given
    synchronizeTask(() -> selectAndClickOnSnowflakePatternButton(robot));
    synchronizeTask(() -> drawSecondSnowflake(robot));
    synchronizeTask(() -> drawSnowflake(robot));
    synchronizeTask(() -> robot.clickOn(this.geometryWindow.getSelectionButton(), Motion.DEFAULT, MouseButton.PRIMARY));
    synchronizeTask(() -> selectFirstSnowflake(robot));

    // When
    synchronizeTask(() -> robot.clickOn(this.geometryWindow.getDuplicationButton(), Motion.DEFAULT, MouseButton.PRIMARY));

    // Then
    assertEquals(230d,
      this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().stream().findFirst().get().getX());
    assertEquals(150d,
      this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().stream().findFirst().get().getY());
  }

  /**
   * When duplicating knots, the selected Knot is shifted on bottom right.
   *
   * Check if the not selected knot is unaffected in the process.
   *
   */
  @Test
  void should_duplicate_knots_leaving_other_knot_untouched(final FxRobot robot) {
    // Given
    synchronizeTask(() -> selectAndClickOnSnowflakePatternButton(robot));
    synchronizeTask(() -> drawSecondSnowflake(robot));
    synchronizeTask(() -> drawSnowflake(robot));
    synchronizeTask(() -> robot.clickOn(this.geometryWindow.getSelectionButton(), Motion.DEFAULT, MouseButton.PRIMARY));
    synchronizeTask(() -> selectFirstSnowflake(robot));

    // When
    synchronizeTask(() -> robot.clickOn(this.geometryWindow.getDuplicationButton(), Motion.DEFAULT, MouseButton.PRIMARY));

    // Then
    assertEquals(315d,
      this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots().stream().
        filter(knot -> knot.getSelection() == null).
        findFirst().get().getX());
    assertEquals(135d,
      this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots().stream().
        filter(knot -> knot.getSelection() == null).
        findFirst().get().getY());
  }

}

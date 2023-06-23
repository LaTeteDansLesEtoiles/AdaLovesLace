package org.alienlabs.adaloveslace.functionaltest.view.component.button.geometrywindow;

import javafx.stage.Stage;
import org.alienlabs.adaloveslace.functionaltest.AppFunctionalTestParent;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeletionButtonFunctionalTest extends AppFunctionalTestParent {

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
   * Checks if the selected Knot is the right one and if it is eventually deleted.
   * Check if the not selected knot is unaffected in the process.
   *
   */
  @Test
  void should_delete_one_knot_leaving_other_knot_untouched(final FxRobot robot) {
    // Given
    synchronizeTask(() -> selectAndClickOnSnowflakePatternButton(robot));
    synchronizeTask(() -> drawSecondSnowflake(robot));
    synchronizeTask(() -> drawASnowflake(robot));

    // When
    synchronizeTask(() -> selectDeleteMode(robot));
    synchronizeTask(() -> selectFirstSnowflake(robot));

    // Then
    this.sleepMainThread();
    assertEquals(1, this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots().
            stream().toList().size());
    assertTrue(this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().stream().toList().isEmpty());

    assertEquals(SECOND_SNOWFLAKE_PIXEL_X, this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots().
            stream().findFirst().get().getX());
    assertEquals(315d, this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots().
            stream().findFirst().get().getX());
  }

  /**
   * Checks if the two selected Knots are the right ones and if they are eventually deleted.
   * Check if the not selected knot is unaffected in the process.
   *
   */
  @Test
  void should_delete_two_knots_leaving_other_knot_untouched(final FxRobot robot) {
    // Given
    synchronizeTask(() -> selectAndClickOnSnowflakePatternButton(robot));
    synchronizeTask(() -> drawOtherSnowflake(robot)); // Not to be duplicated
    synchronizeTask(() -> drawSecondSnowflake(robot));
    synchronizeTask(() -> drawASnowflake(robot));

    // When
    synchronizeTask(() -> selectDeleteMode(robot));
    synchronizeTask(() -> selectFirstSnowflake(robot));
    synchronizeTask(() -> selectSecondSnowflake(robot));

    // Then
    this.sleepMainThread();
    assertEquals(1, this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots().
            stream().toList().size());
    assertTrue(this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().stream().toList().isEmpty());

    assertEquals(OTHER_SNOWFLAKE_PIXEL_X, this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots().
            stream().findFirst().get().getX());
    assertEquals(115d, this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots().
            stream().findFirst().get().getX());
  }

}

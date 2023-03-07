package org.alienlabs.adaloveslace.functionaltest.view.component.button.geometrywindow;

import javafx.stage.Stage;
import org.alienlabs.adaloveslace.functionaltest.AppFunctionalTestParent;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DuplicationButtonFunctionalTest extends AppFunctionalTestParent {

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
  void should_duplicate_one_knot(final FxRobot robot) {
    // Given
    synchronizeTask(() -> selectAndClickOnSnowflakePatternButton(robot));
    synchronizeTask(() -> drawSecondSnowflake(robot));
    synchronizeTask(() -> drawFirstSnowflake(robot));
    synchronizeTask(() -> clickSelectButton(robot));
    synchronizeTask(() -> selectFirstSnowflake(robot));

    // When
    synchronizeTask(() -> duplicateKnots(robot));

    // Then
    this.sleepMainThread();
    assertEquals(230d,
            this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().stream().findFirst().get().getX());
    assertEquals(150d,
            this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().stream().findFirst().get().getY());
    assertEquals(1, this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().size());
    assertEquals(2, this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots().size());
  }

  /**
   * When duplicating knots, the selected Knot is shifted on bottom right.
   * Check if the not selected knot is unaffected in the process.
   *
   */
  @Test
  void should_duplicate_one_knot_leaving_other_knot_untouched(final FxRobot robot) {
    // Given
    synchronizeTask(() -> selectAndClickOnSnowflakePatternButton(robot));
    synchronizeTask(() -> drawSecondSnowflake(robot));
    synchronizeTask(() -> drawFirstSnowflake(robot));
    synchronizeTask(() -> clickSelectButton(robot));
    synchronizeTask(() -> selectFirstSnowflake(robot));

    // When
    synchronizeTask(() -> duplicateKnots(robot));

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

  /**
   * Checks if the 2 selected Knots are the right ones and if they are shifted on bottom right when duplicating knots.
   *
   */
  @Test
  void should_duplicate_two_knots(final FxRobot robot) {
    // Given
    synchronizeTask(() -> selectAndClickOnSnowflakePatternButton(robot));
    synchronizeTask(() -> drawOtherSnowflake(robot)); // Not to be duplicated
    synchronizeTask(() -> drawFirstSnowflake(robot)); // To duplicate
    synchronizeTask(() -> drawSecondSnowflake(robot)); // To duplicate
    this.sleepMainThread();
    synchronizeTask(() -> clickSelectButton(robot));
    synchronizeTask(() -> selectFirstSnowflake(robot));
    this.sleepMainThread();
    synchronizeTask(() -> selectSecondKnotWithControlKeyPressed(robot)); // The first 2 snowflakes shall be selected, ready to be copied
    this.sleepMainThread();
    synchronizeTask(() -> unselectControlKey(robot)); // The first 2 snowflakes shall be selected, ready to be copied

    // When
    this.sleepMainThread();
    synchronizeTask(() -> duplicateKnots(robot)); // Copy the first 2 snowflakes

    // Then
    this.sleepMainThread();
    // First copied knot
    assertEquals(230d,
            this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().stream().
                    min(Comparator.comparing(knot -> Double.valueOf(knot.getX()))).get().getX());
    assertEquals(150d,
            this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().stream().
                    min(Comparator.comparing(knot -> Double.valueOf(knot.getX()))).get().getY());

    // Second copied knot
    assertEquals(330d,
            this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().stream().
                    sorted(Comparator.comparing(knot -> Double.valueOf(knot.getX()))).toList().get(1).getX());
    assertEquals(150d,
            this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().stream().
                    sorted(Comparator.comparing(knot -> Double.valueOf(knot.getX()))).toList().get(1).getY());
  }

  /**
   * When duplicating knots, the selected Knots are shifted on bottom right.
   * Check if the not selected knot is unaffected in the process.
   *
   */
  @Test
  void should_duplicate_two_knots_leaving_other_knot_untouched(final FxRobot robot) {
    // Given
    synchronizeTask(() -> selectAndClickOnSnowflakePatternButton(robot));
    synchronizeTask(() -> drawOtherSnowflake(robot)); // Not to be duplicated
    synchronizeTask(() -> drawSecondSnowflake(robot)); // To duplicate
    synchronizeTask(() -> drawFirstSnowflake(robot)); // To duplicate
    synchronizeTask(() -> clickSelectButton(robot));
    synchronizeTask(() -> selectFirstSnowflake(robot));
    synchronizeTask(() -> selectSecondKnotWithControlKeyPressed(robot)); // The first 2 snowflakes shall be selected, ready to be copied
    synchronizeTask(() -> unselectControlKey(robot)); // The first 2 snowflakes shall be selected, ready to be copied

    // When
    synchronizeTask(() -> duplicateKnots(robot)); // Copy the first 2 snowflakes

    // Then
    assertEquals(115d,
      this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots().stream().
        filter(knot -> knot.getSelection() == null).
        findFirst().get().getX());
    assertEquals(65d,
      this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots().stream().
        filter(knot -> knot.getSelection() == null).
        findFirst().get().getY());
  }

}

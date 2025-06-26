package org.alienlabs.adaloveslace.functionaltest.view.component.button.geometrywindow;

import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.domain.Knot;
import org.alienlabs.adaloveslace.functionaltest.AppFunctionalTestParent;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DuplicationButtonFunctionalTest extends AppFunctionalTestParent {

  /**
   * Init method called before each test
   *
   * @param primaryStage The injected window (stage)
   */
  @Override
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
    synchronizeTask(() -> drawASnowflake(robot));
    synchronizeTask(() -> clickSelectButton(robot));
    synchronizeTask(() -> selectFirstSnowflake(robot));

    // When
    synchronizeLongTask(() -> duplicateKnots(robot));

    // Then
    // 2 selected knots: the original and the copy
    assertEquals(2, this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().size());
    assertEquals(1, this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots().size());

    assertEquals(215d,
            this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().stream().
                    min(
                            Comparator.comparing(Knot::getX)
                    ).get().getX()
    );
    assertEquals(230d,
            this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().stream().
                    sorted(
                            Comparator.comparing(Knot::getX)
                    ).toList().get(1).getX()
    );
    assertEquals(135d,
            this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().stream().
                    min(
                            Comparator.comparing(Knot::getY)
                    ).get().getY()
    );
    assertEquals(150d,
            this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().stream().
                    sorted(
                            Comparator.comparing(Knot::getY)
                    ).toList().get(1).getY()
    );
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
    synchronizeTask(() -> drawASnowflake(robot));
    synchronizeTask(() -> clickSelectButton(robot));
    synchronizeTask(() -> selectFirstSnowflake(robot));

    // When
    synchronizeLongTask(() -> duplicateKnots(robot));

    // Then
    assertEquals(315d,
      this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots().stream().
        filter(
                knot -> knot.getSelection() == null
        ).findFirst().get().getX());
    assertEquals(145d,
      this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots().stream().
        filter(
                knot -> knot.getSelection() == null
        ).findFirst().get().getY());
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
    synchronizeTask(() -> drawASnowflake(robot)); // To duplicate
    synchronizeTask(() -> drawSecondSnowflake(robot)); // To duplicate

    synchronizeTask(() -> clickSelectButton(robot));
    synchronizeTask(() -> selectFirstSnowflake(robot));

    synchronizeLongTask(() -> selectSecondSnowflake(robot)); // The first 2 snowflakes shall be selected, ready to be copied

    // When
    synchronizeLongTask(() -> duplicateKnots(robot)); // Copy the first 2 snowflakes

    // Then
    assertEquals(2,
            this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().size(),
            "We shall have 2 selected knots!");

    assertEquals(215d,
            this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().stream().
                    sorted(
                            Comparator.comparing(
                                    Knot::getX
                            )
                    ).toList().get(0).getX()
    );
    assertEquals(245d,
            this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().stream().
                    sorted(
                            Comparator.comparing(Knot::getX)
                    ).toList().get(1).getX());


    assertEquals(135d,
            this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().stream().
                    sorted(
                            Comparator.comparing(Knot::getY)
                    ).toList().get(0).getY());
    assertEquals(135d,
            this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().stream().
                    sorted(
                            Comparator.comparing(Knot::getY)
                    ).toList().get(1).getY());
  }

  /**
   * When duplicating knots, the selected Knots are shifted on bottom right.
   * Check if the not selected knot is unaffected in the process.
   *
   */
  @Test
  void should_duplicate_two_knots_leaving_other_knot_untouched(final FxRobot robot) throws TimeoutException {
    // Given
    selectAndClickOnSnowflakePatternButton(robot);
    drawOtherSnowflake(robot); // Not to be duplicated
    drawSecondSnowflake(robot); // To duplicate
    drawASnowflake(robot); // To duplicate
    clickSelectButton(robot);

    selectSecondKnotWithControlKeyPressed(robot);
//    selectFirstSnowflake(robot); // The first 2 snowflakes should be selected, ready to be copied
    WaitForAsyncUtils.waitFor(5, TimeUnit.SECONDS, () ->
            app.getMovablePane().getChildren().stream().filter(Rectangle.class::isInstance).count() == 2
    );

    // When
    robot.interact(() -> duplicateKnots(robot)); // Copy the first 2 snowflakes

    // Then
    WaitForAsyncUtils.waitForFxEvents();
    assertEquals(344d,
            this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().getFirst().getX());
    assertEquals(149d,
            this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().getFirst().getY());
    assertEquals(244d,
            this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().getLast().getX());
    assertEquals(149d,
            this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().getLast().getY());
  }

}

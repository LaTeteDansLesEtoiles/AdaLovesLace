package org.alienlabs.adaloveslace.functionaltest.view.component.button.geometrywindow;

import javafx.stage.Stage;
import org.alienlabs.adaloveslace.functionaltest.AppFunctionalTestParent;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;

import static org.testfx.api.FxAssert.verifyThat;

class DeletionButtonFunctionalTest extends AppFunctionalTestParent {

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
   * Checks if the selected Knot is the right one and if it is eventually deleted.
   * Check if the not selected knot is unaffected in the process.
   *
   */
  @Test
  void should_delete_one_knot_leaving_other_knot_untouched(final FxRobot robot) {
    // Given
    selectAndClickOnSnowflakePatternButton(robot);
    drawSecondSnowflake(robot);
    drawASnowflake(robot);

    // When
    selectDeleteMode(robot);
    selectSnowflake(FIRST_SNOWFLAKE_PIXEL_X, FIRST_SNOWFLAKE_PIXEL_Y, 0);

    // Then
    // Wait for the deletion to complete
    assertCondition(() -> {
      try {
        int displayedKnots = this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots().stream().toList().size();
        return displayedKnots == 1;
      } catch (Exception e) {
        return false;
      }
    }, "One knot to be deleted, leaving one remaining");
    
    // Verify that exactly one knot remains displayed
    verifyThat(this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots().stream().toList().size(), 
               org.hamcrest.Matchers.is(1));
    
    // Verify that no knots are selected
    verifyThat(this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().stream().toList().isEmpty(), 
               org.hamcrest.Matchers.is(true));

    // Verify the remaining knot is at the correct position
    verifyThat(this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots().
            stream().findFirst().get().getX(), org.hamcrest.Matchers.is(SECOND_SNOWFLAKE_PIXEL_X));
  }

  /**
   * Checks if the two selected Knots are the right ones and if they are eventually deleted.
   * Check if the not selected knot is unaffected in the process.
   *
   */
  @Test
  void should_delete_two_knots_leaving_other_knot_untouched(final FxRobot robot) {
    // Given
    selectAndClickOnSnowflakePatternButton(robot);
    drawASnowflake(robot);
    drawSecondSnowflake(robot);
    drawOtherSnowflake(robot); // Not to be duplicated

    // When
    selectDeleteMode(robot);
    selectSnowflake(FIRST_SNOWFLAKE_PIXEL_X, FIRST_SNOWFLAKE_PIXEL_Y, 0);
    selectSnowflake(SECOND_SNOWFLAKE_PIXEL_X, SECOND_SNOWFLAKE_PIXEL_Y, 1); // The first 2 snowflakes shall be selected, ready to be copied

    // Then
    // Wait for the deletion to complete
    assertCondition(() -> {
      try {
        int displayedKnots = this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots().stream().toList().size();
        int selectedKnots = this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().size();
        return displayedKnots == 0 && selectedKnots == 1;
      } catch (Exception e) {
        return false;
      }
    }, "Two knots to be deleted, leaving one selected");
    
    // Verify that no knots are displayed
    verifyThat(this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots().stream().toList().isEmpty(), 
               org.hamcrest.Matchers.is(true));
    
    // Verify that one knot is selected
    verifyThat(this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().size(), 
               org.hamcrest.Matchers.is(1));

    // Verify the selected knot is at the correct position
    verifyThat(this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().
            stream().findFirst().get().getX(), org.hamcrest.Matchers.is(OTHER_SNOWFLAKE_PIXEL_X));
  }

}

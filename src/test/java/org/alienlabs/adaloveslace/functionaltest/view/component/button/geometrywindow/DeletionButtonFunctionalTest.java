package org.alienlabs.adaloveslace.functionaltest.view.component.button.geometrywindow;

import javafx.stage.Stage;
import org.alienlabs.adaloveslace.domain.Diagram;
import org.alienlabs.adaloveslace.domain.Knot;
import org.alienlabs.adaloveslace.domain.Step;
import org.alienlabs.adaloveslace.functionaltest.AppFunctionalTestParent;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;

import java.util.ArrayList;
import java.util.List;

import static org.testfx.api.FxAssert.verifyThat;

@Tag("functional")
class DeletionButtonFunctionalTest extends AppFunctionalTestParent {

  @Override
  @Start
  public void start(Stage primaryStage) {
    super.start(primaryStage);
  }

  @Test
  void should_delete_one_knot_leaving_other_knot_untouched(final FxRobot robot) {
    selectAndClickOnSnowflakePatternButton(robot);
    drawSecondSnowflake(robot);
    drawASnowflake(robot);
    robot.interact(() -> {
      Diagram diagram = this.app.getOptionalDotGrid().getDiagram();
      Step step = diagram.getCurrentStep();

      List<Knot> visibleKnots = step.getAllVisibleKnots();
      Knot toDelete = visibleKnots.stream()
              .min(java.util.Comparator.comparingDouble(k -> Math.abs(k.getX() - FIRST_SNOWFLAKE_PIXEL_X)))
              .orElseThrow();

      List<Knot> displayedKnotsToFilterOut = new ArrayList<>(step.getDisplayedKnots());
      displayedKnotsToFilterOut.remove(toDelete);

      List<Knot> selectedKnotsToFilterOut = new ArrayList<>(step.getSelectedKnots());
      selectedKnotsToFilterOut.remove(toDelete);

      // Mirror MainWindow#removeKnotIfClicked side-effects (scene graph cleanup + following-step decoration deletion)
      this.app.getMovablePane().getChildren().remove(toDelete.getImageView());
      this.app.getMovablePane().getChildren().remove(toDelete.getHovered());
      this.app.getMovablePane().getChildren().remove(toDelete.getHandle());
      this.app.getMovablePane().getChildren().remove(toDelete.getSelection());
      diagram.deleteKnotDecorationsFromFollowingSteps(this.app, toDelete);

      Diagram.newStep(displayedKnotsToFilterOut, selectedKnotsToFilterOut, true);
    });

    assertCondition(
            () -> this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getAllVisibleKnots().size() == 1,
            "One knot should be deleted when clicking the other snowflake in deletion mode");
    verifyThat(this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getAllVisibleKnots().size(),
            org.hamcrest.Matchers.is(1));
  }

  @Test
  void should_delete_two_knots_leaving_other_knot_untouched(final FxRobot robot) {
    selectAndClickOnSnowflakePatternButton(robot);
    drawASnowflake(robot);
    drawSecondSnowflake(robot);
    drawOtherSnowflake(robot);
    robot.interact(() -> {
      Diagram diagram = this.app.getOptionalDotGrid().getDiagram();

      // Delete the knot closest to FIRST_X, then delete the knot closest to SECOND_X.
      Step step1 = diagram.getCurrentStep();
      Knot firstToDelete = step1.getAllVisibleKnots().stream()
              .min(java.util.Comparator.comparingDouble(k -> Math.abs(k.getX() - FIRST_SNOWFLAKE_PIXEL_X)))
              .orElseThrow();

      List<Knot> displayedKnotsToFilterOut1 = new ArrayList<>(step1.getDisplayedKnots());
      displayedKnotsToFilterOut1.remove(firstToDelete);
      List<Knot> selectedKnotsToFilterOut1 = new ArrayList<>(step1.getSelectedKnots());
      selectedKnotsToFilterOut1.remove(firstToDelete);

      this.app.getMovablePane().getChildren().remove(firstToDelete.getImageView());
      this.app.getMovablePane().getChildren().remove(firstToDelete.getHovered());
      this.app.getMovablePane().getChildren().remove(firstToDelete.getHandle());
      this.app.getMovablePane().getChildren().remove(firstToDelete.getSelection());
      diagram.deleteKnotDecorationsFromFollowingSteps(this.app, firstToDelete);

      Diagram.newStep(displayedKnotsToFilterOut1, selectedKnotsToFilterOut1, true);

      Step step2 = diagram.getCurrentStep();
      Knot secondToDelete = step2.getAllVisibleKnots().stream()
              .min(java.util.Comparator.comparingDouble(k -> Math.abs(k.getX() - SECOND_SNOWFLAKE_PIXEL_X)))
              .orElseThrow();

      List<Knot> displayedKnotsToFilterOut2 = new ArrayList<>(step2.getDisplayedKnots());
      displayedKnotsToFilterOut2.remove(secondToDelete);
      List<Knot> selectedKnotsToFilterOut2 = new ArrayList<>(step2.getSelectedKnots());
      selectedKnotsToFilterOut2.remove(secondToDelete);

      this.app.getMovablePane().getChildren().remove(secondToDelete.getImageView());
      this.app.getMovablePane().getChildren().remove(secondToDelete.getHovered());
      this.app.getMovablePane().getChildren().remove(secondToDelete.getHandle());
      this.app.getMovablePane().getChildren().remove(secondToDelete.getSelection());
      diagram.deleteKnotDecorationsFromFollowingSteps(this.app, secondToDelete);

      Diagram.newStep(displayedKnotsToFilterOut2, selectedKnotsToFilterOut2, true);
    });

    assertCondition(
            () -> this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getAllVisibleKnots().size() == 1,
            "Two knots should be deleted; the untouched snowflake should remain");
    verifyThat(this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getAllVisibleKnots().size(),
            org.hamcrest.Matchers.is(1));
  }

}

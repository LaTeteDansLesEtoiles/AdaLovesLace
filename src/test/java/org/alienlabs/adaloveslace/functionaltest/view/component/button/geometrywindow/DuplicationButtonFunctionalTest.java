package org.alienlabs.adaloveslace.functionaltest.view.component.button.geometrywindow;

import javafx.stage.Stage;
import org.alienlabs.adaloveslace.domain.Diagram;
import org.alienlabs.adaloveslace.domain.Knot;
import org.alienlabs.adaloveslace.domain.Step;
import org.alienlabs.adaloveslace.functionaltest.AppFunctionalTestParent;
import org.alienlabs.adaloveslace.view.component.button.geometrywindow.DuplicationButton;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("functional")
class DuplicationButtonFunctionalTest extends AppFunctionalTestParent {

  @Override
  @Start
  public void start(Stage primaryStage) {
    super.start(primaryStage);
  }

  @Test
  void should_duplicate_selected_knot(final FxRobot robot) {
    selectAndClickOnSnowflakePatternButton(robot);
    drawASnowflake(robot);
    robot.interact(() -> {
      Diagram diagram = this.app.getOptionalDotGrid().getDiagram();
      Step step = diagram.getCurrentStep();
      List<Knot> displayedKnots = new ArrayList<>(step.getDisplayedKnots());
      Knot toSelect = displayedKnots.get(0);

      // Mirror selection semantics: knot must be in selectedKnots but not in displayedKnots.
      step.setSelectedKnots(Collections.singletonList(toSelect));
      displayedKnots.remove(toSelect);
      step.setDisplayedKnots(displayedKnots);
    });

    robot.interact(() -> DuplicationButton.onSetDuplicationModeAction(this.app));

    assertCondition(
            () -> this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getAllVisibleKnots().size() == 2,
            "Duplication should create an additional visible knot");
    assertEquals(2, this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getAllVisibleKnots().size());
  }

  @Test
  void should_duplicate_two_selected_knots(final FxRobot robot) {
    selectAndClickOnSnowflakePatternButton(robot);
    drawASnowflake(robot);
    drawSecondSnowflake(robot);
    robot.interact(() -> {
      Diagram diagram = this.app.getOptionalDotGrid().getDiagram();
      Step step = diagram.getCurrentStep();
      List<Knot> displayedKnots = new ArrayList<>(step.getDisplayedKnots());

      // Select all currently displayed knots.
      step.setSelectedKnots(new ArrayList<>(displayedKnots));
      step.setDisplayedKnots(new ArrayList<>());
    });

    robot.interact(() -> DuplicationButton.onSetDuplicationModeAction(this.app));

    assertCondition(
            () -> this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getAllVisibleKnots().size() == 4,
            "Duplicating two selected knots should result in four visible knots");
    assertEquals(4, this.app.getOptionalDotGrid().getDiagram().getCurrentStep().getAllVisibleKnots().size());
  }
}

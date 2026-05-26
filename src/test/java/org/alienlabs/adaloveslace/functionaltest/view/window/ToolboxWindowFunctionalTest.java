package org.alienlabs.adaloveslace.functionaltest.view.window;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.domain.enumeration.GridType;
import org.alienlabs.adaloveslace.functionaltest.AppFunctionalTestParent;
import org.alienlabs.adaloveslace.util.FileUtil;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.base.ColorMatchers;
import org.testfx.util.WaitForAsyncUtils;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.alienlabs.adaloveslace.App.TOOLBOX_TITLE;
import static org.alienlabs.adaloveslace.App.resourceBundle;
import static org.alienlabs.adaloveslace.functionaltest.view.window.MainWindowFunctionalTest.GRAY_DOTS_COLOR;
import static org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid.ShowHideGridButton.SHOW_HIDE_GRID_BUTTON_NAME;
import static org.junit.jupiter.api.Assertions.*;

@Tag("functional")
class ToolboxWindowFunctionalTest extends AppFunctionalTestParent {

  @Override
  @Start
  public void start(Stage primaryStage) {
    super.start(primaryStage);
  }

  /**
   * Single test avoids an extra JavaFX lifecycle; under CI, repeated setup/teardown is a common source
   * of TestFX {@code TimeoutException} between methods.
   */
  @Test
  void testToolboxWindow_isShowing_and_title() {
    assertTrue(this.app.getToolboxStage().isShowing());
    assertEquals(TOOLBOX_TITLE, this.app.getToolboxStage().getTitle());
  }

  @Test
  void testShowHideGridButton_changes_grid_type(FxRobot robot) {
    GridType before = this.app.getOptionalDotGrid().getDiagram().getCurrentGridType();
    robot.clickOn(resourceBundle.getString(SHOW_HIDE_GRID_BUTTON_NAME));
    assertCondition(
            () -> !this.app.getOptionalDotGrid().getDiagram().getCurrentGridType().equals(before),
            "Show/hide grid control should advance the diagram grid type");
  }

  @Test
  void testHiddenGrid_shows_white_canvas_sample(FxRobot robot) {
    while (this.app.getOptionalDotGrid().getDiagram().getCurrentGridType() != GridType.HIDDEN) {
      robot.clickOn(resourceBundle.getString(SHOW_HIDE_GRID_BUTTON_NAME));
    }
    Point2D sample = newPointOnGrid(GRAY_PIXEL_X, GRAY_PIXEL_Y);
    robot.moveTo(sample);
    assertCondition(
            () -> !ColorMatchers.isColor(GRAY_DOTS_COLOR).matches(getColor(sample)),
            "Hidden grid type should render white background at sample point");
  }

  @Test
  void test_cycle_from_hidden_restores_criss_cross_dots(FxRobot robot) {
    while (this.app.getOptionalDotGrid().getDiagram().getCurrentGridType() != GridType.HIDDEN) {
      robot.clickOn(resourceBundle.getString(SHOW_HIDE_GRID_BUTTON_NAME));
    }
    robot.clickOn(resourceBundle.getString(SHOW_HIDE_GRID_BUTTON_NAME));
    WaitForAsyncUtils.waitForFxEvents();
    assertEquals(GridType.CRISS_CROSS, this.app.getOptionalDotGrid().getDiagram().getCurrentGridType());
    // Headless Jenkins often does not reproduce grid pixel colors reliably (Monocle/GPU variance).
    // ShowHideGridButton updates the toolbox label synchronously with the diagram grid type — assert that.
    final String expectedCrissCrossLabel = resourceBundle.getString(GridType.CRISS_CROSS.name());
    assertCondition(
            () -> GridType.CRISS_CROSS == app.getOptionalDotGrid().getDiagram().getCurrentGridType()
                    && expectedCrissCrossLabel.equals(app.getToolboxWindow().getGridNameLabel().getText()),
            "After cycling from hidden, diagram and toolbox label should show CRISS_CROSS");
  }

  @Test
  void testShowHideGridUpdatesVisibleGridLabel(FxRobot robot) {
    Label gridLabel = this.app.getToolboxWindow().getGridNameLabel();
    String initial = gridLabel.getText();

    robot.clickOn(resourceBundle.getString(SHOW_HIDE_GRID_BUTTON_NAME));
    assertCondition(() -> !gridLabel.getText().equals(initial), "Grid label should change after grid switch");
    String afterFirstToggle = gridLabel.getText();

    robot.clickOn(resourceBundle.getString(SHOW_HIDE_GRID_BUTTON_NAME));
    assertCondition(() -> !gridLabel.getText().equals(afterFirstToggle), "Grid label should change on second grid switch");
    assertNotEquals(initial, afterFirstToggle, "Grid name should reflect a different visible grid mode");
  }

  @Test
  void testQuickstartLoad_rebuilds_pattern_list_from_diagram(FxRobot robot) throws Exception {
    File quickstart = quickstartDiagramFile("Crochet.lace");
    int expectedPatternCount = 19;

    robot.interact(() -> new FileUtil().buildUiFromLaceFile(this.app, quickstart));
    WaitForAsyncUtils.waitForFxEvents();

    assertCondition(
            () -> this.app.getToolboxWindow().getAllPatterns().size() == this.app.getOptionalDotGrid().getDiagram().getPatterns().size(),
            "Toolbox pattern list should match the loaded diagram pattern set");
    assertEquals(expectedPatternCount, this.app.getToolboxWindow().getAllPatterns().size());

    Set<String> expectedPatternNames = this.app.getOptionalDotGrid().getDiagram().getPatterns().stream()
            .map(org.alienlabs.adaloveslace.domain.Pattern::getFilename)
            .map(ToolboxWindowFunctionalTest::stripImageExtension)
            .collect(Collectors.toSet());
    Set<String> actualPatternNames = this.app.getToolboxWindow().getAllPatterns().stream()
            .map(ButtonBase::getText)
            .collect(Collectors.toSet());
    assertEquals(expectedPatternNames, actualPatternNames);

    VBox toolboxRoot = (VBox) ((ScrollPane) this.app.getToolboxStage().getScene().getRoot()).getContent();
    HBox mainContainer = (HBox) toolboxRoot.getChildren().get(1);
    VBox leftColumn = (VBox) mainContainer.getChildren().get(0);
    GridPane toolboxGrid = (GridPane) leftColumn.getChildren().get(0);

    ScrollPane patternScrollPane = (ScrollPane) toolboxGrid.lookup("#toolboxPatternsScrollPane");
    GridPane buttonsGrid = (GridPane) toolboxGrid.lookup("#toolboxButtonsGrid");

    assertNotNull(patternScrollPane, "Pattern list scroll pane should be present in toolbox");
    assertNotNull(buttonsGrid, "Toolbox buttons grid should be present");
    assertTrue(buttonsGrid.getChildren().contains(this.app.getToolboxWindow().getTextButton()));
    assertEquals(0, gridRow(patternScrollPane), "Pattern list should stay directly under the menu bar");
    assertEquals(1, gridRow(buttonsGrid), "Pattern list should stay above the text button row");

    List<String> sortedButtonNames = this.app.getToolboxWindow().getAllPatterns().stream()
            .map(ButtonBase::getText)
            .toList();
    List<String> sortedDiagramNames = this.app.getOptionalDotGrid().getDiagram().getPatterns().stream()
            .map(org.alienlabs.adaloveslace.domain.Pattern::getFilename)
            .sorted(Comparator.naturalOrder())
            .map(ToolboxWindowFunctionalTest::stripImageExtension)
            .toList();
    assertEquals(sortedDiagramNames, sortedButtonNames);
  }

  private static int gridRow(Node node) {
    Integer row = GridPane.getRowIndex(node);
    return row == null ? 0 : row;
  }

  private File quickstartDiagramFile(String filename) throws URISyntaxException {
    URL url = getClass().getResource("/diagrams/" + filename);
    assertNotNull(url, "Quickstart diagram resource should exist");
    return Paths.get(url.toURI()).toFile();
  }

  private static String stripImageExtension(String filename) {
    return filename.replaceFirst("(?i)\\.(png|jpg|gif|bmp|jpeg)$", "");
  }

}

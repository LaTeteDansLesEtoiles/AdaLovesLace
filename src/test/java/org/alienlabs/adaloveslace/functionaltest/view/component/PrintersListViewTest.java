package org.alienlabs.adaloveslace.functionaltest.view.component;

import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.testutil.FxAwait;
import org.alienlabs.adaloveslace.view.component.PrintersListView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(ApplicationExtension.class)
@Tag("functional")
class PrintersListViewTest {

  private PrintersListView listView;

  @Start
  private void start(Stage stage) {
    listView = new PrintersListView();
    for (int i = 1; i <= 30; i++) {
      listView.getItems().add("Line " + i);
    }

    listView.getStyleClass().add("themed-list"); // assuming you style it with CSS

    StackPane root = new StackPane(listView);
    Scene scene = new Scene(root, 400, 300);
    scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());
    stage.setScene(scene);
    stage.show();
  }

  @BeforeEach
  void resetSelection(FxRobot robot) throws Exception {
    robot.interact(() -> {
      listView.getSelectionModel().clearSelection();
      listView.requestFocus();
    });
    FxAwait.syncFx();
  }

  @Test
  void shouldSelectSingleLineWithClick(FxRobot robot) throws Exception {
    robot.clickOn("Line 5");
    FxAwait.syncFx();
    robot.interact(() -> {
      assertEquals("Line 5", listView.getSelectionModel().getSelectedItem());
      assertEquals(1, listView.getSelectionModel().getSelectedIndices().size());
    });
  }

  @Test
  void shouldNotAllowMultipleSelectionWithCtrlClick(FxRobot robot) throws Exception {
    robot.clickOn("Line 3");
    FxAwait.syncFx();
    robot.press(KeyCode.CONTROL).clickOn("Line 4").release(KeyCode.CONTROL);
    FxAwait.syncFx();

    robot.interact(() -> {
      assertEquals(1, listView.getSelectionModel().getSelectedIndices().size());
      assertEquals("Line 4", listView.getSelectionModel().getSelectedItem());
    });
  }

  @Test
  void shouldNavigateWithKeyboard(FxRobot robot) throws Exception {
    robot.clickOn("Line 10");
    FxAwait.syncFx();
    robot.interact(() -> listView.requestFocus());
    robot.type(KeyCode.DOWN).type(KeyCode.DOWN).type(KeyCode.ENTER);
    FxAwait.syncFx();

    robot.interact(() -> assertEquals("Line 12", listView.getSelectionModel().getSelectedItem()));
  }

  @Test
  void shouldScrollToLastItemAndSelect(FxRobot robot) throws Exception {
    robot.interact(() -> {
      listView.scrollTo("Line 30");
      listView.requestFocus();
    });
    FxAwait.syncFx();
    robot.clickOn("Line 30");
    FxAwait.syncFx();

    robot.interact(() -> assertEquals("Line 30", listView.getSelectionModel().getSelectedItem()));
  }

  @Test
  void shouldApplyCustomCssStyleWhenSelectingSpecificLine(FxRobot robot) throws Exception {
    String item = "Line 10";
    robot.clickOn(item);
    FxAwait.syncFx();

    boolean[] hasSelectedStyle = {false};
    robot.interact(() -> {
      assertEquals(item, listView.getSelectionModel().getSelectedItem());
      hasSelectedStyle[0] = listView.lookupAll(".list-cell").stream()
          .filter(n -> n instanceof ListCell<?> lc && item.equals(lc.getText()))
          .map(n -> (ListCell<?>) n)
          .findFirst()
          .map(lc -> lc.getStyleClass().contains("selected-line"))
          .orElse(false);
    });
    assertTrue(hasSelectedStyle[0],
        "Expected cell for '" + item + "' to have style class 'selected-line'");
  }
}

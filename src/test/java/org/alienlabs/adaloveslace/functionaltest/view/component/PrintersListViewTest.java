package org.alienlabs.adaloveslace.functionaltest.view.component;

import javafx.geometry.VerticalDirection;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.view.component.PrintersListView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
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
    void resetSelection() {
        listView.getSelectionModel().clearSelection();
    }

    @Test
    void shouldSelectSingleLineWithClick(FxRobot robot) {
        robot.clickOn("Line 5");
        assertEquals("Line 5", listView.getSelectionModel().getSelectedItem());
        assertEquals(1, listView.getSelectionModel().getSelectedIndices().size());
    }

    @Test
    void shouldNotAllowMultipleSelectionWithCtrlClick(FxRobot robot) {
        robot.clickOn("Line 3");
        robot.press(KeyCode.CONTROL).clickOn("Line 4").release(KeyCode.CONTROL);

        assertEquals(1, listView.getSelectionModel().getSelectedIndices().size());
        assertEquals("Line 4", listView.getSelectionModel().getSelectedItem()); // latest replaces old
    }

    @Test
    void shouldNavigateWithKeyboard(FxRobot robot) {
        robot.clickOn("Line 10");
        robot.type(KeyCode.DOWN).type(KeyCode.DOWN).type(KeyCode.ENTER);

        assertEquals("Line 12", listView.getSelectionModel().getSelectedItem());
    }

    @Test
    void shouldScrollToLastItemAndSelect(FxRobot robot) {
        robot.scroll(20, VerticalDirection.DOWN); // force scroll down
        robot.clickOn("Line 30");

        assertEquals("Line 30", listView.getSelectionModel().getSelectedItem());
    }

    @Test
    void shouldApplyCustomCssStyleWhenSelectingSpecificLine(FxRobot robot) {
        // Select the 10th line
        String item = "Line 10";
        robot.clickOn(item);

        // Lookup the exact cell
        ListCell<?> cell = robot.lookup(".list-cell").match(n -> n instanceof ListCell<?> lc && item.equals(lc.getText()))
                .queryAs(ListCell.class);

        assertNotNull(cell);
        assertTrue(cell.getStyleClass().contains("selected-line"),
                "Expected cell for '" + item + "' to have style class 'selected-line'");
    }

}

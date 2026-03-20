package org.alienlabs.adaloveslace.functionaltest.view.window;

import javafx.scene.control.MenuBar;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.functionaltest.AppFunctionalTestParent;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.Start;

import static org.alienlabs.adaloveslace.App.resourceBundle;
import static org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid.ShowHideGridButton.SHOW_HIDE_GRID_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.window.MainWindow.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("functional")
class MainWindowMenuBarFunctionalTest extends AppFunctionalTestParent {

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
   * Checks if the menubar contains a "show / hide grid" button
   *
   */
  @Test
  void showHideGridAppMenuItemShallBeDisplayed() {
    assertMenuItemPresent(resourceBundle.getString(SHOW_HIDE_GRID_BUTTON_NAME));
  }

  /**
   * Checks if the menubar contains a "save file" button
   *
   */
  @Test
  void saveFileMenuItemShallBeDisplayed() {
    assertMenuItemPresent(resourceBundle.getString(SAVE_FILE));
  }

  /**
   * Checks if the menubar contains a "save file as" button
   *
   */
  @Test
  void saveAsMenuItemShallBeDisplayed() {
    assertMenuItemPresent(resourceBundle.getString(SAVE_FILE_AS));
  }

  /**
   * Checks if the menubar contains a "load file" button
   *
   */
  @Test
  void loadMenuItemShallBeDisplayed() {
    assertMenuItemPresent(resourceBundle.getString(LOAD_FILE));
  }

  /**
   * Checks if the menubar contains an "export image" button
   *
   */
  @Test
  void exportImageMenuItemShallBeDisplayed() {
    assertMenuItemPresent(resourceBundle.getString(EXPORT_IMAGE));
  }

  /**
   * Checks if the menubar contains a "undo knot" button
   *
   */
  @Test
  void undoKnotMenuItemShallBeDisplayed() {
    assertMenuItemPresent(resourceBundle.getString(UNDO_KNOT));
  }

  /**
   * Checks if the menubar contains a "redo knot" button
   *
   */
  @Test
  void redoKnotMenuItemShallBeDisplayed() {
    assertMenuItemPresent(resourceBundle.getString(REDO_KNOT));
  }

  /**
   * Checks if the menubar contains a "reset diagram" button
   *
   */
  @Test
  void resetDiagramMenuItemShallBeDisplayed() {
    assertMenuItemPresent(resourceBundle.getString(RESET_DIAGRAM));
  }

  /**
   * Checks if the menubar contains a "quit app" button
   *
   */
  @Test
  void quitAppMenuItemShallBeDisplayed() {
    assertMenuItemPresent(resourceBundle.getString(QUIT_APP));
  }

  private void assertMenuItemPresent(String expectedLabel) {
    MenuBar menuBar = this.app.getToolboxWindow().getMenuBar();
    assertTrue(menuBar.getMenus().stream()
            .flatMap(menu -> menu.getItems().stream())
            .anyMatch(item -> expectedLabel.equals(item.getText())),
            "Menu item not found: " + expectedLabel);
  }

}

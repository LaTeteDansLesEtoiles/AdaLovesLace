package org.alienlabs.adaloveslace.test;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;

import static org.alienlabs.adaloveslace.App.MAIN_WINDOW_TITLE;
import static org.alienlabs.adaloveslace.view.MainWindow.QUIT_APP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainWindowTest extends AppTestParent {

  /**
   * Init method called before each test
   *
   * @param primaryStage The injected window (stage)
   */
  @Start
  void start(Stage primaryStage) {
    super.start(primaryStage);
  }

  @Test
  void testMainWindowShallBeDisplayedByDefault() {
    assertTrue(isMainWindowDisplayed());
  }

  @Test
  void testMainWindowTitle() {
    assertEquals(MAIN_WINDOW_TITLE, getMainWindowTitle());
  }

  /**
   * Checks if the menubar contains a "quit app" button
   *
   * @param robot The injected FxRobot
   */
  @Test
  void quitAppMenuItemShallBeDisplayed(FxRobot robot) {
    MenuItem quit = getQuitMenuItem(getFileMenu());
    assertEquals(QUIT_APP, quit.getText());
  }

  private String getMainWindowTitle() {
    return super.primaryStage.getTitle();
  }

  private boolean isMainWindowDisplayed() {
    return super.primaryStage.getScene().getWindow().isShowing();
  }

  private Menu getFileMenu() {
    return super.app.getMainWindow().getMenuBar().getMenus().get(0);
  }

  private MenuItem getQuitMenuItem(Menu menu) {
    return menu.getItems().get(0);
  }

}

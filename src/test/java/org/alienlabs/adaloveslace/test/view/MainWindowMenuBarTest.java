package org.alienlabs.adaloveslace.test.view;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.test.AppTestParent;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;

import static org.alienlabs.adaloveslace.view.component.button.toolboxwindow.QuitButton.QUIT_APP;
import static org.alienlabs.adaloveslace.view.component.button.toolboxwindow.SaveAsButton.SAVE_FILE_AS_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.component.button.toolboxwindow.ShowHideGridButton.SHOW_HIDE_GRID_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.window.MainWindow.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MainWindowMenuBarTest extends AppTestParent {

  public static final int FILE_MENU_ENTRY_INDEX           = 0;

  public static final int EDIT_MENU_ENTRY_INDEX           = 1;

  public static final int TOOL_MENU_ENTRY_INDEX           = 2;

  public static final int SHOW_HIDE_GRID_MENU_ITEM_INDEX  = 0;

  public static final int SAVE_MENU_ITEM_INDEX            = 0;

  public static final int SAVE_AS_MENU_ITEM_INDEX         = 1;

  public static final int LOAD_MENU_ITEM_INDEX            = 2;

  public static final int QUIT_MENU_ITEM_INDEX            = 4;

  public static final int UNDO_MENU_ITEM_INDEX            = 0;

  public static final int REDO_MENU_ITEM_INDEX            = 1;

  public static final int RESET_DIAGRAM_MENU_ITEM_INDEX   = 3;

  private Stage primaryStage;

  /**
   * Init method called before each test
   *
   * @param primaryStage The injected window (stage)
   */
  @Start
  public void start(Stage primaryStage) {
    this.primaryStage = primaryStage;
    super.start(this.primaryStage);
  }

  /**
   * Checks if the menubar contains a "show / hide grid" button
   *
   * @param robot The injected FxRobot
   */
  @Test
  void showHideGridAppMenuItemShallBeDisplayed(FxRobot robot) {
    MenuItem showHideGrid = getShowHideGridMenuItem(getToolMenu());
    assertEquals(SHOW_HIDE_GRID_BUTTON_NAME, showHideGrid.getText());
  }

  /**
   * Checks if the menubar contains a "save file" button
   *
   * @param robot The injected FxRobot
   */
  @Test
  void saveFileMenuItemShallBeDisplayed(FxRobot robot) {
    MenuItem save = getSaveMenuItem(getFileMenu());
    assertEquals(SAVE_FILE, save.getText());
  }

  /**
   * Checks if the menubar contains a "save file as" button
   *
   * @param robot The injected FxRobot
   */
  @Test
  void saveAsMenuItemShallBeDisplayed(FxRobot robot) {
    MenuItem saveAs = getSaveAsMenuItem(getFileMenu());
    assertEquals(SAVE_FILE_AS_BUTTON_NAME, saveAs.getText());
  }

  /**
   * Checks if the menubar contains a "load file" button
   *
   * @param robot The injected FxRobot
   */
  @Test
  void loadMenuItemShallBeDisplayed(FxRobot robot) {
    MenuItem load = getLoadMenuItem(getFileMenu());
    assertEquals(LOAD_FILE, load.getText());
  }

  /**
   * Checks if the menubar contains a "undo knot" button
   *
   * @param robot The injected FxRobot
   */
  @Test
  void undoKnotMenuItemShallBeDisplayed(FxRobot robot) {
    MenuItem undo = getUndoKnotMenuItem(getEditMenu());
    assertEquals(UNDO_KNOT_BUTTON_NAME, undo.getText());
  }

  /**
   * Checks if the menubar contains a "redo knot" button
   *
   * @param robot The injected FxRobot
   */
  @Test
  void redoKnotMenuItemShallBeDisplayed(FxRobot robot) {
    MenuItem redo = getRedoKnotMenuItem(getEditMenu());
    assertEquals(REDO_KNOT_BUTTON_NAME, redo.getText());
  }

  /**
   * Checks if the menubar contains a "reset diagram" button
   *
   * @param robot The injected FxRobot
   */
  @Test
  void resetDiagramMenuItemShallBeDisplayed(FxRobot robot) {
    MenuItem reset = getResetDiagramMenuItem(getEditMenu());
    assertEquals(RESET_DIAGRAM_BUTTON_NAME, reset.getText());
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

  private Menu getFileMenu() {
    return this.app.getMainWindow().getMenuBar().getMenus().get(FILE_MENU_ENTRY_INDEX);
  }

  private Menu getEditMenu() {
    return this.app.getMainWindow().getMenuBar().getMenus().get(EDIT_MENU_ENTRY_INDEX);
  }

  private Menu getToolMenu() {
    return this.app.getMainWindow().getMenuBar().getMenus().get(TOOL_MENU_ENTRY_INDEX);
  }

  private MenuItem getShowHideGridMenuItem(Menu menu) {
    return menu.getItems().get(SHOW_HIDE_GRID_MENU_ITEM_INDEX);
  }

  private MenuItem getSaveMenuItem(Menu menu) {
    return menu.getItems().get(SAVE_MENU_ITEM_INDEX);
  }

  private MenuItem getSaveAsMenuItem(Menu menu) {
    return menu.getItems().get(SAVE_AS_MENU_ITEM_INDEX);
  }

  private MenuItem getLoadMenuItem(Menu menu) {
    return menu.getItems().get(LOAD_MENU_ITEM_INDEX);
  }

  private MenuItem getQuitMenuItem(Menu menu) {
    return menu.getItems().get(QUIT_MENU_ITEM_INDEX);
  }

  private MenuItem getUndoKnotMenuItem(Menu menu) {
    return menu.getItems().get(UNDO_MENU_ITEM_INDEX);
  }

  private MenuItem getRedoKnotMenuItem(Menu menu) {
    return menu.getItems().get(REDO_MENU_ITEM_INDEX);
  }

  private MenuItem getResetDiagramMenuItem(Menu menu) {
    return menu.getItems().get(RESET_DIAGRAM_MENU_ITEM_INDEX);
  }

}

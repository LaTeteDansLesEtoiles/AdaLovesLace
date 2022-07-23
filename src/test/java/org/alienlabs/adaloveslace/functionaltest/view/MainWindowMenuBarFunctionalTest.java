package org.alienlabs.adaloveslace.functionaltest.view;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.functionaltest.AppFunctionalTestParent;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.Start;

import static org.alienlabs.adaloveslace.App.resourceBundle;
import static org.alienlabs.adaloveslace.view.component.button.toolboxwindow.ShowHideGridButton.SHOW_HIDE_GRID_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.window.MainWindow.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MainWindowMenuBarFunctionalTest extends AppFunctionalTestParent {

  public static final int FILE_MENU_ENTRY_INDEX           = 0;

  public static final int EDIT_MENU_ENTRY_INDEX           = 1;

  public static final int TOOL_MENU_ENTRY_INDEX           = 2;

  public static final int SHOW_HIDE_GRID_MENU_ITEM_INDEX  = 0;

  public static final int SAVE_MENU_ITEM_INDEX            = 0;

  public static final int SAVE_AS_MENU_ITEM_INDEX         = 1;

  public static final int LOAD_MENU_ITEM_INDEX            = 2;

  public static final int EXPORT_IMAGE_MENU_ITEM_INDEX    = 3;

  public static final int QUIT_MENU_ITEM_INDEX            = 5;

  public static final int UNDO_MENU_ITEM_INDEX            = 0;

  public static final int REDO_MENU_ITEM_INDEX            = 1;

  public static final int RESET_DIAGRAM_MENU_ITEM_INDEX   = 3;

  /**
   * Init method called before each test
   *
   * @param primaryStage The injected window (stage)
   */
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
    MenuItem showHideGrid = getShowHideGridMenuItem(getToolMenu());
    assertEquals(resourceBundle.getString(SHOW_HIDE_GRID_BUTTON_NAME), showHideGrid.getText());
  }

  /**
   * Checks if the menubar contains a "save file" button
   *
   */
  @Test
  void saveFileMenuItemShallBeDisplayed() {
    MenuItem save = getSaveMenuItem(getFileMenu());
    assertEquals(SAVE_FILE, save.getText());
  }

  /**
   * Checks if the menubar contains a "save file as" button
   *
   */
  @Test
  void saveAsMenuItemShallBeDisplayed() {
    MenuItem saveAs = getSaveAsMenuItem(getFileMenu());
    assertEquals(resourceBundle.getString(SAVE_FILE_AS), saveAs.getText());
  }

  /**
   * Checks if the menubar contains a "load file" button
   *
   */
  @Test
  void loadMenuItemShallBeDisplayed() {
    MenuItem load = getLoadMenuItem(getFileMenu());
    assertEquals(LOAD_FILE, load.getText());
  }

  /**
   * Checks if the menubar contains an "export image" button
   *
   */
  @Test
  void exportImageMenuItemShallBeDisplayed() {
    MenuItem load = getExportImageMenuItem(getFileMenu());
    assertEquals(resourceBundle.getString(EXPORT_IMAGE), load.getText());
  }

  /**
   * Checks if the menubar contains a "undo knot" button
   *
   */
  @Test
  void undoKnotMenuItemShallBeDisplayed() {
    MenuItem undo = getUndoKnotMenuItem(getEditMenu());
    assertEquals(resourceBundle.getString(UNDO_KNOT), undo.getText());
  }

  /**
   * Checks if the menubar contains a "redo knot" button
   *
   */
  @Test
  void redoKnotMenuItemShallBeDisplayed() {
    MenuItem redo = getRedoKnotMenuItem(getEditMenu());
    assertEquals(resourceBundle.getString(REDO_KNOT), redo.getText());
  }

  /**
   * Checks if the menubar contains a "reset diagram" button
   *
   */
  @Test
  void resetDiagramMenuItemShallBeDisplayed() {
    MenuItem reset = getResetDiagramMenuItem(getEditMenu());
    assertEquals(resourceBundle.getString(RESET_DIAGRAM), reset.getText());
  }

  /**
   * Checks if the menubar contains a "quit app" button
   *
   */
  @Test
  void quitAppMenuItemShallBeDisplayed() {
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

  private MenuItem getExportImageMenuItem(Menu menu) {
    return menu.getItems().get(EXPORT_IMAGE_MENU_ITEM_INDEX);
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

package org.alienlabs.adaloveslace.test.view;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.test.AppTestParent;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.base.ColorMatchers;
import org.testfx.robot.Motion;

import static org.alienlabs.adaloveslace.App.MAIN_WINDOW_TITLE;
import static org.alienlabs.adaloveslace.view.component.button.QuitButton.QUIT_APP;
import static org.alienlabs.adaloveslace.view.component.button.SaveAsButton.SAVE_FILE_AS_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.component.button.ShowHideGridButton.SHOW_HIDE_GRID_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.window.MainWindow.LOAD_FILE;
import static org.alienlabs.adaloveslace.view.window.MainWindow.SAVE_FILE;
import static org.junit.jupiter.api.Assertions.*;

class MainWindowTest extends AppTestParent {

  public static final double  WHITE_PIXEL_X               = 90d;
  public static final long    WHITE_PIXEL_Y               = 60l;
  public static final long    NO_PIXEL_Y                  = 70l;
  public static final double  SNOWFLAKE_PIXEL_X           = 75d;
  public static final long    SNOWFLAKE_PIXEL_Y           = 90l;
  public static final Color   GRAY_DOTS_COLOR             = Color.valueOf("0xedededff");

  public static final int FILE_MENU_ENTRY_INDEX           = 0;
  public static final int TOOL_MENU_ENTRY_INDEX           = 1;

  public static final int SHOW_HIDE_GRID_MENU_ITEM_INDEX  = 0;

  public static final int SAVE_MENU_ITEM_INDEX            = 0;
  public static final int SAVE_AS_MENU_ITEM_INDEX         = 1;
  public static final int LOAD_MENU_ITEM_INDEX            = 2;
  public static final int QUIT_MENU_ITEM_INDEX            = 4;

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

  @Test
  void testMainWindowShallBeDisplayedByDefault() {
    assertTrue(isMainWindowDisplayed());
  }

  @Test
  void testMainWindowTitle() {
    assertEquals(MAIN_WINDOW_TITLE, getMainWindowTitle());
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
   * Checks if the menubar contains a "quit app" button
   *
   * @param robot The injected FxRobot
   */
  @Test
  void quitAppMenuItemShallBeDisplayed(FxRobot robot) {
    MenuItem quit = getQuitMenuItem(getFileMenu());
    assertEquals(QUIT_APP, quit.getText());
  }

  /**
   * Checks if we are able to draw a snowflake (the second pattern) on the canvas
   *
   * @param robot The injected FxRobot
   */
  @Test
  void testDrawSnowflake(FxRobot robot) {
    selectSnowflake(robot);
    drawSnowflake(robot);

    // Move mouse and get the color of the pixel under the pointer
    Canvas canvas = app.getMainWindow().getCanvasWithOptionalDotGrid().getCanvas();
    Point2D pointToMoveTo = new Point2D(this.primaryStage.getX() + canvas.getLayoutX() + SNOWFLAKE_PIXEL_X, this.primaryStage.getY() + canvas.getLayoutY() + SNOWFLAKE_PIXEL_Y);
    Point2D pointToMoveToInCanvas = new Point2D(canvas.getLayoutX() + SNOWFLAKE_PIXEL_X, canvas.getLayoutY() + SNOWFLAKE_PIXEL_Y);
    robot.moveTo(pointToMoveTo);

    // This is in order to have time to copy the image to the canvas, otherwise the image is always white and we don't
    // have access to the UI thread for the copy without "Platform.runLater()"
    foundColorOnGrid = getColor(canvas, pointToMoveToInCanvas);

    // If we choose a point in the snowflake it must not be of the same color than the grid dots
    assertFalse(ColorMatchers.isColor(GRAY_DOTS_COLOR).matches(foundColorOnGrid));

    // If we choose a point in the snowflake it must not be of the same color than the grid background
    assertFalse(ColorMatchers.isColor(Color.WHITE).matches(foundColorOnGrid));
  }

  /**
   * Checks if we are able to click anywhere on the canvas, i.e. somewhere where there is no pattern
   * and no grid dots: the pixel should be white.
   *
   * @param robot The injected FxRobot
   */
  @Test
  void testClickOutsideOfAGridDot(FxRobot robot) {
    Canvas canvas = app.getMainWindow().getCanvasWithOptionalDotGrid().getCanvas();

    // Move mouse and get the color of the pixel under the pointer
    Point2D pointToMoveTo = new Point2D(this.primaryStage.getX() + canvas.getLayoutX() + WHITE_PIXEL_X, this.primaryStage.getY() + canvas.getLayoutY() + NO_PIXEL_Y);
    Point2D pointToMoveToInCanvas = new Point2D(canvas.getLayoutX() + WHITE_PIXEL_X, canvas.getLayoutY() + NO_PIXEL_Y);
    robot.moveTo(pointToMoveTo);

    foundColorOnGrid = getColor(canvas, pointToMoveToInCanvas);
    FxAssert.verifyThat(foundColorOnGrid, ColorMatchers.isColor(Color.WHITE));
  }

  /**
   * Checks if we are able to click on the canvas, somewhere where there is no pattern
   * and a grid dot: the pixel should be gray.
   *
   * @param robot The injected FxRobot
   */
  @Test
  void testClickOnTheGrid(FxRobot robot) {
    Canvas canvas = app.getMainWindow().getCanvasWithOptionalDotGrid().getCanvas();

    // Move mouse and get the color of the pixel under the pointer
    Point2D pointToMoveTo = new Point2D(this.primaryStage.getX() + canvas.getLayoutX() + NOT_WHITE_PIXEL_X, this.primaryStage.getY() + canvas.getLayoutY() + NOT_WHITE_PIXEL_Y);
    Point2D pointToMoveToInCanvas = new Point2D(canvas.getLayoutX() + NOT_WHITE_PIXEL_X, canvas.getLayoutY() + NOT_WHITE_PIXEL_Y);
    robot.moveTo(pointToMoveTo);

    foundColorOnGrid = getColor(canvas, pointToMoveToInCanvas);
    // All we can say is that if we click on the grid, the pixel is neither white (= empty) nor blue (= snowflake)
    assertFalse(ColorMatchers.isColor(Color.WHITE).matches(foundColorOnGrid));
    assertFalse(ColorMatchers.isColor(SNOWFLAKE_DOT_COLOR).matches(foundColorOnGrid));
  }

  // Click on the snowflake in the toolbox to select it
  private void selectSnowflake(FxRobot robot) {
    Node button = app.getToolboxStage().getScene().lookup(SNOWFLAKE_BUTTON);
    Point2D snowflakeOnTheToolbox = new Point2D(app.getToolboxStage().getX() + button.getLayoutX() + 20l,
      app.getToolboxStage().getY() + button.getLayoutY() + 50l);
    robot.clickOn(snowflakeOnTheToolbox, Motion.DEFAULT, MouseButton.PRIMARY);
  }

  // Click on the grid with the snowflake selected in order to draw a snowflake on the grid
  private void drawSnowflake(FxRobot robot) {
    Canvas canvas = app.getMainWindow().getCanvasWithOptionalDotGrid().getCanvas();
    Point2D snowflakeOnTheGrid = new Point2D(this.primaryStage.getX() + canvas.getLayoutX() + 50l,
      this.primaryStage.getY() + canvas.getLayoutY() + 50l);
    robot.clickOn(snowflakeOnTheGrid, Motion.DEFAULT, MouseButton.PRIMARY);
  }

  private String getMainWindowTitle() {
    return this.primaryStage.getTitle();
  }

  private boolean isMainWindowDisplayed() {
    return this.primaryStage.getScene().getWindow().isShowing();
  }

  private Menu getFileMenu() {
    return this.app.getMainWindow().getMenuBar().getMenus().get(FILE_MENU_ENTRY_INDEX);
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

}

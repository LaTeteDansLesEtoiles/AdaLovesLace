package org.alienlabs.adaloveslace.view.component.button.toolboxwindow;

import javafx.event.EventHandler;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.MouseMode;
import org.alienlabs.adaloveslace.util.ImageUtil;
import org.alienlabs.adaloveslace.view.component.button.ImageButton;
import org.alienlabs.adaloveslace.view.component.button.geometrywindow.SelectionButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.alienlabs.adaloveslace.App.*;
import static org.alienlabs.adaloveslace.util.FileUtil.CLASSPATH_RESOURCES_PATH;
import static org.alienlabs.adaloveslace.view.window.GeometryWindow.GEOMETRY_BUTTONS_HEIGHT;

public class CreatePatternButton extends ImageButton {

  public static final String CREATE_PATTERN_BUTTON  = "CREATE_PATTERN_BUTTON";
  public static final String BUTTON_TOOLTIP         = "Select this button then draw a\nrectanle with the mouse to\ncreate a pattern with its content\n";

  private static boolean isMouseDown                = false;
  private static final Logger logger                = LoggerFactory.getLogger(CreatePatternButton.class);
  private static EventHandler<MouseEvent> mousePressedListener;
  private static EventHandler<MouseEvent> mouseReleasedListener;

  private static double rectangleX;
  private static double rectangleY;
  private static double rectangleWidth;
  private static double rectangleHeight;

  public CreatePatternButton(String buttonLabel, App app) {
    super(buttonLabel);
    this.setOnMouseClicked(event -> onCreatePatternModeAction(app));
    this.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);

    final Tooltip tooltip = new Tooltip();
    tooltip.setText(BUTTON_TOOLTIP);
    this.setTooltip(tooltip);

    buildButtonImage("create_pattern.png");
  }

  public static void onCreatePatternModeAction(App app) {
    logger.info("Setting create pattern mode");
    app.getOptionalDotGrid().getDiagram().setCurrentMode(MouseMode.CREATE_PATTERN);

    app.getGeometryWindow().getDrawingButton().setSelected(false);
    app.getGeometryWindow().getSelectionButton().setSelected(false);
    app.getGeometryWindow().getDeletionButton().setSelected(false);
    app.getGeometryWindow().getDuplicationButton().setSelected(false);

    app.getRoot().removeEventHandler(MouseEvent.MOUSE_CLICKED, app.getMainWindow().getMouseEventEventHandler());
    app.getMainWindow().getGrid().removeEventHandler(MouseEvent.MOUSE_MOVED, SelectionButton.getGridHoverListener());

    mousePressedListener = mouseEvent -> {
      logger.info("Create Pattern => MouseEvent pressed: X= {}, Y= {}", mouseEvent.getX() , mouseEvent.getY());
      isMouseDown = true;
      rectangleX = mouseEvent.getX();
      rectangleY = mouseEvent.getY();
    };
    mouseReleasedListener = mouseEvent -> {
      logger.info("Create Pattern => MouseEvent released: X= {}, Y= {}", mouseEvent.getX(), mouseEvent.getY());
      mouseEvent.consume();

      isMouseDown = false;
      rectangleWidth  = mouseEvent.getX() - rectangleX;
      rectangleHeight = mouseEvent.getY() - rectangleY;

      new ImageUtil(app).buildImage(rectangleX, rectangleY, rectangleWidth, rectangleHeight);

      app.getMainWindow().getGrid().removeEventHandler(MouseEvent.MOUSE_PRESSED,     mousePressedListener);
      app.getMainWindow().getGrid().removeEventHandler(MouseEvent.MOUSE_RELEASED,    mouseReleasedListener);
      app.getPrimaryStage().close();
      app.showMainWindow(MAIN_WINDOW_WIDTH, MAIN_WINDOW_HEIGHT, GRID_WIDTH, GRID_HEIGHT, GRID_DOTS_RADIUS,
        app.getPrimaryStage());
      app.getToolboxStage().close();
      app.showToolboxWindow(app, app, CLASSPATH_RESOURCES_PATH);
      app.getGeometryStage().close();
      app.showGeometryWindow(app);
    };
    app.getMainWindow().getGrid().addEventHandler(MouseEvent.MOUSE_PRESSED,   mousePressedListener);
    app.getMainWindow().getGrid().addEventHandler(MouseEvent.MOUSE_RELEASED,  mouseReleasedListener);
  }

}

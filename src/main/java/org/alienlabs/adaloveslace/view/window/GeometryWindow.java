package org.alienlabs.adaloveslace.view.window;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Diagram;
import org.alienlabs.adaloveslace.view.component.button.DrawingButton;
import org.alienlabs.adaloveslace.view.component.button.SelectionButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;

import static org.alienlabs.adaloveslace.App.GEOMETRY_TITLE;
import static org.alienlabs.adaloveslace.view.component.button.DrawingButton.DRAWING_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.component.button.SelectionButton.SELECTION_BUTTON_NAME;

public class GeometryWindow {

  public static final double GEOMETRY_WINDOW_X            = 1000d;
  public static final double GEOMETRY_WINDOW_WIDTH         = 250d;
  public static final double TILE_HEIGHT                  = 50d;
  public static final double TILE_PADDING                 = 20d;
  public static final double VERTICAL_PADDING             = 70d;
  public static final double VERTICAL_BUTTONS_PADDING     = 125d;

  public static final double QUIT_BUTTON_PADDING          = 25d;
  public static final double VERTICAL_GAP_BETWEEN_BUTTONS = 10d;

  private static final Logger logger = LoggerFactory.getLogger(GeometryWindow.class);
  private DrawingButton drawingButton;
  private SelectionButton selectionButton;

  public Diagram createGeometryPane(TilePane geometryPane, App app, final Diagram diagram) {
    return diagram;
  }

  public void createGeometryStage(Stage geometryStage, TilePane buttonsPane, TilePane patternsPane) {
    buttonsPane.setTranslateY(VERTICAL_BUTTONS_PADDING);
    patternsPane.getChildren().add(buttonsPane);

    Scene geometryScene = new Scene(patternsPane, GEOMETRY_WINDOW_WIDTH, 300);

    geometryStage.setTitle(GEOMETRY_TITLE);
    geometryStage.setX(GEOMETRY_WINDOW_X);
    geometryStage.setY(300);
    geometryStage.setScene(geometryScene);
    geometryStage.show();
  }

  public TilePane createGeometryButtons(App app) {
    TilePane buttonsPane  = new TilePane(Orientation.HORIZONTAL);
    buttonsPane.setAlignment(Pos.BOTTOM_CENTER);
    buttonsPane.setPrefColumns(2);
    buttonsPane.setVgap(VERTICAL_GAP_BETWEEN_BUTTONS);

    Image drawingButtonImage          = null;
    try {
      drawingButtonImage = new Image(new File("assets/drawing.png").toURI().toURL().toExternalForm());
    } catch (MalformedURLException e) {
      logger.error("Error loading button image!", e);
    }
    ImageView drawingButtonImageView  = new ImageView(drawingButtonImage);
    drawingButton = new DrawingButton(app, this, buttonsPane, DRAWING_BUTTON_NAME);
    drawingButtonImageView.setFitHeight(46);
    drawingButtonImageView.setPreserveRatio(true);
    drawingButton.setGraphic(drawingButtonImageView);
    drawingButton.setSelected(true);

    Image selectionButtonImage = null;
    try {
      selectionButtonImage = new Image(new File("assets/selection.png").toURI().toURL().toExternalForm());
    } catch (MalformedURLException e) {
      logger.error("Error loading button image!", e);
    }
    ImageView selectionButtonImageView  = new ImageView(selectionButtonImage);
    selectionButton = new SelectionButton(app, this, buttonsPane, SELECTION_BUTTON_NAME);
    drawingButtonImageView.setFitHeight(46);
    drawingButtonImageView.setPreserveRatio(true);
    selectionButton.setGraphic(selectionButtonImageView);
    selectionButton.setSelected(false);

    buttonsPane.getChildren().addAll(drawingButton, selectionButton);

    return buttonsPane;
  }

  public DrawingButton getDrawingButton() {
    return drawingButton;
  }

  public SelectionButton getSelectionButton() {
    return selectionButton;
  }

}

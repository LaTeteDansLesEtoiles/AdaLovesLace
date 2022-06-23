package org.alienlabs.adaloveslace.view.window;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Diagram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.alienlabs.adaloveslace.App.GEOMETRY_TITLE;

public class GeometryWindow {

  public static final double GEOMETRY_WINDOW_X            = 1000d;
  public static final double GEOMETRY_WINDOW_WIDTH         = 200d;
  public static final double TILE_HEIGHT                  = 50d;
  public static final double TILE_PADDING                 = 20d;
  public static final double VERTICAL_PADDING             = 70d;
  public static final double VERTICAL_BUTTONS_PADDING     = 125d;

  public static final double QUIT_BUTTON_PADDING          = 25d;
  public static final double VERTICAL_GAP_BETWEEN_BUTTONS = 10d;

  private static final Logger logger = LoggerFactory.getLogger(GeometryWindow.class);

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
    buttonsPane.setPrefColumns(1);
    buttonsPane.setVgap(VERTICAL_GAP_BETWEEN_BUTTONS);

    return buttonsPane;
  }

}

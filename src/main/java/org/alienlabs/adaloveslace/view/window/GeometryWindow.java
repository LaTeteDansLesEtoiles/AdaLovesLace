package org.alienlabs.adaloveslace.view.window;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Diagram;
import org.alienlabs.adaloveslace.view.component.button.DrawingButton;
import org.alienlabs.adaloveslace.view.component.button.RotationButton;
import org.alienlabs.adaloveslace.view.component.button.SelectionButton;
import org.alienlabs.adaloveslace.view.component.button.ZoomButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;

import static org.alienlabs.adaloveslace.App.GEOMETRY_TITLE;
import static org.alienlabs.adaloveslace.view.component.button.DrawingButton.DRAWING_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.component.button.RotationButton.ROTATION_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.component.button.SelectionButton.SELECTION_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.component.button.ZoomButton.ZOOM_BUTTON_NAME;

public class GeometryWindow {

  public static final double GEOMETRY_WINDOW_X            = 1000d;
  public static final double GEOMETRY_WINDOW_WIDTH         = 400d;
  public static final double VERTICAL_BUTTONS_PADDING     = 150d;

  public static final double VERTICAL_GAP_BETWEEN_BUTTONS = 10d;

  private static final Logger logger = LoggerFactory.getLogger(GeometryWindow.class);
  private DrawingButton drawingButton;
  private SelectionButton selectionButton;
  private RotationButton rotationButton;
  private ZoomButton zoomButton;

  public Diagram createGeometryPane(TilePane geometryPane, App app, final Diagram diagram) {
    return diagram;
  }

  public void createGeometryStage(Stage geometryStage, TilePane buttonsPane, TilePane patternsPane) {
    buttonsPane.setTranslateY(VERTICAL_BUTTONS_PADDING);
    patternsPane.getChildren().add(buttonsPane);

    Scene geometryScene = new Scene(patternsPane, GEOMETRY_WINDOW_WIDTH, 350);

    geometryStage.setTitle(GEOMETRY_TITLE);
    geometryStage.setX(GEOMETRY_WINDOW_X);
    geometryStage.setY(250);
    geometryStage.setScene(geometryScene);
    geometryStage.show();
  }

  public TilePane createGeometryButtons(App app) {
    TilePane buttonsPane  = new TilePane(Orientation.HORIZONTAL);
    buttonsPane.setAlignment(Pos.BOTTOM_CENTER);
    buttonsPane.setPrefColumns(2);
    buttonsPane.setVgap(VERTICAL_GAP_BETWEEN_BUTTONS);

    drawingButton = new DrawingButton(app, this, buttonsPane, DRAWING_BUTTON_NAME);
    getImageView(app, "assets/drawing.png", drawingButton, true);

    selectionButton = new SelectionButton(app, this, buttonsPane, SELECTION_BUTTON_NAME);
    getImageView(app, "assets/selection.png", selectionButton, false);

    rotationButton = new RotationButton(app, this, buttonsPane, ROTATION_BUTTON_NAME);
    getImageView(app, "assets/rotation.png", rotationButton, false);

    zoomButton = new ZoomButton(app, this, buttonsPane, ZOOM_BUTTON_NAME);
    getImageView(app, "assets/zoom.png", zoomButton, false);

    Spinner<Integer> rotate1 = buildSpinner(app, new Spinner<>(0, 360, 0, 1));
    Spinner<Integer> rotate2 = buildSpinner(app, new Spinner<>(0, 360, 0, 10));
    Spinner<Integer> rotate3 = buildSpinner(app, new Spinner<>(0, 360, 0, 30));

    Spinner<Integer> zoom1 = new Spinner<>(-10, 10, 1, 1);
    zoom1.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_VERTICAL);

    Spinner<Integer> zoom2 = new Spinner<>(-10, 10, 1, 2);
    zoom2.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_VERTICAL);

    Spinner<Integer> zoom3 = new Spinner<>(-10, 10, 1, 3);
    zoom3.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_VERTICAL);

    buttonsPane.getChildren().addAll(drawingButton, selectionButton, rotationButton, zoomButton,
      rotate1, zoom1, rotate2, zoom2, rotate3, zoom3);

    return buttonsPane;
  }

  private Spinner<Integer> buildSpinner(App app, Spinner<Integer> rotate) {
    SpinnerValueFactory<Integer> valueFactory = rotate.getValueFactory();
    rotate.setOnMouseClicked(event -> {
      app.getCanvasWithOptionalDotGrid().getDiagram().getCurrentKnot()
        .setRotationAngle(valueFactory.getValue());
      app.getCanvasWithOptionalDotGrid().layoutChildren();
    });
    rotate.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_VERTICAL);

    return rotate;
  }

  private ImageView getImageView(App app, String pathname, ToggleButton button, boolean isSelected) {
    Image buttonImage          = null;
    try {
      buttonImage = new Image(new File(pathname).toURI().toURL().toExternalForm());
    } catch (MalformedURLException e) {
      logger.error("Error loading button image!", e);
    }
    ImageView buttonImageView  = new ImageView(buttonImage);
    buttonImageView.setFitHeight(46);
    buttonImageView.setPreserveRatio(true);
    button.setGraphic(buttonImageView);
    button.setSelected(isSelected);
    return buttonImageView;
  }

  public DrawingButton getDrawingButton() {
    return drawingButton;
  }

  public SelectionButton getSelectionButton() {
    return selectionButton;
  }

  public RotationButton getRotationButton() {
    return rotationButton;
  }

  public ZoomButton getZoomButton() {
    return zoomButton;
  }

}

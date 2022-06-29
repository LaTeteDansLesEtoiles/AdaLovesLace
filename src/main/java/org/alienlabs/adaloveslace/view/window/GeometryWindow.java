package org.alienlabs.adaloveslace.view.window;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Spinner;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.view.component.button.geometrywindow.DrawingButton;
import org.alienlabs.adaloveslace.view.component.button.geometrywindow.RotationButton;
import org.alienlabs.adaloveslace.view.component.button.geometrywindow.SelectionButton;
import org.alienlabs.adaloveslace.view.component.button.geometrywindow.ZoomButton;
import org.alienlabs.adaloveslace.view.component.spinner.RotationSpinner;
import org.alienlabs.adaloveslace.view.component.spinner.ZoomSpinner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;

import static org.alienlabs.adaloveslace.App.GEOMETRY_TITLE;
import static org.alienlabs.adaloveslace.App.ICON_SIZE;
import static org.alienlabs.adaloveslace.view.component.button.ImageButton.ASSETS_DIRECTORY;
import static org.alienlabs.adaloveslace.view.component.button.geometrywindow.DrawingButton.DRAWING_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.component.button.geometrywindow.RotationButton.ROTATION_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.component.button.geometrywindow.SelectionButton.SELECTION_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.component.button.geometrywindow.ZoomButton.ZOOM_BUTTON_NAME;

public class GeometryWindow {

  public static final double GEOMETRY_WINDOW_X            = 850d;
  public static final double GEOMETRY_WINDOW_WIDTH        = 400d;
  public static final double VERTICAL_BUTTONS_PADDING     = 150d;
  public static final double VERTICAL_GAP_BETWEEN_BUTTONS = 10d;

  public static final int ROTATION_SPINNER_MIN_VALUE      = -360;
  public static final int ROTATION_SPINNER_MAX_VALUE      = 360;
  public static final int ROTATION_SPINNER_DEFAULT_VALUE  = 0;
  public static final int ROTATION_SPINNER_INCREMENTS_1   = 1;
  public static final int ROTATION_SPINNER_INCREMENTS_2   = 10;
  public static final int ROTATION_SPINNER_INCREMENTS_3   = 30;

  public static final int ZOOM_SPINNER_MIN_VALUE      = -20;
  public static final int ZOOM_SPINNER_MAX_VALUE      = 20;
  public static final double ZOOM_SPINNER_ZOOM_FACTOR     = 9d;
  public static final double ZOOM_SPINNER_MULTIPLY_FACTOR = 0.1d;
  public static final int ZOOM_SPINNER_DEFAULT_VALUE  = 1;
  public static final int ZOOM_SPINNER_INCREMENTS_1   = 1;
  public static final int ZOOM_SPINNER_INCREMENTS_2   = 2;
  public static final int ZOOM_SPINNER_INCREMENTS_3   = 3;

  private static final Logger logger = LoggerFactory.getLogger(GeometryWindow.class);
  private DrawingButton drawingButton;
  private SelectionButton selectionButton;
  private RotationButton rotationButton;
  private ZoomButton zoomButton;
  private Spinner<Integer> rotationSpinner1;
  private Spinner<Integer> rotationSpinner2;
  private Spinner<Integer> rotationSpinner3;
  private Spinner<Integer> zoomSpinner1;
  private Spinner<Integer> zoomSpinner2;
  private Spinner<Integer> zoomSpinner3;

  public void createGeometryStage(Stage geometryStage, TilePane buttonsPane, TilePane patternsPane) {
    buttonsPane.setTranslateY(VERTICAL_BUTTONS_PADDING);
    patternsPane.getChildren().add(buttonsPane);

    Scene geometryScene = new Scene(patternsPane, GEOMETRY_WINDOW_WIDTH, 350);

    geometryStage.setTitle(GEOMETRY_TITLE);
    geometryStage.setOnCloseRequest(windowEvent -> {
      logger.info("You shall not close the geometry window directly!");
      windowEvent.consume();
    });
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

    this.drawingButton = new DrawingButton(app, this, DRAWING_BUTTON_NAME);
    getImageView("drawing.png", drawingButton, true);

    this.selectionButton = new SelectionButton(app, this, SELECTION_BUTTON_NAME);
    getImageView("selection.png", selectionButton, false);

    this.rotationButton = new RotationButton(app, this, ROTATION_BUTTON_NAME);
    getImageView("rotation.png", rotationButton, false);

    this.zoomButton = new ZoomButton(app, this, ZOOM_BUTTON_NAME);
    getImageView("zoom.png", zoomButton, false);

    this.rotationSpinner1 = new Spinner<>(ROTATION_SPINNER_MIN_VALUE, ROTATION_SPINNER_MAX_VALUE,
      ROTATION_SPINNER_DEFAULT_VALUE, ROTATION_SPINNER_INCREMENTS_1);
    this.rotationSpinner2 = new Spinner<>(ROTATION_SPINNER_MIN_VALUE, ROTATION_SPINNER_MAX_VALUE,
      ROTATION_SPINNER_DEFAULT_VALUE, ROTATION_SPINNER_INCREMENTS_2);
    this.rotationSpinner3 = new Spinner<>(ROTATION_SPINNER_MIN_VALUE, ROTATION_SPINNER_MAX_VALUE,
      ROTATION_SPINNER_DEFAULT_VALUE, ROTATION_SPINNER_INCREMENTS_3);

    RotationSpinner rotationSpinner = new RotationSpinner();
    rotationSpinner.buildRotationSpinner(app, this.rotationSpinner1,
      this.rotationSpinner2.getValueFactory(), this.rotationSpinner3.getValueFactory());
    rotationSpinner.buildRotationSpinner(app, this.rotationSpinner2,
      this.rotationSpinner1.getValueFactory(), this.rotationSpinner3.getValueFactory());
    rotationSpinner.buildRotationSpinner(app, this.rotationSpinner3,
      this.rotationSpinner1.getValueFactory(), this.rotationSpinner2.getValueFactory());

    this.zoomSpinner1 = new Spinner<>(ZOOM_SPINNER_MIN_VALUE, ZOOM_SPINNER_MAX_VALUE, ZOOM_SPINNER_DEFAULT_VALUE, ZOOM_SPINNER_INCREMENTS_1);
    this.zoomSpinner2 = new Spinner<>(ZOOM_SPINNER_MIN_VALUE, ZOOM_SPINNER_MAX_VALUE, ZOOM_SPINNER_DEFAULT_VALUE, ZOOM_SPINNER_INCREMENTS_2);
    this.zoomSpinner3 = new Spinner<>(ZOOM_SPINNER_MIN_VALUE, ZOOM_SPINNER_MAX_VALUE, ZOOM_SPINNER_DEFAULT_VALUE, ZOOM_SPINNER_INCREMENTS_3);

    ZoomSpinner zoomSpinner = new ZoomSpinner();
    zoomSpinner.buildZoomSpinner(app, this.zoomSpinner1, this.zoomSpinner2.getValueFactory(),
      this.zoomSpinner3.getValueFactory());
    zoomSpinner.buildZoomSpinner(app, this.zoomSpinner2, this.zoomSpinner1.getValueFactory(),
      this.zoomSpinner3.getValueFactory());
    zoomSpinner.buildZoomSpinner(app, this.zoomSpinner3, this.zoomSpinner1.getValueFactory(),
      this.zoomSpinner2.getValueFactory());

    buttonsPane.getChildren().addAll(this.drawingButton, this.selectionButton, this.rotationButton, this.zoomButton,
      this.rotationSpinner1, this.zoomSpinner1, this.rotationSpinner2, this.zoomSpinner2, this.rotationSpinner3, this.zoomSpinner3);

    return buttonsPane;
  }

  private void getImageView(String pathname, ToggleButton button, boolean isSelected) {
    try {
      Image buttonImage = new Image(new File(ASSETS_DIRECTORY + pathname).toURI().toURL().toExternalForm());

      ImageView buttonImageView  = new ImageView(buttonImage);
      buttonImageView.setFitHeight(ICON_SIZE);
      buttonImageView.setPreserveRatio(true);
      button.setGraphic(buttonImageView);
      button.setSelected(isSelected);
    } catch (MalformedURLException e) {
      logger.error("Error loading button image!", e);
    }
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

  public Spinner<Integer> getRotationSpinner1() {
    return rotationSpinner1;
  }

  public Spinner<Integer> getRotationSpinner2() {
    return rotationSpinner2;
  }

  public Spinner<Integer> getRotationSpinner3() {
    return rotationSpinner3;
  }

  public Spinner<Integer> getZoomSpinner1() {
    return zoomSpinner1;
  }

  public Spinner<Integer> getZoomSpinner2() {
    return zoomSpinner2;
  }

  public Spinner<Integer> getZoomSpinner3() {
    return zoomSpinner3;
  }

  public ZoomButton getZoomButton() {
    return zoomButton;
  }

}

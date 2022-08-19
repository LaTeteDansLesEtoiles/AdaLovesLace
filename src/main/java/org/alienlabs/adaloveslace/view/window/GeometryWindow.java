package org.alienlabs.adaloveslace.view.window;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Spinner;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.view.component.button.geometrywindow.*;
import org.alienlabs.adaloveslace.view.component.button.geometrywindow.move.*;
import org.alienlabs.adaloveslace.view.component.spinner.RotationSpinner;
import org.alienlabs.adaloveslace.view.component.spinner.ZoomSpinner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;

import static org.alienlabs.adaloveslace.App.*;
import static org.alienlabs.adaloveslace.business.model.Knot.DEFAULT_ROTATION;
import static org.alienlabs.adaloveslace.business.model.Knot.DEFAULT_ZOOM;
import static org.alienlabs.adaloveslace.view.component.button.geometrywindow.DeletionButton.DELETION_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.component.button.geometrywindow.DuplicationButton.DUPLICATION_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.component.button.geometrywindow.HorizontalFlippingButton.HORIZONTAL_FLIPPING_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.component.button.geometrywindow.RotationButton.ROTATION_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.component.button.geometrywindow.SelectionButton.SELECTION_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.component.button.geometrywindow.VerticalFlippingButton.VERTICAL_FLIPPING_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.component.button.geometrywindow.ZoomButton.ZOOM_BUTTON_NAME;

public class GeometryWindow {

  public static final double GEOMETRY_WINDOW_X                  = 1150d;
  public static final double GEOMETRY_WINDOW_WIDTH              = 400d;
  public static final double GEOMETRY_WINDOW_HEIGHT             = 670d;
  public static final double GAP_BETWEEN_BUTTONS                = 10d;

  public static final double GEOMETRY_BUTTONS_HEIGHT            = 50d;
  public static final int ROTATION_SPINNER_MIN_VALUE            = -360;
  public static final int ROTATION_SPINNER_MAX_VALUE            = 360;
  public static final int ROTATION_SPINNER_INCREMENTS_1         = 1;
  public static final int ROTATION_SPINNER_INCREMENTS_2         = 10;
  public static final int ROTATION_SPINNER_INCREMENTS_3         = 30;

  public static final int ZOOM_SPINNER_MIN_VALUE                = -20;
  public static final int ZOOM_SPINNER_MAX_VALUE                = 20;
  public static final double ZOOM_SPINNER_POSITIVE_ZOOM_MULTIPLY_FACTOR = 0.15d;
  public static final double ZOOM_SPINNER_NEGATIVE_ZOOM_DIVISION_FACTOR = 1.5d;
  public static final int ZOOM_SPINNER_INCREMENTS_1             = 1;
  public static final int ZOOM_SPINNER_INCREMENTS_2             = 2;
  public static final int ZOOM_SPINNER_INCREMENTS_3             = 3;

  private DrawingButton drawingButton;
  private UpButton upButton;
  private DownButton downButton;
  private LeftButton leftButton;
  private RightButton rightButton;
  private DownLeftButton downLeftButton;
  private DownRightButton downRightButton;
  private UpLeftButton upLeftButton;
  private UpRightButton upRightButton;
  private FastMoveModeButton fastMoveModeButton;
  private SelectionButton selectionButton;
  private DeletionButton deletionButton;
  private DuplicationButton duplicationButton;
  private VerticalFlippingButton verticalFlippingButton;
  private HorizontalFlippingButton horizontalFlippingButton;
  private RotationButton rotationButton;
  private ZoomButton zoomButton;
  private Spinner<Integer> rotationSpinner1;
  private Spinner<Integer> rotationSpinner2;
  private Spinner<Integer> rotationSpinner3;
  private Spinner<Integer> zoomSpinner1;
  private Spinner<Integer> zoomSpinner2;
  private Spinner<Integer> zoomSpinner3;

  private static final Logger logger = LoggerFactory.getLogger(GeometryWindow.class);
  private RotationSpinner rotationSpinnerObject1;
  private RotationSpinner rotationSpinnerObject2;
  private RotationSpinner rotationSpinnerObject3;
  private ZoomSpinner zoomSpinnerObject1;
  private ZoomSpinner zoomSpinnerObject2;
  private ZoomSpinner zoomSpinnerObject3;
  private Stage geometryStage;

  public void createGeometryStage(App app, Stage geometryStage, Pane parent) {
    Scene geometryScene = new Scene(parent, GEOMETRY_WINDOW_WIDTH, GEOMETRY_WINDOW_HEIGHT);

    this.geometryStage = geometryStage;
    geometryStage.setTitle(resourceBundle.getString(GEOMETRY_TITLE));
    geometryStage.setOnCloseRequest(windowEvent -> {
      logger.info("You shall not close the geometry window directly!");
    });
    geometryStage.setX(GEOMETRY_WINDOW_X);
    geometryStage.setY(MAIN_WINDOW_Y);
    geometryStage.setScene(geometryScene);
    geometryStage.show();
  }

  public void createGeometryButtons(App app, GridPane parent) {
    TilePane buttonsPane  = new TilePane(Orientation.HORIZONTAL);
    buttonsPane.setAlignment(Pos.BOTTOM_CENTER);
    buttonsPane.setPrefColumns(2);
    buttonsPane.setVgap(GAP_BETWEEN_BUTTONS);

    this.deletionButton = new DeletionButton(app, this, resourceBundle.getString(DELETION_BUTTON_NAME));
    getImageView("deletion.png", deletionButton, false);

    this.duplicationButton = new DuplicationButton(app, this, resourceBundle.getString(DUPLICATION_BUTTON_NAME));
    getImageView("duplication.png", duplicationButton, false);

    this.verticalFlippingButton = new VerticalFlippingButton(app, this, resourceBundle.getString(VERTICAL_FLIPPING_BUTTON_NAME));
    getImageView("flip_vertically.png", verticalFlippingButton, false);

    this.horizontalFlippingButton = new HorizontalFlippingButton(app, this, resourceBundle.getString(HORIZONTAL_FLIPPING_BUTTON_NAME));
    getImageView("flip_horizontally.png", horizontalFlippingButton, false);

    this.drawingButton = new DrawingButton(app, this, resourceBundle.getString(DrawingButton.DRAWING_BUTTON_NAME));
    getImageView("drawing.png", drawingButton, true);

    this.selectionButton = new SelectionButton(app, this, resourceBundle.getString(SELECTION_BUTTON_NAME));
    getImageView("selection.png", selectionButton, false);

    this.rotationButton = new RotationButton(app, this, resourceBundle.getString(ROTATION_BUTTON_NAME));
    getImageView("rotation.png", rotationButton, false);

    this.zoomButton = new ZoomButton(app, this, resourceBundle.getString(ZOOM_BUTTON_NAME));
    getImageView("zoom.png", zoomButton, false);

    this.rotationSpinner1 = new Spinner<>(ROTATION_SPINNER_MIN_VALUE, ROTATION_SPINNER_MAX_VALUE,
      DEFAULT_ROTATION, ROTATION_SPINNER_INCREMENTS_1);
    this.rotationSpinner2 = new Spinner<>(ROTATION_SPINNER_MIN_VALUE, ROTATION_SPINNER_MAX_VALUE,
      DEFAULT_ROTATION, ROTATION_SPINNER_INCREMENTS_2);
    this.rotationSpinner3 = new Spinner<>(ROTATION_SPINNER_MIN_VALUE, ROTATION_SPINNER_MAX_VALUE,
      DEFAULT_ROTATION, ROTATION_SPINNER_INCREMENTS_3);

    rotationSpinnerObject1 = new RotationSpinner();
    rotationSpinnerObject1.buildRotationSpinner(app, this.rotationSpinner1,
      this.rotationSpinner2.getValueFactory(), this.rotationSpinner3.getValueFactory(), ROTATION_SPINNER_INCREMENTS_1);

    rotationSpinnerObject2 = new RotationSpinner();
    rotationSpinnerObject2.buildRotationSpinner(app, this.rotationSpinner2,
      this.rotationSpinner1.getValueFactory(), this.rotationSpinner3.getValueFactory(), ROTATION_SPINNER_INCREMENTS_2);

    rotationSpinnerObject3 = new RotationSpinner();
    rotationSpinnerObject3.buildRotationSpinner(app, this.rotationSpinner3,
      this.rotationSpinner1.getValueFactory(), this.rotationSpinner2.getValueFactory(), ROTATION_SPINNER_INCREMENTS_3);

    this.zoomSpinner1 = new Spinner<>(ZOOM_SPINNER_MIN_VALUE, ZOOM_SPINNER_MAX_VALUE, DEFAULT_ZOOM, ZOOM_SPINNER_INCREMENTS_1);
    this.zoomSpinner2 = new Spinner<>(ZOOM_SPINNER_MIN_VALUE, ZOOM_SPINNER_MAX_VALUE, DEFAULT_ZOOM, ZOOM_SPINNER_INCREMENTS_2);
    this.zoomSpinner3 = new Spinner<>(ZOOM_SPINNER_MIN_VALUE, ZOOM_SPINNER_MAX_VALUE, DEFAULT_ZOOM, ZOOM_SPINNER_INCREMENTS_3);

    zoomSpinnerObject1 = new ZoomSpinner();
    zoomSpinnerObject1.buildZoomSpinner(app, this.zoomSpinner1, this.zoomSpinner2.getValueFactory(),
      this.zoomSpinner3.getValueFactory(), ZOOM_SPINNER_INCREMENTS_1);

    zoomSpinnerObject2 = new ZoomSpinner();
    zoomSpinnerObject2.buildZoomSpinner(app, this.zoomSpinner2, this.zoomSpinner1.getValueFactory(),
      this.zoomSpinner3.getValueFactory(), ZOOM_SPINNER_INCREMENTS_2);

    zoomSpinnerObject3 = new ZoomSpinner();
    zoomSpinnerObject3.buildZoomSpinner(app, this.zoomSpinner3, this.zoomSpinner1.getValueFactory(),
      this.zoomSpinner2.getValueFactory(), ZOOM_SPINNER_INCREMENTS_3);

    buttonsPane.getChildren().addAll(this.rotationSpinner1, this.zoomSpinner1,
      this.rotationSpinner2, this.zoomSpinner2,
      this.rotationSpinner3, this.zoomSpinner3,
      this.rotationButton, this.zoomButton,
      this.drawingButton, this.selectionButton,
      this.deletionButton, this.duplicationButton,
      this.verticalFlippingButton, this.horizontalFlippingButton);

    parent.add(buttonsPane, 0, 0);
  }


  public void createMoveKnotButtons(App app, GridPane parent) {
    GridPane moveKnotPane = app.newGridPane();

    this.upButton = new UpButton(app, this);
    getImageView("up.png", this.upButton, false);
    moveKnotPane.add(this.upButton, 1, 0);

    this.downButton = new DownButton(app, this);
    getImageView("down.png", this.downButton, false);
    moveKnotPane.add(this.downButton, 1, 2);

    this.leftButton = new LeftButton(app, this);
    getImageView("left.png", this.leftButton, false);
    moveKnotPane.add(this.leftButton, 0, 1);

    this.rightButton = new RightButton(app, this);
    getImageView("right.png", this.rightButton, false);
    moveKnotPane.add(this.rightButton, 2, 1);

    this.downLeftButton = new DownLeftButton(app, this);
    getImageView("down_left.png", this.downLeftButton, false);
    moveKnotPane.add(this.downLeftButton, 0, 2);

    this.upLeftButton = new UpLeftButton(app, this);
    getImageView("up_left.png", this.upLeftButton, false);
    moveKnotPane.add(this.upLeftButton, 0, 0);

    this.downRightButton = new DownRightButton(app, this);
    getImageView("down_right.png", this.downRightButton, false);
    moveKnotPane.add(this.downRightButton, 2, 2);

    this.upRightButton = new UpRightButton(app, this);
    getImageView("up_right.png", this.upRightButton, false);
    moveKnotPane.add(this.upRightButton, 2, 0);

    this.fastMoveModeButton = new FastMoveModeButton(app, this);
    getImageView("fast.png", this.fastMoveModeButton, false);
    moveKnotPane.add(this.fastMoveModeButton, 1, 1);

    parent.add(moveKnotPane, 0, 2);
  }

  private void getImageView(String pathname, ButtonBase button, boolean isSelected) {
    try {
      Image buttonImage = new Image(new File(ASSETS_DIRECTORY + pathname).toURI().toURL().toExternalForm());

      ImageView buttonImageView  = new ImageView(buttonImage);
      buttonImageView.setFitHeight(ICON_SIZE);
      buttonImageView.setPreserveRatio(true);
      button.setGraphic(buttonImageView);

      if (isSelected) {
        ((ToggleButton)button).setSelected(true);
      }
    } catch (MalformedURLException e) {
      logger.error("Error loading button image!", e);
    }
  }

  public DrawingButton getDrawingButton() {
    return drawingButton;
  }

  public SelectionButton getSelectionButton() {
    return this.selectionButton;
  }

  public DeletionButton getDeletionButton() {
    return deletionButton;
  }

  public DuplicationButton getDuplicationButton() {
    return duplicationButton;
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

  public RotationSpinner getRotationSpinnerObject1() {
    return rotationSpinnerObject1;
  }

  public ZoomSpinner getZoomSpinnerObject1() {
    return zoomSpinnerObject1;
  }

  public ZoomButton getZoomButton() {
    return zoomButton;
  }

  public Stage getGeometryStage() {
    return geometryStage;
  }

}

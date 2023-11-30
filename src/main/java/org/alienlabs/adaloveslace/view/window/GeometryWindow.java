package org.alienlabs.adaloveslace.view.window;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Spinner;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.util.ImageUtil;
import org.alienlabs.adaloveslace.view.component.button.geometrywindow.*;
import org.alienlabs.adaloveslace.view.component.button.geometrywindow.move.*;
import org.alienlabs.adaloveslace.view.component.spinner.RotationSpinner;
import org.alienlabs.adaloveslace.view.component.spinner.ZoomSpinner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
  public static final int ZOOM_SPINNER_INCREMENTS_1             = 1;
  public static final int ZOOM_SPINNER_INCREMENTS_2             = 2;
  public static final int ZOOM_SPINNER_INCREMENTS_3             = 3;

  private DrawingButton drawingButton;
  private SelectionButton selectionButton;
  private DeletionButton deletionButton;
  private DuplicationButton duplicationButton;
  private Spinner<Integer> rotationSpinner1;
  private Spinner<Integer> rotationSpinner2;
  private Spinner<Integer> rotationSpinner3;
  private Spinner<Integer> zoomSpinner1;
  private Spinner<Integer> zoomSpinner2;
  private Spinner<Integer> zoomSpinner3;

  private static final Logger logger = LoggerFactory.getLogger(GeometryWindow.class);
  private Stage geometryStage;

  public void createGeometryStage(Stage geometryStage, Pane parent) {
    Scene geometryScene = new Scene(parent, GEOMETRY_WINDOW_WIDTH, GEOMETRY_WINDOW_HEIGHT);

    this.geometryStage = geometryStage;
    geometryStage.setTitle(resourceBundle.getString(GEOMETRY_TITLE));
    geometryStage.setOnCloseRequest(windowEvent -> logger.debug("You shall not close the geometry window directly!"));    geometryStage.setX(GEOMETRY_WINDOW_X);
    geometryStage.setY(MAIN_WINDOW_Y);
    geometryStage.setScene(geometryScene);
    geometryStage.show();
  }

  public void createGeometryButtons(App app, GridPane parent) {
    TilePane buttonsPane  = new TilePane(Orientation.HORIZONTAL);
    buttonsPane.setAlignment(Pos.BOTTOM_CENTER);
    buttonsPane.setPrefColumns(2);
    buttonsPane.setVgap(GAP_BETWEEN_BUTTONS);
    ImageUtil util = new ImageUtil(app);

    this.deletionButton = new DeletionButton(app, this, resourceBundle.getString(DELETION_BUTTON_NAME));
    util.getImageView("deletion.png", deletionButton, false);

    this.duplicationButton = new DuplicationButton(app, this, resourceBundle.getString(DUPLICATION_BUTTON_NAME));
    util.getImageView("duplication.png", duplicationButton, false);

    VerticalFlippingButton verticalFlippingButton = new VerticalFlippingButton(app, resourceBundle.getString(VERTICAL_FLIPPING_BUTTON_NAME));
    util.getImageView("flip_vertically.png", verticalFlippingButton, false);

    HorizontalFlippingButton horizontalFlippingButton = new HorizontalFlippingButton(app, resourceBundle.getString(HORIZONTAL_FLIPPING_BUTTON_NAME));
    util.getImageView("flip_horizontally.png", horizontalFlippingButton, false);

    this.drawingButton = new DrawingButton(app, this, resourceBundle.getString(DrawingButton.DRAWING_BUTTON_NAME));
    util.getImageView("drawing.png", drawingButton, true);

    this.selectionButton = new SelectionButton(app, this, resourceBundle.getString(SELECTION_BUTTON_NAME));
    util.getImageView("selection.png", selectionButton, false);

    RotationButton rotationButton = new RotationButton(resourceBundle.getString(ROTATION_BUTTON_NAME));
    util.getImageView("rotation.png", rotationButton, false);

    ZoomButton zoomButton = new ZoomButton(resourceBundle.getString(ZOOM_BUTTON_NAME));
    util.getImageView("zoom.png", zoomButton, false);

    this.rotationSpinner1 = new Spinner<>(ROTATION_SPINNER_MIN_VALUE, ROTATION_SPINNER_MAX_VALUE,
      DEFAULT_ROTATION, ROTATION_SPINNER_INCREMENTS_1);
    this.rotationSpinner2 = new Spinner<>(ROTATION_SPINNER_MIN_VALUE, ROTATION_SPINNER_MAX_VALUE,
      DEFAULT_ROTATION, ROTATION_SPINNER_INCREMENTS_2);
    this.rotationSpinner3 = new Spinner<>(ROTATION_SPINNER_MIN_VALUE, ROTATION_SPINNER_MAX_VALUE,
      DEFAULT_ROTATION, ROTATION_SPINNER_INCREMENTS_3);

    RotationSpinner rotationSpinnerObject1 = new RotationSpinner();
    rotationSpinnerObject1.buildRotationSpinner(app, this.rotationSpinner1,
      this.rotationSpinner2.getValueFactory(), this.rotationSpinner3.getValueFactory());

    RotationSpinner rotationSpinnerObject2 = new RotationSpinner();
    rotationSpinnerObject2.buildRotationSpinner(app, this.rotationSpinner2,
      this.rotationSpinner1.getValueFactory(), this.rotationSpinner3.getValueFactory());

    RotationSpinner rotationSpinnerObject3 = new RotationSpinner();
    rotationSpinnerObject3.buildRotationSpinner(app, this.rotationSpinner3,
      this.rotationSpinner1.getValueFactory(), this.rotationSpinner2.getValueFactory());

    this.zoomSpinner1 = new Spinner<>(ZOOM_SPINNER_MIN_VALUE, ZOOM_SPINNER_MAX_VALUE, DEFAULT_ZOOM, ZOOM_SPINNER_INCREMENTS_1);
    this.zoomSpinner2 = new Spinner<>(ZOOM_SPINNER_MIN_VALUE, ZOOM_SPINNER_MAX_VALUE, DEFAULT_ZOOM, ZOOM_SPINNER_INCREMENTS_2);
    this.zoomSpinner3 = new Spinner<>(ZOOM_SPINNER_MIN_VALUE, ZOOM_SPINNER_MAX_VALUE, DEFAULT_ZOOM, ZOOM_SPINNER_INCREMENTS_3);

    ZoomSpinner zoomSpinnerObject1 = new ZoomSpinner();
    zoomSpinnerObject1.buildZoomSpinner(app, this.zoomSpinner1, this.zoomSpinner2.getValueFactory(),
      this.zoomSpinner3.getValueFactory());

    ZoomSpinner zoomSpinnerObject2 = new ZoomSpinner();
    zoomSpinnerObject2.buildZoomSpinner(app, this.zoomSpinner2, this.zoomSpinner1.getValueFactory(),
      this.zoomSpinner3.getValueFactory());

    ZoomSpinner zoomSpinnerObject3 = new ZoomSpinner();
    zoomSpinnerObject3.buildZoomSpinner(app, this.zoomSpinner3, this.zoomSpinner1.getValueFactory(),
      this.zoomSpinner2.getValueFactory());

    buttonsPane.getChildren().addAll(this.rotationSpinner1, this.zoomSpinner1,
      this.rotationSpinner2, this.zoomSpinner2,
      this.rotationSpinner3, this.zoomSpinner3,
      rotationButton, zoomButton,
      this.drawingButton, this.selectionButton,
      this.deletionButton, this.duplicationButton,
      verticalFlippingButton, horizontalFlippingButton);

    parent.add(buttonsPane, 0, 0);
  }


  public void createMoveKnotButtons(App app, GridPane parent) {
    GridPane moveKnotPane = app.newGridPane();
    ImageUtil util = new ImageUtil(app);

    UpButton upButton = new UpButton(app);
    util.getImageView("up.png", upButton, false);
    moveKnotPane.add(upButton, 1, 0);

    DownButton downButton = new DownButton(app);
    util.getImageView("down.png", downButton, false);
    moveKnotPane.add(downButton, 1, 2);

    LeftButton leftButton = new LeftButton(app);
    util.getImageView("left.png", leftButton, false);
    moveKnotPane.add(leftButton, 0, 1);

    RightButton rightButton = new RightButton(app);
    util.getImageView("right.png", rightButton, false);
    moveKnotPane.add(rightButton, 2, 1);

    DownLeftButton downLeftButton = new DownLeftButton(app);
    util.getImageView("down_left.png", downLeftButton, false);
    moveKnotPane.add(downLeftButton, 0, 2);

    UpLeftButton upLeftButton = new UpLeftButton(app);
    util.getImageView("up_left.png", upLeftButton, false);
    moveKnotPane.add(upLeftButton, 0, 0);

    DownRightButton downRightButton = new DownRightButton(app);
    util.getImageView("down_right.png", downRightButton, false);
    moveKnotPane.add(downRightButton, 2, 2);

    UpRightButton upRightButton = new UpRightButton(app);
    util.getImageView("up_right.png", upRightButton, false);
    moveKnotPane.add(upRightButton, 2, 0);

    FastMoveModeButton fastMoveModeButton = new FastMoveModeButton();
    util.getImageView("fast.png", fastMoveModeButton, false);
    moveKnotPane.add(fastMoveModeButton, 1, 1);

    parent.add(moveKnotPane, 0, 2);
  }

  public DrawingButton getDrawingButton() {
    return this.drawingButton;
  }

  public SelectionButton getSelectionButton() {
    return this.selectionButton;
  }

  public DeletionButton getDeletionButton() {
    return this.deletionButton;
  }

  public DuplicationButton getDuplicationButton() {
    return this.duplicationButton;
  }

  public Spinner<Integer> getRotationSpinner1() {
    return this.rotationSpinner1;
  }

  public Spinner<Integer> getRotationSpinner2() {
    return this.rotationSpinner2;
  }

  public Spinner<Integer> getRotationSpinner3() {
    return this.rotationSpinner3;
  }

  public Spinner<Integer> getZoomSpinner1() {
    return this.zoomSpinner1;
  }

  public Spinner<Integer> getZoomSpinner2() {
    return this.zoomSpinner2;
  }

  public Spinner<Integer> getZoomSpinner3() {
    return this.zoomSpinner3;
  }

  public Stage getGeometryStage() {
    return this.geometryStage;
  }

}

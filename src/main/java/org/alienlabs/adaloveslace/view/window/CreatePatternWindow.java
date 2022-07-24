package org.alienlabs.adaloveslace.view.window;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.CreatePatternButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.util.Optional;

import static javafx.scene.control.Alert.AlertType.CONFIRMATION;
import static javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE;
import static org.alienlabs.adaloveslace.App.*;
import static org.alienlabs.adaloveslace.util.FileUtil.CLASSPATH_RESOURCES_PATH;

public class CreatePatternWindow {

  public static final double CREATE_PATTERN_WINDOW_WIDTH    = 550d;

  public static final String CREATE_PATTERN_WINDOW_TITLE     = "CREATE_PATTERN_WINDOW_TITLE";
  public static final String CREATE_PATTERN_HEADER_TEXT      = "CREATE_PATTERN_HEADER_TEXT";
  public static final String CREATE_PATTERN_CONTENT_TEXT     = "CREATE_PATTERN_CONTENT_TEXT";
  public static final String CREATE_PATTERN_BUTTON_TEXT      = "CREATE_PATTERN_BUTTON_TEXT";
  public static final String CANCEL_BUTTON_TEXT              = "CANCEL_BUTTON_TEXT";
  public static final String CREATE_PATTERN_PREVIEW_LABEL    = "CREATE_PATTERN_PREVIEW_LABEL";

  private static final Logger logger = LoggerFactory.getLogger(CreatePatternWindow.class);
  private final File previewFile;

  public CreatePatternWindow(App app, Rectangle rectangle, File previewFile) {
    this.previewFile = previewFile;
    Alert alert = new Alert(CONFIRMATION);

    ButtonType createPatternButton = buildAlertWindow(alert);
    GridPane gridPane = buildGridPane();
    alert.getDialogPane().setContent(gridPane);

    app.getRoot().getChildren().add(rectangle);
    Optional<ButtonType> result = alert.showAndWait();

    if (result.isPresent() && result.get() == createPatternButton) {
      logger.info("Accepted pattern creation");

      app.getMainWindow().getGrid().removeEventHandler(MouseEvent.MOUSE_MOVED, CreatePatternButton.getMouseMovedListener());
      app.getMainWindow().getGrid().removeEventHandler(MouseEvent.MOUSE_CLICKED, CreatePatternButton.getMouseClickedListener());
      app.getPrimaryStage().close();
      app.showMainWindow(MAIN_WINDOW_WIDTH, MAIN_WINDOW_HEIGHT, GRID_WIDTH, GRID_HEIGHT, GRID_DOTS_RADIUS,
        app.getPrimaryStage());
      app.getToolboxStage().close();
      app.showToolboxWindow(app, app, CLASSPATH_RESOURCES_PATH);
      app.getGeometryStage().close();
      app.showGeometryWindow(app);
    } else {
      logger.info("Pattern creation cancelled");

      try {
        Files.delete(previewFile.toPath());
      } catch (IOException e) {
        logger.error("Error deleting file during pattern creation window!", e);
      }
    }

    alert.close();
  }

  private ButtonType buildAlertWindow(Alert alert) {
    alert.setTitle(resourceBundle.getString(CREATE_PATTERN_WINDOW_TITLE));
    alert.setHeaderText(resourceBundle.getString(CREATE_PATTERN_HEADER_TEXT));
    alert.setContentText(resourceBundle.getString(CREATE_PATTERN_CONTENT_TEXT));

    ButtonType createDiagramButton  = new ButtonType(resourceBundle.getString(CREATE_PATTERN_BUTTON_TEXT));
    ButtonType cancelButton = new ButtonType(resourceBundle.getString(CANCEL_BUTTON_TEXT), CANCEL_CLOSE);

    alert.getButtonTypes().setAll(createDiagramButton, cancelButton);
    return createDiagramButton;
  }

  private GridPane buildGridPane() {
    GridPane gridPane = new GridPane();
    gridPane.setPrefWidth(CREATE_PATTERN_WINDOW_WIDTH);
    gridPane.setMaxWidth(Double.MAX_VALUE);

    try {
      Image preview = new Image(new File(this.previewFile.getAbsolutePath()).toURI().toURL().toExternalForm());
      ImageView view = new ImageView(preview);

      Label imagePreviewLabel = new Label(resourceBundle.getString(CREATE_PATTERN_PREVIEW_LABEL));
      gridPane.add(imagePreviewLabel, 0, 0);
      gridPane.add(view, 1, 0);
    } catch (MalformedURLException e) {
      logger.error("Error reading pattern file during pattern creation window!", e);
    }

    return gridPane;
  }

}

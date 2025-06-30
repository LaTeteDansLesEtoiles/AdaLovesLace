package org.alienlabs.adaloveslace.view.window;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.util.FileChooserUtil;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid.CreatePatternButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.util.Optional;

import static javafx.scene.control.Alert.AlertType.CONFIRMATION;
import static javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE;
import static org.alienlabs.adaloveslace.App.resourceBundle;

public class CreatePatternWindow {

  public static final double CREATE_PATTERN_WINDOW_WIDTH    = 550d;

  public static final String CREATE_PATTERN_WINDOW_TITLE     = "CREATE_PATTERN_WINDOW_TITLE";
  public static final String CREATE_PATTERN_HEADER_TEXT      = "CREATE_PATTERN_HEADER_TEXT";
  public static final String CREATE_PATTERN_CONTENT_TEXT     = "CREATE_PATTERN_CONTENT_TEXT";
  public static final String CREATE_PATTERN_BUTTON_TEXT      = "CREATE_PATTERN_BUTTON_TEXT";
  public static final String CANCEL_BUTTON_TEXT              = "CancelButtonText";
  public static final String CREATE_PATTERN_PREVIEW_LABEL    = "CREATE_PATTERN_PREVIEW_LABEL";

  private static final Logger logger = LoggerFactory.getLogger(CreatePatternWindow.class);
    private final File previewFile;

    public CreatePatternWindow(App app, File previewFile) {
      this.previewFile = previewFile;
      Alert alert = new Alert(CONFIRMATION);

      ButtonType createPatternButton = buildAlertWindow(alert);
      GridPane gridPane = buildGridPane();
      alert.getDialogPane().setContent(gridPane);

      Optional<ButtonType> result = alert.showAndWait();

      if (result.isPresent() && result.get() == createPatternButton) {
        logger.debug("Accepted pattern creation");

        app.getMovablePane().removeEventHandler(MouseEvent.MOUSE_MOVED, CreatePatternButton.getMouseMovedListener());
        app.getMovablePane().removeEventHandler(MouseEvent.MOUSE_CLICKED, CreatePatternButton.getMouseClickedListener());
        new FileChooserUtil().restartGui(app);
      } else {
        logger.debug("Pattern creation cancelled");

        try {
          if (Files.exists(this.previewFile.toPath())) {
            Files.delete(this.previewFile.toPath());
          }
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
    alert.getDialogPane().getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

    ButtonType createDiagramButton  = new ButtonType(resourceBundle.getString(CREATE_PATTERN_BUTTON_TEXT));
    ButtonType cancelButton = new ButtonType(resourceBundle.getString(CANCEL_BUTTON_TEXT), CANCEL_CLOSE);

    alert.getButtonTypes().setAll(createDiagramButton, cancelButton);
    return createDiagramButton;
  }

  private GridPane buildGridPane() {
    GridPane gridPane = new GridPane();
    gridPane.setPrefWidth(CREATE_PATTERN_WINDOW_WIDTH);

    try {
      Image preview = new Image(new File(this.previewFile.getAbsolutePath()).toURI().toURL().toExternalForm());
      ImageView view = new ImageView(preview);

      Label imagePreviewLabel = new Label(resourceBundle.getString(CREATE_PATTERN_PREVIEW_LABEL));
      gridPane.add(imagePreviewLabel, 0, 0);
      gridPane.add(view, 1, 0);

      gridPane.setStyle(
    "-fx-border-color: white;" +
    "-fx-border-width: 2;" +
    "-fx-border-radius: 4;" +
    "-fx-background-radius: 4;"
);
    } catch (MalformedURLException e) {
      logger.error("Error reading pattern file during pattern creation window!", e);
    }

    return gridPane;
  }

}

package org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid;

import javafx.event.EventHandler;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.enumeration.GridType;
import org.alienlabs.adaloveslace.util.FileChooserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static org.alienlabs.adaloveslace.App.*;
import static org.alienlabs.adaloveslace.util.FileUtil.APP_FOLDER_IN_USER_HOME;
import static org.alienlabs.adaloveslace.view.window.ToolboxWindow.PATTERN_TEXT_AND_COLOR_BUTTON;

public class AddKnotButton extends ToggleButton {

  public static final String KNOT_FILES            = "image files (*.jpg or *.png)";
  public static final String KNOT_FILES_FILTER1      = "*.jpg";
  public static final String KNOT_FILES_FILTER2      = "*.jpeg";
  public static final String KNOT_FILES_FILTER3      = "*.png";

  private static App app;

  private static final Logger logger                  = LoggerFactory.getLogger(AddKnotButton.class);

  public AddKnotButton(App app) {
    super(resourceBundle.getString(ADD_KNOT_BUTTON_NAME));

    AddKnotButton.app = app;

    this.getStyleClass().add(PATTERN_TEXT_AND_COLOR_BUTTON);
    this.setSelected(false);

    this.setOnMouseClicked(onAddKnotButtonClicked);
  }

  private final EventHandler<MouseEvent> onAddKnotButtonClicked = _ -> {
    app.unselectPatternsAndTextButtons();
    app.getMovablePane().setOnKeyPressed(null);
    this.setSelected(true);

    FileChooser load = new FileChooserUtil().getAddKnotFileChooser(
            app.getOptionalDotGrid().getDiagram().getCurrentGridType() == GridType.CRISS_CROSS
                    ? resourceBundle.getString(ADD_KNOT_WITH_SIZE_DIALOG_TITLE)
                    : resourceBundle.getString(ADD_KNOT_BUTTON_NAME),
            APP_FOLDER_IN_USER_HOME,
            KNOT_FILES
    );
    File file = load.showOpenDialog(app.getScene().getWindow());

    if (file != null) {
      Path targetDir = Path.of(APP_FOLDER_IN_USER_HOME, PATTERNS_DIRECTORY_NAME);
      Path targetPath = targetDir.resolve(file.getName());

      try {
        Files.copy(
                file.toPath(),
                targetPath,
                StandardCopyOption.REPLACE_EXISTING
        );

        new FileChooserUtil().restartGui(app);
      } catch (IOException e) {
        logger.error("Error adding knot", e);
      }
    }
  };

}

package org.alienlabs.adaloveslace.view.window;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Diagram;
import org.alienlabs.adaloveslace.util.FileUtil;
import org.alienlabs.adaloveslace.view.component.button.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import static org.alienlabs.adaloveslace.App.*;
import static org.alienlabs.adaloveslace.util.FileUtil.HOME_DIRECTORY_RESOURCES_PATH;
import static org.alienlabs.adaloveslace.view.component.button.LoadButton.LOAD_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.component.button.QuitButton.QUIT_APP;
import static org.alienlabs.adaloveslace.view.component.button.SaveAsButton.SAVE_FILE_AS_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.component.button.SaveButton.SAVE_FILE_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.component.button.ShowHideGridButton.SHOW_HIDE_GRID_BUTTON_NAME;

public class ToolboxWindow {

  public static final double TOOLBOX_WINDOW_X             = 750d;
  public static final double TOOLBOX_WINDOW_WIDTH         = 200d;
  public static final double TILE_HEIGHT                  = 50d;
  public static final double TILE_PADDING                 = 25d;
  public static final double VERTICAL_PADDING             = 70d;
  public static final double VERTICAL_BUTTONS_PADDING     = 50d;
  public static final double VERTICAL_GAP_BETWEEN_BUTTONS = 10d;

  private List<String> classpathResourceFiles;

  private static final Logger logger = LoggerFactory.getLogger(ToolboxWindow.class);

  public Diagram createToolboxPane(TilePane toolboxPane, Object classpathBase, String resourcesPath, App app, final Diagram diagram) {
    this.classpathResourceFiles = loadPatternsResourcesFiles(resourcesPath, classpathBase);
    List<String> homeDirectoryResourceFiles;

    if (classpathBase.equals(app)) {
      File homeDirectoryResourcesPath = new File(System.getProperty("user.home") + File.separator + PROJECT_NAME + File.separator + PATTERNS_DIRECTORY_NAME);
      if (!homeDirectoryResourcesPath.exists() || !homeDirectoryResourcesPath.canWrite()) {
        showNoPatternDirectoryDialog(homeDirectoryResourcesPath);
      } else {
        homeDirectoryResourceFiles = loadPatternsFolderResourcesFiles(HOME_DIRECTORY_RESOURCES_PATH,
          homeDirectoryResourcesPath);

        if (homeDirectoryResourceFiles == null || homeDirectoryResourceFiles.isEmpty()) {
          showEmptyPatternDirectoryDialog(homeDirectoryResourcesPath);
        } else {
          this.classpathResourceFiles.addAll(homeDirectoryResourceFiles);
        }
      }
    }

    for (int i = 0; i < this.classpathResourceFiles.size(); i++) {
      String filename = this.classpathResourceFiles.get(i);
      File file = new File(filename);

      if (file.exists()) {
        String label = file.getName();

        try (FileInputStream fis = new FileInputStream(filename)) {
          org.alienlabs.adaloveslace.business.model.Pattern pattern = new org.alienlabs.adaloveslace.business.model.Pattern(filename);

          if (i == 0) {
            diagram.setCurrentPattern(pattern);
          }

          Button button = new PatternButton(app, label, new ImageView(new Image(fis)), pattern);
          button.setId(TOOLBOX_BUTTON + (i + 1));
          toolboxPane.getChildren().add(button);

          diagram.addPattern(pattern);
        } catch (IOException e) {
          logger.error("Exception reading toolbox file!", e);
        }
      }
    }

    return diagram;
  }

  /**
   * Gets sorted (by String's default sort) Pattern list resources from classpath.
   *
   * @param resourcesPath the classpath resource pattern to load
   * @param classpathBase the main app, needed for tests
   * @return the sorted Pattern list from classpath, by name
   */
  public List<String> loadPatternsResourcesFiles(String resourcesPath, Object classpathBase) {
    List<String> resourceFiles = new FileUtil().getResources(classpathBase, Pattern.compile(resourcesPath));
    Collections.sort(resourceFiles);

    return resourceFiles;
  }

  /**
   * Gets sorted (by String's default sort) Pattern list resources from folder.
   *
   * @param resourcesPath the folder resource pattern to load
   * @param classpathBase the main app, needed for tests
   * @return the sorted Pattern list from folder, by name
   */
  public List<String> loadPatternsFolderResourcesFiles(String resourcesPath, File classpathBase) {
    List<String> resourceFiles = new FileUtil().getDirectoryResources(classpathBase, Pattern.compile(resourcesPath));
    Collections.sort(resourceFiles);

    return resourceFiles;
  }

  public void createToolboxStage(Stage toolboxStage, TilePane buttonsPane, TilePane patternsPane) {
    buttonsPane.setTranslateY(VERTICAL_BUTTONS_PADDING);
    patternsPane.getChildren().add(buttonsPane);

    Scene toolboxScene = new Scene(patternsPane, TOOLBOX_WINDOW_WIDTH,
      this.classpathResourceFiles.size() * (TILE_HEIGHT + TILE_PADDING) + VERTICAL_PADDING * 3 + VERTICAL_GAP_BETWEEN_BUTTONS);

    toolboxStage.setTitle(TOOLBOX_TITLE);
    toolboxStage.setX(TOOLBOX_WINDOW_X);
    toolboxStage.setY(MAIN_WINDOW_Y);
    toolboxStage.setScene(toolboxScene);
    toolboxStage.show();
  }

  public TilePane createToolboxButtons(App app) {
    TilePane buttonsPane  = new TilePane(Orientation.HORIZONTAL);
    buttonsPane.setAlignment(Pos.BOTTOM_CENTER);
    buttonsPane.setPrefColumns(1);
    buttonsPane.setVgap(VERTICAL_GAP_BETWEEN_BUTTONS);

    SaveButton    saveButton      = new SaveButton  (app, buttonsPane, SAVE_FILE_BUTTON_NAME);
    SaveAsButton  saveAsButton    = new SaveAsButton(app, buttonsPane, SAVE_FILE_AS_BUTTON_NAME);
    LoadButton    loadButton      = new LoadButton  (app, buttonsPane, LOAD_BUTTON_NAME);
    buttonsPane.getChildren().addAll(saveButton, saveAsButton, loadButton);

    ShowHideGridButton showHideGridButton = new ShowHideGridButton(SHOW_HIDE_GRID_BUTTON_NAME, app);
    buttonsPane.getChildren().add(showHideGridButton);

    QuitButton showQuitButton = new QuitButton(QUIT_APP);
    showQuitButton.setTranslateY(VERTICAL_BUTTONS_PADDING);
    buttonsPane.getChildren().add(showQuitButton);

    return buttonsPane;
  }

  private void showNoPatternDirectoryDialog(final File directory) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(ADA_LOVES_LACE);
    alert.setHeaderText(ERROR);
    alert.setContentText("The following folder: '" + directory.getAbsolutePath() + "' shall be used for storing patterns images and it is either non-existent either non-writable!");

    alert.showAndWait();
  }

  private void showEmptyPatternDirectoryDialog(final File directory) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(ADA_LOVES_LACE);
    alert.setHeaderText(ERROR);
    alert.setContentText("The following folder: '" + directory.getAbsolutePath() + "' shall be used for storing patterns images and it is empty!");

    alert.showAndWait();
  }

  public List<String> getClasspathResourceFiles() {
    return new ArrayList<>(this.classpathResourceFiles);
  }

}

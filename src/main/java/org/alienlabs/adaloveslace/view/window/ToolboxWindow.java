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
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.*;
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
import static org.alienlabs.adaloveslace.view.component.button.toolboxwindow.LoadButton.LOAD_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.component.button.toolboxwindow.QuitButton.QUIT_APP_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.component.button.toolboxwindow.RedoKnotButton.REDO_KNOT_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.component.button.toolboxwindow.ResetDiagramButton.RESET_DIAGRAM_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.component.button.toolboxwindow.SaveAsButton.SAVE_FILE_AS_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.component.button.toolboxwindow.SaveButton.SAVE_FILE_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.component.button.toolboxwindow.ShowHideGridButton.SHOW_HIDE_GRID_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.component.button.toolboxwindow.UndoKnotButton.UNDO_KNOT_BUTTON_NAME;

public class ToolboxWindow {

  public static final double TOOLBOX_WINDOW_X             = 750d;
  public static final double TOOLBOX_WINDOW_WIDTH         = 200d;
  public static final double TILE_HEIGHT                  = 50d;
  public static final double TILE_PADDING                 = 20d;
  public static final double VERTICAL_PADDING             = 70d;
  public static final double VERTICAL_BUTTONS_PADDING     = 125d;

  public static final double QUIT_BUTTON_PADDING          = 25d;
  public static final double VERTICAL_GAP_BETWEEN_BUTTONS = 10d;

  private List<String> classpathResourceFiles;

  private static final Logger logger = LoggerFactory.getLogger(ToolboxWindow.class);

  public Diagram createToolboxPane(TilePane toolboxPane, Object classpathBase, String resourcesPath, App app, final Diagram diagram) {
    this.classpathResourceFiles = loadPatternsResourcesFiles(resourcesPath, classpathBase);

    if (classpathBase.equals(app)) {
      createProjectHomeDirectory(new File(System.getProperty("user.home") + File.separator + PROJECT_NAME));
      File patternsDirectoryResourcesPath = createPatternDirectory();
      managePatternResourceFiles(patternsDirectoryResourcesPath);
    }

    for (int i = 0; i < this.classpathResourceFiles.size(); i++) {
      buildPatternButton(toolboxPane, app, diagram, i);
    }

    return diagram;
  }

  // Add a Pattern button to the toolbox for each image present in the home pattern folder
  private void buildPatternButton(TilePane toolboxPane, App app, Diagram diagram, int buttonIndex) {
    String filename = this.classpathResourceFiles.get(buttonIndex);
    File file = new File(filename);

    if (file.exists()) {
      String label = file.getName();

      try (FileInputStream fis = new FileInputStream(filename)) {
        buildButton(toolboxPane, app, diagram, buttonIndex, filename, label, fis);
      } catch (IOException e) {
        logger.error("Exception reading toolbox file!", e);
      }
    }
  }

  // The Pattern button itself
  private void buildButton(TilePane toolboxPane, App app, Diagram diagram, int i, String filename, String label, FileInputStream fis) {
    org.alienlabs.adaloveslace.business.model.Pattern pattern = new org.alienlabs.adaloveslace.business.model.Pattern(filename);

    Image img = new Image(fis);
    ImageView iv = new ImageView(img);

    pattern.setCenterX(img.getWidth() / 2);
    pattern.setCenterY(img.getHeight() / 2);

    Button button = new PatternButton(app, label, iv, pattern);
    button.setId(TOOLBOX_BUTTON + (i + 1));
    toolboxPane.getChildren().add(button);

    if (i == 0) {
      diagram.setCurrentPattern(pattern);
    }

    diagram.addPattern(pattern);
  }

  private void managePatternResourceFiles(File patternsDirectoryResourcesPath) {
    List<String> homeDirectoryResourceFiles;
    homeDirectoryResourceFiles = loadPatternsFolderResourcesFiles(HOME_DIRECTORY_RESOURCES_PATH,
      patternsDirectoryResourcesPath);

    if (homeDirectoryResourceFiles == null || homeDirectoryResourceFiles.isEmpty()) {
      showEmptyPatternDirectoryDialog(patternsDirectoryResourcesPath);
    } else {
      // We don't add duplicated resources to our toolbox buttons (i.e. filename must be different in both
      // classpathResourceFiles & homeDirectoryResourceFiles
      this.classpathResourceFiles.addAll(
        getAllResourceFilesWithoutDuplicates(homeDirectoryResourceFiles));
    }
  }

  // We don't add duplicated resources to our toolbox buttons (i.e. filename must be different in both
  // classpathResourceFiles & homeDirectoryResourceFiles
  private List<String> getAllResourceFilesWithoutDuplicates(List<String> homeDirectoryResourceFiles) {
    return homeDirectoryResourceFiles.stream().filter(patternDirectoryResource -> classpathResourceFiles.stream().noneMatch(
      classpathResource -> patternDirectoryResource.split(File.separator)[patternDirectoryResource.split(File.separator).length - 1]
        .equals(classpathResource.split(File.separator)[classpathResource.split(File.separator).length - 1]))).toList();
  }

  private File createPatternDirectory() {
    File patternsDirectoryResourcesPath = new File(System.getProperty("user.home") + File.separator + PROJECT_NAME + File.separator + PATTERNS_DIRECTORY_NAME);
    if (!patternsDirectoryResourcesPath.exists() && !patternsDirectoryResourcesPath.mkdir()) {
        showNoPatternDirectoryDialog(patternsDirectoryResourcesPath);
    }

    if (!patternsDirectoryResourcesPath.canWrite()) {
      showNoPatternDirectoryDialog(patternsDirectoryResourcesPath);
    }

    return patternsDirectoryResourcesPath;
  }

  private void createProjectHomeDirectory(File projectHomeDirectory) {
    if (!projectHomeDirectory.exists() && !projectHomeDirectory.mkdir()) {
        showNoHomeDirectoryDialog(projectHomeDirectory);
    }

    if (!projectHomeDirectory.canWrite()) {
      showNoHomeDirectoryDialog(projectHomeDirectory);
    }
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
      this.classpathResourceFiles.size() * (TILE_HEIGHT + TILE_PADDING) + VERTICAL_PADDING * 5 + VERTICAL_GAP_BETWEEN_BUTTONS);

    toolboxStage.setTitle(TOOLBOX_TITLE);
    toolboxStage.setOnCloseRequest(windowEvent -> {
      logger.info("You shall not close the toolbox window directly!");
      windowEvent.consume();
    });
    toolboxStage.setX(TOOLBOX_WINDOW_X);
    toolboxStage.setY(MAIN_WINDOW_Y);
    toolboxStage.setScene(toolboxScene);
    toolboxStage.show();
  }

  public TilePane createToolboxButtons(App app) {
    TilePane buttonsPane = buildButtonPane();
    buildFileButtons(app, buttonsPane);
    buildEditButtons(app, buttonsPane);
    buildShowHideGridButton(app, buttonsPane);
    buildQuitButton(buttonsPane);

    return buttonsPane;
  }

  private void buildQuitButton(TilePane buttonsPane) {
    QuitButton showQuitButton = new QuitButton(QUIT_APP_BUTTON_NAME);
    showQuitButton.setTranslateY(QUIT_BUTTON_PADDING);
    buttonsPane.getChildren().add(showQuitButton);
  }

  private void buildShowHideGridButton(App app, TilePane buttonsPane) {
    ShowHideGridButton  showHideGridButton  = new ShowHideGridButton  (SHOW_HIDE_GRID_BUTTON_NAME, app);
    buttonsPane.getChildren().add(showHideGridButton);
  }

  private void buildEditButtons(App app, TilePane buttonsPane) {
    UndoKnotButton      undoKnotButton      = new UndoKnotButton      (UNDO_KNOT_BUTTON_NAME, app);
    RedoKnotButton      redoKnotButton      = new RedoKnotButton      (REDO_KNOT_BUTTON_NAME, app);
    ResetDiagramButton  resetDiagramButton  = new ResetDiagramButton    (RESET_DIAGRAM_BUTTON_NAME, app);
    buttonsPane.getChildren().addAll(undoKnotButton, redoKnotButton, resetDiagramButton);
  }

  private void buildFileButtons(App app, TilePane buttonsPane) {
    SaveButton          saveButton          = new SaveButton          (app, SAVE_FILE_BUTTON_NAME);
    SaveAsButton        saveAsButton        = new SaveAsButton        (app, SAVE_FILE_AS_BUTTON_NAME);
    LoadButton          loadButton          = new LoadButton          (app, LOAD_BUTTON_NAME);
    buttonsPane.getChildren().addAll(saveButton, saveAsButton, loadButton);
  }

  private TilePane buildButtonPane() {
    TilePane buttonsPane  = new TilePane(Orientation.HORIZONTAL);
    buttonsPane.setAlignment(Pos.BOTTOM_CENTER);
    buttonsPane.setPrefColumns(1);
    buttonsPane.setVgap(VERTICAL_GAP_BETWEEN_BUTTONS);
    return buttonsPane;
  }

  private void showNoHomeDirectoryDialog(final File directory) {
    showErrorDialog("The following folder: '" + directory.getAbsolutePath() + "' shall be used as an " + ADA_LOVES_LACE + " home folder and it is either non-existent either non-writable!");
  }

  private void showNoPatternDirectoryDialog(final File directory) {
    showErrorDialog("The following folder: '" + directory.getAbsolutePath() + "' shall be used for storing pattern images and it is either non-existent either non-writable!");
  }

  private void showEmptyPatternDirectoryDialog(final File directory) {
    showErrorDialog("The following folder: '" + directory.getAbsolutePath() + "' shall be used for storing pattern images and it is empty!");
  }

  private void showErrorDialog(String text) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(ADA_LOVES_LACE);
    alert.setHeaderText(ERROR);
    alert.setContentText(text);

    alert.showAndWait();
  }

  public List<String> getClasspathResourceFiles() {
    return new ArrayList<>(this.classpathResourceFiles);
  }

}

package org.alienlabs.adaloveslace.view.window;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Diagram;
import org.alienlabs.adaloveslace.util.FileUtil;
import org.alienlabs.adaloveslace.util.PrintUtil;
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
import static org.alienlabs.adaloveslace.view.component.button.toolboxwindow.CreatePatternButton.CREATE_PATTERN_BUTTON;
import static org.alienlabs.adaloveslace.view.component.button.toolboxwindow.ExportPdfButton.EXPORT_PDF_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.component.button.toolboxwindow.ShareButton.SHARE_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.component.button.toolboxwindow.ShowHideGridButton.SHOW_HIDE_GRID_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.window.MainWindow.*;

public class ToolboxWindow {

  public static final double TOOLBOX_WINDOW_X             = 600d;
  public static final double TOOLBOX_WINDOW_WIDTH         = 550d;
  public static final double WINDOW_SPACING               = 60d;

  public static final String THE_FOLLOWING_FOLDER_STRING  = "The following folder: '";

  private List<String>        classpathResourceFiles;

  private UndoKnotButton      undoKnotButton;
  private RedoKnotButton      redoKnotButton;
  private ResetDiagramButton  resetDiagramButton;
  private ToggleButton        snowflakeButton;
  private final List<ToggleButton>  allPatterns;

  private static final Logger logger = LoggerFactory.getLogger(ToolboxWindow.class);
  private TextArea printersTextArea;
  private Stage toolboxStage;
  private CreatePatternButton createPatternButton;

  public ToolboxWindow() {
    this.allPatterns = new ArrayList<>();
  }

  public Diagram createToolboxPane(GridPane parent, Object classpathBase, String resourcesPath, App app, final Diagram diagram) {
    this.classpathResourceFiles = loadPatternsResourcesFiles(resourcesPath, classpathBase);

    if (classpathBase.equals(app)) {
      createProjectHomeDirectory(new File(System.getProperty(USER_HOME) + File.separator + PROJECT_NAME));
      File patternsDirectoryResourcesPath = createPatternDirectory();
      managePatternResourceFiles(patternsDirectoryResourcesPath);
    }

    for (int i = 0; i < this.classpathResourceFiles.size(); i++) {
      // i % 2 = 2 columns
      // i / 2 = as many rows as necessary
      parent.add(buildPatternButton(app, diagram, i), i % 2, i / 2);
    }


    return diagram;
  }

  // Add a Pattern button to the toolbox for each image present in the home pattern folder
  private ToggleButton buildPatternButton(App app, Diagram diagram, int buttonIndex) {
    String filename = this.classpathResourceFiles.get(buttonIndex);
    File file = new File(filename);

    if (file.exists()) {
      String label = file.getName();

      try (FileInputStream fis = new FileInputStream(filename)) {
        return buildPatternButton(app, diagram, buttonIndex, filename, label, fis);
      } catch (IOException e) {
        logger.error("Exception reading toolbox file!", e);
      }
    }

    return null;
  }

  // The Pattern button itself
  private ToggleButton buildPatternButton(App app, Diagram diagram, int i, String filename, String label, FileInputStream fis) {
    org.alienlabs.adaloveslace.business.model.Pattern pattern = new org.alienlabs.adaloveslace.business.model.Pattern(filename);

    Image img = new Image(fis);

    pattern.setCenterX(img.getWidth() / 2);
    pattern.setCenterY(img.getHeight() / 2);
    pattern.setWidth(img.getWidth());
    pattern.setHeight(img.getHeight());

    ToggleButton button = new PatternButton(app, label, img, pattern);
    button.setId(TOOLBOX_BUTTON + (i + 1));
    this.allPatterns.add(button);

    if (pattern.getFilename().equals("snowflake_small.jpg")) {
      this.snowflakeButton = button;
      button.setStyle("-fx-border-color: blue;");
    }

    if (i == 0) {
      diagram.setCurrentPattern(pattern);
    }

    diagram.addPattern(pattern);

    return button;
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
    File patternsDirectoryResourcesPath = new File(System.getProperty(USER_HOME) + File.separator + PROJECT_NAME + File.separator + PATTERNS_DIRECTORY_NAME);
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

  public void createToolboxStage(Pane root, Stage toolboxStage, GridPane parent, App app, int posY) {
    this.toolboxStage = toolboxStage;
    buildPrintButtons(app, parent, posY);

    Scene toolboxScene = new Scene(root);
    toolboxStage.setX(TOOLBOX_WINDOW_X);
//    this.toolboxStage.setX(app.getPrimaryStage().getX() + app.getPrimaryStage().getWidth() + WINDOW_SPACING);
    this.toolboxStage.setY(MAIN_WINDOW_Y);
    this.toolboxStage.setWidth(TOOLBOX_WINDOW_WIDTH);
    this.toolboxStage.setHeight(computeWindowHeight(app));
    this.toolboxStage.setScene(toolboxScene);
    this.toolboxStage.show();

    this.toolboxStage.setTitle(resourceBundle.getString(TOOLBOX_TITLE));
    this.toolboxStage.setOnCloseRequest(windowEvent -> {
      logger.info("You shall not close the toolbox window directly!");
    });
  }

  private int computeWindowHeight(App app) {
    if (app.getDiagram().getPatterns().isEmpty()) {
      return 900;
    } else {
      if (app.getDiagram().getPatterns().size() > 12) {
        return 900;
      }

      return app.getDiagram().getPatterns().size() * 60;
    }
  }

  public void createToolboxButtons(GridPane parent, App app, int posY) {
    buildFileButtons(app, parent, posY);
    buildEditButtons(app, parent, posY);
    buildShowHideGridButton(app, parent, posY);
    buildQuitButton(parent, posY);
  }

  /** Print diagram buttons.
   *
   */
  public void buildPrintButtons(App  app, GridPane parent, int posY) {
    printersTextArea = new TextArea();
    printersTextArea.setPrefColumnCount(8);

    Button getPrintersButton    = new Button(resourceBundle.getString(GET_PRINTERS_BUTTON_NAME));
    final Tooltip tooltip       = new Tooltip();
    tooltip.setText("Please click here first before printing");
    getPrintersButton.setTooltip(tooltip);

    Button printButton          = new Button(resourceBundle.getString(PRINT_BUTTON_NAME));
    final Tooltip tooltip2      = new Tooltip();
    tooltip2.setText("Please click here secondly in order to print");
    printButton.setTooltip(tooltip2);

    parent.add(getPrintersButton,   0, posY + 7);
    parent.add(printButton,         1, posY + 7);
    parent.add(printersTextArea,    0, posY + 8);

    PrintUtil printer = new PrintUtil(app);
    printer.printersButtonOnAction(printersTextArea, getPrintersButton);
    printer.printButtonOnAction(printButton);
  }

  private void buildQuitButton(GridPane buttonsPane, int posY) {
    QuitButton showQuitButton = new QuitButton(resourceBundle.getString(QUIT_APP));
    buttonsPane.add(showQuitButton, 0, posY + 6);
  }

  private void buildShowHideGridButton(App app, GridPane buttonsPane, int posY) {
    buttonsPane.add(new ShowHideGridButton  (resourceBundle.getString(SHOW_HIDE_GRID_BUTTON_NAME), app), 1, posY + 5);
  }

  private void buildEditButtons(App app, GridPane buttonsPane, int posY) {
    this.undoKnotButton           = new UndoKnotButton      (resourceBundle.getString(UNDO_KNOT), app);
    this.redoKnotButton           = new RedoKnotButton      (resourceBundle.getString(REDO_KNOT), app);
    this.createPatternButton      = new CreatePatternButton (resourceBundle.getString(CREATE_PATTERN_BUTTON), app);
    this.resetDiagramButton       = new ResetDiagramButton  (resourceBundle.getString(RESET_DIAGRAM), app);
    buttonsPane.add(this.undoKnotButton,      0, posY + 3);
    buttonsPane.add(this.redoKnotButton,      1, posY + 3);
    buttonsPane.add(this.createPatternButton, 0, posY + 4);
    buttonsPane.add(this.resetDiagramButton,  0, posY + 5);
  }

  private void buildFileButtons(App app, GridPane buttonsPane, int posY) {
    SaveButton          saveButton          = new SaveButton          (app, resourceBundle.getString(SAVE_FILE));
    SaveAsButton        saveAsButton        = new SaveAsButton        (app, resourceBundle.getString(SAVE_FILE_AS));
    LoadButton          loadButton          = new LoadButton          (app, resourceBundle.getString(LOAD_FILE));
    ShareButton         shareButton         = new ShareButton         (app, resourceBundle.getString(SHARE_BUTTON_NAME));
    ExportImageButton   exportImageButton   = new ExportImageButton   (app, resourceBundle.getString(EXPORT_IMAGE));
    ExportPdfButton     exportPdfButton     = new ExportPdfButton     (app, resourceBundle.getString(EXPORT_PDF_BUTTON_NAME));

    buttonsPane.add(saveButton, 0, posY);
    buttonsPane.add(saveAsButton, 1, posY);
    buttonsPane.add(loadButton, 0, posY + 1);
    buttonsPane.add(shareButton, 1, posY + 1);
    buttonsPane.add(exportImageButton, 0, posY + 2);
    buttonsPane.add(exportPdfButton, 1, posY + 2);
  }

  private void showNoHomeDirectoryDialog(final File directory) {
    showErrorDialog(THE_FOLLOWING_FOLDER_STRING + directory.getAbsolutePath() + "' shall be used as an " + ADA_LOVES_LACE + " home folder and it is either non-existent either non-writable!");
  }

  private void showNoPatternDirectoryDialog(final File directory) {
    showErrorDialog(THE_FOLLOWING_FOLDER_STRING + directory.getAbsolutePath() + "' shall be used for storing pattern images and it is either non-existent either non-writable!");
  }

  private void showEmptyPatternDirectoryDialog(final File directory) {
    showErrorDialog(THE_FOLLOWING_FOLDER_STRING + directory.getAbsolutePath() + "' shall be used for storing pattern images and it is empty!");
  }

  private void showErrorDialog(String text) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(ADA_LOVES_LACE);
    alert.setHeaderText(ERROR);
    alert.setContentText(text);

    alert.showAndWait();
  }

  public UndoKnotButton getUndoKnotButton() {
    return this.undoKnotButton;
  }

  public RedoKnotButton getRedoKnotButton() {
    return this.redoKnotButton;
  }

  public ResetDiagramButton getResetDiagramButton() {
    return this.resetDiagramButton;
  }

  public TextArea getPrintersTextArea() {
    return printersTextArea;
  }

  public List<ToggleButton> getAllPatterns() {
    return this.allPatterns;
  }

  public ToggleButton getSnowflakeButton() {
    return this.snowflakeButton;
  }

  public Stage getToolboxStage() {
    return toolboxStage;
  }

}

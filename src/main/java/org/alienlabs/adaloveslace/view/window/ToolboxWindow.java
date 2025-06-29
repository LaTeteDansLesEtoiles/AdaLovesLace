package org.alienlabs.adaloveslace.view.window;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.Diagram;
import org.alienlabs.adaloveslace.util.FileUtil;
import org.alienlabs.adaloveslace.util.PrintUtil;
import org.alienlabs.adaloveslace.view.component.PrintersListView;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.QuitButton;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.ShareButton;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.file.*;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid.*;
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
import static org.alienlabs.adaloveslace.view.component.button.toolboxwindow.ShareButton.SHARE_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.component.button.toolboxwindow.file.ExportPdfButton.EXPORT_PDF_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid.CreatePatternButton.CREATE_PATTERN_BUTTON;
import static org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid.ShowHideGridButton.SHOW_HIDE_GRID_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.window.MainWindow.*;

public class ToolboxWindow {

    public static final double DEFAULT_TOOLBOX_WINDOW_X         = 600d;
    public static final double DEFAULT_TOOLBOX_WINDOW_Y         = DEFAULT_MAIN_WINDOW_Y;
    public static final double DEFAULT_TOOLBOX_WINDOW_WIDTH     = 550d;
    public static final double MENU_BAR_Y                       = 0d;

    public static final String THE_FOLLOWING_FOLDER_STRING      = "The following folder: '";
    public static final String BUTTON_SELECTED                  = "button-selected";
    public static final String BUTTON_WAITING_SELECTION         = "button-waiting-selection";
    public static final String PATTERN_TEXT_AND_COLOR_BUTTON    = "pattern-text-and-color-button";

    private List<String> classpathResourceFiles;

    private UndoKnotButton undoKnotButton;
    private ToggleButton snowflakeButton;
    private ToggleButton colorWheelButton;
    private final List<ToggleButton> allPatterns;
    private MenuBar menuBar;

    private Stage toolboxStage;
    private TextButton textButton;
    private AddKnotButton addKnotButton;
    private ResetKnotsButton resetKnotSButton;
    private ColorButton colorButton;
    private BackInBlackButton backInBlackButton;

    private static App app;

    private static final Logger logger = LoggerFactory.getLogger(ToolboxWindow.class);

    public ToolboxWindow() {
        this.allPatterns = new ArrayList<>();
    }

    public Diagram createToolboxPane(GridPane parent, Object classpathBase, String resourcesPath, App app, final Diagram diagram) {
        ToolboxWindow.app = app;
        this.classpathResourceFiles = loadPatternsResourcesFiles(resourcesPath, classpathBase);

        if (classpathBase.equals(app)) {
            createProjectHomeDirectory(new File(System.getProperty(USER_HOME) + File.separator + PROJECT_NAME));
            File patternsDirectoryResourcesPath = createPatternDirectory();
            managePatternResourceFiles(patternsDirectoryResourcesPath);
        }

        for (int i = 0; i < this.classpathResourceFiles.size(); i++) {
            // i % 2 = 2 columns
            // i / 2 = as many rows as necessary
            parent.add(buildPatternButton(app, diagram, i), i % 2, (i + 2) / 2);
        }

        this.textButton = new TextButton(app);
        this.addKnotButton = new AddKnotButton(app);
        this.resetKnotSButton = new ResetKnotsButton(app);
        this.colorButton = new ColorButton(app);
        this.backInBlackButton = new BackInBlackButton(app);
        parent.add(
                this.textButton,
                0,
                (int)Math.floor((double) (this.classpathResourceFiles.size() + 3) / 2)
        );
        parent.add(
                this.addKnotButton,
                0,
                (int)Math.floor((double) (this.classpathResourceFiles.size() + 5) / 2)
        );
        parent.add(
                this.resetKnotSButton,
                1,
                (int)Math.floor((double) (this.classpathResourceFiles.size() + 5) / 2)
        );
        parent.add(
                this.colorButton,
                0,
                (int)Math.floor((double) (this.classpathResourceFiles.size() + 7) / 2)
        );
        parent.add(
                this.backInBlackButton,
                1,
                (int)Math.floor((double) (this.classpathResourceFiles.size() + 7) / 2)
        );

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
        org.alienlabs.adaloveslace.domain.Pattern pattern = new org.alienlabs.adaloveslace.domain.Pattern(filename);

        Image img = new Image(fis);

        if (diagram.getCurrentPattern() == null) {
            diagram.setCurrentPattern(pattern);
        }

        pattern.setCenterX(img.getWidth() / 2);
        pattern.setCenterY(img.getHeight() / 2);
        pattern.setWidth(img.getWidth());
        pattern.setHeight(img.getHeight());

        ToggleButton button = new PatternButton(app, label, img, pattern);
        button.setId(TOOLBOX_BUTTON + (i + 1));
        this.allPatterns.add(button);

        if (i == 0) {
            this.colorWheelButton = button;
            button.getStyleClass().add(BUTTON_SELECTED);
            diagram.setCurrentPattern(pattern);
            app.getOptionalDotGrid().getCurrentPatternProperty().set(pattern);
        }

        if (i == 1) {
            this.snowflakeButton = button;
        }

        diagram.addPattern(pattern);
        return button;
    }

    private void managePatternResourceFiles(File patternsDirectoryResourcesPath) {
        List<String> homeDirectoryResourceFiles;
        homeDirectoryResourceFiles = loadPatternsFolderResourcesFiles(HOME_DIRECTORY_RESOURCES_PATH,
                patternsDirectoryResourcesPath);

        if (homeDirectoryResourceFiles == null || homeDirectoryResourceFiles.isEmpty()) {
            // Nothing to do here
        } else {
            // We don't add duplicated resources to our toolbox buttons (i.e., filename must be different in both
            // classpathResourceFiles & homeDirectoryResourceFiles
            this.classpathResourceFiles.addAll(
                    getAllResourceFilesWithoutDuplicates(homeDirectoryResourceFiles));
        }
    }

    // We don't add duplicated resources to our toolbox buttons (i.e., filename must be different in both
    // classpathResourceFiles & homeDirectoryResourceFiles
    private List<String> getAllResourceFilesWithoutDuplicates(List<String> homeDirectoryResourceFiles) {
        return homeDirectoryResourceFiles.stream().filter(patternDirectoryResource -> classpathResourceFiles.stream().noneMatch(
                classpathResource -> patternDirectoryResource.split(String.valueOf(File.separatorChar))
                        [patternDirectoryResource.split((String.valueOf(File.separatorChar))).length - 1]
                        .equals(classpathResource.split((String.valueOf(File.separatorChar)))
                                [classpathResource.split((String.valueOf(File.separatorChar))).length - 1]))).toList();
    }

    private File createPatternDirectory() {
        File patternsDirectoryResourcesPath = new File(System.getProperty(USER_HOME) + File.separator + PROJECT_NAME + File.separator + PATTERNS_DIRECTORY_NAME);
        if (!patternsDirectoryResourcesPath.exists() && !patternsDirectoryResourcesPath.mkdir()) {
            // Nothing to do here
        }

        if (!patternsDirectoryResourcesPath.canWrite()) {
            // Nothing to do here
        }

        return patternsDirectoryResourcesPath;
    }

    private void createProjectHomeDirectory(File projectHomeDirectory) {
        if (!projectHomeDirectory.exists() && !projectHomeDirectory.mkdir()) {
            // Nothing to do here
        }

        if (!projectHomeDirectory.canWrite()) {
            // Nothing to do here
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

    public void createToolboxStage(Stage toolboxStage, GridPane parent, App app, int posY) {
        this.toolboxStage = toolboxStage;

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(parent);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        VBox root = new VBox(scrollPane);
        root.setPrefSize(app.getResizes().getToolboxWindowWidth(), computeWindowHeight(app));
        root.getStyleClass().add("toolbox");

        buildPrintButtons(app, parent, posY);

        Scene toolboxScene = new Scene(root);
        toolboxScene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

        toolboxStage.setX(app.getWindowRepositionEvents().getToolboxWindowX());
        toolboxStage.setY(app.getWindowRepositionEvents().getToolboxWindowY());
        toolboxStage.setWidth(app.getResizes().getToolboxWindowWidth());
        toolboxStage.setHeight(computeWindowHeight(app));
        toolboxStage.setScene(toolboxScene);
        toolboxStage.setTitle(resourceBundle.getString(TOOLBOX_TITLE));

        toolboxStage.setOnCloseRequest(windowEvent -> {
            logger.debug("You shall not close the toolbox window directly!");
            windowEvent.consume();
        });

        toolboxStage.show();
    }

    private int computeWindowHeight(App app) {
        if (app.getResizes().getToolboxWindowHeight() != 0d) {
            return (int) app.getResizes().getToolboxWindowHeight();
        }
        if (app.getOptionalDotGrid().getDiagram().getPatterns().isEmpty()) {
            return 900;
        } else {
            if (app.getOptionalDotGrid().getDiagram().getPatterns().size() > 12) {
                return 900;
            }

            return app.getOptionalDotGrid().getDiagram().getPatterns().size() * 200;
        }
    }

    public void createToolboxButtons(GridPane parent, App app, int posY) {
        buildFileButtons(app, parent, posY);
        buildEditButtons(app, parent, posY);
        buildShowHideGridButton(app, parent, posY);
        buildQuitButton(parent, posY);
    }

    /**
     * Print diagram buttons.
     */
    public void buildPrintButtons(App app, GridPane parent, int posY) {
        Button getPrintersButton = new Button(resourceBundle.getString(GET_PRINTERS_BUTTON_NAME));
        final Tooltip tooltip = new Tooltip();
        tooltip.setText(resourceBundle.getString("GET_PRINTERS_BUTTON_TOOLTIP"));
        tooltip.setShowDuration(TOOLTIPS_DURATION);
        getPrintersButton.setTooltip(tooltip);

        Button printButton = new Button(resourceBundle.getString(PRINT_BUTTON_NAME));
        final Tooltip tooltip2 = new Tooltip();
        tooltip2.setText(resourceBundle.getString("PRINT_BUTTON_TOOLTIP"));
        tooltip2.setShowDuration(TOOLTIPS_DURATION);
        printButton.setTooltip(tooltip2);

        parent.add(getPrintersButton, 0, posY + 7);
        parent.add(printButton, 1, posY + 7);
        PrintersListView listView = new PrintersListView();
        parent.add(listView, 0, posY + 8);

        PrintUtil printer = new PrintUtil(app);
        printer.printersButtonOnAction(listView, getPrintersButton);
        printer.printButtonOnAction(printButton);
    }

    private void buildQuitButton(GridPane buttonsPane, int posY) {
        QuitButton showQuitButton = new QuitButton(app, resourceBundle.getString(QUIT_APP));
        buttonsPane.add(showQuitButton, 0, posY + 6);
    }

    private void buildShowHideGridButton(App app, GridPane buttonsPane, int posY) {
        buttonsPane.add(new ShowHideGridButton(resourceBundle.getString(SHOW_HIDE_GRID_BUTTON_NAME), app), 1, posY + 5);
    }

    private void buildEditButtons(App app, GridPane buttonsPane, int posY) {
        RedoKnotButton redoKnotButton;
        ResetDiagramButton resetDiagramButton;
        this.undoKnotButton = new UndoKnotButton(resourceBundle.getString(UNDO_KNOT), app);
        redoKnotButton = new RedoKnotButton(resourceBundle.getString(REDO_KNOT), app);
        CreatePatternButton createPatternButton = new CreatePatternButton(resourceBundle.getString(CREATE_PATTERN_BUTTON), app);
        resetDiagramButton = new ResetDiagramButton(resourceBundle.getString(RESET_DIAGRAM), app);
        buttonsPane.add(this.undoKnotButton, 0, posY + 3);
        buttonsPane.add(redoKnotButton, 1, posY + 3);
        buttonsPane.add(createPatternButton, 0, posY + 4);
        buttonsPane.add(resetDiagramButton, 0, posY + 5);
    }

    private void buildFileButtons(App app, GridPane buttonsPane, int posY) {
        SaveButton saveButton = new SaveButton(app, resourceBundle.getString(SAVE_FILE));
        SaveAsButton saveAsButton = new SaveAsButton(app, resourceBundle.getString(SAVE_FILE_AS));
        LoadButton loadButton = new LoadButton(app, resourceBundle.getString(LOAD_FILE));
        ShareButton shareButton = new ShareButton(app, resourceBundle.getString(SHARE_BUTTON_NAME));
        ExportImageButton exportImageButton = new ExportImageButton(app, resourceBundle.getString(EXPORT_IMAGE));
        ExportPdfButton exportPdfButton = new ExportPdfButton(app, resourceBundle.getString(EXPORT_PDF_BUTTON_NAME));

        buttonsPane.add(saveButton, 0, posY);
        buttonsPane.add(saveAsButton, 1, posY);
        buttonsPane.add(loadButton, 0, posY + 1);
        buttonsPane.add(shareButton, 1, posY + 1);
        buttonsPane.add(exportImageButton, 0, posY + 2);
        buttonsPane.add(exportPdfButton, 1, posY + 2);
    }

    public UndoKnotButton getUndoKnotButton() {
        return this.undoKnotButton;
    }

    public List<ToggleButton> getAllPatterns() {
        return this.allPatterns;
    }

    public TextButton getTextButton() {
        return this.textButton;
    }

    public ColorButton getColorButton() {
        return this.colorButton;
    }

    public BackInBlackButton getBackInBlackButton() {
        return this.backInBlackButton;
    }

    public ToggleButton getSnowflakeButton() {
        return this.snowflakeButton;
    }

    public ToggleButton getColorWheelButton() {
        return this.colorWheelButton;
    }

    public Stage getToolboxStage() {
        return this.toolboxStage;
    }

    public static void restartApp() {
        app.getGeometryWindow().getGeometryStage().close();
        app.getToolboxWindow().getToolboxStage().close();
        app.getStateWindow().getStateStage().close();
        app.getPrimaryStage().close();
        app.start(new Stage());
    }

    public MenuBar getMenuBar() {
        return this.menuBar;
    }

}

package org.alienlabs.adaloveslace.view.window;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.Diagram;
import org.alienlabs.adaloveslace.util.FileUtil;
import org.alienlabs.adaloveslace.util.ImageUtil;
import org.alienlabs.adaloveslace.util.PrintUtil;
import org.alienlabs.adaloveslace.view.component.PrintersListView;
import org.alienlabs.adaloveslace.view.component.button.geometrywindow.*;
import org.alienlabs.adaloveslace.view.component.button.geometrywindow.move.*;
import org.alienlabs.adaloveslace.view.component.button.statewindow.InvisibleButton;
import org.alienlabs.adaloveslace.view.component.button.statewindow.SelectableButton;
import org.alienlabs.adaloveslace.view.component.button.statewindow.UnselectableButton;
import org.alienlabs.adaloveslace.view.component.button.statewindow.VisibleButton;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.QuitButton;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.ShareButton;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.file.*;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid.*;
import org.alienlabs.adaloveslace.view.component.spinner.RotationSpinner;
import org.alienlabs.adaloveslace.view.component.spinner.ZoomSpinner;
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
import static org.alienlabs.adaloveslace.domain.Knot.DEFAULT_ROTATION;
import static org.alienlabs.adaloveslace.domain.Knot.DEFAULT_ZOOM;
import static org.alienlabs.adaloveslace.util.FileUtil.HOME_DIRECTORY_RESOURCES_PATH;
import static org.alienlabs.adaloveslace.view.component.button.geometrywindow.DeletionButton.DELETION_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.component.button.geometrywindow.DuplicationButton.DUPLICATION_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.component.button.geometrywindow.HorizontalFlippingButton.HORIZONTAL_FLIPPING_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.component.button.geometrywindow.RotationButton.ROTATION_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.component.button.geometrywindow.SelectionButton.SELECTION_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.component.button.geometrywindow.VerticalFlippingButton.VERTICAL_FLIPPING_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.component.button.geometrywindow.ZoomButton.ZOOM_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.component.button.statewindow.InvisibleButton.INVISIBLE_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.component.button.statewindow.SelectableButton.SELECTABLE_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.component.button.statewindow.UnselectableButton.UNSELECTABLE_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.component.button.statewindow.VisibleButton.VISIBLE_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.component.button.toolboxwindow.ShareButton.SHARE_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.component.button.toolboxwindow.file.ExportPdfButton.EXPORT_PDF_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid.CreatePatternButton.CREATE_PATTERN_BUTTON;
import static org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid.ShowHideGridButton.SHOW_HIDE_GRID_BUTTON_NAME;
import static org.alienlabs.adaloveslace.view.window.MainWindow.*;

public class ToolboxWindow {

    public static final double DEFAULT_TOOLBOX_WINDOW_X         = DEFAULT_MAIN_WINDOW_X + DEFAULT_MAIN_WINDOW_WIDTH;
    public static final double DEFAULT_TOOLBOX_WINDOW_Y         = DEFAULT_MAIN_WINDOW_Y;
    public static final double DEFAULT_TOOLBOX_WINDOW_WIDTH     = 1100d;
    public static final double MENU_BAR_Y                       = 0d;
    public static final int MAX_PATTERNS_WITHOUT_SCROLL         = 14; // 7 rows × 2 columns = 14 patterns max
    
    // Constants for StateWindow buttons (previously in StateWindow.java)
    public static final double STATE_BUTTONS_HEIGHT = 30d;
    
    // Constants for GeometryWindow buttons (previously in GeometryWindow.java)
    public static final double GEOMETRY_BUTTONS_HEIGHT = 30d;

    public static final String THE_FOLLOWING_FOLDER_STRING      = "The following folder: '";
    public static final String BUTTON_SELECTED                  = "button-selected";
    public static final String BUTTON_WAITING_SELECTION         = "button-waiting-selection";
    public static final String PATTERN_TEXT_AND_COLOR_BUTTON    = "pattern-text-and-color-button";

    // Constants for GeometryWindow
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
    public static final double MIN_TOOLBOX_WIDTH_FOR_SCROLL       = 700d;

    private List<String> classpathResourceFiles;

    private UndoKnotButton undoKnotButton;
    private ToggleButton snowflakeButton;
    private ToggleButton colorWheelButton;
    private final List<ToggleButton> allPatterns;
    private MenuBar menuBar;

    private Stage toolboxStage;
    private TextButton textButton;
    private AddKnotButton addKnotButton;
    private ResetAllButton resetAllButton;
    private ColorButton colorButton;
    private BackInBlackButton backInBlackButton;

    // Attributs de GeometryWindow
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

    private static App app;

    private static final Logger logger = LoggerFactory.getLogger(ToolboxWindow.class);
    private Label gridNameLabel;

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

        // Patterns are now handled in createToolboxStage with a ScrollPane
        // No need to add patterns here
        this.textButton = new TextButton(app);
        this.addKnotButton = new AddKnotButton(app);
        this.resetAllButton = new ResetAllButton(app);
        this.colorButton = new ColorButton(app);
        this.backInBlackButton = new BackInBlackButton(app);
        
        // Buttons are now added in createToolboxStage with a separate GridPane
        // Geometry buttons and arrows are now handled in createToolboxStage

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
            this.snowflakeButton.setId("snowflakeButton");
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

    public void createToolboxStage(
            Stage toolboxStage,
            MenuBar menuBar,
            GridPane parent,
            App app,
            Diagram diagram
    ) {
        this.toolboxStage = toolboxStage;

        // Create a separate GridPane for patterns with scrolling
        GridPane patternsPane = new GridPane();
        patternsPane.setVgap(5);
        patternsPane.setHgap(5);
        patternsPane.setPadding(new Insets(5));
        
        // Add patterns to the patterns GridPane
        for (int i = 0; i < this.classpathResourceFiles.size(); i++) {
            patternsPane.add(buildPatternButton(app, diagram, i), i % 2, i / 2);
        }
        
        // Create a ScrollPane specifically for patterns
        ScrollPane patternsScrollPane = new ScrollPane();
        patternsScrollPane.setContent(patternsPane);
        patternsScrollPane.setFitToWidth(true);
        patternsScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        patternsScrollPane.setMinWidth(450); // Largeur minimale pour les patterns
        patternsScrollPane.setPrefWidth(450);
        
        // Enable vertical scrollbar if more than 14 patterns
        if (this.classpathResourceFiles.size() > MAX_PATTERNS_WITHOUT_SCROLL) {
            patternsScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        } else {
            patternsScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        }
        
        // Limit the ScrollPane height so it takes only the needed space
        if (this.classpathResourceFiles.size() > MAX_PATTERNS_WITHOUT_SCROLL) {
            patternsScrollPane.setMaxHeight(400); // Height for 7 rows of patterns
            patternsScrollPane.setMinHeight(400); // Minimum height to ensure visibility
        } else {
            // If fewer than 14 patterns, compute the exact required height
            int patternsRows = (int) Math.ceil((double) this.classpathResourceFiles.size() / 2);
            double exactHeight = patternsRows * 60d + 20d; // 60px per row + padding
            patternsScrollPane.setMaxHeight(exactHeight);
            patternsScrollPane.setMinHeight(exactHeight);
        }

        // Create a horizontal HBox to place the patterns ScrollPane and geometry buttons side by side
        HBox topRow = new HBox();
        topRow.setSpacing(10);
        topRow.getChildren().add(patternsScrollPane);
        
        // Create a temporary GridPane to initialize geometry buttons
        GridPane tempGrid = new GridPane();
        createGeometryButtons(app, tempGrid, 0);
        
        // Create the missing rotation and zoom buttons
        ImageUtil util = new ImageUtil(app);
        RotationButton rotationButton = new RotationButton(resourceBundle.getString(ROTATION_BUTTON_NAME));
        util.getImageView("rotation.png", rotationButton, false);
        
        ZoomButton zoomButton = new ZoomButton(resourceBundle.getString(ZOOM_BUTTON_NAME));
        util.getImageView("zoom.png", zoomButton, false);
        
        // Create a GridPane for geometry buttons (2 columns)
        GridPane geometryGrid = new GridPane();
        geometryGrid.setVgap(5);
        geometryGrid.setHgap(5);
        geometryGrid.setPadding(new Insets(10, 5, 5, 5)); // More space at top (10px instead of 5px)
        geometryGrid.setAlignment(Pos.TOP_CENTER); // Align top so spinners share the same Y level
        
        // Add geometry buttons to the GridPane (2 columns)
        // Row 0: rotation and zoom spinners
        if (this.rotationSpinner1 != null) {
            geometryGrid.add(this.rotationSpinner1, 0, 0);
            GridPane.setHalignment(this.rotationSpinner1, HPos.CENTER);
        }
        if (this.zoomSpinner1 != null) {
            geometryGrid.add(this.zoomSpinner1, 1, 0);
            GridPane.setHalignment(this.zoomSpinner1, HPos.CENTER);
        }
        
        // Row 1: rotation and zoom spinners
        if (this.rotationSpinner2 != null) {
            geometryGrid.add(this.rotationSpinner2, 0, 1);
            GridPane.setHalignment(this.rotationSpinner2, HPos.CENTER);
        }
        if (this.zoomSpinner2 != null) {
            geometryGrid.add(this.zoomSpinner2, 1, 1);
            GridPane.setHalignment(this.zoomSpinner2, HPos.CENTER);
        }
        
        // Row 2: rotation and zoom spinners
        if (this.rotationSpinner3 != null) {
            geometryGrid.add(this.rotationSpinner3, 0, 2);
            GridPane.setHalignment(this.rotationSpinner3, HPos.CENTER);
        }
        if (this.zoomSpinner3 != null) {
            geometryGrid.add(this.zoomSpinner3, 1, 2);
            GridPane.setHalignment(this.zoomSpinner3, HPos.CENTER);
        }
        
        // Row 3: rotation and zoom buttons
        geometryGrid.add(rotationButton, 0, 3);
        GridPane.setHalignment(rotationButton, HPos.CENTER);
        geometryGrid.add(zoomButton, 1, 3);
        GridPane.setHalignment(zoomButton, HPos.CENTER);
        
        if (this.drawingButton != null) {
            geometryGrid.add(this.drawingButton, 0, 7);
            GridPane.setHalignment(this.drawingButton, HPos.CENTER);
        }
        if (this.selectionButton != null) {
            geometryGrid.add(this.selectionButton, 1, 7);
            GridPane.setHalignment(this.selectionButton, HPos.CENTER);
        }
        
        if (this.deletionButton != null) {
            geometryGrid.add(this.deletionButton, 0, 8);
            GridPane.setHalignment(this.deletionButton, HPos.CENTER);
        }
        if (this.duplicationButton != null) {
            geometryGrid.add(this.duplicationButton, 1, 8);
            GridPane.setHalignment(this.duplicationButton, HPos.CENTER);
        }
        
        // Add flipping buttons with 2 extra rows of spacing
        VerticalFlippingButton verticalFlippingButton = new VerticalFlippingButton(app, resourceBundle.getString(VERTICAL_FLIPPING_BUTTON_NAME));
        util.getImageView("flip_vertically.png", verticalFlippingButton, false);
        geometryGrid.add(verticalFlippingButton, 0, 12); // Row 12 to have 2 extra spacer rows
        GridPane.setHalignment(verticalFlippingButton, HPos.CENTER);
        
        HorizontalFlippingButton horizontalFlippingButton = new HorizontalFlippingButton(app, resourceBundle.getString(HORIZONTAL_FLIPPING_BUTTON_NAME));
        util.getImageView("flip_horizontally.png", horizontalFlippingButton, false);
        geometryGrid.add(horizontalFlippingButton, 1, 12); // Row 12 to have 2 extra spacer rows
        GridPane.setHalignment(horizontalFlippingButton, HPos.CENTER);
        
        // Add the HBox to the main GridPane
        parent.add(topRow, 0, 0, 4, 1);
        
        // Create a separate GridPane for first-column buttons with margin
        GridPane buttonsGrid = new GridPane();
        buttonsGrid.setPadding(new Insets(20, 0, 10, 10)); // Reduced right padding to shift the 2nd sub-column
        buttonsGrid.setVgap(10); // Larger vertical spacing
        buttonsGrid.setHgap(10); // Larger horizontal spacing
        
        // Add buttons directly to the main GridPane (no inner margin)
        // The margin will be managed by mainContainer
        buttonsGrid.add(this.textButton, 0, 0);
        buttonsGrid.add(this.addKnotButton, 0, 1);
        buttonsGrid.add(this.resetAllButton, 1, 1);
        buttonsGrid.add(this.colorButton, 0, 2);
        buttonsGrid.add(this.backInBlackButton, 1, 2);
        
        // Add save and print buttons
        buildFileButtons(app, buttonsGrid);
        buildPrintButtons(app, buttonsGrid);
        
        // Ajouter le buttonsGrid au GridPane principal
        parent.add(buttonsGrid, 0, 1, 2, 1); // Colonnes 0-1, ligne 1
        
        // Create a separate GridPane for arrows with even spacing
        GridPane arrowsGrid = new GridPane();
        arrowsGrid.setVgap(5);
        arrowsGrid.setHgap(5);
        arrowsGrid.setPadding(new Insets(5));
        arrowsGrid.setAlignment(Pos.CENTER); // Center arrows within their column
        
        // Add arrows to the separate GridPane (3x3 square)
        // Top row: UpLeft, Up, UpRight
        UpLeftButton upLeftButton = new UpLeftButton(app);
        util.getImageView("up_left.png", upLeftButton, false);
        arrowsGrid.add(upLeftButton, 0, 0);

        UpButton upButton = new UpButton(app);
        util.getImageView("up.png", upButton, false);
        arrowsGrid.add(upButton, 1, 0);

        UpRightButton upRightButton = new UpRightButton(app);
        util.getImageView("up_right.png", upRightButton, false);
        arrowsGrid.add(upRightButton, 2, 0);

        // Middle row: Left, Fast, Right
        LeftButton leftButton = new LeftButton(app);
        util.getImageView("left.png", leftButton, false);
        arrowsGrid.add(leftButton, 0, 1);

        FastMoveModeButton fastMoveModeButton = new FastMoveModeButton();
        util.getImageView("fast.png", fastMoveModeButton, false);
        arrowsGrid.add(fastMoveModeButton, 1, 1);

        RightButton rightButton = new RightButton(app);
        util.getImageView("right.png", rightButton, false);
        arrowsGrid.add(rightButton, 2, 1);

        // Bottom row: DownLeft, Down, DownRight
        DownLeftButton downLeftButton = new DownLeftButton(app);
        util.getImageView("down_left.png", downLeftButton, false);
        arrowsGrid.add(downLeftButton, 0, 2);

        DownButton downButton = new DownButton(app);
        util.getImageView("down.png", downButton, false);
        arrowsGrid.add(downButton, 1, 2);

        DownRightButton downRightButton = new DownRightButton(app);
        util.getImageView("down_right.png", downRightButton, false);
        arrowsGrid.add(downRightButton, 2, 2);
        
        // Create a separate layout for arrows - do not add them to the main GridPane
        // The arrows will be added to the root VBox separately
        
        // Base buttons are now in the buttonsGrid GridPane with margin

        // Create columns with dynamic margins that can grow
        HBox mainContainer = new HBox();
        mainContainer.setSpacing(0); // No fixed spacing
        mainContainer.setAlignment(Pos.TOP_LEFT);
        
        // Column 1: patterns + buttons with maximum width for text
        VBox leftColumn = new VBox();
        leftColumn.setMinWidth(700); // Larger width
        leftColumn.setPrefWidth(700);
        leftColumn.setMaxWidth(800); // Maximum limit
        leftColumn.setSpacing(15); // Vertical spacing between elements
        leftColumn.setPadding(new Insets(0, 5, 0, 0)); // Very small right padding
        leftColumn.getChildren().add(parent);
        mainContainer.getChildren().add(leftColumn);
        
        // Flexible margin that grows dynamically but stays within the viewport
        Region spacer1 = new Region();
        spacer1.setMinWidth(0); // Normal margin
        spacer1.setPrefWidth(Region.USE_COMPUTED_SIZE);
        spacer1.setMaxWidth(50); // Normal maximum limit
        HBox.setHgrow(spacer1, Priority.NEVER); // No expansion
        mainContainer.getChildren().add(spacer1);
        
        // Column 2: spinners + geometry buttons shifted 50px to the left
        VBox centerColumn = new VBox();
        centerColumn.setAlignment(Pos.TOP_CENTER);
        centerColumn.setMinWidth(350); // Increased minimum width for labels
        centerColumn.setPrefWidth(350);
        centerColumn.setTranslateX(-135); // Shift 135px to the left
        centerColumn.getChildren().add(geometryGrid);
        mainContainer.getChildren().add(centerColumn);
        
        // Flexible margin that grows dynamically but stays within the viewport
        Region spacer2 = new Region();
        spacer2.setMinWidth(20); // Normal margin between columns 2 and 3
        spacer2.setPrefWidth(Region.USE_COMPUTED_SIZE);
        spacer2.setMaxWidth(100); // Normal maximum limit
        HBox.setHgrow(spacer2, Priority.SOMETIMES); // Normal expansion
        mainContainer.getChildren().add(spacer2);
        
        // Column 3: arrows + buttons
        VBox arrowsContainer = new VBox();
        arrowsContainer.setAlignment(Pos.TOP_CENTER);
        arrowsContainer.setSpacing(10);
        
        // Add a spacer to vertically align with patterns and spinners
        arrowsContainer.getChildren().add(new Region()); // Spacer to align with patterns
        
        // Add arrows
        arrowsContainer.getChildren().add(arrowsGrid);
        
        // Add buttons in order: arrows -> change grid -> grid name -> quit
        // Retrieve existing buttons from buildQuitAndGridNameButtons
        QuitButton showQuitButton = new QuitButton(app, resourceBundle.getString(QUIT_APP));
        this.gridNameLabel = new Label(resourceBundle.getString(app.getOptionalDotGrid().getDiagram().getCurrentGridType().name()));
        
        // Create the "change grid" button
        ShowHideGridButton changeGridButton = new ShowHideGridButton(resourceBundle.getString(SHOW_HIDE_GRID_BUTTON_NAME), app);
        
        // Create the StateWindow buttons
        UnselectableButton unselectableButton = new UnselectableButton(app, resourceBundle.getString(UNSELECTABLE_BUTTON_NAME));
        SelectableButton selectableButton = new SelectableButton(app, resourceBundle.getString(SELECTABLE_BUTTON_NAME));
        InvisibleButton invisibleButton = new InvisibleButton(app, resourceBundle.getString(INVISIBLE_BUTTON_NAME));
        VisibleButton visibleButton = new VisibleButton(app, resourceBundle.getString(VISIBLE_BUTTON_NAME));
        
        // Add buttons in the desired order
        arrowsContainer.getChildren().add(new Region());
        arrowsContainer.getChildren().add(changeGridButton); // Change grid button under the arrows
        arrowsContainer.getChildren().add(this.gridNameLabel); // Grid name under the change grid button
        
        // Add spacers to move the quit button down by one line
        arrowsContainer.getChildren().add(new Region()); // Spacer to move the quit button down
        
        arrowsContainer.getChildren().add(new Region()); // Espace 1
        arrowsContainer.getChildren().add(showQuitButton); // Quit button moved down by one line

        // Add spacers to move down by 5 lines in total
        arrowsContainer.getChildren().add(new Region()); // Spacer 1
        arrowsContainer.getChildren().add(new Region()); // Spacer 1

        // Add edit buttons above the StateWindow buttons
        arrowsContainer.getChildren().add(this.undoKnotButton); // Undo knot button
        arrowsContainer.getChildren().add(new RedoKnotButton(resourceBundle.getString(REDO_KNOT), app)); // Redo knot button
        arrowsContainer.getChildren().add(new CreatePatternButton(resourceBundle.getString(CREATE_PATTERN_BUTTON), app)); // Create pattern button
        arrowsContainer.getChildren().add(new ResetDiagramButton(resourceBundle.getString(RESET_DIAGRAM), app)); // Reset diagram button
        
        // Add spacers to move down the StateWindow buttons
        arrowsContainer.getChildren().add(new Region()); // Spacer 6
        arrowsContainer.getChildren().add(new Region()); // Spacer 7
        arrowsContainer.getChildren().add(new Region()); // Spacer 8
        
        // Add the 4 StateWindow buttons at the same X coordinate, lower
        arrowsContainer.getChildren().add(unselectableButton); // Unselectable button
        arrowsContainer.getChildren().add(selectableButton); // Selectable button
        arrowsContainer.getChildren().add(invisibleButton); // Invisible button
        arrowsContainer.getChildren().add(visibleButton); // Visible button
        
        // Column 3: arrows + buttons
        VBox rightColumn = new VBox();
        rightColumn.setAlignment(Pos.TOP_CENTER);
        rightColumn.setSpacing(10);
        rightColumn.setTranslateX(-175); // Shift 175px to the left
        rightColumn.getChildren().add(arrowsContainer);
        mainContainer.getChildren().add(rightColumn);
        
        // Set a minimum width to force scrollbars if needed
        mainContainer.setMinWidth(1080); // 700 + 0 + 150 + 20 + 150 + 60 = 1080px minimum
        
        VBox root = new VBox(menuBar, mainContainer);
        root.setPrefSize(app.getResizes().getToolboxWindowWidth() + 60, computeWindowHeight(app));
        root.getStyleClass().add("toolbox");

        // Create a global ScrollPane for the entire ToolboxWindow
        ScrollPane globalScrollPane = new ScrollPane();
        globalScrollPane.setContent(root);
        globalScrollPane.setFitToWidth(true); // Automatically adjust width
        globalScrollPane.setFitToHeight(true);
        
        // Do not set a minimum width on the content to avoid a bar at startup
        // The horizontal bar will appear only if the window is resized below MIN_TOOLBOX_WIDTH_FOR_SCROLL
        
        // Add a listener to adjust the content's minimum width when the window is resized
        toolboxStage.widthProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() < MIN_TOOLBOX_WIDTH_FOR_SCROLL) {
                root.setMinWidth(MIN_TOOLBOX_WIDTH_FOR_SCROLL);
            } else {
                root.setMinWidth(Region.USE_PREF_SIZE);
            }
        });
        
        // Barre horizontale : AS_NEEDED seulement quand nécessaire
        globalScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        globalScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        globalScrollPane.setPannable(true);

        Scene toolboxScene = new Scene(globalScrollPane);
        toolboxScene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

        toolboxStage.setX(app.getWindowRepositionEvents().getToolboxWindowX());
        toolboxStage.setY(app.getWindowRepositionEvents().getToolboxWindowY());
        toolboxStage.setWidth(app.getResizes().getToolboxWindowWidth() + 60);
        toolboxStage.setHeight(computeWindowHeight(app));
        toolboxStage.setMinWidth(400); // Minimum width to allow resizing
        toolboxStage.setMinHeight(300); // Minimum height to allow resizing
        toolboxStage.setResizable(true); // Allow resizing
        toolboxStage.setScene(toolboxScene);
        toolboxStage.setTitle(resourceBundle.getString(TOOLBOX_TITLE));

        toolboxStage.setOnCloseRequest(windowEvent -> {
            logger.info("You shall not close the toolbox window directly!");
            windowEvent.consume();
        });

        toolboxStage.show();
    }

    private int computeWindowHeight(App app) {
        if (app.getResizes().getToolboxWindowHeight() != 0d) {
            return (int) app.getResizes().getToolboxWindowHeight();
        }
        
        // Fixed window height; scrolling is handled by the patterns ScrollPane
        return 800; // Larger height to leave more space for the ScrollPane
    }

    public void createToolboxButtons(GridPane parent, App app) {
        // File buttons are now in the buttonsGrid
        buildEditButtons(app);
    }

    /**
     * Print diagram buttons.
     */
    public void buildPrintButtons(App app, GridPane buttonsPane) {
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

        buttonsPane.add(getPrintersButton, 0, 6);  // Ligne 6
        buttonsPane.add(printButton, 1, 6);  // Ligne 6
        PrintersListView listView = new PrintersListView();
        buttonsPane.add(listView, 0, 7);  // Ligne 7

        PrintUtil printer = new PrintUtil(app);
        printer.printersButtonOnAction(listView, getPrintersButton);
        printer.printButtonOnAction(printButton);
    }

    private void buildEditButtons(App app) {
        // These buttons are now in the VBox arrowsContainer
        // Do not add them to the main GridPane anymore
        this.undoKnotButton = new UndoKnotButton(resourceBundle.getString(UNDO_KNOT), app);
    }

    private void buildFileButtons(App app, GridPane buttonsPane) {
        SaveButton saveButton = new SaveButton(app, resourceBundle.getString(SAVE_FILE));
        SaveAsButton saveAsButton = new SaveAsButton(app, resourceBundle.getString(SAVE_FILE_AS));
        LoadButton loadButton = new LoadButton(app, resourceBundle.getString(LOAD_FILE));
        ShareButton shareButton = new ShareButton(app, resourceBundle.getString(SHARE_BUTTON_NAME));
        ExportImageButton exportImageButton = new ExportImageButton(app, resourceBundle.getString(EXPORT_IMAGE));
        ExportPdfButton exportPdfButton = new ExportPdfButton(app, resourceBundle.getString(EXPORT_PDF_BUTTON_NAME));

        buttonsPane.add(saveButton, 0, 3);  // Ligne 3
        buttonsPane.add(saveAsButton, 1, 3);  // Ligne 3
        GridPane.setColumnSpan(saveAsButton, 2); // Étendre le bouton "sauvegarder sous" sur 2 colonnes
        buttonsPane.add(loadButton, 0, 4);  // Ligne 4
        buttonsPane.add(shareButton, 1, 4);  // Ligne 4
        buttonsPane.add(exportImageButton, 0, 5);  // Ligne 5
        buttonsPane.add(exportPdfButton, 1, 5);  // Ligne 5
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
        app.getToolboxWindow().getToolboxStage().close();
        app.getPrimaryStage().close();
        app.start(new Stage());
    }

    public MenuBar getMenuBar() {
        return this.menuBar;
    }

    public Label getGridNameLabel() {
        return this.gridNameLabel;
    }

    // Methods added from GeometryWindow
    public void createGeometryButtons(App app, GridPane parent, int startRow) {
        ImageUtil util = new ImageUtil(app);

        this.deletionButton = new DeletionButton(app, resourceBundle.getString(DELETION_BUTTON_NAME));
        util.getImageView("deletion.png", deletionButton, false);

        this.duplicationButton = new DuplicationButton(app, resourceBundle.getString(DUPLICATION_BUTTON_NAME));
        this.duplicationButton.setId("duplicationButton");
        util.getImageView("duplication.png", duplicationButton, false);

        VerticalFlippingButton verticalFlippingButton = new VerticalFlippingButton(app, resourceBundle.getString(VERTICAL_FLIPPING_BUTTON_NAME));
        util.getImageView("flip_vertically.png", verticalFlippingButton, false);

        HorizontalFlippingButton horizontalFlippingButton = new HorizontalFlippingButton(app, resourceBundle.getString(HORIZONTAL_FLIPPING_BUTTON_NAME));
        util.getImageView("flip_horizontally.png", horizontalFlippingButton, false);

        this.drawingButton = new DrawingButton(app, resourceBundle.getString(DrawingButton.DRAWING_BUTTON_NAME));
        util.getImageView("drawing.png", drawingButton, true);

        this.selectionButton = new SelectionButton(app, resourceBundle.getString(SELECTION_BUTTON_NAME));
        util.getImageView("selection.png", selectionButton, false);

        RotationButton rotationButton = new RotationButton(resourceBundle.getString(ROTATION_BUTTON_NAME));
        util.getImageView("rotation.png", rotationButton, false);

        ZoomButton zoomButton = new ZoomButton(resourceBundle.getString(ZOOM_BUTTON_NAME));
        util.getImageView("zoom.png", zoomButton, false);

        // Rotation spinners
        this.rotationSpinner1 = new Spinner<>(ROTATION_SPINNER_MIN_VALUE, ROTATION_SPINNER_MAX_VALUE,
            DEFAULT_ROTATION, ROTATION_SPINNER_INCREMENTS_1);
        this.rotationSpinner1.setId("rotationSpinner1");
        this.rotationSpinner1.setEditable(true);
        this.rotationSpinner2 = new Spinner<>(ROTATION_SPINNER_MIN_VALUE, ROTATION_SPINNER_MAX_VALUE,
            DEFAULT_ROTATION, ROTATION_SPINNER_INCREMENTS_2);
        this.rotationSpinner2.setId("rotationSpinner2");
        this.rotationSpinner2.setEditable(true);
        this.rotationSpinner3 = new Spinner<>(ROTATION_SPINNER_MIN_VALUE, ROTATION_SPINNER_MAX_VALUE,
            DEFAULT_ROTATION, ROTATION_SPINNER_INCREMENTS_3);
        this.rotationSpinner3.setId("rotationSpinner3");
        this.rotationSpinner3.setEditable(true);

        // Zoom spinners
        this.zoomSpinner1 = new Spinner<>(ZOOM_SPINNER_MIN_VALUE, ZOOM_SPINNER_MAX_VALUE, DEFAULT_ZOOM, ZOOM_SPINNER_INCREMENTS_1);
        this.zoomSpinner1.setId("zoomSpinner1");
        this.zoomSpinner1.setEditable(true);
        this.zoomSpinner2 = new Spinner<>(ZOOM_SPINNER_MIN_VALUE, ZOOM_SPINNER_MAX_VALUE, DEFAULT_ZOOM, ZOOM_SPINNER_INCREMENTS_2);
        this.zoomSpinner2.setId("zoomSpinner2");
        this.zoomSpinner2.setEditable(true);
        this.zoomSpinner3 = new Spinner<>(ZOOM_SPINNER_MIN_VALUE, ZOOM_SPINNER_MAX_VALUE, DEFAULT_ZOOM, ZOOM_SPINNER_INCREMENTS_3);
        this.zoomSpinner3.setId("zoomSpinner3");
        this.zoomSpinner3.setEditable(true);

        // Spinners configuration
        RotationSpinner rotationSpinnerObject1 = new RotationSpinner();
        rotationSpinnerObject1.buildRotationSpinner(app, this.rotationSpinner1,
            this.rotationSpinner2.getValueFactory(), this.rotationSpinner3.getValueFactory());

        RotationSpinner rotationSpinnerObject2 = new RotationSpinner();
        rotationSpinnerObject2.buildRotationSpinner(app, this.rotationSpinner2,
            this.rotationSpinner1.getValueFactory(), this.rotationSpinner3.getValueFactory());

        RotationSpinner rotationSpinnerObject3 = new RotationSpinner();
        rotationSpinnerObject3.buildRotationSpinner(app, this.rotationSpinner3,
            this.rotationSpinner1.getValueFactory(), this.rotationSpinner2.getValueFactory());

        ZoomSpinner zoomSpinnerObject1 = new ZoomSpinner();
        zoomSpinnerObject1.buildZoomSpinner(app, this.zoomSpinner1, this.zoomSpinner2.getValueFactory(),
            this.zoomSpinner3.getValueFactory());

        ZoomSpinner zoomSpinnerObject2 = new ZoomSpinner();
        zoomSpinnerObject2.buildZoomSpinner(app, this.zoomSpinner2, this.zoomSpinner1.getValueFactory(),
            this.zoomSpinner3.getValueFactory());

        ZoomSpinner zoomSpinnerObject3 = new ZoomSpinner();
        zoomSpinnerObject3.buildZoomSpinner(app, this.zoomSpinner3, this.zoomSpinner1.getValueFactory(),
            this.zoomSpinner2.getValueFactory());

        // Geometry buttons are now added into geometryGrid in createToolboxStage
    }

    // Getters pour les éléments de GeometryWindow
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

}

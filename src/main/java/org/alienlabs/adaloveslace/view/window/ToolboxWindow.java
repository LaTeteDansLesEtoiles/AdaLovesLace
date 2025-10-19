package org.alienlabs.adaloveslace.view.window;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
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

    public static final double DEFAULT_TOOLBOX_WINDOW_X         = 600d;
    public static final double DEFAULT_TOOLBOX_WINDOW_Y         = DEFAULT_MAIN_WINDOW_Y;
    public static final double DEFAULT_TOOLBOX_WINDOW_WIDTH     = 950d; // Agrandi de 550 à 950
    public static final double MENU_BAR_Y                       = 0d;
    public static final int MAX_PATTERNS_WITHOUT_SCROLL         = 14; // 7 lignes × 2 colonnes = 14 patterns max
    
    // Constantes pour les boutons StateWindow (anciennement dans StateWindow.java)
    public static final double STATE_BUTTONS_HEIGHT = 30d;
    
    // Constantes pour les boutons GeometryWindow (anciennement dans GeometryWindow.java)
    public static final double GEOMETRY_BUTTONS_HEIGHT = 30d;

    public static final String THE_FOLLOWING_FOLDER_STRING      = "The following folder: '";
    public static final String BUTTON_SELECTED                  = "button-selected";
    public static final String BUTTON_WAITING_SELECTION         = "button-waiting-selection";
    public static final String PATTERN_TEXT_AND_COLOR_BUTTON    = "pattern-text-and-color-button";

    // Constantes de GeometryWindow
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
    public static final double BUTTON_MAX_WIDTH                   = 400d;

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

        // Les patterns sont maintenant gérés dans createToolboxStage avec ScrollPane
        // Plus besoin d'ajouter les patterns ici

        this.textButton = new TextButton(app);
        this.addKnotButton = new AddKnotButton(app);
        this.resetAllButton = new ResetAllButton(app);
        this.colorButton = new ColorButton(app);
        this.backInBlackButton = new BackInBlackButton(app);
        
        // Positionner les boutons avec un espace entre le bouton "texte" et les patterns
        parent.add(
                this.textButton,
                0,
                1  // Décalé d'une ligne pour laisser de l'espace avec les patterns
        );
        
        // Les boutons de StateWindow seront ajoutés au VBox arrowsContainer à droite
        
        // Les boutons StateWindow sont maintenant dans le VBox arrowsContainer à droite
        parent.add(
                this.addKnotButton,
                0,
                4  // Décalé de deux lignes
        );
        parent.add(
                this.resetAllButton,
                1,
                4  // Décalé de deux lignes
        );
        parent.add(
                this.colorButton,
                0,
                5  // Décalé de deux lignes
        );
        parent.add(
                this.backInBlackButton,
                1,
                5  // Décalé de deux lignes
        );

        // Les boutons de géométrie et les flèches sont maintenant gérés dans createToolboxStage

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
            int posY,
            Diagram diagram
    ) {
        this.toolboxStage = toolboxStage;

        // Créer un GridPane séparé pour les patterns avec défilement
        GridPane patternsPane = new GridPane();
        patternsPane.setVgap(5);
        patternsPane.setHgap(5);
        patternsPane.setPadding(new Insets(5));
        
        // Ajouter les patterns au GridPane des patterns
        for (int i = 0; i < this.classpathResourceFiles.size(); i++) {
            patternsPane.add(buildPatternButton(app, diagram, i), i % 2, i / 2);
        }
        
        // Créer un ScrollPane spécifiquement pour les patterns
        ScrollPane patternsScrollPane = new ScrollPane();
        patternsScrollPane.setContent(patternsPane);
        patternsScrollPane.setFitToWidth(true);
        patternsScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        // Activer la barre de défilement verticale si plus de 14 patterns
        if (this.classpathResourceFiles.size() > MAX_PATTERNS_WITHOUT_SCROLL) {
            patternsScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        } else {
            patternsScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        }
        
        // Limiter la hauteur du ScrollPane pour qu'il ne prenne que l'espace nécessaire
        if (this.classpathResourceFiles.size() > MAX_PATTERNS_WITHOUT_SCROLL) {
            patternsScrollPane.setMaxHeight(400); // Hauteur pour 7 lignes de patterns
            patternsScrollPane.setMinHeight(400); // Hauteur minimale pour assurer la visibilité
        } else {
            // Si moins de 14 patterns, calculer la hauteur exacte nécessaire
            int patternsRows = (int) Math.ceil((double) this.classpathResourceFiles.size() / 2);
            double exactHeight = patternsRows * 60 + 20; // 60px par ligne + padding
            patternsScrollPane.setMaxHeight(exactHeight);
            patternsScrollPane.setMinHeight(exactHeight);
        }

        // Créer un HBox horizontal pour mettre le ScrollPane des patterns et les boutons de géométrie côte à côte
        HBox topRow = new HBox();
        topRow.setSpacing(10);
        topRow.getChildren().add(patternsScrollPane);
        
        // Créer un GridPane temporaire pour initialiser les boutons de géométrie
        GridPane tempGrid = new GridPane();
        createGeometryButtons(app, tempGrid, 0);
        
        // Créer les boutons rotation et zoom manquants
        ImageUtil util = new ImageUtil(app);
        RotationButton rotationButton = new RotationButton(resourceBundle.getString(ROTATION_BUTTON_NAME));
        util.getImageView("rotation.png", rotationButton, false);
        
        ZoomButton zoomButton = new ZoomButton(resourceBundle.getString(ZOOM_BUTTON_NAME));
        util.getImageView("zoom.png", zoomButton, false);
        
        // Créer un GridPane pour les boutons de géométrie (2 colonnes)
        GridPane geometryGrid = new GridPane();
        geometryGrid.setVgap(5);
        geometryGrid.setHgap(5);
        geometryGrid.setPadding(new Insets(5));
        
        // Ajouter les boutons de géométrie au GridPane (2 colonnes)
        // Ligne 0 : Spinners de rotation et zoom
        if (this.rotationSpinner1 != null) {
            geometryGrid.add(this.rotationSpinner1, 0, 0);
        }
        if (this.zoomSpinner1 != null) {
            geometryGrid.add(this.zoomSpinner1, 1, 0);
        }
        
        // Ligne 1 : Spinners de rotation et zoom
        if (this.rotationSpinner2 != null) {
            geometryGrid.add(this.rotationSpinner2, 0, 1);
        }
        if (this.zoomSpinner2 != null) {
            geometryGrid.add(this.zoomSpinner2, 1, 1);
        }
        
        // Ligne 2 : Spinners de rotation et zoom
        if (this.rotationSpinner3 != null) {
            geometryGrid.add(this.rotationSpinner3, 0, 2);
        }
        if (this.zoomSpinner3 != null) {
            geometryGrid.add(this.zoomSpinner3, 1, 2);
        }
        
        // Ligne 3 : Boutons rotation et zoom
        geometryGrid.add(rotationButton, 0, 3);
        geometryGrid.add(zoomButton, 1, 3);
        
        if (this.drawingButton != null) {
            geometryGrid.add(this.drawingButton, 0, 7);
        }
        if (this.selectionButton != null) {
            geometryGrid.add(this.selectionButton, 1, 7);
        }
        
        if (this.deletionButton != null) {
            geometryGrid.add(this.deletionButton, 0, 8);
        }
        if (this.duplicationButton != null) {
            geometryGrid.add(this.duplicationButton, 1, 8);
        }
        
        topRow.getChildren().add(geometryGrid);
        
        // Ajouter le HBox au GridPane principal
        parent.add(topRow, 0, 0, 4, 1);
        
        // Créer un GridPane séparé pour les flèches avec espacement uniforme
        GridPane arrowsGrid = new GridPane();
        arrowsGrid.setVgap(5);
        arrowsGrid.setHgap(5);
        arrowsGrid.setPadding(new Insets(5));
        arrowsGrid.setAlignment(Pos.CENTER); // Aligner les flèches au centre de leur colonne
        
        // Ajouter les flèches au GridPane séparé (carré 3x3)
        // Ligne du haut : UpLeft, Up, UpRight
        UpLeftButton upLeftButton = new UpLeftButton(app);
        util.getImageView("up_left.png", upLeftButton, false);
        arrowsGrid.add(upLeftButton, 0, 0);

        UpButton upButton = new UpButton(app);
        util.getImageView("up.png", upButton, false);
        arrowsGrid.add(upButton, 1, 0);

        UpRightButton upRightButton = new UpRightButton(app);
        util.getImageView("up_right.png", upRightButton, false);
        arrowsGrid.add(upRightButton, 2, 0);

        // Ligne du milieu : Left, Fast, Right
        LeftButton leftButton = new LeftButton(app);
        util.getImageView("left.png", leftButton, false);
        arrowsGrid.add(leftButton, 0, 1);

        FastMoveModeButton fastMoveModeButton = new FastMoveModeButton();
        util.getImageView("fast.png", fastMoveModeButton, false);
        arrowsGrid.add(fastMoveModeButton, 1, 1);

        RightButton rightButton = new RightButton(app);
        util.getImageView("right.png", rightButton, false);
        arrowsGrid.add(rightButton, 2, 1);

        // Ligne du bas : DownLeft, Down, DownRight
        DownLeftButton downLeftButton = new DownLeftButton(app);
        util.getImageView("down_left.png", downLeftButton, false);
        arrowsGrid.add(downLeftButton, 0, 2);

        DownButton downButton = new DownButton(app);
        util.getImageView("down.png", downButton, false);
        arrowsGrid.add(downButton, 1, 2);

        DownRightButton downRightButton = new DownRightButton(app);
        util.getImageView("down_right.png", downRightButton, false);
        arrowsGrid.add(downRightButton, 2, 2);
        
        // Créer un layout séparé pour les flèches - ne pas les ajouter au GridPane principal
        // Les flèches seront ajoutées au VBox root séparément
        
        // Les boutons de base sont déjà correctement positionnés dans createToolboxPane

        buildPrintButtons(app, parent, posY);

        // Créer un HBox pour contenir le GridPane principal et les flèches
        HBox mainContainer = new HBox();
        mainContainer.setSpacing(10);
        
        // Ajouter le GridPane principal à gauche
        mainContainer.getChildren().add(parent);
        
        // Créer un VBox pour les flèches et les boutons supplémentaires à droite
        VBox arrowsContainer = new VBox();
        arrowsContainer.setAlignment(Pos.TOP_CENTER);
        arrowsContainer.setSpacing(10);
        
        // Ajouter un espace pour aligner verticalement avec les patterns et spinners
        arrowsContainer.getChildren().add(new Region()); // Espace pour aligner avec les patterns
        
        // Ajouter les flèches
        arrowsContainer.getChildren().add(arrowsGrid);
        
        // Ajouter les boutons dans l'ordre : flèches -> changer grille -> nom grille -> quitter
        // Récupérer les boutons existants depuis buildQuitAndGridNameButtons
        QuitButton showQuitButton = new QuitButton(app, resourceBundle.getString(QUIT_APP));
        this.gridNameLabel = new Label(resourceBundle.getString(app.getOptionalDotGrid().getDiagram().getCurrentGridType().name()));
        
        // Créer le bouton "changer la grille"
        ShowHideGridButton changeGridButton = new ShowHideGridButton(resourceBundle.getString(SHOW_HIDE_GRID_BUTTON_NAME), app);
        
        // Créer les boutons de StateWindow avec une largeur de texte
        UnselectableButton unselectableButton = new UnselectableButton(app, resourceBundle.getString(UNSELECTABLE_BUTTON_NAME));
        unselectableButton.setPrefWidth(BUTTON_MAX_WIDTH); // Largeur suffisante pour le texte français
        SelectableButton selectableButton = new SelectableButton(app, resourceBundle.getString(SELECTABLE_BUTTON_NAME));
        selectableButton.setPrefWidth(BUTTON_MAX_WIDTH); // Largeur suffisante pour le texte français
        InvisibleButton invisibleButton = new InvisibleButton(app, resourceBundle.getString(INVISIBLE_BUTTON_NAME));
        invisibleButton.setPrefWidth(BUTTON_MAX_WIDTH); // Largeur suffisante pour le texte français
        VisibleButton visibleButton = new VisibleButton(app, resourceBundle.getString(VISIBLE_BUTTON_NAME));
        visibleButton.setPrefWidth(BUTTON_MAX_WIDTH); // Largeur suffisante pour le texte français
        
        // Ajouter les boutons dans l'ordre souhaité
        arrowsContainer.getChildren().add(new Region());
        arrowsContainer.getChildren().add(changeGridButton); // Bouton changer grille sous les flèches
        arrowsContainer.getChildren().add(this.gridNameLabel); // Nom de la grille sous le bouton changer grille
        
        // Ajouter des espaces pour descendre le bouton quitter d'une ligne
        arrowsContainer.getChildren().add(new Region()); // Espace pour descendre le bouton quitter
        
        arrowsContainer.getChildren().add(new Region()); // Espace 1
        arrowsContainer.getChildren().add(showQuitButton); // Bouton quitter descendu d'une ligne

        // Ajouter des espaces pour descendre de 5 lignes au total
        arrowsContainer.getChildren().add(new Region()); // Espace 1
        arrowsContainer.getChildren().add(new Region()); // Espace 1

        // Ajouter les boutons d'édition au-dessus des boutons StateWindow
        arrowsContainer.getChildren().add(this.undoKnotButton); // Bouton annuler un noeud
        arrowsContainer.getChildren().add(new RedoKnotButton(resourceBundle.getString(REDO_KNOT), app)); // Bouton refaire un noeud
        arrowsContainer.getChildren().add(new CreatePatternButton(resourceBundle.getString(CREATE_PATTERN_BUTTON), app)); // Bouton créer un motif
        arrowsContainer.getChildren().add(new ResetDiagramButton(resourceBundle.getString(RESET_DIAGRAM), app)); // Bouton réinitialiser le diagramme
        
        // Ajouter des espaces pour descendre les boutons StateWindow
        arrowsContainer.getChildren().add(new Region()); // Espace 6
        arrowsContainer.getChildren().add(new Region()); // Espace 7
        arrowsContainer.getChildren().add(new Region()); // Espace 8
        
        // Ajouter les 4 boutons StateWindow à la même coordonnée X, plus bas
        arrowsContainer.getChildren().add(unselectableButton); // Bouton non-sélectionnable
        arrowsContainer.getChildren().add(selectableButton); // Bouton sélectionnable
        arrowsContainer.getChildren().add(invisibleButton); // Bouton invisible
        arrowsContainer.getChildren().add(visibleButton); // Bouton visible
        
        // Ajouter le conteneur des flèches à droite
        mainContainer.getChildren().add(arrowsContainer);
        
        VBox root = new VBox(menuBar, mainContainer);
        root.setPrefSize(app.getResizes().getToolboxWindowWidth(), computeWindowHeight(app));
        root.getStyleClass().add("toolbox");

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
        
        // Hauteur fixe pour la fenêtre, le défilement est géré par le ScrollPane des patterns
        return 800; // Hauteur plus grande pour laisser plus d'espace au ScrollPane
    }

    public void createToolboxButtons(GridPane parent, App app, int posY) {
        buildFileButtons(app, parent, posY);
        buildEditButtons(app, parent, posY);
        buildShowHideGridButton(app, parent, posY);
        buildQuitAndGridNameButtons(parent, posY);
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

        parent.add(getPrintersButton, 0, 12);  // Décalé de deux lignes
        parent.add(printButton, 1, 12);  // Décalé de deux lignes
        PrintersListView listView = new PrintersListView();
        parent.add(listView, 0, 13);  // Décalé d'une ligne

        PrintUtil printer = new PrintUtil(app);
        printer.printersButtonOnAction(listView, getPrintersButton);
        printer.printButtonOnAction(printButton);
    }

    private void buildQuitAndGridNameButtons(GridPane buttonsPane, int posY) {
        // Ces boutons sont maintenant dans le VBox arrowsContainer
        // Ne plus les ajouter au GridPane principal
    }

    private void buildShowHideGridButton(App app, GridPane buttonsPane, int posY) {
        // Ce bouton est maintenant dans le VBox arrowsContainer
        // Ne plus l'ajouter au GridPane principal
    }

    private void buildEditButtons(App app, GridPane buttonsPane, int posY) {
        // Ces boutons sont maintenant dans le VBox arrowsContainer
        // Ne plus les ajouter au GridPane principal
        this.undoKnotButton = new UndoKnotButton(resourceBundle.getString(UNDO_KNOT), app);
    }

    private void buildFileButtons(App app, GridPane buttonsPane, int posY) {
        SaveButton saveButton = new SaveButton(app, resourceBundle.getString(SAVE_FILE));
        SaveAsButton saveAsButton = new SaveAsButton(app, resourceBundle.getString(SAVE_FILE_AS));
        LoadButton loadButton = new LoadButton(app, resourceBundle.getString(LOAD_FILE));
        ShareButton shareButton = new ShareButton(app, resourceBundle.getString(SHARE_BUTTON_NAME));
        ExportImageButton exportImageButton = new ExportImageButton(app, resourceBundle.getString(EXPORT_IMAGE));
        ExportPdfButton exportPdfButton = new ExportPdfButton(app, resourceBundle.getString(EXPORT_PDF_BUTTON_NAME));

        buttonsPane.add(saveButton, 0, 8);  // Décalé de deux lignes
        buttonsPane.add(saveAsButton, 1, 8);  // Décalé de deux lignes
        buttonsPane.add(loadButton, 0, 9);  // Décalé de deux lignes
        buttonsPane.add(shareButton, 1, 9);  // Décalé de deux lignes
        buttonsPane.add(exportImageButton, 0, 10);  // Décalé de deux lignes
        buttonsPane.add(exportPdfButton, 1, 10);  // Décalé de deux lignes
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
        // GeometryWindow est maintenant intégrée dans ToolboxWindow
        // app.getGeometryWindow().getGeometryStage().close();
        app.getToolboxWindow().getToolboxStage().close();
        // StateWindow est maintenant intégrée dans ToolboxWindow
        // app.getStateWindow().getStateStage().close();
        app.getPrimaryStage().close();
        app.start(new Stage());
    }

    public MenuBar getMenuBar() {
        return this.menuBar;
    }

    public Label getGridNameLabel() {
        return this.gridNameLabel;
    }

    // Méthodes ajoutées de GeometryWindow
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

        // Spinners de rotation
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

        // Spinners de zoom
        this.zoomSpinner1 = new Spinner<>(ZOOM_SPINNER_MIN_VALUE, ZOOM_SPINNER_MAX_VALUE, DEFAULT_ZOOM, ZOOM_SPINNER_INCREMENTS_1);
        this.zoomSpinner1.setId("zoomSpinner1");
        this.zoomSpinner1.setEditable(true);
        this.zoomSpinner2 = new Spinner<>(ZOOM_SPINNER_MIN_VALUE, ZOOM_SPINNER_MAX_VALUE, DEFAULT_ZOOM, ZOOM_SPINNER_INCREMENTS_2);
        this.zoomSpinner2.setId("zoomSpinner2");
        this.zoomSpinner2.setEditable(true);
        this.zoomSpinner3 = new Spinner<>(ZOOM_SPINNER_MIN_VALUE, ZOOM_SPINNER_MAX_VALUE, DEFAULT_ZOOM, ZOOM_SPINNER_INCREMENTS_3);
        this.zoomSpinner3.setId("zoomSpinner3");
        this.zoomSpinner3.setEditable(true);

        // Configuration des spinners
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

        // Organiser les boutons directement dans le GridPane sur plusieurs lignes compactes
        // Ligne 1 : Spinners de rotation et zoom
        parent.add(this.rotationSpinner1, 2, startRow);
        parent.add(this.zoomSpinner1, 3, startRow);
        parent.add(this.rotationSpinner2, 2, startRow + 1);
        parent.add(this.zoomSpinner2, 3, startRow + 1);
        parent.add(this.rotationSpinner3, 2, startRow + 2);
        parent.add(this.zoomSpinner3, 3, startRow + 2);
        
        // Ligne 2 : Boutons rotation et zoom
        parent.add(rotationButton, 2, startRow + 3);
        parent.add(zoomButton, 3, startRow + 3);
        
        // Ligne vide pour espacement
        
        // Ligne 3 : Boutons principaux
        parent.add(this.drawingButton, 2, startRow + 5);
        parent.add(this.selectionButton, 3, startRow + 5);
        parent.add(this.deletionButton, 2, startRow + 6);
        parent.add(this.duplicationButton, 3, startRow + 6);
        
        // Ligne vide pour espacement
        
        // Ligne 4 : Boutons de retournement
        parent.add(verticalFlippingButton, 2, startRow + 8);
        parent.add(horizontalFlippingButton, 3, startRow + 8);
    }

    public void createMoveKnotButtons(App app, GridPane parent, int startRow) {
        ImageUtil util = new ImageUtil(app);

        // Ajouter les boutons de mouvement avec un espacement uniforme
        // en respectant leurs positions respectives
        
        // Carré de 3x3 compact avec espacements uniformes
        // Utiliser les colonnes 2, 3, 4 pour un carré parfait
        // Ligne du haut : UpLeft, Up, UpRight
        UpLeftButton upLeftButton = new UpLeftButton(app);
        util.getImageView("up_left.png", upLeftButton, false);
        parent.add(upLeftButton, 2, startRow);

        UpButton upButton = new UpButton(app);
        util.getImageView("up.png", upButton, false);
        parent.add(upButton, 3, startRow);

        UpRightButton upRightButton = new UpRightButton(app);
        util.getImageView("up_right.png", upRightButton, false);
        parent.add(upRightButton, 4, startRow);

        // Ligne du milieu : Left, Fast, Right
        LeftButton leftButton = new LeftButton(app);
        util.getImageView("left.png", leftButton, false);
        parent.add(leftButton, 2, startRow + 1);

        FastMoveModeButton fastMoveModeButton = new FastMoveModeButton();
        util.getImageView("fast.png", fastMoveModeButton, false);
        parent.add(fastMoveModeButton, 3, startRow + 1);

        RightButton rightButton = new RightButton(app);
        util.getImageView("right.png", rightButton, false);
        parent.add(rightButton, 4, startRow + 1);

        // Ligne du bas : DownLeft, Down, DownRight
        DownLeftButton downLeftButton = new DownLeftButton(app);
        util.getImageView("down_left.png", downLeftButton, false);
        parent.add(downLeftButton, 2, startRow + 2);

        DownButton downButton = new DownButton(app);
        util.getImageView("down.png", downButton, false);
        parent.add(downButton, 3, startRow + 2);

        DownRightButton downRightButton = new DownRightButton(app);
        util.getImageView("down_right.png", downRightButton, false);
        parent.add(downRightButton, 4, startRow + 2);
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

package org.alienlabs.adaloveslace.domain;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.enumeration.GridType;
import org.alienlabs.adaloveslace.domain.enumeration.MouseMode;
import org.alienlabs.adaloveslace.domain.enumeration.PatternOrTextMode;
import org.alienlabs.adaloveslace.util.NodeUtil;
import org.alienlabs.adaloveslace.view.component.GridUtil;
import org.alienlabs.adaloveslace.view.window.event.GridEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.alienlabs.adaloveslace.domain.Knot.*;
import static org.alienlabs.adaloveslace.view.component.button.geometrywindow.SelectionButton.putAllEventsOnKnot;

/**
 * What is drawn on a Canvas: the Diagram is the desired, final business object consisting of Knots drawn with Patterns.
 *
 * @see Pattern
 * @see Knot
 *
 */
@XmlRootElement(name = "Diagram")
@XmlAccessorType(XmlAccessType.FIELD)
public class Diagram {

    private String              name;

    private final Set<Pattern>  patterns;

    private Integer             currentStepIndex;

    private List<Step>          allSteps = new ArrayList<>();

    private double              x;

    private double              y;

    private GridType currentGridType = GridType.CRISS_CROSS;

    public static boolean       isNewText;

    @XmlTransient
    private Pattern             currentPattern;

    @XmlTransient
    private Knot                currentKnot;

    @XmlTransient
    private Color               currentColor;

    @XmlTransient
    private boolean             isKnotSelected;

    @XmlTransient
    private static App          app;

    @XmlTransient
    private MouseMode currentMode;

    @XmlTransient
    private MouseMode           oldMode;

    @XmlTransient
    private static final Logger logger = LoggerFactory.getLogger(Diagram.class);

    @XmlTransient
    public static final double SPACING_X_FOR_DOTS = 25d; // The X space between the dots

    @XmlTransient
    public static final double SPACING_Y_FOR_DOTS = 10d; // The Y space between the dots

    @XmlTransient
    public static final double SPACING_X_FOR_CRISS_CROSS = 25d; // The X space between the lines

    @XmlTransient
    public static final double SPACING_Y_FOR_CRISS_CROSS = 25d; // The Y space between the lines

    @XmlTransient
    public static final Color DOT_GRID_COLOR = Color.gray(0d, 0.2d);

    @XmlTransient
    public static final Color CRISS_CROSS_GRID_COLOR = Color.DARKGRAY;

    @XmlTransient
    public Runnable updateImage;

    @XmlTransient
    private NodeUtil nodeUtil;

    @XmlTransient
    private List<Picture> previews = new ArrayList<>();

    // For JAXB
    public Diagram() {
        this.patterns           = new HashSet<>();
        this.currentMode        = MouseMode.DRAWING;
        this.currentStepIndex   = 0;
        this.nodeUtil           = new NodeUtil();
        setUpdateImage();
    }

    public Diagram(App app) {
        this.patterns           = new HashSet<>();
        this.currentMode        = MouseMode.DRAWING;
        this.currentStepIndex   = 0;
        Diagram.app             = app;
        this.allSteps.add(new Step());
        this.nodeUtil           = new NodeUtil();
        setUpdateImage();
    }

    public Diagram(final Diagram diagram, App app) {
        this.patterns               = new HashSet<>(diagram.getPatterns());
        this.allSteps               = new ArrayList<>(diagram.getAllSteps());
        this.currentStepIndex       = diagram.getCurrentStepIndex();
        this.currentMode            = diagram.getCurrentMode();
        this.currentKnot            = diagram.getCurrentKnot();
        this.isKnotSelected         = diagram.isKnotSelected();
        this.setCurrentPattern(diagram.getCurrentPattern());
        this.currentStepIndex       = 0;
        Diagram.app                 = app;
        this.allSteps.add(new Step());
        this.nodeUtil               = new NodeUtil();
        setUpdateImage();
    }

    private void setUpdateImage() {
        updateImage = () -> {
            logger.info("updateImage() called: currentKnot={}, typedText={}",
                    this.getCurrentKnot(),
                    this.getCurrentKnot() != null && this.getCurrentKnot().getTypedText() != null ?
                    this.getCurrentKnot().getTypedText().toString() : "null");
            
            getCurrentStep().getAllVisibleKnots().forEach(
                    knot -> putAllEventsOnKnot(app, knot)
            );

            ImageView currentImageView = null;
            if (this.getCurrentKnot() != null && this.getCurrentKnot().getImageView() != null) {
                currentImageView = this.getCurrentKnot().getImageView();
            }

            logger.debug("updateImage() calling createImageViewWithStep with x={}, y={}", this.x, this.y);
            createImageViewWithStep(
                    this.x,
                    this.y,
                    currentImageView,
                    null,
                    this.getCurrentColor()
            );
        };
    }

    public Set<Pattern> getPatterns() {
        return this.patterns;
    }

    public void addPattern(final Pattern pattern) {
        this.patterns.add(pattern);
    }

    public void undoLastStep(App app, boolean layoutChildren) {
        logger.info("Undo step, current step={}, total steps={}", 
                this.getCurrentStepIndex(), 
                this.getAllSteps().size());

        List<Node> nodeListToRemove = new ArrayList<>();

        if ((!this.getAllSteps().isEmpty() ) && (this.getCurrentStepIndex() > 0)) {
            for (Knot k : this.getAllSteps().get(this.getCurrentStepIndex() - 1).getAllVisibleKnots()) {
                nodeListToRemove.add(k.getImageView());
                removeKnotDecorations(nodeListToRemove, k);
            }
        }

        int previousStepIndex = this.getCurrentStepIndex();
        if (this.getCurrentStepIndex() > 1) {
            this.setCurrentStepIndex(this.getCurrentStepIndex() - 1);
            logger.info("Undo: Decremented stepIndex from {} to {}", previousStepIndex, this.getCurrentStepIndex());
        } else {
            logger.info("Undo: Cannot go below step 1, staying at step {}", this.getCurrentStepIndex());
        }

        Step currentStep = this.getCurrentStep();
        logger.info("Undo: getCurrentStep() returned step with index={}, selectedKnots.size()={}, displayedKnots.size()={}",
                currentStep.getStepIndex(),
                currentStep.getSelectedKnots().size(),
                currentStep.getDisplayedKnots().size());
        
        if (!currentStep.getSelectedKnots().isEmpty()) {
            Knot firstSelected = currentStep.getSelectedKnots().get(0);
            logger.info("Undo: First selected knot in current step: text='{}', typedText='{}'",
                    firstSelected.getText().orElse("empty"),
                    firstSelected.getTypedText() != null ? firstSelected.getTypedText().toString() : "null");
        }
        
        List<Knot> displayedKnots = new ArrayList<>(currentStep.getDisplayedKnots());
        List<Knot> displayedCopy = new ArrayList<>();
        List<Knot> selectedCopy = new ArrayList<>();

        for (Knot knot : currentStep.getAllVisibleKnots()) {
            if (!knot.isSelectable()) {
                Knot knotCopy = this.nodeUtil.copyKnot(knot);

                knot.getImageView().removeEventHandler(MouseEvent.MOUSE_MOVED, GridEvents.getGridHoverEventHandler(app));
                knot.getImageView().removeEventHandler(MouseEvent.MOUSE_CLICKED, GridEvents.getMouseClickEventHandler(app));
                knot.setSelection(null);
                knot.setHovered(null);
                knot.setHandle(null);

                if (displayedKnots.contains(knot)) {
                    displayedCopy.add(knotCopy);
                } else {
                    selectedCopy.add(knotCopy);
                }
            } else {
                Knot knotCopy = this.nodeUtil.copyKnot(knot);

                if (this.getCurrentMode() == MouseMode.SELECTION) {
                    putAllEventsOnKnot(app, knotCopy);
                } else {
                    knotCopy.getImageView().addEventHandler(MouseEvent.MOUSE_MOVED, GridEvents.getGridHoverEventHandler(app));
                    knotCopy.getImageView().addEventHandler(MouseEvent.MOUSE_CLICKED, GridEvents.getMouseClickEventHandler(app));
                }

                if (displayedKnots.contains(knot)) {
                    displayedCopy.add(knotCopy);
                } else {
                    selectedCopy.add(knotCopy);
                    nodeListToRemove.add(knot.getHandle());
                }
            }

            nodeListToRemove.add(knot.getImageView());
        }

        app.getMovablePane().getChildren().removeAll(nodeListToRemove);
        this.getCurrentStep().setDisplayedKnots(displayedCopy);
        this.getCurrentStep().setSelectedKnots(selectedCopy);
        
        // Mettre ? jour currentKnot pour pointer vers le knot restaur? du step pr?c?dent
        // Pour le texte, prendre le premier knot s?lectionn? s'il y en a un
        if (!selectedCopy.isEmpty()) {
            // Pour le texte, prendre le knot de texte s?lectionn?
            Knot restoredKnot = selectedCopy.stream()
                    .filter(knot -> knot.getPattern().isEmpty())
                    .findFirst()
                    .orElse(selectedCopy.get(0));
            this.setCurrentKnot(restoredKnot);
            logger.info("Undo: Updated currentKnot to restored knot: text='{}', typedText='{}'",
                    restoredKnot.getText().orElse("empty"),
                    restoredKnot.getTypedText() != null ? restoredKnot.getTypedText().toString() : "null");
        } else if (!displayedCopy.isEmpty()) {
            // S'il n'y a pas de knot s?lectionn?, prendre le premier displayed knot
            this.setCurrentKnot(displayedCopy.get(0));
            logger.info("Undo: Updated currentKnot to first displayed knot");
        } else {
            // Aucun knot dans le step restaur?
            this.setCurrentKnot(null);
            logger.info("Undo: Set currentKnot to null (no knots in restored step)");
        }

        if (layoutChildren) {
            app.getOptionalDotGrid().layoutChildren(); // Display nodes from new state
        }

        logger.info("Undo step completed, new step={}, restored step has {} selectedKnots and {} displayedKnots", 
                this.getCurrentStepIndex(),
                selectedCopy.size(),
                displayedCopy.size());
    }

    public void removeKnotDecorations(List<Node> nodeListToRemove, Knot k) {
        if (k.getHovered() != null) {
            nodeListToRemove.add(k.getHovered());
        }
        if (k.getSelection() != null) {
            nodeListToRemove.add(k.getSelection());
        }
        if (k.getHandle() != null) {
            nodeListToRemove.add(k.getHandle());
        }
    }

    // Workaround for move mode, lest handles without knots appear on the grid
    public void removeAllHandles() {
        app.getMovablePane().getChildren().removeAll(
                app.getMovablePane().getChildren().stream()
                        .filter(Circle.class::isInstance).toList()
        );
    }

    public void redoLastStep(App app, boolean layoutChildren) {
        logger.info("Redo 0 step, current step={}", this.getCurrentStepIndex());

        List<Node> nodeListToRemove = new ArrayList<>();

        if ((!this.getAllSteps().isEmpty()) && (this.getCurrentStepIndex() > 0)) {
            for (Knot k : this.getAllSteps().get(this.getCurrentStepIndex() - 1).getAllVisibleKnots()) {
                nodeListToRemove.add(k.getImageView());
                removeKnotDecorations(nodeListToRemove, k);
            }
        }

        if (this.getCurrentStepIndex() <
                this.getAllSteps().size()) {
            this.setCurrentStepIndex(this.getCurrentStepIndex() + 1);
        }

        List<Knot> displayedCopy = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots());
        List<Knot> selectedCopy = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots());

        for (Knot knot : app.getOptionalDotGrid().getDiagram().getCurrentStep().getAllVisibleKnots()) {
            if (!knot.isSelectable()) {
                Knot knotCopy = this.nodeUtil.copyKnot(knot);

                knot.getImageView().removeEventHandler(MouseEvent.MOUSE_MOVED, GridEvents.getGridHoverEventHandler(app));
                knot.getImageView().removeEventHandler(MouseEvent.MOUSE_CLICKED, GridEvents.getMouseClickEventHandler(app));
                knot.setSelection(null);
                knot.setHovered(null);
                knot.setHandle(null);

                if (app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots().contains(knot)) {
                    displayedCopy.remove(knot);
                    displayedCopy.add(knotCopy);
                } else {
                    selectedCopy.remove(knot);
                    selectedCopy.add(knotCopy);
                }
            } else {
                Knot knotCopy = this.nodeUtil.copyKnot(knot);

                if (this.getCurrentMode() == MouseMode.SELECTION) {
                    putAllEventsOnKnot(app, knotCopy);
                } else {
                    knotCopy.getImageView().addEventHandler(MouseEvent.MOUSE_MOVED, GridEvents.getGridHoverEventHandler(app));
                    knotCopy.getImageView().addEventHandler(MouseEvent.MOUSE_CLICKED, GridEvents.getMouseClickEventHandler(app));
                }

                if (this.getCurrentStep().getDisplayedKnots().contains(knot)) {
                    displayedCopy.remove(knot);
                    displayedCopy.add(knotCopy);
                } else {
                    selectedCopy.remove(knot);
                    selectedCopy.add(knotCopy);
                }
            }

            nodeListToRemove.add(knot.getImageView());
            removeKnotDecorations(nodeListToRemove, knot);
        }

        if (layoutChildren) {
            app.getOptionalDotGrid().getDiagram().getCurrentStep().setDisplayedKnots(displayedCopy);
            app.getOptionalDotGrid().getDiagram().getCurrentStep().setSelectedKnots(selectedCopy);
            app.getMovablePane().getChildren().removeAll(nodeListToRemove);
            app.getOptionalDotGrid().layoutChildren(); // Display nodes from new state
        }

        logger.info("Redo 2 step, new step={}", this.getCurrentStepIndex());
    }

    public static void newStep(List<Knot> displayedKnots, List<Knot> selectedKnots, boolean layoutChildren) {
        new Step(
                app,
                app.getOptionalDotGrid().getDiagram(),
                displayedKnots,
                selectedKnots,
                layoutChildren
        );
    }

    public void drawKnot(double x, double y) {
        logger.info("Current pattern  -> {}", this.getCurrentPattern());
        ImageView iv;
        Point2D coord = app.getGridStrategy().getDrawCoordinates(x, y);

        if (PatternOrTextMode.PATTERN == app.getOptionalDotGrid().getCurrentPatternOrTextModeProperty().get()) {
            iv = this.nodeUtil.drawPattern(coord.getX(), coord.getY(), this.getCurrentPattern());

            if (null != iv) {
                isNewText = true;
                createImageViewWithStep(
                        coord.getX(),
                        coord.getY(),
                        iv,
                        this.getCurrentPattern(),
                        this.getCurrentColor()
                );
            }
        } else {
            isNewText = true;
            this.nodeUtil.drawText(this, x, y);
        }
    }

    private void createImageViewWithStep(double x, double y, ImageView imageView, Pattern pattern, Color currentColor) {
        logger.debug("createImageViewWithStep() called: x={}, y={}, pattern={}, currentKnot={}", 
                x, y, pattern, this.getCurrentKnot());

        Knot oldCurrentKnot = (this.getCurrentKnot() == null)
                ? null
                : this.getCurrentKnot();

        logger.debug("oldCurrentKnot hashCode={}, getCurrentKnot() hashCode={}, same object? {}", 
                oldCurrentKnot != null ? oldCurrentKnot.hashCode() : "null",
                this.getCurrentKnot() != null ? this.getCurrentKnot().hashCode() : "null",
                oldCurrentKnot != null && this.getCurrentKnot() != null ? 
                oldCurrentKnot == this.getCurrentKnot() : "N/A");

        // Sauvegarder typedText AVANT de cr?er le nouveau n?ud car newKnot() pourrait cr?er un nouveau n?ud
        // et getCurrentKnot() pourrait changer apr?s l'appel ? newKnot()
        StringBuilder savedTypedText = null;
        if (oldCurrentKnot != null) {
            logger.debug("oldCurrentKnot is not null, typedText={}", 
                    oldCurrentKnot.getTypedText() != null ? oldCurrentKnot.getTypedText().toString() : "null");
            if (oldCurrentKnot.getTypedText() != null) {
                savedTypedText = new StringBuilder(oldCurrentKnot.getTypedText());
                logger.debug("Saved typedText from oldCurrentKnot: '{}'", savedTypedText.toString());
            }
        } else {
            logger.debug("oldCurrentKnot is null");
        }

        boolean textMode = (pattern == null);

        Knot currentKnot = this.nodeUtil.newKnot(x, y, imageView, pattern, getCurrentKnot(), this.getCurrentColor());

        if (isNewText && currentKnot.getPattern().isEmpty()) {
            currentKnot.setTextId(UUID.randomUUID());
        } else if (currentKnot.getPattern().isEmpty()) {
            if (oldCurrentKnot == null || oldCurrentKnot.getTextId() == null) {
                currentKnot.setTextId(UUID.randomUUID());
            } else {
                currentKnot.setTextId(oldCurrentKnot.getTextId());
            }
        }

        if (currentKnot.getPattern().isEmpty()) {
            currentKnot.setRotationAngle(oldCurrentKnot == null ? DEFAULT_ROTATION : oldCurrentKnot.getRotationAngle());
            currentKnot.setZoomFactor(oldCurrentKnot == null ? DEFAULT_ZOOM : oldCurrentKnot.getZoomFactor());
        }

        // Restaurer typedText depuis oldCurrentKnot (qui avait le texte mis ? jour par keyHandler)
        // Utiliser savedTypedText qui a ?t? sauvegard? AVANT l'appel ? newKnot()
        if (savedTypedText != null) {
            currentKnot.setTypedText(savedTypedText);
            logger.debug("Restored typedText to new knot: '{}'", currentKnot.getTypedText().toString());
        } else if (textMode) {
            // Initialiser typedText avec NEW_TEXT si c'est null (premier clic)
            currentKnot.setTypedText(new StringBuilder(NEW_TEXT));
            logger.debug("Initialized typedText with NEW_TEXT");
        }
        this.setCurrentKnot(currentKnot);
        
        // Cr?er l'imageView du texte APR?S avoir mis ? jour typedText
        if (textMode) {
            imageView = this.nodeUtil.createText(x, y, currentKnot.getImageView(), currentKnot, currentColor);
            currentKnot.setImageView(imageView);
        }
        
        // V?rifier AVANT de cr?er la s?lection si le dernier step contient juste un texte vide
        // Cela ?vite d'avoir deux steps pour la premi?re lettre (clic initial + premi?re frappe)
        // Et sauvegarder le rectangle et le handle pour les r?utiliser
        boolean shouldRemoveLastStep = false;
        Node savedSelection = null;
        Node savedHandle = null;
        if (!this.getAllSteps().isEmpty()) {
            Step lastStep = this.getAllSteps().get(this.getAllSteps().size() - 1);
            List<Knot> allKnotsInLastStep = new ArrayList<>(lastStep.getSelectedKnots());
            allKnotsInLastStep.addAll(lastStep.getDisplayedKnots());
            for (Knot knot : allKnotsInLastStep) {
                if (knot.getPattern().isEmpty()) {
                    String typedTextStr = knot.getTypedText() != null ? knot.getTypedText().toString() : "null";
                    boolean isEmptyOrSpace = typedTextStr.trim().isEmpty() || typedTextStr.equals(NEW_TEXT.toString());
                    if (isEmptyOrSpace && (lastStep.getSelectedKnots().size() == 1 || lastStep.getDisplayedKnots().size() == 1)) {
                        shouldRemoveLastStep = true;
                        logger.debug("Will remove last step with empty text before creating new step");
                        // Sauvegarder le rectangle et le handle du n?ud vide pour les transf?rer au nouveau step
                        savedSelection = knot.getSelection();
                        savedHandle = knot.getHandle();
                        logger.debug("Saved selection and handle from empty text knot");
                        break;
                    }
                }
            }
        }
        
        // Pour le texte, ne pas supprimer la s?lection si elle existe d?j? (r?utilisation lors de la mise ? jour)
        if (pattern != null) {
            currentKnot.setSelection(null);
        } else {
            // Pour le texte, toujours supprimer l'ancienne s?lection pour ?viter les doublons
            // et recr?er une nouvelle avec la bonne taille bas?e sur le texte actuel
            if (oldCurrentKnot != null && oldCurrentKnot.getSelection() != null) {
                app.getMovablePane().getChildren().remove(oldCurrentKnot.getSelection());
            }
            currentKnot.setSelection(null);
        }
        currentKnot.setHandle(null);
        currentKnot.setHovered(null);
        
        // Supprimer aussi l'ancien handle si il existe
        if (oldCurrentKnot != null && oldCurrentKnot.getHandle() != null) {
            app.getMovablePane().getChildren().remove(oldCurrentKnot.getHandle());
        }

        if (pattern == null) {
            // Cr?er la s?lection si elle n'existe pas encore pour le texte
            // R?utiliser le rectangle sauvegard? du step vide si disponible
            Node selection = currentKnot.getSelection();
            GridUtil gridUtil = new GridUtil(app.getMovablePane());
            if (savedSelection != null && savedSelection instanceof Rectangle) {
                // R?utiliser le rectangle du step vide et le mettre ? jour avec la bonne taille
                Rectangle savedRect = (Rectangle) savedSelection;
                currentKnot.setSelection(savedRect);
                selection = savedRect;
                // Mettre ? jour la taille et la position du rectangle avec les nouvelles valeurs du texte
                if (currentKnot.getImageView() != null && currentKnot.getImageView().getImage() != null) {
                    savedRect.setWidth(currentKnot.getImageView().getImage().getWidth());
                    savedRect.setHeight(currentKnot.getImageView().getImage().getHeight());
                }
                savedRect.setLayoutX(currentKnot.getX());
                savedRect.setLayoutY(currentKnot.getY());
                // Mettre ? jour les propri?t?s (zoom, rotation) sans changer l'ID
                double zoomFactor = gridUtil.computeZoomFactor(currentKnot);
                savedRect.setScaleX(zoomFactor);
                savedRect.setScaleY(zoomFactor);
                savedRect.setRotate(currentKnot.getRotationAngle());
                // S'assurer que le rectangle est dans le pane
                if (!app.getMovablePane().getChildren().contains(savedRect)) {
                    app.getMovablePane().getChildren().add(savedRect);
                }
                logger.info("Reusing saved selection from empty text step and updating size");
            } else if (!(selection instanceof Rectangle)) {
                Rectangle rec = gridUtil.newRectangle(currentKnot, Color.BLUE);
                currentKnot.setSelection(rec);
                selection = rec;
                // Ajouter la s?lection au pane si elle n'y est pas d?j?
                if (!app.getMovablePane().getChildren().contains(selection)) {
                    app.getMovablePane().getChildren().add(selection);
                }
            }
            
            if (selection != null && selection instanceof Rectangle) {
                Circle handle = null;
                if (savedHandle != null && savedHandle instanceof Circle) {
                    // R?utiliser le handle du step vide et le mettre ? jour avec la bonne position
                    handle = (Circle) savedHandle;
                    currentKnot.setHandle(handle);
                    // Mettre ? jour la position du handle
                    Circle newHandle = gridUtil.newHandleForText(currentKnot, (Rectangle) selection);
                    if (newHandle != null) {
                        handle.setLayoutX(newHandle.getLayoutX());
                        handle.setLayoutY(newHandle.getLayoutY());
                        handle.setRadius(newHandle.getRadius());
                    }
                    logger.info("Reusing saved handle from empty text step and updating position");
                } else {
                    handle = gridUtil.newHandleForText(currentKnot, (Rectangle) selection);
                    if (handle != null) {
                        currentKnot.setHandle(handle);
                    }
                }
                if (handle != null) {
                    // Ajouter le handle au pane s'il n'y est pas d?j?
                    if (!app.getMovablePane().getChildren().contains(handle)) {
                        app.getMovablePane().getChildren().add(handle);
                    }
                } else {
                    logger.warn("Failed to create handle for text knot");
                }
            }
            
            // L'imageView sera ajout? au pane par layoutChildren() via drawSelectedKnot()
        }

        putAllEventsOnKnot(app, currentKnot);

        if (null != oldCurrentKnot && textMode) {
            // Supprimer l'ancien imageView du pane pour permettre au nouveau de s'afficher
            // car on cr?e un nouveau step avec un nouvel imageView
            if (oldCurrentKnot.getImageView() != null) {
                app.getMovablePane().getChildren().remove(oldCurrentKnot.getImageView());
            }
        }

        List<Knot> displayedKnots = new ArrayList<>(this.getCurrentStep().getDisplayedKnots());
        List<Knot> selectedKnots = new ArrayList<>();
        
        // Pour le texte, garder les n?uds de texte pr?c?dents dans selectedKnots
        // pour qu'ils continuent d'?tre affich?s pendant la frappe
        List<Knot> previousSelectedKnots = this.getCurrentStep().getSelectedKnots();
        logger.debug("Previous selectedKnots.size()={}", previousSelectedKnots.size());
        
        // V?rifier s'il y a d?j? un n?ud du m?me type ? la m?me position dans les previousSelectedKnots
        // pour le remplacer par currentKnot
        Knot existingKnot = null;
        if (textMode) {
            existingKnot = previousSelectedKnots.stream()
                    .filter(knot -> knot.getPattern().isEmpty() &&
                                   Math.abs(knot.getX() - currentKnot.getX()) < 0.1 && 
                                   Math.abs(knot.getY() - currentKnot.getY()) < 0.1)
                    .findFirst()
                    .orElse(null);
            if (existingKnot != null) {
                logger.info("Found existingKnot at same position: text='{}', typedText='{}'", 
                        existingKnot.getText().orElse("empty"),
                        existingKnot.getTypedText() != null ? existingKnot.getTypedText().toString() : "null");
            } else {
                logger.info("No existingKnot found at same position");
            }
        }
        
        for (Knot knot : previousSelectedKnots) {
            logger.info("  Previous knot: pattern={}, text={}, typedText={}, equals existingKnot? {}", 
                    knot.getPattern().isPresent() ? knot.getPattern().get().getFilename() : "none",
                    knot.getText().orElse("empty"),
                    knot.getTypedText() != null ? knot.getTypedText().toString() : "null",
                    existingKnot != null && knot.equals(existingKnot));
            // Garder les n?uds de texte (sans pattern) dans selectedKnots
            // SAUF si c'est le n?ud existant qui sera remplac? par currentKnot
            if (knot.getPattern().isEmpty()) {
                // Ne pas ajouter si c'est le n?ud existant qui sera remplac?
                // Utiliser la comparaison de r?f?rence ou equals pour ?tre s?r
                if (existingKnot == null || (knot != existingKnot && !knot.equals(existingKnot))) {
                    selectedKnots.add(knot);
                    logger.info("    Added previous knot to selectedKnots");
                } else {
                    logger.info("    Skipped existingKnot, will be replaced by currentKnot");
                }
            } else {
                // Les patterns vont dans displayedKnots
                displayedKnots.add(knot);
            }
        }
        
        if (existingKnot != null) {
            // Supprimer l'ancien n?ud du m?me type
            displayedKnots.remove(existingKnot);
            selectedKnots.remove(existingKnot);
            app.getMovablePane().getChildren().remove(existingKnot.getImageView());
            if (existingKnot.getSelection() != null) {
                app.getMovablePane().getChildren().remove(existingKnot.getSelection());
            }
            if (existingKnot.getHovered() != null) {
                app.getMovablePane().getChildren().remove(existingKnot.getHovered());
            }
            if (existingKnot.getHandle() != null) {
                app.getMovablePane().getChildren().remove(existingKnot.getHandle());
            }
        }

        // Pour le texte, toujours ajouter aux selectedKnots pour qu'il soit affich?
        if (currentKnot.getPattern().isEmpty()) {
            // S'assurer que text est d?fini pour l'affichage
            if (currentKnot.getTypedText() != null && currentKnot.getTypedText().length() > 0) {
                currentKnot.setText(Optional.of(currentKnot.getTypedText().toString()));
            } else {
                currentKnot.setText(Optional.of(NEW_TEXT.toString()));
            }
            selectedKnots.add(currentKnot);
            logger.info("Added text knot to selectedKnots: typedText='{}', text='{}', selectedKnots.size()={}",
                    currentKnot.getTypedText() != null ? currentKnot.getTypedText().toString() : "null",
                    currentKnot.getText().orElse("empty"),
                    selectedKnots.size());
        } else {
            displayedKnots.add(currentKnot);
        }
        isNewText = false;

        // Supprimer le dernier step si n?cessaire (d?cid? plus t?t)
        if (shouldRemoveLastStep) {
            logger.debug("Removing last step with empty text before creating new step");
            Step lastStep = this.getAllSteps().get(this.getAllSteps().size() - 1);
            List<Step> allStepsList = this.getAllSteps();
            List<Step> newSteps = new ArrayList<>();
            for (Step step : allStepsList) {
                if (!step.equals(lastStep)) {
                    newSteps.add(step);
                }
            }
            this.setAllSteps(newSteps);
            // Mettre ? jour les stepIndex des steps restants
            for (int i = 0; i < newSteps.size(); i++) {
                newSteps.get(i).setStepIndex(i + 1);
            }
            // Mettre ? jour l'index pour pointer vers le nouveau dernier step
            if (!newSteps.isEmpty()) {
                this.setCurrentStepIndex(newSteps.size());
            } else {
                this.setCurrentStepIndex(0);
            }
        }

        logger.debug("Creating new step: displayedKnots.size()={}, selectedKnots.size()={}, layoutChildren=true",
                displayedKnots.size(), selectedKnots.size());
        newStep(
                displayedKnots,
                selectedKnots,
                true
        );
    }

    public void deleteKnotDecorationsFromFollowingSteps(App app, Knot knot) {
        app.getMovablePane().getChildren().remove(knot.getSelection());
        app.getMovablePane().getChildren().remove(knot.getHovered());
        app.getMovablePane().getChildren().removeAll(knot.getGuideLines());
        knot.getGuideLines().clear();
    }

    public void deleteKnotDecorationsFromFollowingSteps(Pane root) {
        root.getChildren().removeAll(root.getChildren().stream().filter(node ->
                (node instanceof Line || node instanceof Rectangle)
        ).toList());
    }

    // We don't lose the undo / redo history
    public void resetDiagram(App app) {
        app.getOptionalDotGrid().removeKnotDecorations();
        app.getMovablePane().getChildren().removeAll(this.getCurrentStep().getAllVisibleKnots().stream().
            map(Knot::getImageView).toList());
        this.getAllSteps().clear();
        this.currentStepIndex = -1;
        app.getOptionalDotGrid().layoutChildren();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getX() {
        return this.x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return this.y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public GridType getCurrentGridType() {
        return this.currentGridType;
    }

    public void setCurrentGridType(GridType currentGridType) {
        this.currentGridType = currentGridType;
    }

    public Runnable getUpdateImage() {
        return this.updateImage;
    }

    public Pattern getCurrentPattern() {
        return this.currentPattern;
    }

    public void setCurrentPattern(Pattern currentPattern) {
        this.currentPattern = currentPattern;
    }

    public Knot getCurrentKnot() {
        return currentKnot;
    }

    public void setCurrentKnot(Knot currentKnot) {
        this.currentKnot = currentKnot;
    }

    public Color getCurrentColor() {
        return this.currentColor;
    }

    public void setCurrentColor(Color currentColor) {
        this.currentColor = currentColor;
    }

    public boolean isKnotSelected() {
        return isKnotSelected;
    }

    public MouseMode getCurrentMode() {
        return currentMode;
    }

    public void setCurrentMode(MouseMode currentMode) {
        this.currentMode = currentMode;
    }

    public MouseMode getOldMode() {
        return oldMode;
    }

    public void setOldMode(MouseMode oldMode) {
        this.oldMode = oldMode;
    }

    public List<Picture> getPreviews() {
        return this.previews;
    }

    public void setPreviews(List<Picture> previews) {
        this.previews = previews;
    }

    public Integer getCurrentStepIndex() {
        return this.currentStepIndex;
    }

    public void setCurrentStepIndex(Integer currentStepIndex) {
        this.currentStepIndex = currentStepIndex;
    }

    public Step getCurrentStep() {
        if (currentStepIndex <= 0) {
            return new Step();
        }

        return this.getAllSteps().get(currentStepIndex - 1);
    }

    public List<Step> getAllSteps() {
        return this.allSteps;
    }

    public void setAllSteps(List<Step> allSteps) {
        this.allSteps = allSteps;
    }

    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        Diagram.app = app;
    }

}

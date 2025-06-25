package org.alienlabs.adaloveslace.business.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.enumeration.GridType;
import org.alienlabs.adaloveslace.business.model.enumeration.MouseMode;
import org.alienlabs.adaloveslace.business.model.enumeration.PatternOrTextMode;
import org.alienlabs.adaloveslace.util.NodeUtil;
import org.alienlabs.adaloveslace.view.component.GridUtil;
import org.alienlabs.adaloveslace.view.window.event.GridEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.alienlabs.adaloveslace.business.model.Knot.DEFAULT_ROTATION;
import static org.alienlabs.adaloveslace.business.model.Knot.DEFAULT_ZOOM;
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
            getCurrentStep().getAllVisibleKnots().forEach(
                    knot -> putAllEventsOnKnot(app, knot)
            );

            createImageViewWithStep(
                    this.x,
                    this.y,
                    null,
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
        logger.debug("Undo step, current step={}", this.getCurrentStepIndex());

        if (this.getCurrentStepIndex() > 1) {
            this.setCurrentStepIndex(this.getCurrentStepIndex() - 1);
        }

        List<Node> nodeListToRemove = new ArrayList<>();

        if (!this.getAllSteps().isEmpty()) {
            for (Step s : this.getAllSteps().subList(
                    this.getCurrentStepIndex(),
                    this.getAllSteps().size())) {
                for (Knot k : s.getAllVisibleKnots()) {
                    nodeListToRemove.add(k.getImageView());
                    removeKnotDecorations(nodeListToRemove, k);
                }
            }
        }

        List<Knot> displayedKnots = new ArrayList<>(this.getCurrentStep().getDisplayedKnots());
        List<Knot> displayedCopy = new ArrayList<>();
        List<Knot> selectedCopy = new ArrayList<>();

        for (Knot knot : this.getCurrentStep().getAllVisibleKnots()) {
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

        if (layoutChildren) {
            app.getOptionalDotGrid().layoutChildren(); // Display nodes from new state
        }

        logger.debug("Undo step, new step={}", this.getCurrentStepIndex());
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
        logger.debug("Redo 0 step, current step={}", this.getCurrentStepIndex());

        if (this.getCurrentStepIndex() <
                this.getAllSteps().size()) {
            this.setCurrentStepIndex(this.getCurrentStepIndex() + 1);
        }

        List<Node> nodeListToRemove = new ArrayList<>();

        if (!this.getAllSteps().isEmpty()) {
            for (Step s : this.getAllSteps().subList(
                    0,
                    this.getCurrentStepIndex())
            ) {
                for (Knot k : s.getAllVisibleKnots()) {
                    nodeListToRemove.add(k.getImageView());
                    removeKnotDecorations(nodeListToRemove, k);
                }
            }
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
                    app.getOptionalDotGrid().getDiagram().getCurrentStep().setDisplayedKnots(displayedCopy);
                } else {
                    selectedCopy.remove(knot);
                    selectedCopy.add(knotCopy);
                    app.getOptionalDotGrid().getDiagram().getCurrentStep().setSelectedKnots(selectedCopy);
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
                    app.getOptionalDotGrid().getDiagram().getCurrentStep().setDisplayedKnots(displayedCopy);
                } else {
                    selectedCopy.remove(knot);
                    selectedCopy.add(knotCopy);
                    app.getOptionalDotGrid().getDiagram().getCurrentStep().setSelectedKnots(selectedCopy);
                }
            }

            nodeListToRemove.add(knot.getImageView());
            removeKnotDecorations(nodeListToRemove, knot);
        }

        if (layoutChildren) {
            app.getMovablePane().getChildren().removeAll(nodeListToRemove);
            app.getOptionalDotGrid().layoutChildren(); // Display nodes from new state
        }

        logger.debug("Redo 2 step, new step={}", this.getCurrentStepIndex());
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
        logger.debug("Current pattern  -> {}", this.getCurrentPattern());
        ImageView iv;
        Coordinate coord = app.getGridStrategy().getDrawCoordinates(x, y);

        if (PatternOrTextMode.PATTERN == app.getOptionalDotGrid().getCurrentPatternOrTextModeProperty().get()) {
            iv = this.nodeUtil.drawPattern(coord.x(), coord.y(), this.getCurrentPattern());

            if (null != iv) {
                isNewText = true;
                createImageViewWithStep(
                        coord.x(),
                        coord.y(),
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
        if (this.getCurrentStep().getSelectedKnots().size() > 1) {
            return;
        }

        Knot oldCurrentKnot = (this.getCurrentKnot() == null)
                ? null
                : this.getCurrentKnot();

        boolean textMode = (pattern == null);

        if (textMode) {
            imageView = this.nodeUtil.createText(x, y, imageView, getCurrentKnot(), currentColor);
        }

        currentKnot = this.nodeUtil.newKnot(x, y, imageView, pattern, getCurrentKnot(), this.getCurrentColor());

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

        currentKnot.setTypedText(oldCurrentKnot == null ? null : oldCurrentKnot.getTypedText());
        this.setCurrentKnot(currentKnot);
        currentKnot.setSelection(new GridUtil(app.getMovablePane()).newRectangle(currentKnot, Color.BLUE));

        if (pattern == null) {
            currentKnot.setHandle(new GridUtil(app.getMovablePane()).newHandleForText(currentKnot, (Rectangle) currentKnot.getSelection()));
        } else {
            currentKnot.setHandle(new GridUtil(app.getMovablePane()).newHandleForPattern(currentKnot, (Rectangle) currentKnot.getSelection()));
        }

        putAllEventsOnKnot(app, currentKnot);

        if (null != oldCurrentKnot && textMode) {
            app.getMovablePane().getChildren().remove(oldCurrentKnot.getImageView());
        }


        List<Knot> displayedKnots = new ArrayList<>(this.getCurrentStep().getDisplayedKnots());
        List<Knot> selectedKnots = new ArrayList<>();
        displayedKnots.addAll(
                this.getCurrentStep().getSelectedKnots().stream().filter(
                                knot -> (
                                        knot.getPattern().isPresent() || isNewText
                                )
                        )
                        .toList()
        );

        if (currentKnot.getPattern().isEmpty() && null != currentKnot.getTypedText()) {
            currentKnot.setText(Optional.of(currentKnot.getTypedText().toString()));
        }
        selectedKnots.add(currentKnot);
        isNewText = false;

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
        app.getMovablePane().getChildren().removeAll(this.getCurrentStep().getDisplayedKnots().stream().
            map(Knot::getImageView).toList());
        app.getOptionalDotGrid().clearSelections();
        this.getAllSteps().clear();
        this.currentStepIndex = -1;
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

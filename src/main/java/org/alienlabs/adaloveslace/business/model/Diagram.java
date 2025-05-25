package org.alienlabs.adaloveslace.business.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.util.Events;
import org.alienlabs.adaloveslace.util.NodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import static org.alienlabs.adaloveslace.App.CANVAS_TEXT_FONT_SIZE;
import static org.alienlabs.adaloveslace.App.PATTERNS_DIRECTORY_NAME;
import static org.alienlabs.adaloveslace.util.Events.keyHandler;
import static org.alienlabs.adaloveslace.util.FileUtil.APP_FOLDER_IN_USER_HOME;

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

    @XmlTransient
    private Pattern             currentPattern;

    @XmlTransient
    private Knot                currentKnot;

    @XmlTransient
    private boolean             isKnotSelected;

    @XmlTransient
    private static App          app;

    @XmlTransient
    private MouseMode           currentMode;

    @XmlTransient
    private MouseMode           oldMode;

    @XmlTransient
    private static final Logger logger = LoggerFactory.getLogger(Diagram.class);

    @XmlTransient
    private static final double SPACING_X = 25d; // The X space between the dots

    @XmlTransient
    private static final double SPACING_Y = 10d; // The Y space between the dots

    @XmlTransient
    public static final String NEW_TEXT = " ";

    @XmlTransient
    private static final Color GRID_COLOR  = Color.gray(0d, 0.2d);
    public static StringBuilder typedText = new StringBuilder(" ");
    public static Runnable updateImage;

    // For JAXB
    public Diagram() {
        this.patterns           = new HashSet<>();
        this.currentMode        = MouseMode.DRAWING;
        this.currentStepIndex   = 0;
    }

    public Diagram(App app) {
        this.patterns           = new HashSet<>();
        this.currentMode        = MouseMode.DRAWING;
        this.currentStepIndex   = 0;
        Diagram.app             = app;
        this.allSteps.add(new Step());
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
    }

    public Set<Pattern> getPatterns() {
        return this.patterns;
    }

    public void addPattern(final Pattern pattern) {
        this.patterns.add(pattern);
    }

    public void resetKnotsText() {
        typedText = new StringBuilder(NEW_TEXT);
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
                Knot knotCopy = new NodeUtil().copyKnot(knot);

                knot.getImageView().removeEventHandler(MouseEvent.MOUSE_MOVED, Events.getGridHoverEventHandler(app));
                knot.getImageView().removeEventHandler(MouseEvent.MOUSE_CLICKED, Events.getMouseClickEventHandler(app));
                knot.setSelection(null);
                knot.setHovered(null);
                knot.setHandle(null);

                if (displayedKnots.contains(knot)) {
                    displayedCopy.add(knotCopy);
                } else {
                    selectedCopy.add(knotCopy);
                }
            } else {
                Knot knotCopy = new NodeUtil().copyKnot(knot);

                knotCopy.getImageView().addEventHandler(MouseEvent.MOUSE_MOVED, Events.getGridHoverEventHandler(app));
                knotCopy.getImageView().addEventHandler(MouseEvent.MOUSE_CLICKED, Events.getMouseClickEventHandler(app));

                if (displayedKnots.contains(knot)) {
                    displayedCopy.add(knotCopy);
                } else {
                    selectedCopy.add(knotCopy);
                    nodeListToRemove.add(knot.getHandle());
                }
            }

            nodeListToRemove.add(knot.getImageView());
        }

        app.getRoot().getChildren().removeAll(nodeListToRemove);
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
        app.getOptionalDotGrid().getRoot().getChildren().removeAll(
                app.getOptionalDotGrid().getRoot().getChildren().stream()
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
                Knot knotCopy = new NodeUtil().copyKnot(knot);

                knot.getImageView().removeEventHandler(MouseEvent.MOUSE_MOVED, Events.getGridHoverEventHandler(app));
                knot.getImageView().removeEventHandler(MouseEvent.MOUSE_CLICKED, Events.getMouseClickEventHandler(app));
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
                Knot knotCopy = new NodeUtil().copyKnot(knot);

                if (knotCopy.getHandle() != null) {
                    knotCopy.getHandle().setOnMousePressed(Events.getDragInitiatedOverHandleEventHandler());
                    knotCopy.getHandle().setOnMouseDragged(Events.getMouseDragOverHandleEventHandler());
                    knotCopy.getHandle().setOnMouseReleased(Events.getMouseDragDroppedHandleEventHandler());
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
            app.getRoot().getChildren().removeAll(nodeListToRemove);
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

    public void drawGrid(double w, double h, double desiredRadius, List<Shape> grid) {
        app.getOptionalDotGrid().hideGrid();

        for (double x = 10d; x < w; x += SPACING_X) {
            for (double y = 10d; y < (h - 50d); y += SPACING_Y) {
                double offsetY = (y % (2d * SPACING_Y)) == 0d ? SPACING_X / 2d : 0d;
                Ellipse ell = new Ellipse(x - desiredRadius + offsetY,y - desiredRadius, desiredRadius, desiredRadius); // A dot
                ell.setFill(GRID_COLOR);
                ell.toFront();

                grid.add(ell);
                app.getOptionalDotGrid().getRoot().getChildren().add(ell);
            }
        }
    }

    public void drawKnot(double x, double y) {
        logger.debug("Current pattern  -> {}", this.getCurrentPattern());
        ImageView iv;

        if (PatternOrTextMode.PATTERN == app.getOptionalDotGrid().getCurrentPatternOrTextModeProperty().get()) {
            iv = drawPattern(x, y);

            if (null != iv) {
                createImageViewWithStep(x, y, iv, this.getCurrentPattern());
            }
        } else {
            drawText(x, y);
        }
    }

    private void createImageViewWithStep(double x, double y, ImageView iv, Pattern pattern) {
        Knot oldCurrentKnot = this.getCurrentKnot();
        currentKnot = new Knot(
                x,
                y,
                pattern == null ? Optional.empty() : Optional.of(pattern),
                pattern == null ? Optional.of(typedText.toString()) : Optional.empty(),
                iv
        );
        currentKnot.setSelection(app.getOptionalDotGrid().newRectangle(currentKnot, Color.BLUE));
        currentKnot.setHandle(app.getOptionalDotGrid().newHandleForText(currentKnot, (Rectangle)currentKnot.getSelection()));

        this.setCurrentKnot(currentKnot);
        List<Knot> displayedKnots = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots());
        List<Knot> selectedKnots = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots());

        if (oldCurrentKnot != null) {
            app.getRoot().getChildren().remove(oldCurrentKnot.getImageView());
            app.getRoot().getChildren().remove(oldCurrentKnot.getSelection());
            app.getRoot().getChildren().remove(oldCurrentKnot.getHandle());
            displayedKnots.add(oldCurrentKnot);
        }

        selectedKnots.clear();
        selectedKnots.add(currentKnot);

        newStep(
                displayedKnots,
                selectedKnots,
                true
        );
    }

    public void drawText(double x, double y) {
        this.resetKnotsText();

        logger.info("text -> {}", typedText);
        app.getScene().addEventHandler(KeyEvent.KEY_PRESSED, keyHandler);
        app.getOptionalDotGrid().getDiagram().setCurrentMode(MouseMode.SELECTION);
        app.getGeometryWindow().getDrawingButton().setSelected(false);
        app.getGeometryWindow().getSelectionButton()   .setSelected(true);
        app.getGeometryWindow().getDeletionButton()    .setSelected(false);
        app.getGeometryWindow().getDuplicationButton() .setSelected(false);

        updateImage = () -> {
            logger.info("updated text -> {}", typedText);
            ImageView imageView = new ImageView();

            final Text text = new Text();
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);

            text.setText(typedText.toString());
            text.setFont(new Font(CANVAS_TEXT_FONT_SIZE));
            text.setFill(Color.BLACK);
            text.setLayoutX(x);
            text.setLayoutY(y);

            WritableImage s = text.snapshot(params, null);
            imageView.setImage(s);
            createImageViewWithStep(x, y, imageView, null);
        };

        updateImage.run();
    }

    private ImageView drawPattern(double x, double y) {
        ImageView iv = null;

        try (FileInputStream fis = new FileInputStream(new File(APP_FOLDER_IN_USER_HOME + PATTERNS_DIRECTORY_NAME, this.getCurrentPattern().getFilename()))) {
            Image image = new Image(fis);
            iv = new ImageView(image);

            iv.setLayoutX(x);
            iv.setLayoutY(y);
            iv.setRotate(0d);

            logger.debug("Top left corner of the knot {} is ({},{})", this.getCurrentPattern().getFilename(), x, y);
        } catch (IOException e) {
            logger.error("Problem with pattern resource file!", e);
        }

        return iv;
    }

    public void deleteKnotDecorationsFromFollowingSteps(App app, Knot knot) {
        app.getOptionalDotGrid().getRoot().getChildren().remove(knot.getSelection());
        app.getOptionalDotGrid().getRoot().getChildren().remove(knot.getHovered());
        app.getOptionalDotGrid().getRoot().getChildren().removeAll(knot.getGuideLines());
        knot.getGuideLines().clear();
    }

    public void deleteKnotDecorationsFromFollowingSteps(Pane root) {
        root.getChildren().removeAll(root.getChildren().stream().filter(node ->
                (node instanceof Line || node instanceof Rectangle)
        ).toList());
    }

    // We don't lose the undo / redo history
    public void resetDiagram(App app) {
        app.getRoot().getChildren().removeAll(this.getCurrentStep().getDisplayedKnots().stream().
            map(Knot::getImageView).toList());
        app.getOptionalDotGrid().clearSelections();
        this.getAllSteps().clear();
        this.currentStepIndex = -1;
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

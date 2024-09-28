package org.alienlabs.adaloveslace.business.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.util.Events;
import org.alienlabs.adaloveslace.util.NodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.alienlabs.adaloveslace.App.PATTERNS_DIRECTORY_NAME;
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

    private final List<Pattern> patterns;

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
    private static final Logger logger = LoggerFactory.getLogger(Diagram.class);

    @XmlTransient
    private static final double SPACING_X = 25d; // The X space between the dots

    @XmlTransient
    private static final double SPACING_Y = 10d; // The Y space between the dots

    @XmlTransient
    private static final Color GRID_COLOR  = Color.gray(0d, 0.2d);

    // For JAXB
    public Diagram() {
        this.patterns           = new ArrayList<>();
        this.currentMode        = MouseMode.DRAWING;
        this.currentStepIndex   = 0;
    }

    public Diagram(App app) {
        this.patterns           = new ArrayList<>();
        this.currentMode        = MouseMode.DRAWING;
        this.currentStepIndex   = 0;
        Diagram.app             = app;
        this.allSteps.add(new Step());
    }

    public Diagram(final Diagram diagram, App app) {
        this.patterns               = new ArrayList<>(diagram.getPatterns());
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

    public List<Pattern> getPatterns() {
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
                    this.getCurrentStepIndex() - 1,
                    this.getAllSteps().size())
            ) {
                for (Knot k : s.getAllVisibleKnots()) {
                   nodeListToRemove.add(k.getImageView());
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

                knotCopy.getImageView().addEventHandler(MouseEvent.MOUSE_MOVED, Events.getGridHoverEventHandler(app));
                knotCopy.getImageView().addEventHandler(MouseEvent.MOUSE_CLICKED, Events.getMouseClickEventHandler(app));

                if (app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots().contains(knot)) {
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
        }

        if (layoutChildren) {
            app.getRoot().getChildren().removeAll(nodeListToRemove);
            app.getOptionalDotGrid().layoutChildren(); // Display nodes from new state
        }

        logger.debug("Undo step, new step={}", this.getCurrentStepIndex());
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

                knotCopy.getImageView().addEventHandler(MouseEvent.MOUSE_MOVED, Events.getGridHoverEventHandler(app));
                knotCopy.getImageView().addEventHandler(MouseEvent.MOUSE_CLICKED, Events.getMouseClickEventHandler(app));

                if (app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots().contains(knot)) {
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
        }

        if (layoutChildren) {
            app.getRoot().getChildren().removeAll(nodeListToRemove);
            app.getOptionalDotGrid().layoutChildren(); // Display nodes from new state
        }

        logger.debug("Redo 2 step, new step={}", this.getCurrentStepIndex());
    }

    public static void newStep(List<Knot> displayedKnots, List<Knot> selectedKnots, boolean layoutChildren, Circle... handle) {
        new Step(app,
                app.getOptionalDotGrid().getDiagram(),
                displayedKnots,
                selectedKnots,
                layoutChildren, handle
        );
    }

    public void drawGrid(double w, double h, double desiredRadius, List<Shape> grid) {
        app.getOptionalDotGrid().hideGrid();

        for (double x = 40d; x < (w - 185d); x += SPACING_X) {
            for (double y = 60d; y < (h - 50d); y += SPACING_Y) {
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

        try (FileInputStream fis = new FileInputStream(new File(APP_FOLDER_IN_USER_HOME + PATTERNS_DIRECTORY_NAME, this.getCurrentPattern().getFilename()))) {
            Image image = new Image(fis);
            ImageView iv = new ImageView(image);

            iv.setX(x);
            iv.setY(y);
            iv.setRotate(0d);

            logger.debug("Top left corner of the knot {} is ({},{})", this.getCurrentPattern().getFilename(), x, y);

            app.getOptionalDotGrid().getRoot().getChildren().add(iv);
            currentKnot = new Knot(x, y, this.getCurrentPattern(), iv);
            this.setCurrentKnot(currentKnot);

            List<Knot> displayed = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots());
            displayed.add(currentKnot);

            newStep(displayed, app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots(), true);
        } catch (IOException e) {
            logger.error("Problem with pattern resource file!", e);
        }
    }

    public void deleteNodesFromFollowingSteps(App app, Knot knot) {
        app.getOptionalDotGrid().getRoot().getChildren().remove(knot.getSelection());
        app.getOptionalDotGrid().getRoot().getChildren().remove(knot.getHovered());
        app.getOptionalDotGrid().getRoot().getChildren().removeAll(knot.getGuideLines());
        knot.getGuideLines().clear();
    }

    public void deleteNodesFromFollowingSteps(Group root) {
        root.getChildren().removeAll(root.getChildren().stream().filter(node ->
                (node instanceof Line ||
                        node instanceof Rectangle ||
                        node instanceof Circle)).toList());
    }

    public void deleteHandlesFromCurrentStep(Group root) {
        root.getChildren().removeAll(root.getChildren().stream().filter(Circle.class::isInstance).toList());
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

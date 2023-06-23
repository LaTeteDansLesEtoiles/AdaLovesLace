package org.alienlabs.adaloveslace.business.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import javafx.scene.Group;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import org.alienlabs.adaloveslace.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    private final List<Knot>    knots;

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

    // For JAXB
    public Diagram() {
        this.patterns           = new ArrayList<>();
        this.knots              = new ArrayList<>();
        this.currentMode        = MouseMode.DRAWING;
        this.currentStepIndex   = 0;
    }

    public Diagram(App app) {
        this.patterns           = new ArrayList<>();
        this.knots              = new ArrayList<>();
        this.currentMode        = MouseMode.DRAWING;
        this.currentStepIndex   = 0;
        Diagram.app             = app;
        this.allSteps.add(new Step());
    }

    public Diagram(final Diagram diagram, App app) {
        this.patterns               = new ArrayList<>(diagram.getPatterns());
        this.knots                  = new ArrayList<>(diagram.getKnots());
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

    public List<Knot> getKnots() {
        return this.knots;
    }

    public void addPattern(final Pattern pattern) {
        this.patterns.add(pattern);
    }

    public void undoLastStep(App app, Boolean... layoutChildren) {
        logger.info("Undo step, current step={}", this.getCurrentStepIndex());

        if (this.getCurrentStepIndex() > 0) {
            this.setCurrentStepIndex(this.getCurrentStepIndex() - 1);
        }

        if (layoutChildren.length == 0) {
            app.getOptionalDotGrid().layoutChildren(); // Display nodes from new state
        }

        logger.info("Undo step, new step={} >= 1", this.getCurrentStepIndex());
    }

    public void redoLastStep(App app, Boolean... layoutChildren) {
        logger.info("Redo 0 step, current step={}", this.getCurrentStepIndex());

        if (this.getCurrentStepIndex() <
                this.getAllSteps().size() - 1) {
            this.setCurrentStepIndex(this.getCurrentStepIndex() + 1);
        }

        if (layoutChildren.length == 0) {
            app.getOptionalDotGrid().layoutChildren(); // Display nodes from new state
        }

        logger.info("Redo 2 step, new step={} < max", this.getCurrentStepIndex());
    }

    public static void newStep(Set<Knot> displayedKnots, Set<Knot> selectedKnots, Circle... handle) {
        new Step(app,
                app.getOptionalDotGrid().getDiagram(),
                displayedKnots,
                selectedKnots,
                handle
        );
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
        return this.getAllSteps().get(currentStepIndex);
    }

    public List<Step> getAllSteps() {
        return this.allSteps;
    }

    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
    }

}

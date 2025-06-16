package org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.business.model.enumeration.MouseMode;
import org.alienlabs.adaloveslace.util.ImageUtil;
import org.alienlabs.adaloveslace.view.component.button.ImageButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.alienlabs.adaloveslace.App.TOOLTIPS_DURATION;
import static org.alienlabs.adaloveslace.App.resourceBundle;
import static org.alienlabs.adaloveslace.business.model.Diagram.newStep;
import static org.alienlabs.adaloveslace.view.window.GeometryWindow.GEOMETRY_BUTTONS_HEIGHT;

public class CreatePatternButton extends ImageButton {

    public static final String CREATE_PATTERN_BUTTON    = "CREATE_PATTERN_BUTTON";

    private static EventHandler<MouseEvent> mouseMovedListener;
    private static EventHandler<MouseEvent> mouseClickedListener;

    private static Point2D firstClickLocal = null;
    private static Point2D secondClickLocal = null;
    private static final Rectangle selectionRectangle   = new Rectangle();

    private static final Logger logger                  = LoggerFactory.getLogger(CreatePatternButton.class);

    public CreatePatternButton(String buttonLabel, App app) {
        super(buttonLabel);
        this.setOnMouseClicked(event -> onCreatePatternModeAction(app));
        this.setPrefHeight(GEOMETRY_BUTTONS_HEIGHT);

        final Tooltip tooltip = new Tooltip();
        tooltip.setText(resourceBundle.getString("CREATE_PATTERN_BUTTON_TOOLTIP"));
        tooltip.setShowDuration(TOOLTIPS_DURATION);
        this.setTooltip(tooltip);

        buildButtonImage("create_pattern.png");
    }

    public static void onCreatePatternModeAction(App app) {
        logger.debug("Setting create pattern mode");

        app.getOptionalDotGrid().getDiagram().setCurrentMode(MouseMode.CREATE_PATTERN);

        app.getGeometryWindow().getDrawingButton().setSelected(false);
        app.getGeometryWindow().getSelectionButton().setSelected(false);
        app.getGeometryWindow().getDeletionButton().setSelected(false);
        app.getGeometryWindow().getDuplicationButton().setSelected(false);

        app.getOptionalDotGrid().clearSelections();
        app.getOptionalDotGrid().clearHovered();
        app.getOptionalDotGrid().clearAllGuideLines();
        app.getOptionalDotGrid().clearHandles();

        List<Knot> displayedKnots = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots());
        List<Knot> selectedKnots = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots());
        displayedKnots.addAll(new ArrayList<>(selectedKnots));
        selectedKnots.clear();
        app.getMovablePane().setOnMouseMoved(null);
        app.getMovablePane().setOnMouseClicked(null);

        newStep(displayedKnots, selectedKnots, true);

        Pane pane = app.getMovablePane();
        selectionRectangle.setStroke(Color.DARKGREEN);
        selectionRectangle.setFill(Color.TRANSPARENT);
        selectionRectangle.setStrokeWidth(1);
        selectionRectangle.setVisible(false);
        pane.getChildren().add(selectionRectangle);

        mouseMovedListener = mouseEvent -> {
            Point2D mouseInParent = app.getMovablePane().sceneToLocal(mouseEvent.getSceneX(), mouseEvent.getSceneY());
            logger.debug("Create Pattern => MouseEvent moved: X= {}, Y= {}", mouseInParent.getX(), mouseInParent.getY());

            if (firstClickLocal != null) {
                Point2D clickPoint = pane.sceneToLocal(mouseEvent.getSceneX(), mouseEvent.getSceneY());

                removeRectangle(app);
                double x = Math.min(firstClickLocal.getX(), clickPoint.getX());
                double y = Math.min(firstClickLocal.getY(), clickPoint.getY());
                double w = Math.abs(firstClickLocal.getX() - clickPoint.getX());
                double h = Math.abs(firstClickLocal.getY() - clickPoint.getY());

                selectionRectangle.setX(x);
                selectionRectangle.setY(y);
                selectionRectangle.setWidth(w);
                selectionRectangle.setHeight(h);
                selectionRectangle.setVisible(true);
                pane.getChildren().add(selectionRectangle);
            }
        };

        mouseClickedListener = mouseEvent -> {
            if (mouseEvent.getButton() != MouseButton.PRIMARY) {
                return;
            }

            Point2D local = pane.sceneToLocal(mouseEvent.getSceneX(), mouseEvent.getSceneY());

            if (firstClickLocal == null) {
                firstClickLocal = local;
                logger.debug("Create Pattern => first click {}", firstClickLocal);
            } else if (secondClickLocal == null) {
                secondClickLocal = local;
                logger.debug("Create Pattern => second click {}", secondClickLocal);

                double xMin = Math.min(firstClickLocal.getX(), secondClickLocal.getX());
                double yMin = Math.min(firstClickLocal.getY(), secondClickLocal.getY());
                double wLog = Math.abs(firstClickLocal.getX() - secondClickLocal.getX());
                double hLog = Math.abs(firstClickLocal.getY() - secondClickLocal.getY());

                selectionRectangle.setX(xMin);
                selectionRectangle.setY(yMin);
                selectionRectangle.setWidth(wLog);
                selectionRectangle.setHeight(hLog);
                selectionRectangle.setVisible(true);

                new ImageUtil(app).buildImage(xMin, yMin, wLog, hLog);
                firstClickLocal = null;
                secondClickLocal = null;
            }
        };

        app.getMovablePane().setOnMouseMoved(mouseMovedListener);
        app.getMovablePane().setOnMouseClicked(mouseClickedListener);
    }

    private static void removeRectangle(App app) {
        app.getMovablePane().getChildren().remove(selectionRectangle);
        app.getOptionalDotGrid().layoutChildren();
    }

    public static EventHandler<MouseEvent> getMouseMovedListener() {
        return mouseMovedListener;
    }

    public static EventHandler<MouseEvent> getMouseClickedListener() {
        return mouseClickedListener;
    }

}

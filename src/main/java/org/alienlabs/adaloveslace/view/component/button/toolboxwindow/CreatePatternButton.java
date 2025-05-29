package org.alienlabs.adaloveslace.view.component.button.toolboxwindow;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.business.model.MouseMode;
import org.alienlabs.adaloveslace.util.Events;
import org.alienlabs.adaloveslace.util.ImageUtil;
import org.alienlabs.adaloveslace.view.component.button.ImageButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.alienlabs.adaloveslace.App.TOOLTIPS_DURATION;
import static org.alienlabs.adaloveslace.App.resourceBundle;
import static org.alienlabs.adaloveslace.business.model.Diagram.newStep;
import static org.alienlabs.adaloveslace.view.component.OptionalDotGrid.CREATE_PATTERN_MARGIN;
import static org.alienlabs.adaloveslace.view.window.GeometryWindow.GEOMETRY_BUTTONS_HEIGHT;

public class CreatePatternButton extends ImageButton {

    public static final String CREATE_PATTERN_BUTTON  = "CREATE_PATTERN_BUTTON";

    private static int mouseClicks                    = 0;
    private static final Logger logger                = LoggerFactory.getLogger(CreatePatternButton.class);
    private static EventHandler<MouseEvent> mouseMovedListener;
    private static EventHandler<MouseEvent> mouseClickedListener;

    private static double firstClickX;
    private static double firstClickY;
    private static double rectangleX;
    private static double rectangleY;
    private static double rectangleWidth;
    private static double rectangleHeight;
    private static Rectangle rectangle;

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

        if (app.getOptionalDotGrid().getDiagram().getCurrentMode() == MouseMode.CREATE_PATTERN && mouseClicks == 0) {
            return;
        }

        app.getOptionalDotGrid().getDiagram().setCurrentMode(MouseMode.CREATE_PATTERN);

        app.getGeometryWindow().getDrawingButton().setSelected(false);
        app.getGeometryWindow().getSelectionButton().setSelected(false);
        app.getGeometryWindow().getDeletionButton().setSelected(false);
        app.getGeometryWindow().getDuplicationButton().setSelected(false);

        app.getOptionalDotGrid().clearSelections();
        app.getOptionalDotGrid().clearHovered();
        app.getOptionalDotGrid().clearAllGuideLines();

        List<Knot> displayedKnots = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots());
        List<Knot> selectedKnots = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots());
        displayedKnots.addAll(new ArrayList<>(selectedKnots));
        selectedKnots.clear();
        app.getMovablePane().removeEventHandler(MouseEvent.MOUSE_CLICKED, Events.getMouseClickEventHandler(app));

        if (Events.getGridHoverEventHandler(app) != null) {
            app.getMovablePane().removeEventHandler(MouseEvent.MOUSE_MOVED, Events.getGridHoverEventHandler(app));
        }
        newStep(displayedKnots, selectedKnots, true);

        if (mouseClicks == 2) {
            removeRectangle(app);
            mouseClicks = 0;
            new ImageUtil(app).buildImage(firstClickX, firstClickY + CREATE_PATTERN_MARGIN, rectangleWidth, rectangleHeight);
        }

        if (app.getOptionalDotGrid().getDiagram().getCurrentMode() == MouseMode.CREATE_PATTERN) {
            mouseMovedListener = mouseEvent -> {
                Point2D mouseInParent = app.getOptionalDotGrid().getRoot().sceneToLocal(mouseEvent.getSceneX(), mouseEvent.getSceneY());
                logger.debug("Create Pattern => MouseEvent moved: X= {}, Y= {}", mouseInParent.getX(), mouseInParent.getY());

                if (mouseClicks == 1) {
                    computeRectangleX(mouseInParent.getX(), firstClickX);
                    computeRectangleY(mouseInParent.getY(), firstClickY);

                    removeRectangle(app);

                    rectangle = newRectangle();
                    app.getMovablePane().getChildren().add(rectangle);
                }
            };

            mouseClickedListener = mouseEvent -> {
                Point2D mouseInParent = app.getOptionalDotGrid().getRoot().sceneToLocal(mouseEvent.getSceneX(), mouseEvent.getSceneY());
                logger.debug("Create Pattern => MouseEvent pressed: X= {}, Y= {}, source= {}",
                        mouseInParent.getX(),
                        mouseInParent.getY(),
                        mouseEvent.getSource());

                mouseClicks++;

                if (mouseClicks == 1) {
                    rectangleX = mouseInParent.getX();
                    rectangleY = mouseInParent.getY();
                    firstClickX = mouseInParent.getX();
                    firstClickY = mouseInParent.getY();
                } else if (mouseClicks == 2) {
                    rectangleX = mouseInParent.getX();
                    rectangleY = mouseInParent.getY();
                    rectangleWidth  = (rectangleX >= firstClickX) ? rectangleX - firstClickX : firstClickX - rectangleX;
                    rectangleHeight = (rectangleY >= firstClickY) ? rectangleY - firstClickY : firstClickY - rectangleY;
                } else if (mouseClicks > 2) {
                    mouseClicks = 1;
                    rectangleX = mouseInParent.getX();
                    rectangleY = mouseInParent.getY();
                    firstClickX = mouseInParent.getX();
                    firstClickY = mouseInParent.getY();

                    removeRectangle(app);
                }
            };

        }

        app.getMovablePane().addEventHandler(MouseEvent.MOUSE_MOVED, mouseMovedListener);
        app.getMovablePane().addEventHandler(MouseEvent.MOUSE_CLICKED, mouseClickedListener);
    }

    private static Rectangle newRectangle() {
        Rectangle rec = new Rectangle(rectangleX, rectangleY, rectangleWidth, rectangleHeight);
        rec.setStroke(Color.DARKGREEN);
        rec.setStrokeWidth(2d);
        rec.setFill(Color.TRANSPARENT);

        rec.addEventHandler(MouseEvent.MOUSE_MOVED, mouseMovedListener);
        rec.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseClickedListener);

        return rec;
    }

    private static void removeRectangle(App app) {
        if (null != rectangle) {
            app.getMovablePane().getChildren().remove(rectangle);
            app.getOptionalDotGrid().layoutChildren();
        }
    }

    private static void computeRectangleY(double mouseEvent, double firstClickY) {
        if (mouseEvent < firstClickY) {
            rectangleY = mouseEvent + 2;
            rectangleHeight = firstClickY - rectangleY;
        } else {
            rectangleY = firstClickY;
            rectangleHeight = mouseEvent - firstClickY - 1;
        }
    }

    private static void computeRectangleX(double mouseEvent, double firstClickX) {
        if (mouseEvent < firstClickX) {
            rectangleX = mouseEvent + 2;
            rectangleWidth = firstClickX - rectangleX;
        } else {
            rectangleX = firstClickX;
            rectangleWidth = mouseEvent - firstClickX - 1;
        }
    }

    public static EventHandler<MouseEvent> getMouseMovedListener() {
        return mouseMovedListener;
    }

    public static EventHandler<MouseEvent> getMouseClickedListener() {
        return mouseClickedListener;
    }

}

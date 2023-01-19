package org.alienlabs.adaloveslace.view.component.button.toolboxwindow;

import javafx.event.EventHandler;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.MouseMode;
import org.alienlabs.adaloveslace.util.Events;
import org.alienlabs.adaloveslace.util.ImageUtil;
import org.alienlabs.adaloveslace.view.component.OptionalDotGrid;
import org.alienlabs.adaloveslace.view.component.button.ImageButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.alienlabs.adaloveslace.view.window.GeometryWindow.GEOMETRY_BUTTONS_HEIGHT;

public class CreatePatternButton extends ImageButton {

    public static final String CREATE_PATTERN_BUTTON  = "CREATE_PATTERN_BUTTON";
    public static final String BUTTON_TOOLTIP         = "Select this button then draw a\nrectanle with the mouse to\ncreate a pattern with its content\n";

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
        tooltip.setText(BUTTON_TOOLTIP);
        this.setTooltip(tooltip);

        buildButtonImage("create_pattern.png");
    }

    public static void onCreatePatternModeAction(App app) {
        logger.info("Setting create pattern mode");
        app.getOptionalDotGrid().getDiagram().setCurrentMode(MouseMode.CREATE_PATTERN);

        app.getGeometryWindow().getDrawingButton().setSelected(false);
        app.getGeometryWindow().getSelectionButton().setSelected(false);
        app.getGeometryWindow().getDeletionButton().setSelected(false);
        app.getGeometryWindow().getDuplicationButton().setSelected(false);

        app.getOptionalDotGrid().clearSelections();
        app.getOptionalDotGrid().clearHovered();
        app.getOptionalDotGrid().clearAllGuideLines();
        app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots().clear();

        app.getRoot().removeEventHandler(MouseEvent.MOUSE_CLICKED, Events.getMouseClickEventHandler(app));

        if (Events.getGridHoverEventHandler(app) != null) {
            app.getMainWindow().getGrid().removeEventHandler(MouseEvent.MOUSE_MOVED, Events.getGridHoverEventHandler(app));
        }

        if (mouseClicks == 2) {
            removeRectangle(app);
            mouseClicks = 0;
            new ImageUtil(app).buildImage(firstClickX, firstClickY + OptionalDotGrid.TOP_MARGIN, rectangleWidth, rectangleHeight);
        }

        if (app.getOptionalDotGrid().getDiagram().getCurrentMode() == MouseMode.CREATE_PATTERN) {
            mouseMovedListener = mouseEvent -> {
                logger.debug("Create Pattern => MouseEvent moved: X= {}, Y= {}", mouseEvent.getX(), mouseEvent.getY());

                if (mouseClicks == 1) {
                    computeRectangleX(mouseEvent.getX(), firstClickX);
                    computeRectangleY(mouseEvent.getY(), firstClickY);

                    removeRectangle(app);

                    rectangle = newRectangle();
                    app.getRoot().getChildren().add(rectangle);
                }
            };

            mouseClickedListener = mouseEvent -> {
                logger.info("Create Pattern => MouseEvent pressed: X= {}, Y= {}", mouseEvent.getX(), mouseEvent.getY());

                mouseClicks++;

                if (mouseClicks == 1) {
                    rectangleX = mouseEvent.getX();
                    rectangleY = mouseEvent.getY();
                    firstClickX = mouseEvent.getX();
                    firstClickY = mouseEvent.getY();
                } else if (mouseClicks == 2) {
                    rectangleX = mouseEvent.getX();
                    rectangleY = mouseEvent.getY();
                    rectangleWidth  = (rectangleX >= firstClickX) ? rectangleX - firstClickX : firstClickX - rectangleX;
                    rectangleHeight = (rectangleY >= firstClickY) ? rectangleY - firstClickY : firstClickY - rectangleY;
                } else if (mouseClicks > 2) {
                    mouseClicks = 1;
                    rectangleX = mouseEvent.getX();
                    rectangleY = mouseEvent.getY();
                    firstClickX = mouseEvent.getX();
                    firstClickY = mouseEvent.getY();

                    removeRectangle(app);
                }
            };

            app.getMainWindow().getGrid().addEventHandler(MouseEvent.MOUSE_MOVED, mouseMovedListener);
            app.getMainWindow().getGrid().addEventHandler(MouseEvent.MOUSE_CLICKED, mouseClickedListener);
        }
    }

    private static Rectangle newRectangle() {
        Rectangle rec = new Rectangle(rectangleX, rectangleY, rectangleWidth, rectangleHeight);
        rec.setStroke(Color.YELLOW);
        rec.setStrokeWidth(2d);
        rec.setFill(Color.TRANSPARENT);

        rec.addEventHandler(MouseEvent.MOUSE_MOVED, mouseMovedListener);
        rec.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseClickedListener);

        return rec;
    }

    private static void removeRectangle(App app) {
        if (null != rectangle) {
            app.getRoot().getChildren().remove(rectangle);
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

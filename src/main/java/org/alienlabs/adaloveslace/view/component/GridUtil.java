package org.alienlabs.adaloveslace.view.component;

import javafx.geometry.Point3D;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import org.alienlabs.adaloveslace.domain.Knot;
import org.alienlabs.adaloveslace.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

import static org.alienlabs.adaloveslace.App.PATTERNS_DIRECTORY_NAME;
import static org.alienlabs.adaloveslace.util.FileUtil.APP_FOLDER_IN_USER_HOME;
import static org.alienlabs.adaloveslace.util.NodeUtil.HANDLE_SIZE;
import static org.alienlabs.adaloveslace.view.component.grid.OptionalDotGrid.BLUE_HANDLE;

public class GridUtil {

    private final Pane root;

    private static final Logger logger = LoggerFactory.getLogger(GridUtil.class);

    public GridUtil(Pane root) {
        this.root = root;
    }

    // Rotate Pattern knot with an angle in degrees
    public ImageView rotatePatternKnot(Knot knot) {
        if (knot.getImageView() == null) {
            try (FileInputStream fis = new FileInputStream(APP_FOLDER_IN_USER_HOME + PATTERNS_DIRECTORY_NAME + File.separator
                    + knot.getPattern().get().getFilename())) {
                new FileUtil().buildKnotImageView(knot, fis);
            } catch (IOException e) {
                logger.error("Problem with pattern resource file!", e);
            }
        } else {
            knot.getImageView().setLayoutX(knot.getX());
            knot.getImageView().setLayoutY(knot.getY());
            knot.getImageView().setFitHeight(knot.getPattern().get().getHeight());
            knot.getImageView().setFitWidth(knot.getPattern().get().getWidth());
        }

        knot.getImageView().getTransforms().clear();
        knot.getImageView().setRotate(knot.getRotationAngle());

        if (!root.getChildren().contains(knot.getImageView())) {
            root.getChildren().add(knot.getImageView());
        }

        logger.debug("rotated knot {} at angle {}",
                knot.getPattern().get().getFilename(),
                knot.getRotationAngle());

        return knot.getImageView();
    }
    // Zoom factor goes from -10 to 10, 0 being don't zoom knot, < 0 being shrink knot, > 0 being enlarge knot
    public double zoomAndFlipPatternKnot(Knot knot) {
        flipPattern(knot.isFlippedVertically(), Rotate.Y_AXIS, knot);
        flipPattern(knot.isFlippedHorizontally(), Rotate.X_AXIS, knot);

        return zoomPattern(knot);
    }

    private double zoomPattern(Knot knot) {
        double scaleFactor = computeZoomFactor(knot);
        knot.getImageView().setScaleX(scaleFactor);
        knot.getImageView().setScaleY(scaleFactor);

        logger.debug("zoomed knot {} at zoom factor {} and scale factor {}",
                knot.getPattern().get().getFilename(), knot.getZoomFactor(), scaleFactor);

        return scaleFactor;
    }

    // Zoom factor goes from -10 to 10, 0 being don't zoom knot, < 0 being shrink knot, > 0 being enlarge knot
    public void zoomTextKnot(Knot knot) {
        double scaleFactor = this.computeZoomFactor(knot);
        knot.getImageView().setScaleX(scaleFactor);
        knot.getImageView().setScaleY(scaleFactor);

        logger.debug("zoomed knot {} at zoom factor {} and scale factor {}",
                knot.getText(), knot.getZoomFactor(), scaleFactor);
    }

    private void flipPattern(boolean flip, Point3D axis, Knot knot) {
        Rotate rot = new Rotate();
        rot.setAxis(axis);
        rot.setAngle(flip ? 180d : 0d);
        rot.setPivotX(knot.getPattern().get().getWidth() / 2);
        rot.setPivotY(knot.getPattern().get().getHeight() / 2);

        knot.getImageView().getTransforms().add(rot);
    }


    public double computeZoomFactor(double factor) {
        if (factor == 0) {
            return 1;
        } else {
            return (10d + factor) / 10d;
        }
    }

    public double computeZoomFactor(Knot knot) {
        return this.computeZoomFactor(knot.getZoomFactor());
    }

    // The handle is the top left corner of the rectangle of the zoomed, rotated knot
    // @see https://stackoverflow.com/questions/41898990/find-corners-of-a-rotated-rectangle-given-its-center-point-and-rotation
    // And invert "TOP LEFT VERTEX:" & "BOTTOM LEFT VERTEX:" (small error from the author)
    public Circle newHandleForPattern(Knot knot, Rectangle rec) {
        Circle circle = new Circle(
                knot.getImageView().getBoundsInParent().getCenterX() -
                        (knot.getPattern().get().getWidth() / 2 * rec.getScaleX()) *
                                Math.cos(Math.toRadians(knot.getRotationAngle())) +
                        (knot.getPattern().get().getHeight() / 2 * rec.getScaleY()) *
                                Math.sin(Math.toRadians(knot.getRotationAngle())),
                knot.getImageView().getBoundsInParent().getCenterY() -
                        (knot.getPattern().get().getWidth() / 2 * rec.getScaleX()) *
                                Math.sin(Math.toRadians(knot.getRotationAngle())) -
                        (knot.getPattern().get().getHeight() / 2 * rec.getScaleY()) *
                                Math.cos(Math.toRadians(knot.getRotationAngle())),
                HANDLE_SIZE * computeZoomFactor(knot),
                BLUE_HANDLE);
        circle.setId(UUID.randomUUID().toString());
        return circle;
    }

    // The handle is the top left corner of the rectangle of the zoomed, rotated knot
    // @see https://stackoverflow.com/questions/41898990/find-corners-of-a-rotated-rectangle-given-its-center-point-and-rotation
    // And invert "TOP LEFT VERTEX:" & "BOTTOM LEFT VERTEX:" (small error from the author)
    public Circle newHandleForText(Knot knot, Rectangle rec) {
        // Vérification de sécurité pour éviter NullPointerException
        if (rec == null) {
            logger.warn("Rectangle is null in newHandleForText, cannot create handle");
            return null;
        }
        
        Circle circle = new Circle(
                knot.getImageView().getBoundsInParent().getCenterX() -
                        (knot.getImageView().getImage().getWidth() / 2 * rec.getScaleX()) *
                                Math.cos(Math.toRadians(knot.getRotationAngle())) +
                        (knot.getImageView().getImage().getHeight() / 2 * rec.getScaleY()) *
                                Math.sin(Math.toRadians(knot.getRotationAngle())),
                knot.getImageView().getBoundsInParent().getCenterY() -
                        (knot.getImageView().getImage().getWidth() / 2 * rec.getScaleX()) *
                                Math.sin(Math.toRadians(knot.getRotationAngle())) -
                        (knot.getImageView().getImage().getHeight() / 2 * rec.getScaleY()) *
                                Math.cos(Math.toRadians(knot.getRotationAngle())),
                HANDLE_SIZE * computeZoomFactor(knot),
                BLUE_HANDLE);
        circle.setId(UUID.randomUUID().toString());
        return circle;
    }

    public Rectangle newRectangle(Knot knot, Color color) {
        Rectangle rec;

        if (knot.getText().isPresent() && (knot.getPattern().isEmpty())) {
            rec = new Rectangle(
                    0, // x
                    0, // y
                    knot.getImageView().getImage().getWidth(),
                    knot.getImageView().getImage().getHeight()
            );
            setRectangleProperties(knot, color, rec);
            rec.setLayoutX(knot.getX());
            rec.setLayoutY(knot.getY());
            return rec;
        } else if (knot.getPattern().isPresent()) {
            rec = new Rectangle(
                    0, // x
                    0, // y
                    knot.getPattern().get().getWidth(),
                    knot.getPattern().get().getHeight()
            );
            setRectangleProperties(knot, color, rec);
            rec.setLayoutX(knot.getX());
            rec.setLayoutY(knot.getY());
            return rec;
        }

        return null;
    }

    private void setRectangleProperties(Knot knot, Color color, Rectangle rec) {
        rec.setId(UUID.randomUUID().toString());
        rec.setStroke(color);
        rec.setStrokeWidth(2d);
        rec.setFill(Color.TRANSPARENT);
        rec.setScaleX(computeZoomFactor(knot));
        rec.setScaleY(computeZoomFactor(knot));
        rec.setRotate(knot.getRotationAngle());
    }

}

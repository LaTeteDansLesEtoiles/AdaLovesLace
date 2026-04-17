package org.alienlabs.adaloveslace.view.component;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import org.alienlabs.adaloveslace.domain.Knot;

public class GuideLinesUtil {

  public static final double MAGNET_SIZE_X              = 1d;
  public static final double MAGNET_SIZE_Y              = 1d;
  public static final double PADDING_HEIGHT             = 60d;
  public static final double PADDING_WIDTH              = 60d;
  public static final double MAX_NUMBER_OF_GUIDELINES   = 10000;
  public static int CURRENT_NUMBER_OF_GUIDELINES        = 0;

  public GuideLinesUtil(Knot knot, Knot otherKnot, Pane root) {
      if (CURRENT_NUMBER_OF_GUIDELINES <= MAX_NUMBER_OF_GUIDELINES) {
          lineXLeftLeft(knot, otherKnot, root);
          lineXRightRight(knot, otherKnot, root);
          lineXLeftRight(knot, otherKnot, root);
          lineXRightLeft(knot, otherKnot, root);
          lineXCenterCenter(knot, otherKnot, root);

          lineYTopTop(knot, otherKnot, root);
          lineYBottomBottom(knot, otherKnot, root);
          lineYTopBottom(knot, otherKnot, root);
          lineYBottomTop(knot, otherKnot, root);
          lineYCenterCenter(knot, otherKnot, root);
      }
  }

    private static boolean inMagnetRange(double source, double target, double magnetSize) {
        return source - magnetSize <= target && source + magnetSize >= target;
    }

    private static void registerGuideLine(Knot knot, Pane root, Line line) {
        line.setStroke(Color.BLACK);
        knot.getGuideLines().add(line);
        root.getChildren().add(line);
        CURRENT_NUMBER_OF_GUIDELINES++;
    }

    private static void maybeAddVerticalGuideLine(Knot knot, Knot otherKnot, Pane root, double x, double otherXForMagnet) {
        if (!inMagnetRange(x, otherXForMagnet, MAGNET_SIZE_X)) {
            return;
        }
        double y1 = knot.getY() >= otherKnot.getY()
                ? knot.getY() + knot.getImageView().getImage().getHeight() + PADDING_HEIGHT
                : knot.getY() - PADDING_HEIGHT;
        double y2 = knot.getY() >= otherKnot.getY()
                ? otherKnot.getY() - PADDING_HEIGHT
                : otherKnot.getY() + knot.getImageView().getImage().getHeight() + PADDING_HEIGHT;
        registerGuideLine(knot, root, new Line(x, y1, x, y2));
    }

    private static void maybeAddHorizontalGuideLine(Knot knot, Knot otherKnot, Pane root, double y, double otherYForMagnet) {
        if (!inMagnetRange(y, otherYForMagnet, MAGNET_SIZE_Y)) {
            return;
        }
        double x1 = knot.getX() >= otherKnot.getX()
                ? knot.getX() + knot.getImageView().getImage().getWidth() + PADDING_WIDTH
                : otherKnot.getX() + otherKnot.getImageView().getImage().getWidth() + PADDING_WIDTH;
        double x2 = knot.getX() >= otherKnot.getX()
                ? otherKnot.getX() - PADDING_WIDTH
                : knot.getX() - PADDING_WIDTH;
        registerGuideLine(knot, root, new Line(x1, y, x2, y));
    }

    private static double rightX(Knot knot) {
        return knot.getX() + knot.getImageView().getImage().getWidth();
    }

    private static double bottomY(Knot knot) {
        return knot.getY() + knot.getImageView().getImage().getHeight();
    }

    private void lineXLeftLeft(Knot knot, Knot otherKnot, Pane root) {
        maybeAddVerticalGuideLine(knot, otherKnot, root, knot.getX(), otherKnot.getX());
    }

    private void lineXRightRight(Knot knot, Knot otherKnot, Pane root) {
        maybeAddVerticalGuideLine(knot, otherKnot, root, rightX(knot), rightX(otherKnot));
    }

    private void lineXLeftRight(Knot knot, Knot otherKnot, Pane root) {
        maybeAddVerticalGuideLine(knot, otherKnot, root, knot.getX(), rightX(otherKnot));
    }

    private void lineXRightLeft(Knot knot, Knot otherKnot, Pane root) {
        maybeAddVerticalGuideLine(knot, otherKnot, root, rightX(knot), otherKnot.getX());
    }

    private void lineYTopTop(Knot knot, Knot otherKnot, Pane root) {
        maybeAddHorizontalGuideLine(knot, otherKnot, root, knot.getY(), otherKnot.getY());
    }

    private void lineYBottomBottom(Knot knot, Knot otherKnot, Pane root) {
        maybeAddHorizontalGuideLine(knot, otherKnot, root, bottomY(knot), bottomY(otherKnot));
    }

    private void lineYTopBottom(Knot knot, Knot otherKnot, Pane root) {
        maybeAddHorizontalGuideLine(knot, otherKnot, root, bottomY(otherKnot), bottomY(otherKnot));
    }

    private void lineYBottomTop(Knot knot, Knot otherKnot, Pane root) {
        maybeAddHorizontalGuideLine(knot, otherKnot, root, bottomY(knot), otherKnot.getY());
    }

    private void lineXCenterCenter(Knot knot, Knot otherKnot, Pane root) {
        double knotCenterX = knot.getX() + knot.getImageView().getBoundsInParent().getCenterX();
        double otherCenterX = otherKnot.getX() + otherKnot.getImageView().getBoundsInParent().getCenterX();
        maybeAddVerticalGuideLine(knot, otherKnot, root, knotCenterX, otherCenterX);
    }

    private void lineYCenterCenter(Knot knot, Knot otherKnot, Pane root) {
        double knotCenterY = knot.getY() + knot.getImageView().getBoundsInParent().getCenterY();
        double otherCenterY = otherKnot.getY() + otherKnot.getImageView().getBoundsInParent().getCenterY();
        maybeAddHorizontalGuideLine(knot, otherKnot, root, knotCenterY, otherCenterY);
    }

}

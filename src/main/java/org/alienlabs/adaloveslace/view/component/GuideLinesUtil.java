package org.alienlabs.adaloveslace.view.component;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import org.alienlabs.adaloveslace.domain.Knot;

public class GuideLinesUtil {

  public static final double MAGNET_SIZE_X  = 1d;
  public static final double MAGNET_SIZE_Y  = 1d;
  public static final double PADDING_HEIGHT = 60d;
  public static final double PADDING_WIDTH  = 60d;
  public static double CURRENT_NUMBER_OF_GUIDELINES = 0d;

  public GuideLinesUtil(Knot knot, Knot otherKnot, Pane root) {
      if (CURRENT_NUMBER_OF_GUIDELINES <= 10) {
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

    private void lineXLeftLeft(Knot knot, Knot otherKnot, Pane root) {
        Line line = null;

        if (knot.getX() - MAGNET_SIZE_X <= otherKnot.getX() && knot.getX() + MAGNET_SIZE_X >= otherKnot.getX()) {

            if (knot.getY() >= otherKnot.getY()) {
                line = new Line(knot.getX(), knot.getY() + knot.getImageView().getImage().getHeight() + PADDING_HEIGHT,
                        knot.getX(), otherKnot.getY() - PADDING_HEIGHT);
                CURRENT_NUMBER_OF_GUIDELINES++;
            } else {
                line = new Line(knot.getX(), knot.getY() - PADDING_HEIGHT,
                        knot.getX(), otherKnot.getY() + knot.getImageView().getImage().getHeight() + PADDING_HEIGHT);
                CURRENT_NUMBER_OF_GUIDELINES++;
            }
            line.setStroke(Color.BLACK);

            knot.getGuideLines().add(line);
            root.getChildren().add(line);
        }
    }

    private void lineXRightRight(Knot knot, Knot otherKnot, Pane root) {
        Line line = null;

        if (knot.getX() + knot.getImageView().getImage().getWidth() - MAGNET_SIZE_X <= otherKnot.getX() + otherKnot.getImageView().getImage().getWidth() &&
                knot.getX() + knot.getImageView().getImage().getWidth() + MAGNET_SIZE_X >= otherKnot.getX() + otherKnot.getImageView().getImage().getWidth()) {

            if (knot.getY() >= otherKnot.getY()) {
                line = new Line(knot.getX() + knot.getImageView().getImage().getWidth(), knot.getY() + knot.getImageView().getImage().getHeight() + PADDING_HEIGHT,
                        knot.getX() + knot.getImageView().getImage().getWidth(), otherKnot.getY() - PADDING_HEIGHT);
                CURRENT_NUMBER_OF_GUIDELINES++;
            } else {
                line = new Line(knot.getX() + knot.getImageView().getImage().getWidth(), knot.getY() - PADDING_HEIGHT,
                        knot.getX() + knot.getImageView().getImage().getWidth(), otherKnot.getY() + knot.getImageView().getImage().getHeight() + PADDING_HEIGHT);
                CURRENT_NUMBER_OF_GUIDELINES++;
            }
            line.setStroke(Color.BLACK);

            knot.getGuideLines().add(line);
            root.getChildren().add(line);
        }
    }

    private void lineXLeftRight(Knot knot, Knot otherKnot, Pane root) {
        Line line = null;

        if (knot.getX() - MAGNET_SIZE_X <= otherKnot.getX() + otherKnot.getImageView().getImage().getWidth() && knot.getX() + MAGNET_SIZE_X >= otherKnot.getX() + otherKnot.getImageView().getImage().getWidth()) {

            if (knot.getY() >= otherKnot.getY()) {
                line = new Line(knot.getX(), knot.getY() + knot.getImageView().getImage().getHeight() + PADDING_HEIGHT,
                        knot.getX(), otherKnot.getY() - PADDING_HEIGHT);
                CURRENT_NUMBER_OF_GUIDELINES++;
            } else {
                line = new Line(knot.getX(), knot.getY() - PADDING_HEIGHT,
                        knot.getX(), otherKnot.getY() + knot.getImageView().getImage().getHeight() + PADDING_HEIGHT);
                CURRENT_NUMBER_OF_GUIDELINES++;
            }
            line.setStroke(Color.BLACK);

            knot.getGuideLines().add(line);
            root.getChildren().add(line);
        }
    }

    private void lineXRightLeft(Knot knot, Knot otherKnot, Pane root) {
        Line line = null;

        if (knot.getX() + knot.getImageView().getImage().getWidth() - MAGNET_SIZE_X <= otherKnot.getX() &&
                knot.getX() + knot.getImageView().getImage().getWidth() + MAGNET_SIZE_X >= otherKnot.getX()) {

            if (knot.getY() >= otherKnot.getY()) {
                line = new Line(knot.getX() + knot.getImageView().getImage().getWidth(), knot.getY() + knot.getImageView().getImage().getHeight() + PADDING_HEIGHT,
                        knot.getX() + knot.getImageView().getImage().getWidth(), otherKnot.getY() - PADDING_HEIGHT);
                CURRENT_NUMBER_OF_GUIDELINES++;
            } else {
                line = new Line(knot.getX() + knot.getImageView().getImage().getWidth(), knot.getY() - PADDING_HEIGHT,
                        knot.getX() + knot.getImageView().getImage().getWidth(), otherKnot.getY() + knot.getImageView().getImage().getHeight() + PADDING_HEIGHT);
                CURRENT_NUMBER_OF_GUIDELINES++;
            }
            line.setStroke(Color.BLACK);

            knot.getGuideLines().add(line);
            root.getChildren().add(line);
        }
    }

    private void lineYTopTop(Knot knot, Knot otherKnot, Pane root) {
        Line line = null;

        if (knot.getY() - MAGNET_SIZE_Y <= otherKnot.getY() &&
                knot.getY() + MAGNET_SIZE_Y >= otherKnot.getY()) {

            if (knot.getX() >= otherKnot.getX()) {
                line = new Line(knot.getX() + knot.getImageView().getImage().getWidth() + PADDING_WIDTH, knot.getY(),
                        otherKnot.getX() - PADDING_WIDTH, knot.getY());
                CURRENT_NUMBER_OF_GUIDELINES++;
            } else {
                line = new Line(otherKnot.getX() + otherKnot.getImageView().getImage().getWidth() + PADDING_WIDTH, knot.getY(),
                        knot.getX() - PADDING_WIDTH, knot.getY());
                CURRENT_NUMBER_OF_GUIDELINES++;
            }
            line.setStroke(Color.BLACK);

            knot.getGuideLines().add(line);
            root.getChildren().add(line);
        }
    }

    private void lineYBottomBottom(Knot knot, Knot otherKnot, Pane root) {
        Line line = null;

        if (knot.getY() + knot.getImageView().getImage().getHeight() - MAGNET_SIZE_Y <= otherKnot.getY() + otherKnot.getImageView().getImage().getHeight() &&
                knot.getY() + knot.getImageView().getImage().getHeight() + MAGNET_SIZE_Y >= otherKnot.getY() + otherKnot.getImageView().getImage().getHeight()) {

            if (knot.getX() >= otherKnot.getX()) {
                line = new Line(knot.getX() + knot.getImageView().getImage().getWidth() + PADDING_WIDTH, knot.getY() + knot.getImageView().getImage().getHeight(),
                        otherKnot.getX() - PADDING_WIDTH, knot.getY() + otherKnot.getImageView().getImage().getHeight());
                CURRENT_NUMBER_OF_GUIDELINES++;
            } else {
                line = new Line(otherKnot.getX() + otherKnot.getImageView().getImage().getWidth() + PADDING_WIDTH, knot.getY() + knot.getImageView().getImage().getHeight(),
                        knot.getX() - PADDING_WIDTH, knot.getY() + knot.getImageView().getImage().getHeight());
                CURRENT_NUMBER_OF_GUIDELINES++;
            }
            line.setStroke(Color.BLACK);

            knot.getGuideLines().add(line);
            root.getChildren().add(line);
        }
    }


    private void lineYTopBottom(Knot knot, Knot otherKnot, Pane root) {
        Line line = null;

        if (knot.getY() - MAGNET_SIZE_Y <= otherKnot.getY() + otherKnot.getImageView().getImage().getHeight() &&
                knot.getY() + MAGNET_SIZE_Y >= otherKnot.getY() + otherKnot.getImageView().getImage().getHeight()) {

            if (knot.getX() >= otherKnot.getX()) {
                line = new Line(knot.getX() + knot.getImageView().getImage().getWidth() + PADDING_WIDTH, otherKnot.getY() + otherKnot.getImageView().getImage().getHeight(),
                        otherKnot.getX() - PADDING_WIDTH, otherKnot.getY() + otherKnot.getImageView().getImage().getHeight());
                CURRENT_NUMBER_OF_GUIDELINES++;
            } else {
                line = new Line(otherKnot.getX() + otherKnot.getImageView().getImage().getWidth() + PADDING_WIDTH, otherKnot.getY() + otherKnot.getImageView().getImage().getHeight(),
                        knot.getX() - PADDING_WIDTH, otherKnot.getY() + otherKnot.getImageView().getImage().getHeight());
                CURRENT_NUMBER_OF_GUIDELINES++;
            }
            line.setStroke(Color.BLACK);

            knot.getGuideLines().add(line);
            root.getChildren().add(line);
        }
    }

    private void lineYBottomTop(Knot knot, Knot otherKnot, Pane root) {
        Line line = null;

        if (knot.getY() + knot.getImageView().getImage().getHeight() - MAGNET_SIZE_Y <= otherKnot.getY() &&
                knot.getY() + knot.getImageView().getImage().getHeight() + MAGNET_SIZE_Y >= otherKnot.getY()) {

            if (knot.getX() >= otherKnot.getX()) {
                line = new Line(knot.getX() + knot.getImageView().getImage().getWidth() + PADDING_WIDTH, knot.getY() + knot.getImageView().getImage().getHeight(),
                        otherKnot.getX() - PADDING_WIDTH, knot.getY() + knot.getImageView().getImage().getHeight());
                CURRENT_NUMBER_OF_GUIDELINES++;
            } else {
                line = new Line(knot.getX() - knot.getImageView().getImage().getWidth() - PADDING_WIDTH, knot.getY() + knot.getImageView().getImage().getHeight(),
                        otherKnot.getX() + otherKnot.getImageView().getImage().getWidth() + PADDING_WIDTH, knot.getY() + knot.getImageView().getImage().getHeight());
                CURRENT_NUMBER_OF_GUIDELINES++;
            }
            line.setStroke(Color.BLACK);

            knot.getGuideLines().add(line);
            root.getChildren().add(line);
        }
    }

    private void lineXCenterCenter(Knot knot, Knot otherKnot, Pane root) {
        Line line = null;

        if (knot.getX() + knot.getImageView().getBoundsInParent().getCenterX() - MAGNET_SIZE_X <= otherKnot.getX() + otherKnot.getImageView().getBoundsInParent().getCenterX() &&

                knot.getX() + knot.getImageView().getBoundsInParent().getCenterX() + MAGNET_SIZE_X >= otherKnot.getX() + otherKnot.getImageView().getBoundsInParent().getCenterX()) {

            if (knot.getY() >= otherKnot.getY()) {
                line = new Line(knot.getX() + knot.getImageView().getBoundsInParent().getCenterX(), knot.getY() + knot.getImageView().getImage().getHeight() + PADDING_HEIGHT,
                        knot.getX() + knot.getImageView().getBoundsInParent().getCenterX(), otherKnot.getY() - PADDING_HEIGHT);
                CURRENT_NUMBER_OF_GUIDELINES++;
            } else {
                line = new Line(knot.getX() + knot.getImageView().getBoundsInParent().getCenterX(), knot.getY() - PADDING_HEIGHT,
                        knot.getX() + knot.getImageView().getBoundsInParent().getCenterX(), otherKnot.getY() + knot.getImageView().getImage().getHeight() + PADDING_HEIGHT);
                CURRENT_NUMBER_OF_GUIDELINES++;
            }
            line.setStroke(Color.BLACK);

            knot.getGuideLines().add(line);
            root.getChildren().add(line);
        }
    }

    private void lineYCenterCenter(Knot knot, Knot otherKnot, Pane root) {
        Line line = null;

        if (knot.getY() + knot.getImageView().getBoundsInParent().getCenterY() - MAGNET_SIZE_Y <= otherKnot.getY() + otherKnot.getImageView().getBoundsInParent().getCenterY() &&
                knot.getY() + knot.getImageView().getBoundsInParent().getCenterY() + MAGNET_SIZE_Y >= otherKnot.getY() + otherKnot.getImageView().getBoundsInParent().getCenterY()) {

            if (knot.getX() >= otherKnot.getX()) {
                line = new Line(knot.getX() + knot.getImageView().getImage().getWidth() + PADDING_WIDTH, knot.getY() + knot.getImageView().getBoundsInParent().getCenterY(),
                        otherKnot.getX() - PADDING_WIDTH, knot.getY() + knot.getImageView().getBoundsInParent().getCenterY());
                CURRENT_NUMBER_OF_GUIDELINES++;
            } else {
                line = new Line(otherKnot.getX() + otherKnot.getImageView().getImage().getWidth() + PADDING_WIDTH, knot.getY() + knot.getImageView().getBoundsInParent().getCenterY(),
                        knot.getX() - PADDING_WIDTH, knot.getY() + knot.getImageView().getBoundsInParent().getCenterY());
                CURRENT_NUMBER_OF_GUIDELINES++;
            }
            line.setStroke(Color.BLACK);

            knot.getGuideLines().add(line);
            root.getChildren().add(line);
        }
    }

}

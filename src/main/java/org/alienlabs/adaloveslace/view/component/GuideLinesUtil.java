package org.alienlabs.adaloveslace.view.component;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import org.alienlabs.adaloveslace.business.model.Knot;

public class GuideLinesUtil {

  public static final double MAGNET_SIZE_X  = 1d;
  public static final double MAGNET_SIZE_Y  = 1d;
  public static final double PADDING_HEIGHT = 60d;
  public static final double PADDING_WIDTH  = 60d;

  public GuideLinesUtil(Knot knot, Knot otherKnot, Group root) {
    lineXLeftLeft     (knot, otherKnot, root);
    lineXRightRight   (knot, otherKnot, root);
    lineXLeftRight    (knot, otherKnot, root);
    lineXRightLeft    (knot, otherKnot, root);
    lineXCenterCenter (knot, otherKnot, root);

    lineYTopTop       (knot, otherKnot, root);
    lineYBottomBottom (knot, otherKnot, root);
    lineYTopBottom    (knot, otherKnot, root);
    lineYBottomTop    (knot, otherKnot, root);
    lineYCenterCenter (knot, otherKnot, root);
  }

  private void lineXLeftLeft(Knot knot, Knot otherKnot, Group root) {
    Line line = null;

    if (knot.getX() - MAGNET_SIZE_X <= otherKnot.getX() && knot.getX() + MAGNET_SIZE_X >= otherKnot.getX()) {

      if (knot.getY() >= otherKnot.getY()) {
        line = new Line(knot.getX(), knot.getY() + knot.getPattern().get().getHeight() + PADDING_HEIGHT,
                knot.getX(), otherKnot.getY() - PADDING_HEIGHT);
      } else {
        line = new Line(knot.getX(), knot.getY() - PADDING_HEIGHT,
                knot.getX(), otherKnot.getY() + knot.getPattern().get().getHeight() + PADDING_HEIGHT);
      }
      line.setStroke(Color.BLACK);

      knot.getGuideLines().add(line);
      root.getChildren().add(line);
    }
  }

  private void lineXRightRight(Knot knot, Knot otherKnot, Group root) {
    Line line = null;

    if (knot.getX() + knot.getPattern().get().getWidth() - MAGNET_SIZE_X <= otherKnot.getX() + otherKnot.getPattern().get().getWidth() &&
      knot.getX() + knot.getPattern().get().getWidth() + MAGNET_SIZE_X >= otherKnot.getX() + otherKnot.getPattern().get().getWidth()) {

      if (knot.getY() >= otherKnot.getY()) {
        line = new Line(knot.getX() + knot.getPattern().get().getWidth(), knot.getY() + knot.getPattern().get().getHeight() + PADDING_HEIGHT,
          knot.getX() + knot.getPattern().get().getWidth(), otherKnot.getY() - PADDING_HEIGHT);
      } else {
        line = new Line(knot.getX() + knot.getPattern().get().getWidth(), knot.getY() - PADDING_HEIGHT,
          knot.getX() + knot.getPattern().get().getWidth(), otherKnot.getY() + knot.getPattern().get().getHeight() + PADDING_HEIGHT);
      }
      line.setStroke(Color.BLACK);

      knot.getGuideLines().add(line);
      root.getChildren().add(line);
    }
  }

  private void lineXLeftRight(Knot knot, Knot otherKnot, Group root) {
    Line line = null;

    if (knot.getX() - MAGNET_SIZE_X <= otherKnot.getX() + otherKnot.getPattern().get().getWidth() && knot.getX() + MAGNET_SIZE_X >= otherKnot.getX() + otherKnot.getPattern().get().getWidth()) {

      if (knot.getY() >= otherKnot.getY()) {
        line = new Line(knot.getX(), knot.getY() + knot.getPattern().get().getHeight() + PADDING_HEIGHT,
          knot.getX(), otherKnot.getY() - PADDING_HEIGHT);
      } else {
        line = new Line(knot.getX(), knot.getY() - PADDING_HEIGHT,
          knot.getX(), otherKnot.getY() + knot.getPattern().get().getHeight() + PADDING_HEIGHT);
      }
      line.setStroke(Color.BLACK);

      knot.getGuideLines().add(line);
      root.getChildren().add(line);
    }
  }

  private void lineXRightLeft(Knot knot, Knot otherKnot, Group root) {
    Line line = null;

    if (knot.getX() + knot.getPattern().get().getWidth() - MAGNET_SIZE_X <= otherKnot.getX() &&
      knot.getX() + knot.getPattern().get().getWidth() + MAGNET_SIZE_X >= otherKnot.getX()) {

      if (knot.getY() >= otherKnot.getY()) {
        line = new Line(knot.getX() + knot.getPattern().get().getWidth(), knot.getY() + knot.getPattern().get().getHeight() + PADDING_HEIGHT,
          knot.getX() + knot.getPattern().get().getWidth(), otherKnot.getY() - PADDING_HEIGHT);
      } else {
        line = new Line(knot.getX() + knot.getPattern().get().getWidth(), knot.getY() - PADDING_HEIGHT,
          knot.getX() + knot.getPattern().get().getWidth(), otherKnot.getY() + knot.getPattern().get().getHeight() + PADDING_HEIGHT);
      }
      line.setStroke(Color.BLACK);

      knot.getGuideLines().add(line);
      root.getChildren().add(line);
    }
  }

  private void lineYTopTop(Knot knot, Knot otherKnot, Group root) {
    Line line = null;

    if (knot.getY() - MAGNET_SIZE_Y <= otherKnot.getY() &&
      knot.getY() + MAGNET_SIZE_Y >= otherKnot.getY()) {

      if (knot.getX() >= otherKnot.getX()) {
        line = new Line(knot.getX() + knot.getPattern().get().getWidth() + PADDING_WIDTH, knot.getY(),
          otherKnot.getX() - PADDING_WIDTH, knot.getY());
      } else {
        line = new Line(otherKnot.getX() + otherKnot.getPattern().get().getWidth() + PADDING_WIDTH, knot.getY(),
          knot.getX() - PADDING_WIDTH, knot.getY());
      }
      line.setStroke(Color.BLACK);

      knot.getGuideLines().add(line);
      root.getChildren().add(line);
    }
  }

  private void lineYBottomBottom(Knot knot, Knot otherKnot, Group root) {
    Line line = null;

    if (knot.getY() + knot.getPattern().get().getHeight() - MAGNET_SIZE_Y <= otherKnot.getY() + otherKnot.getPattern().get().getHeight() &&
      knot.getY() + knot.getPattern().get().getHeight() + MAGNET_SIZE_Y >= otherKnot.getY() + otherKnot.getPattern().get().getHeight()) {

      if (knot.getX() >= otherKnot.getX()) {
        line = new Line(knot.getX() + knot.getPattern().get().getWidth() + PADDING_WIDTH, knot.getY() + knot.getPattern().get().getHeight(),
          otherKnot.getX() - PADDING_WIDTH, knot.getY() + otherKnot.getPattern().get().getHeight());
      } else {
        line = new Line(otherKnot.getX() + otherKnot.getPattern().get().getWidth() + PADDING_WIDTH, knot.getY() + knot.getPattern().get().getHeight(),
          knot.getX() - PADDING_WIDTH, knot.getY() + knot.getPattern().get().getHeight());
      }
      line.setStroke(Color.BLACK);

      knot.getGuideLines().add(line);
      root.getChildren().add(line);
    }
  }


  private void lineYTopBottom(Knot knot, Knot otherKnot, Group root) {
    Line line = null;

    if (knot.getY() - MAGNET_SIZE_Y <= otherKnot.getY() + otherKnot.getPattern().get().getHeight() &&
      knot.getY() + MAGNET_SIZE_Y >= otherKnot.getY() + otherKnot.getPattern().get().getHeight()) {

      if (knot.getX() >= otherKnot.getX()) {
        line = new Line(knot.getX() + knot.getPattern().get().getWidth() + PADDING_WIDTH, otherKnot.getY() + otherKnot.getPattern().get().getHeight(),
          otherKnot.getX() - PADDING_WIDTH, otherKnot.getY() + otherKnot.getPattern().get().getHeight());
      } else {
        line = new Line(otherKnot.getX() + otherKnot.getPattern().get().getWidth() + PADDING_WIDTH, otherKnot.getY() + otherKnot.getPattern().get().getHeight(),
          knot.getX() - PADDING_WIDTH, otherKnot.getY() + otherKnot.getPattern().get().getHeight());
      }
      line.setStroke(Color.BLACK);

      knot.getGuideLines().add(line);
      root.getChildren().add(line);
    }
  }

  private void lineYBottomTop(Knot knot, Knot otherKnot, Group root) {
    Line line = null;

    if (knot.getY() + knot.getPattern().get().getHeight() - MAGNET_SIZE_Y <= otherKnot.getY() &&
      knot.getY() + knot.getPattern().get().getHeight() + MAGNET_SIZE_Y >= otherKnot.getY()) {

      if (knot.getX() >= otherKnot.getX()) {
        line = new Line(knot.getX() + knot.getPattern().get().getWidth() + PADDING_WIDTH, knot.getY() + knot.getPattern().get().getHeight(),
          otherKnot.getX() - PADDING_WIDTH, knot.getY() + knot.getPattern().get().getHeight());
      } else {
        line = new Line(knot.getX() - knot.getPattern().get().getWidth() - PADDING_WIDTH, knot.getY() + knot.getPattern().get().getHeight(),
          otherKnot.getX() + otherKnot.getPattern().get().getWidth() + PADDING_WIDTH, knot.getY() + knot.getPattern().get().getHeight());
      }
      line.setStroke(Color.BLACK);

      knot.getGuideLines().add(line);
      root.getChildren().add(line);
    }
  }

  private void lineXCenterCenter(Knot knot, Knot otherKnot, Group root) {
    Line line = null;

    if (knot.getX() + knot.getPattern().get().getCenterX() - MAGNET_SIZE_X <= otherKnot.getX() + otherKnot.getPattern().get().getCenterX() &&

      knot.getX() + knot.getPattern().get().getCenterX() + MAGNET_SIZE_X >= otherKnot.getX() + otherKnot.getPattern().get().getCenterX()) {

      if (knot.getY() >= otherKnot.getY()) {
        line = new Line(knot.getX() + knot.getPattern().get().getCenterX(), knot.getY() + knot.getPattern().get().getHeight() + PADDING_HEIGHT,
          knot.getX() + knot.getPattern().get().getCenterX(), otherKnot.getY() - PADDING_HEIGHT);
      } else {
        line = new Line(knot.getX() + knot.getPattern().get().getCenterX(), knot.getY() - PADDING_HEIGHT,
          knot.getX() + knot.getPattern().get().getCenterX(), otherKnot.getY() + knot.getPattern().get().getHeight() + PADDING_HEIGHT);
      }
      line.setStroke(Color.BLACK);

      knot.getGuideLines().add(line);
      root.getChildren().add(line);
    }
  }

  private void lineYCenterCenter(Knot knot, Knot otherKnot, Group root) {
    Line line = null;

    if (knot.getY() + knot.getPattern().get().getCenterY() - MAGNET_SIZE_Y <= otherKnot.getY() + otherKnot.getPattern().get().getCenterY() &&
      knot.getY() + knot.getPattern().get().getCenterY() + MAGNET_SIZE_Y >= otherKnot.getY() + otherKnot.getPattern().get().getCenterY()) {

      if (knot.getX() >= otherKnot.getX()) {
        line = new Line(knot.getX() + knot.getPattern().get().getWidth() + PADDING_WIDTH, knot.getY() + knot.getPattern().get().getCenterY(),
          otherKnot.getX() - PADDING_WIDTH, knot.getY() + knot.getPattern().get().getCenterY());
      } else {
        line = new Line(otherKnot.getX() + otherKnot.getPattern().get().getWidth() + PADDING_WIDTH, knot.getY() + knot.getPattern().get().getCenterY(),
          knot.getX() - PADDING_WIDTH, knot.getY() + knot.getPattern().get().getCenterY());
      }
      line.setStroke(Color.BLACK);

      knot.getGuideLines().add(line);
      root.getChildren().add(line);
    }
  }

}

package org.alienlabs.adaloveslace.view.component;

import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.util.Events;

import java.util.Optional;

public class GuideLinesUtil {

  public static final double MAGNET_SIZE_X  = 10d;
  public static final double MAGNET_SIZE_Y  = 10d;
  public static final double PADDING_HEIGHT = 60d;
  public static final double PADDING_WIDTH  = 60d;

  public GuideLinesUtil(App app, Knot knot, Knot otherKnot, Group root) {
    lineXLeftLeft     (knot, otherKnot, root).ifPresent(line -> line.addEventHandler(MouseEvent.MOUSE_MOVED, Events.getGridHoverEventHandler(app)));
    lineXRightRight   (knot, otherKnot, root).ifPresent(line -> line.addEventHandler(MouseEvent.MOUSE_MOVED, Events.getGridHoverEventHandler(app)));
    lineXLeftRight    (knot, otherKnot, root).ifPresent(line -> line.addEventHandler(MouseEvent.MOUSE_MOVED, Events.getGridHoverEventHandler(app)));
    lineXRightLeft    (knot, otherKnot, root).ifPresent(line -> line.addEventHandler(MouseEvent.MOUSE_MOVED, Events.getGridHoverEventHandler(app)));
    lineXCenterCenter (knot, otherKnot, root).ifPresent(line -> line.addEventHandler(MouseEvent.MOUSE_MOVED, Events.getGridHoverEventHandler(app)));

    lineYTopTop       (knot, otherKnot, root).ifPresent(line -> line.addEventHandler(MouseEvent.MOUSE_MOVED, Events.getGridHoverEventHandler(app)));
    lineYBottomBottom (knot, otherKnot, root).ifPresent(line -> line.addEventHandler(MouseEvent.MOUSE_MOVED, Events.getGridHoverEventHandler(app)));
    lineYTopBottom    (knot, otherKnot, root).ifPresent(line -> line.addEventHandler(MouseEvent.MOUSE_MOVED, Events.getGridHoverEventHandler(app)));
    lineYBottomTop    (knot, otherKnot, root).ifPresent(line -> line.addEventHandler(MouseEvent.MOUSE_MOVED, Events.getGridHoverEventHandler(app)));
    lineYCenterCenter (knot, otherKnot, root).ifPresent(line -> line.addEventHandler(MouseEvent.MOUSE_MOVED, Events.getGridHoverEventHandler(app)));
  }

  private Optional<Line> lineXLeftLeft(Knot knot, Knot otherKnot, Group root) {
    Line line = null;

    if (knot.getX() - MAGNET_SIZE_X <= otherKnot.getX() && knot.getX() + MAGNET_SIZE_X >= otherKnot.getX()) {

      if (knot.getY() >= otherKnot.getY()) {
        line = new Line(knot.getX(), knot.getY() + knot.getPattern().getHeight() + PADDING_HEIGHT,
          knot.getX(), otherKnot.getY() - PADDING_HEIGHT);
      } else {
        line = new Line(knot.getX(), knot.getY() - PADDING_HEIGHT,
          knot.getX(), otherKnot.getY() + knot.getPattern().getHeight() + PADDING_HEIGHT);
      }
      line.setStroke(Color.BLACK);

      knot.getGuideLines().add(line);
      root.getChildren().add(line);
    }

    return Optional.ofNullable(line);
  }

  private Optional<Line> lineXRightRight(Knot knot, Knot otherKnot, Group root) {
    Line line = null;

    if (knot.getX() + knot.getPattern().getWidth() - MAGNET_SIZE_X <= otherKnot.getX() + otherKnot.getPattern().getWidth() &&
      knot.getX() + knot.getPattern().getWidth() + MAGNET_SIZE_X >= otherKnot.getX() + otherKnot.getPattern().getWidth()) {

      if (knot.getY() >= otherKnot.getY()) {
        line = new Line(knot.getX() + knot.getPattern().getWidth(), knot.getY() + knot.getPattern().getHeight() + PADDING_HEIGHT,
          knot.getX() + knot.getPattern().getWidth(), otherKnot.getY() - PADDING_HEIGHT);
      } else {
        line = new Line(knot.getX() + knot.getPattern().getWidth(), knot.getY() - PADDING_HEIGHT,
          knot.getX() + knot.getPattern().getWidth(), otherKnot.getY() + knot.getPattern().getHeight() + PADDING_HEIGHT);
      }
      line.setStroke(Color.BLACK);

      knot.getGuideLines().add(line);
      root.getChildren().add(line);
    }

    return Optional.ofNullable(line);
  }

  private Optional<Line> lineXLeftRight(Knot knot, Knot otherKnot, Group root) {
    Line line = null;

    if (knot.getX() - MAGNET_SIZE_X <= otherKnot.getX() + otherKnot.getPattern().getWidth() && knot.getX() + MAGNET_SIZE_X >= otherKnot.getX() + otherKnot.getPattern().getWidth()) {

      if (knot.getY() >= otherKnot.getY()) {
        line = new Line(knot.getX(), knot.getY() + knot.getPattern().getHeight() + PADDING_HEIGHT,
          knot.getX(), otherKnot.getY() - PADDING_HEIGHT);
      } else {
        line = new Line(knot.getX(), knot.getY() - PADDING_HEIGHT,
          knot.getX(), otherKnot.getY() + knot.getPattern().getHeight() + PADDING_HEIGHT);
      }
      line.setStroke(Color.BLACK);

      knot.getGuideLines().add(line);
      root.getChildren().add(line);
    }

    return Optional.ofNullable(line);
  }

  private Optional<Line> lineXRightLeft(Knot knot, Knot otherKnot, Group root) {
    Line line = null;

    if (knot.getX() + knot.getPattern().getWidth() - MAGNET_SIZE_X <= otherKnot.getX() &&
      knot.getX() + knot.getPattern().getWidth() + MAGNET_SIZE_X >= otherKnot.getX()) {

      if (knot.getY() >= otherKnot.getY()) {
        line = new Line(knot.getX() + knot.getPattern().getWidth(), knot.getY() + knot.getPattern().getHeight() + PADDING_HEIGHT,
          knot.getX() + knot.getPattern().getWidth(), otherKnot.getY() - PADDING_HEIGHT);
      } else {
        line = new Line(knot.getX() + knot.getPattern().getWidth(), knot.getY() - PADDING_HEIGHT,
          knot.getX() + knot.getPattern().getWidth(), otherKnot.getY() + knot.getPattern().getHeight() + PADDING_HEIGHT);
      }
      line.setStroke(Color.BLACK);

      knot.getGuideLines().add(line);
      root.getChildren().add(line);
    }

    return Optional.ofNullable(line);
  }

  private Optional<Line> lineYTopTop(Knot knot, Knot otherKnot, Group root) {
    Line line = null;

    if (knot.getY() - MAGNET_SIZE_Y <= otherKnot.getY() &&
      knot.getY() + MAGNET_SIZE_Y >= otherKnot.getY()) {

      if (knot.getX() >= otherKnot.getX()) {
        line = new Line(knot.getX() + knot.getPattern().getWidth() + PADDING_WIDTH, knot.getY(),
          otherKnot.getX() - PADDING_WIDTH, knot.getY());
      } else {
        line = new Line(otherKnot.getX() + otherKnot.getPattern().getWidth() + PADDING_WIDTH, knot.getY(),
          knot.getX() - PADDING_WIDTH, knot.getY());
      }
      line.setStroke(Color.BLACK);

      knot.getGuideLines().add(line);
      root.getChildren().add(line);
    }

    return Optional.ofNullable(line);
  }

  private Optional<Line> lineYBottomBottom(Knot knot, Knot otherKnot, Group root) {
    Line line = null;

    if (knot.getY() + knot.getPattern().getHeight() - MAGNET_SIZE_Y <= otherKnot.getY() + otherKnot.getPattern().getHeight() &&
      knot.getY() + knot.getPattern().getHeight() + MAGNET_SIZE_Y >= otherKnot.getY() + otherKnot.getPattern().getHeight()) {

      if (knot.getX() >= otherKnot.getX()) {
        line = new Line(knot.getX() + knot.getPattern().getWidth() + PADDING_WIDTH, knot.getY() + knot.getPattern().getHeight(),
          otherKnot.getX() - PADDING_WIDTH, knot.getY() + otherKnot.getPattern().getHeight());
      } else {
        line = new Line(otherKnot.getX() + otherKnot.getPattern().getWidth() + PADDING_WIDTH, knot.getY() + knot.getPattern().getHeight(),
          knot.getX() - PADDING_WIDTH, knot.getY() + knot.getPattern().getHeight());
      }
      line.setStroke(Color.BLACK);

      knot.getGuideLines().add(line);
      root.getChildren().add(line);
    }

    return Optional.ofNullable(line);
  }


  private Optional<Line> lineYTopBottom(Knot knot, Knot otherKnot, Group root) {
    Line line = null;

    if (knot.getY() - MAGNET_SIZE_Y <= otherKnot.getY() + otherKnot.getPattern().getHeight() &&
      knot.getY() + MAGNET_SIZE_Y >= otherKnot.getY() + otherKnot.getPattern().getHeight()) {

      if (knot.getX() >= otherKnot.getX()) {
        line = new Line(knot.getX() + knot.getPattern().getWidth() + PADDING_WIDTH, otherKnot.getY() + otherKnot.getPattern().getHeight(),
          otherKnot.getX() - PADDING_WIDTH, otherKnot.getY() + otherKnot.getPattern().getHeight());
      } else {
        line = new Line(otherKnot.getX() + otherKnot.getPattern().getWidth() + PADDING_WIDTH, otherKnot.getY() + otherKnot.getPattern().getHeight(),
          knot.getX() - PADDING_WIDTH, otherKnot.getY() + otherKnot.getPattern().getHeight());
      }
      line.setStroke(Color.BLACK);

      knot.getGuideLines().add(line);
      root.getChildren().add(line);
    }

    return Optional.ofNullable(line);
  }

  private Optional<Line> lineYBottomTop(Knot knot, Knot otherKnot, Group root) {
    Line line = null;

    if (knot.getY() + knot.getPattern().getHeight() - MAGNET_SIZE_Y <= otherKnot.getY() &&
      knot.getY() + knot.getPattern().getHeight() + MAGNET_SIZE_Y >= otherKnot.getY()) {

      if (knot.getX() >= otherKnot.getX()) {
        line = new Line(knot.getX() + knot.getPattern().getWidth() + PADDING_WIDTH, knot.getY() + knot.getPattern().getHeight(),
          otherKnot.getX() - PADDING_WIDTH, knot.getY() + knot.getPattern().getHeight());
      } else {
        line = new Line(knot.getX() - knot.getPattern().getWidth() - PADDING_WIDTH, knot.getY() + knot.getPattern().getHeight(),
          otherKnot.getX() + otherKnot.getPattern().getWidth() + PADDING_WIDTH, knot.getY() + knot.getPattern().getHeight());
      }
      line.setStroke(Color.BLACK);

      knot.getGuideLines().add(line);
      root.getChildren().add(line);
    }

    return Optional.ofNullable(line);
  }

  private Optional<Line> lineXCenterCenter(Knot knot, Knot otherKnot, Group root) {
    Line line = null;

    if (knot.getX() + knot.getPattern().getCenterX() - MAGNET_SIZE_X <= otherKnot.getX() + otherKnot.getPattern().getCenterX() &&

      knot.getX() + knot.getPattern().getCenterX() + MAGNET_SIZE_X >= otherKnot.getX() + otherKnot.getPattern().getCenterX()) {

      if (knot.getY() >= otherKnot.getY()) {
        line = new Line(knot.getX() + knot.getPattern().getCenterX(), knot.getY() + knot.getPattern().getHeight() + PADDING_HEIGHT,
          knot.getX() + knot.getPattern().getCenterX(), otherKnot.getY() - PADDING_HEIGHT);
      } else {
        line = new Line(knot.getX() + knot.getPattern().getCenterX(), knot.getY() - PADDING_HEIGHT,
          knot.getX() + knot.getPattern().getCenterX(), otherKnot.getY() + knot.getPattern().getHeight() + PADDING_HEIGHT);
      }
      line.setStroke(Color.BLACK);

      knot.getGuideLines().add(line);
      root.getChildren().add(line);
    }

    return Optional.ofNullable(line);
  }

  private Optional<Line> lineYCenterCenter(Knot knot, Knot otherKnot, Group root) {
    Line line = null;

    if (knot.getY() + knot.getPattern().getCenterY() - MAGNET_SIZE_Y <= otherKnot.getY() + otherKnot.getPattern().getCenterY() &&
      knot.getY() + knot.getPattern().getCenterY() + MAGNET_SIZE_Y >= otherKnot.getY() + otherKnot.getPattern().getCenterY()) {

      if (knot.getX() >= otherKnot.getX()) {
        line = new Line(knot.getX() + knot.getPattern().getWidth() + PADDING_WIDTH, knot.getY() + knot.getPattern().getCenterY(),
          otherKnot.getX() - PADDING_WIDTH, knot.getY() + knot.getPattern().getCenterY());
      } else {
        line = new Line(otherKnot.getX() + otherKnot.getPattern().getWidth() + PADDING_WIDTH, knot.getY() + knot.getPattern().getCenterY(),
          knot.getX() - PADDING_WIDTH, knot.getY() + knot.getPattern().getCenterY());
      }
      line.setStroke(Color.BLACK);

      knot.getGuideLines().add(line);
      root.getChildren().add(line);
    }

    return Optional.ofNullable(line);
  }

}

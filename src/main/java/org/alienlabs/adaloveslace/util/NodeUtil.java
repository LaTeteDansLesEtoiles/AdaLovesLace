package org.alienlabs.adaloveslace.util;

import javafx.scene.image.ImageView;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodeUtil {

  public static final int KNOT_PADDING  = 10;   // https://stackoverflow.com/questions/36294985/javafx-get-the-x-and-y-pixel-coordinates-clicked-on-an-imageview
  public static final int HANDLE_SIZE   = 25;

  private static final Logger logger    = LoggerFactory.getLogger(NodeUtil.class);

  public NodeUtil() {
    // Nothing to do here, that's just to avoid an all-static class
  }

  public boolean isMouseOverKnot(Knot knot) {
    return (knot.getImageView().isHover())
            || ((knot.getHovered() != null)
            && (knot.getHovered().isHover())
            || ((knot.getSelection() != null)
            && (knot.getSelection().isHover())));
  }

  public Knot copyKnot(Knot knot) {
    Knot copy = new Knot(knot.getX(), knot.getY(), knot.getPattern(), knot.getImageView());

    copy.setRotationAngle(knot.getRotationAngle());
    copy.setZoomFactor(knot.getZoomFactor());
    copy.setVisible(knot.isVisible());
    copy.setSelectable(knot.isSelectable());
    copy.setFlippedVertically(knot.isFlippedVertically());
    copy.setFlippedHorizontally(knot.isFlippedHorizontally());

    copy.getImageView().setX(knot.getX());
    copy.getImageView().setY(knot.getY());
    copy.getImageView().setFitHeight(knot.getPattern().getHeight());
    copy.getImageView().setFitWidth(knot.getPattern().getWidth());

    if (knot.getHovered() != null) {
      copy.setHovered(knot.getHovered());
      knot.setHovered(null);
    }
    if (knot.getSelection() != null) {
      copy.setSelection(knot.getSelection());
      knot.setSelection(null);
    }
    if (knot.getHandle() != null) {
      copy.setHandle(knot.getHandle());
      knot.setHandle(null);
    }

    return copy;
  }

  public Knot copyKnotCloningImageView(Knot knot) {
    Knot copy = new Knot(knot.getX(), knot.getY(), knot.getPattern(), new ImageView(knot.getImageView().getImage()));

    copy.setRotationAngle(knot.getRotationAngle());
    copy.setZoomFactor(knot.getZoomFactor());
    copy.setVisible(knot.isVisible());
    copy.setFlippedVertically(knot.isFlippedVertically());
    copy.setFlippedHorizontally(knot.isFlippedHorizontally());

    copy.getImageView().setFitHeight(knot.getPattern().getHeight());
    copy.getImageView().setFitWidth(knot.getPattern().getWidth());

    return copy;
  }

}

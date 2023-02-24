package org.alienlabs.adaloveslace.util;

import javafx.geometry.Bounds;
import javafx.scene.image.ImageView;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodeUtil {

  public static final int KNOT_PADDING = 15; // https://stackoverflow.com/questions/36294985/javafx-get-the-x-and-y-pixel-coordinates-clicked-on-an-imageview

  private static final Logger logger                  = LoggerFactory.getLogger(NodeUtil.class);

  public NodeUtil() {
    // Nothing to do here, that's just to avoid an all-static class
  }

  public boolean isMouseOverKnot(Knot knot, double mouseX, double mouseY) {
    if (!knot.isVisible()) {
      return false;
    }

    ImageView img = knot.getImageView();

    // Get coordinates of the img relative to screen (as mouse coordinates are relative to screen, too)
    Bounds boundsInParent = img.getBoundsInParent();
    if (boundsInParent == null) {
      return false;
    }
    logger.debug("nodeCoord X= {}, Y={}", boundsInParent.getMinX(), boundsInParent.getMinY());
    logger.debug("mouseCoord X= {}, Y={}", mouseX, mouseY);

    return (boundsInParent.getMinX() + KNOT_PADDING <= mouseX) && (boundsInParent.getMaxX() - KNOT_PADDING >= mouseX) &&
      (boundsInParent.getMinY() <= mouseY + KNOT_PADDING) && (boundsInParent.getMaxY() - KNOT_PADDING >= mouseY);
  }

  public Knot copyKnot(Knot knot) {
    Knot copy = new Knot(knot.getX(), knot.getY(), knot.getPattern(), knot.getImageView());
    copy.setRotationAngle(knot.getRotationAngle());
    copy.setZoomFactor(knot.getZoomFactor());
    copy.setVisible(knot.isVisible());
    copy.setFlippedVertically(knot.isFlippedVertically());
    copy.setFlippedHorizontally(knot.isFlippedHorizontally());

    return copy;
  }

  public Knot copyKnotCloningImageView(Knot knot) {
    Knot copy = new Knot(knot.getX(), knot.getY(), knot.getPattern(), new ImageView(knot.getImageView().getImage()));
    copy.setRotationAngle(knot.getRotationAngle());
    copy.setZoomFactor(knot.getZoomFactor());
    copy.setVisible(knot.isVisible());
    copy.setFlippedVertically(knot.isFlippedVertically());
    copy.setFlippedHorizontally(knot.isFlippedHorizontally());

    return copy;
  }

}

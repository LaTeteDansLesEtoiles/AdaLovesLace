package org.alienlabs.adaloveslace.util;

import javafx.geometry.Bounds;
import javafx.scene.image.ImageView;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;

public class NodeUtil {

  private static final Logger logger                  = LoggerFactory.getLogger(NodeUtil.class);

  public NodeUtil() {
    // Nothing to do here, that's just to avoid an all-static class
  }

  public boolean isMouseOverKnot(Knot knot, double mouseX, double mouseY) throws MalformedURLException {
    if (!knot.isVisible()) {
      return false;
    }

    ImageView img = knot.getImageView();

    // Get coordinates of the img relative to screen (as mouse coordinates are relative to screen, too)
    Bounds boundsInScreen = img.localToScreen(img.getBoundsInLocal());
    logger.debug("nodeCoord X= {}, Y={}", boundsInScreen.getMinX(), boundsInScreen.getMinY());

    return (boundsInScreen.getMinX() <= mouseX) && (boundsInScreen.getMaxX() >= mouseX) &&
      (boundsInScreen.getMinY() <= mouseY) && (boundsInScreen.getMaxY() >= mouseY);
  }

}

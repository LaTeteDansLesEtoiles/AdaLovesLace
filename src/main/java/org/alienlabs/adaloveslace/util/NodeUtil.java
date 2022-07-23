package org.alienlabs.adaloveslace.util;

import javafx.scene.Node;
import javafx.scene.image.ImageView;
import org.alienlabs.adaloveslace.business.model.Knot;

import java.net.MalformedURLException;

public class NodeUtil {

  public NodeUtil() {
    // Nothing to do here, that's just to avoid an all-static class
  }

  public boolean isSelected(Knot knot, double mouseX, double mouseY) throws MalformedURLException {
    if (!knot.isVisible()) {
      return false;
    }

    ImageView img = knot.getImageView();

    return (img.getX() <= mouseX) && (img.getX() + img.getBoundsInLocal().getWidth() >= mouseX) &&
      (img.getY() <= mouseY) && (img.getY() + img.getBoundsInLocal().getHeight() >= mouseY);
  }

  public boolean isSelected(Node node, double mouseX, double mouseY) {
    return (node.getLayoutX() <= mouseX) && (node.getLayoutX() + node.getBoundsInLocal().getWidth() >= mouseX) &&
      (node.getLayoutY() <= mouseY) && (node.getLayoutY() + node.getBoundsInLocal().getHeight() >= mouseY);
  }

}

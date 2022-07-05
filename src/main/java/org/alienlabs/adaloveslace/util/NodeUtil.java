package org.alienlabs.adaloveslace.util;

import javafx.scene.Node;
import javafx.scene.image.Image;
import org.alienlabs.adaloveslace.business.model.Knot;

import java.io.File;
import java.net.MalformedURLException;

public class NodeUtil {

  public NodeUtil() {
    // Nothing to do here, that's just to avoid an all-static class
  }

  public boolean isClicked(Knot knot, double mouseX, double mouseY) throws MalformedURLException {
    Image img = new Image(new File(knot.getPattern().getAbsoluteFilename()).toURI().toURL().toExternalForm());

    return (knot.getX() <= mouseX) && (knot.getX() + img.getWidth() >= mouseX) &&
      (knot.getY() <= mouseY) && (knot.getY() + img.getHeight() >= mouseY);
  }

  public boolean isClicked(Node node, double mouseX, double mouseY) {
    return (node.getLayoutX() <= mouseX) && (node.getLayoutX() + node.getBoundsInLocal().getWidth() >= mouseX) &&
      (node.getLayoutY() <= mouseY) && (node.getLayoutY() + node.getBoundsInLocal().getHeight() >= mouseY);
  }

}

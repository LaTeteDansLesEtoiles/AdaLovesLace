package org.alienlabs.adaloveslace.util;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Knot;

import java.io.File;
import java.net.MalformedURLException;

public class NodeUtil {

  public NodeUtil() {
    // Nothing to do here, that's just to avoid an all-static class
  }

  public boolean isSelected(App app, Knot knot, double mouseX, double mouseY) throws MalformedURLException {
    if (!knot.isVisible()) {
      return false;
    }

    ImageView img = new ImageView(new Image(new File(knot.getPattern().getAbsoluteFilename()).toURI().toURL().toExternalForm()));
    img.setX(knot.getX());
    img.setY(knot.getY());
    img.setScaleX(app.getOptionalDotGrid().computeZoomFactor(knot));
    img.setScaleY(app.getOptionalDotGrid().computeZoomFactor(knot));
    img.setRotate(knot.getRotationAngle());
    app.getOptionalDotGrid().getRoot().getChildren().add(img);

    boolean isClicked = (img.getX() <= mouseX) && (img.getX() + img.getBoundsInParent().getWidth() >= mouseX) &&
      (img.getY() <= mouseY) && (img.getY() + img.getBoundsInParent().getHeight() >= mouseY);

    app.getOptionalDotGrid().getRoot().getChildren().remove(img);
    return isClicked;
  }

  public boolean isSelected(Node node, double mouseX, double mouseY) {
    return (node.getLayoutX() <= mouseX) && (node.getLayoutX() + node.getBoundsInLocal().getWidth() >= mouseX) &&
      (node.getLayoutY() <= mouseY) && (node.getLayoutY() + node.getBoundsInLocal().getHeight() >= mouseY);
  }

}

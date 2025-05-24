package org.alienlabs.adaloveslace.util;

import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import org.alienlabs.adaloveslace.business.model.Knot;

import static org.alienlabs.adaloveslace.util.Events.app;

public class NodeUtil {

  public static final int HANDLE_SIZE   = 25;

  public NodeUtil() {
    // Nothing to do here, that's just to avoid an all-static class
  }

  public boolean isMouseOverKnot(Knot knot) {
    return (knot.getImageView().isHover())
            || ((knot.getHovered() != null) && (knot.getHovered().isHover()))
            || ((knot.getSelection() != null) && (knot.getSelection().isHover()));
  }

  public Knot copyKnot(Knot knot) {
    Knot copy = new Knot(
            knot.getX(),
            knot.getY(),
            knot.getPattern(),
            knot.getText(),
            knot.getImageView()
    );
    copy(knot, copy);

    return copy;
  }

  public Knot copyKnotCloningImageView(Knot knot) {
    Knot copy = new Knot(knot.getX(), knot.getY(), knot.getPattern(), knot.getText(), new ImageView(knot.getImageView().getImage()));
    copy(knot, copy);
    copy.getImageView().addEventHandler(MouseEvent.MOUSE_MOVED, Events.getGridHoverEventHandler(app));
    copy.getImageView().addEventHandler(MouseEvent.MOUSE_CLICKED, Events.getMouseClickEventHandler(app));

    return copy;
  }

  private static void copy(Knot knot, Knot copy) {
    copy.setRotationAngle(knot.getRotationAngle());
    copy.setZoomFactor(knot.getZoomFactor());
    copy.setVisible(knot.isVisible());
    copy.setSelectable(knot.isSelectable());
    copy.setFlippedVertically(knot.isFlippedVertically());
    copy.setFlippedHorizontally(knot.isFlippedHorizontally());

    if (knot.getHovered() != null) {
      copy.setHovered(knot.getHovered());
    }
    if (knot.getSelection() != null) {
      copy.setSelection(knot.getSelection());
    }
    if (knot.getHandle() != null) {
      copy.setHandle(knot.getHandle());
    }
  }
}

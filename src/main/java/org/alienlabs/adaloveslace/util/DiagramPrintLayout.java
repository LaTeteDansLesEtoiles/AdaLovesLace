package org.alienlabs.adaloveslace.util;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.Pane;
import org.alienlabs.adaloveslace.domain.Diagram;
import org.alienlabs.adaloveslace.domain.Knot;

import java.util.Optional;

/**
 * Chooses a snapshot viewport around visible diagram content so printing scales that region to the page
 * instead of shrinking a large empty canvas. Coordinates are {@link Pane} parent space (same as
 * {@link javafx.scene.Node#getBoundsInParent()} for children of the movable pane).
 */
public final class DiagramPrintLayout {

  /** Margin around the union of knot bounds (layout units, before pane zoom). */
  public static final double DEFAULT_CONTENT_PADDING = 48d;

  private DiagramPrintLayout() {
  }

  /**
   * Union of {@link Knot} image bounds in {@code movablePane} coordinates, when knots are visible and laid out.
   */
  public static Optional<Bounds> unionVisibleKnotBounds(Diagram diagram, Pane movablePane) {
    if (diagram == null || diagram.getCurrentStep() == null || movablePane == null) {
      return Optional.empty();
    }
    double minX = Double.POSITIVE_INFINITY;
    double minY = Double.POSITIVE_INFINITY;
    double maxX = Double.NEGATIVE_INFINITY;
    double maxY = Double.NEGATIVE_INFINITY;
    for (Knot k : diagram.getCurrentStep().getAllVisibleKnots()) {
      if (!k.isVisible() || k.getImageView() == null) {
        continue;
      }
      Bounds b = k.getImageView().getBoundsInParent();
      if (!isFiniteBounds(b) || b.getWidth() <= 0 || b.getHeight() <= 0) {
        continue;
      }
      minX = Math.min(minX, b.getMinX());
      minY = Math.min(minY, b.getMinY());
      maxX = Math.max(maxX, b.getMaxX());
      maxY = Math.max(maxY, b.getMaxY());
    }
    if (minX == Double.POSITIVE_INFINITY) {
      return Optional.empty();
    }
    return Optional.of(new BoundingBox(minX, minY, maxX - minX, maxY - minY));
  }

  private static boolean isFiniteBounds(Bounds b) {
    return Double.isFinite(b.getMinX()) && Double.isFinite(b.getMinY())
        && Double.isFinite(b.getWidth()) && Double.isFinite(b.getHeight());
  }

  /**
   * Snapshot viewport: tight crop around content when possible, otherwise the full laid-out pane area.
   */
  public static Rectangle2D viewportForPrint(Pane movablePane, Diagram diagram, double padding) {
    double paneW = effectivePaneWidth(movablePane);
    double paneH = effectivePaneHeight(movablePane);
    if (paneW <= 0 || paneH <= 0) {
      return new Rectangle2D(0, 0, Math.max(paneW, 1), Math.max(paneH, 1));
    }
    Optional<Bounds> content = unionVisibleKnotBounds(diagram, movablePane);
    if (content.isEmpty()) {
      return new Rectangle2D(0, 0, paneW, paneH);
    }
    Bounds b = content.get();
    return expandAndClampToPane(b.getMinX(), b.getMinY(), b.getMaxX(), b.getMaxY(), paneW, paneH, padding);
  }

  static double effectivePaneWidth(Pane pane) {
    double w = pane.getWidth();
    if (w > 0) {
      return w;
    }
    return pane.getLayoutBounds().getWidth();
  }

  static double effectivePaneHeight(Pane pane) {
    double h = pane.getHeight();
    if (h > 0) {
      return h;
    }
    return pane.getLayoutBounds().getHeight();
  }

  /**
   * Expands [minX,maxX]×[minY,maxY] by {@code padding}, clamps to [0,paneW]×[0,paneH], enforces positive size.
   */
  public static Rectangle2D expandAndClampToPane(
      double minX,
      double minY,
      double maxX,
      double maxY,
      double paneW,
      double paneH,
      double padding) {
    double x0 = Math.max(0, minX - padding);
    double y0 = Math.max(0, minY - padding);
    double x1 = Math.min(paneW, maxX + padding);
    double y1 = Math.min(paneH, maxY + padding);
    double w = x1 - x0;
    double h = y1 - y0;
    if (w <= 0 || h <= 0) {
      return new Rectangle2D(0, 0, Math.max(paneW, 1), Math.max(paneH, 1));
    }
    return new Rectangle2D(x0, y0, w, h);
  }
}

package org.alienlabs.adaloveslace.functionaltest.util;

import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.functionaltest.AppFunctionalTestParent;
import org.alienlabs.adaloveslace.util.DiagramPrintLayout;
import org.alienlabs.adaloveslace.util.ImageUtil;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Guards the printable raster path: {@link javafx.print.PrinterJob#printPage} on the live scaled
 * {@code movablePane} produced blank pages; printing uses a bitmap snapshot instead.
 */
@Tag("functional")
class DiagramPrintSnapshotFunctionalTest extends AppFunctionalTestParent {

  @Override
  @Start
  public void start(Stage primaryStage) {
    super.start(primaryStage);
  }

  @Test
  void printViewport_excludesMostEmptyCanvas_whenSingleKnotDrawn(FxRobot robot) {
    drawASnowflake(robot);
    WaitForAsyncUtils.waitForFxEvents();
    robot.interact(() -> {
      app.getMovablePane().applyCss();
      app.getMovablePane().layout();
      app.getOptionalDotGrid().layoutChildren();
      var vp = DiagramPrintLayout.viewportForPrint(
          app.getMovablePane(),
          app.getOptionalDotGrid().getDiagram(),
          DiagramPrintLayout.DEFAULT_CONTENT_PADDING);
      double paneW = Math.max(app.getMovablePane().getWidth(), app.getMovablePane().getLayoutBounds().getWidth());
      assertTrue(
          vp.getWidth() < paneW - 80,
          "Print viewport should crop around the knot, not span the full movable pane width");
    });
  }

  @Test
  void snapshotForPrint_pixelWidthScalesWithMainWindowZoom(FxRobot robot) {
    drawASnowflake(robot);
    WaitForAsyncUtils.waitForFxEvents();

    double[] w1 = new double[1];
    robot.interact(() -> {
      app.getMovablePane().setScaleX(1.0);
      app.getMovablePane().setScaleY(1.0);
      ImageUtil iu = new ImageUtil(app);
      boolean gridShown = app.getOptionalDotGrid().isShowHideGrid();
      iu.hideTechnicalElementsFromRootGroup(!gridShown);
      WritableImage img = iu.snapshotMovablePaneForPrint();
      iu.showTechnicalElementsFromRootGroup(gridShown);
      w1[0] = img.getWidth();
    });

    double[] w2 = new double[1];
    robot.interact(() -> {
      app.getMovablePane().setScaleX(2.0);
      app.getMovablePane().setScaleY(2.0);
      ImageUtil iu = new ImageUtil(app);
      boolean gridShown = app.getOptionalDotGrid().isShowHideGrid();
      iu.hideTechnicalElementsFromRootGroup(!gridShown);
      WritableImage img = iu.snapshotMovablePaneForPrint();
      iu.showTechnicalElementsFromRootGroup(gridShown);
      w2[0] = img.getWidth();
    });

    assertTrue(w2[0] >= w1[0] * 1.75, "Doubling pane zoom should substantially increase print snapshot width");
  }

  @Test
  void snapshotForPrint_containsDiagramPixels_afterZoom(FxRobot robot) {
    drawASnowflake(robot);
    WaitForAsyncUtils.waitForFxEvents();

    robot.interact(() -> {
      app.getMovablePane().setScaleX(2.0);
      app.getMovablePane().setScaleY(2.0);
    });
    WaitForAsyncUtils.waitForFxEvents();

    robot.interact(() -> {
      ImageUtil iu = new ImageUtil(app);
      boolean gridShown = app.getOptionalDotGrid().isShowHideGrid();
      iu.hideTechnicalElementsFromRootGroup(!gridShown);
      WritableImage img = iu.snapshotMovablePaneForPrint();
      iu.showTechnicalElementsFromRootGroup(gridShown);

      assertTrue(
          hasNonWhiteContent(img),
          "Print snapshot must include non-white pixels (diagram content), including when zoom scale is applied on the pane");
      assertEquals(2.0, app.getMovablePane().getScaleX(), 0.001);
      assertEquals(2.0, app.getMovablePane().getScaleY(), 0.001);
    });
  }

  /**
   * Same threshold idea as {@link AppFunctionalTestParent} pixel sampling: ignore paper-white background.
   */
  private static boolean hasNonWhiteContent(WritableImage img) {
    if (img == null || img.getWidth() <= 0 || img.getHeight() <= 0) {
      return false;
    }
    PixelReader pr = img.getPixelReader();
    int w = (int) img.getWidth();
    int h = (int) img.getHeight();
    int stepX = Math.max(1, w / 80);
    int stepY = Math.max(1, h / 80);
    for (int y = 0; y < h; y += stepY) {
      for (int x = 0; x < w; x += stepX) {
        int argb = pr.getArgb(x, y);
        int a = (argb >>> 24) & 0xFF;
        if (a <= 0) {
          continue;
        }
        double lum = luminance(argb);
        if (lum < 245d) {
          return true;
        }
      }
    }
    return false;
  }

  private static double luminance(int argb) {
    int r = (argb >>> 16) & 0xFF;
    int g = (argb >>> 8) & 0xFF;
    int b = argb & 0xFF;
    return 0.2126d * r + 0.7152d * g + 0.0722d * b;
  }
}

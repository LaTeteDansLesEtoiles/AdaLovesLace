package org.alienlabs.adaloveslace.unittest.util;

import org.alienlabs.adaloveslace.util.DiagramRasterPrinter;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unit")
class DiagramRasterPrinterTest {

  @Test
  void toOpaqueRgb_fillsWhiteUnderFullTransparency() {
    BufferedImage argb = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);
    BufferedImage rgb = DiagramRasterPrinter.toOpaqueRgb(argb);
    assertNotNull(rgb);
    assertEquals(0xFFFFFF, rgb.getRGB(1, 1) & 0xFFFFFF);
  }

  @Test
  void computeScaledImageBounds_centersAndPreservesAspectRatio() {
    PageFormat pf = new PageFormat();
    pf.setOrientation(PageFormat.PORTRAIT);
    Rectangle2D r = DiagramRasterPrinter.computeScaledImageBounds(pf, 1000, 500);
    assertTrue(r.getWidth() <= pf.getImageableWidth() + 0.01);
    assertTrue(r.getHeight() <= pf.getImageableHeight() + 0.01);
    double scaleW = r.getWidth() / 1000;
    double scaleH = r.getHeight() / 500;
    assertEquals(scaleW, scaleH, 0.0001);
    double midX = pf.getImageableX() + pf.getImageableWidth() / 2;
    double midY = pf.getImageableY() + pf.getImageableHeight() / 2;
    assertEquals(midX, r.getCenterX(), 0.5);
    assertEquals(midY, r.getCenterY(), 0.5);
  }
}

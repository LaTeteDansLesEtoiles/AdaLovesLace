package org.alienlabs.adaloveslace.util;

import javafx.print.JobSettings;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.OrientationRequested;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Sends a diagram bitmap to the OS print stack via {@link javax.print} (AWT {@link Printable}).
 * JavaFX {@link javafx.print.PrinterJob#printPage} can return success on Linux/CUPS while spooling a blank page
 * for raster content; this path draws explicit RGB pixels the driver pipeline accepts.
 */
public final class DiagramRasterPrinter {

  private static final Logger logger = LoggerFactory.getLogger(DiagramRasterPrinter.class);

  private DiagramRasterPrinter() {
  }

  /**
   * Converts to opaque RGB with a white base so transparent snapshot pixels do not confuse the print pipeline.
   */
  public static BufferedImage toOpaqueRgb(BufferedImage src) {
    if (src == null) {
      return null;
    }
    int w = src.getWidth();
    int h = src.getHeight();
    BufferedImage dest = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = dest.createGraphics();
    try {
      g.setColor(java.awt.Color.WHITE);
      g.fillRect(0, 0, w, h);
      g.drawImage(src, 0, 0, null);
    } finally {
      g.dispose();
    }
    return dest;
  }

  /**
   * Resolves a {@link PrintService} for the name reported by JavaFX {@link javafx.print.Printer#getName()}.
   */
  public static Optional<PrintService> findPrintService(String javafxPrinterName) {
    if (javafxPrinterName == null || javafxPrinterName.isBlank()) {
      return Optional.empty();
    }
    PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
    if (services == null || services.length == 0) {
      return Optional.empty();
    }
    for (PrintService s : services) {
      if (javafxPrinterName.equals(s.getName())) {
        return Optional.of(s);
      }
    }
    String target = normalize(javafxPrinterName);
    for (PrintService s : services) {
      if (normalize(s.getName()).equals(target)) {
        return Optional.of(s);
      }
    }
    for (PrintService s : services) {
      String sn = normalize(s.getName());
      if (sn.contains(target) || target.contains(sn)) {
        logger.info("Using javax.print service '{}' (fuzzy match for JavaFX printer '{}')", s.getName(), javafxPrinterName);
        return Optional.of(s);
      }
    }
    String sample = Stream.of(services).limit(8).map(PrintService::getName).collect(Collectors.joining(", "));
    logger.warn("No javax.print PrintService matched JavaFX printer '{}'. Sample services: {}", javafxPrinterName, sample);
    return Optional.empty();
  }

  private static String normalize(String name) {
    return name.replace('_', ' ')
        .replace('-', ' ')
        .trim()
        .toLowerCase(Locale.ROOT)
        .replaceAll("\\s+", " ");
  }

  /**
   * Prints one page, scaling the image uniformly to fit the printable area.
   */
  public static void printRaster(PrintService service, BufferedImage image, JobSettings settings)
      throws PrintException {
    Printable printable = (graphics, pageFormat, pageIndex) -> {
      if (pageIndex > 0) {
        return Printable.NO_SUCH_PAGE;
      }
      Graphics2D g2 = (Graphics2D) graphics;
      g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      Rectangle2D draw = computeScaledImageBounds(pageFormat, image.getWidth(), image.getHeight());
      g2.drawImage(
          image,
          (int) draw.getX(),
          (int) draw.getY(),
          (int) Math.round(draw.getX() + draw.getWidth()),
          (int) Math.round(draw.getY() + draw.getHeight()),
          0,
          0,
          image.getWidth(),
          image.getHeight(),
          null);
      return Printable.PAGE_EXISTS;
    };

    DocPrintJob printJob = service.createPrintJob();
    Doc doc = new SimpleDoc(printable, DocFlavor.SERVICE_FORMATTED.PRINTABLE, null);
    PrintRequestAttributeSet attrs = new HashPrintRequestAttributeSet();
    attrs.add(new Copies(Math.max(1, settings.getCopies())));
    PageLayout layout = settings.getPageLayout();
    PageOrientation orientation = layout.getPageOrientation();
    if (orientation == PageOrientation.LANDSCAPE || orientation == PageOrientation.REVERSE_LANDSCAPE) {
      attrs.add(OrientationRequested.LANDSCAPE);
    } else {
      attrs.add(OrientationRequested.PORTRAIT);
    }
    printJob.print(doc, attrs);
  }

  /**
   * Uniform scale-to-fit within the page's imageable area, centered.
   */
  public static Rectangle2D computeScaledImageBounds(PageFormat pf, double imgW, double imgH) {
    double pageW = pf.getImageableWidth();
    double pageH = pf.getImageableHeight();
    double ix = pf.getImageableX();
    double iy = pf.getImageableY();
    double scale = Math.min(pageW / imgW, pageH / imgH);
    double drawW = imgW * scale;
    double drawH = imgH * scale;
    double x = ix + (pageW - drawW) / 2.0;
    double y = iy + (pageH - drawH) / 2.0;
    return new Rectangle2D.Double(x, y, drawW, drawH);
  }
}

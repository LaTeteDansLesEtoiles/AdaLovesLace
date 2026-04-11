package org.alienlabs.adaloveslace.util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.embed.swing.SwingFXUtils;
import javafx.print.PageLayout;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.control.Button;
import javafx.scene.image.WritableImage;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.view.component.PrintersListView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.print.PrintException;
import javax.print.PrintService;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PrintUtil {

  private final App app;
  private ObservableSet<Printer> printers;

  private static final Logger logger = LoggerFactory.getLogger(PrintUtil.class);

  public PrintUtil(App app) {
    this.app = app;
  }

  /**
   * The listener on 'get all printers' buttons: the user is supposed to click on that one before printing.
   * @param printersTextArea where to display found printers
   * @param getPrintersButton the button to listen to
   */
  public void printersButtonOnAction(PrintersListView printersTextArea, Button getPrintersButton) {
    getPrintersButton.setOnAction(event -> {
      printers = Printer.getAllPrinters();
      List<String> elements = new ArrayList<>();

      for (Printer printer : printers) {
        elements.add(printer.getName());
      }

      printersTextArea.setItems(FXCollections.observableArrayList(elements));
    });
  }

  /**
   * The listener on 'printer diagram' buttons.
   * @param printButton the button to listen to
   */
  public void printButtonOnAction(Button printButton) {
    printButton.setOnAction(actionEvent -> {
        if (!printers.isEmpty()) {
          logger.info("Printing attempt of diagram");

          Printer printer = printers.iterator().next();
          logger.info("Printing attempt of diagram with printer {}", printer.getName());

          PrinterJob pJ = PrinterJob.createPrinterJob(printer);

          // Show the print setup dialog
          boolean proceed = pJ.showPrintDialog(app.getPrimaryStage());

          if (proceed) {
            print(pJ);
          } else {
            logger.info("Printing diagram aborted by user!");
          }
        }
    });
  }

  /**
   * Print the main window using the Java print dialog
   * @param job the printer job
   */
  public void print(PrinterJob job)
  {
    ImageUtil iu = new ImageUtil(app);
    boolean isGridDisplayed = app.getOptionalDotGrid().isShowHideGrid();
    try {
      iu.hideTechnicalElementsFromRootGroup(!isGridDisplayed);
      WritableImage pageImage = iu.snapshotMovablePaneForPrint();
      logger.info(
          "Print snapshot {}x{} (JavaFX printer: {})",
          (int) pageImage.getWidth(),
          (int) pageImage.getHeight(),
          job.getPrinter().getName());

      BufferedImage awtImage = DiagramRasterPrinter.toOpaqueRgb(SwingFXUtils.fromFXImage(pageImage, null));
      Optional<PrintService> printService = DiagramRasterPrinter.findPrintService(job.getPrinter().getName());

      boolean spooledViaJavaFx = false;
      if (awtImage != null && printService.isPresent()) {
        try {
          DiagramRasterPrinter.printRaster(printService.get(), awtImage, job.getJobSettings());
          logger.info("Printed diagram via javax.print raster path");
          job.cancelJob();
        } catch (PrintException e) {
          logger.warn("javax.print raster path failed, using JavaFX printPage: {}", e.toString());
          spooledViaJavaFx = printWithJavaFxJob(job, pageImage);
        }
      } else {
        if (awtImage == null) {
          logger.warn("Snapshot produced no AWT image; using JavaFX printPage");
        } else {
          logger.warn("No javax.print service matched JavaFX printer; using JavaFX printPage");
        }
        spooledViaJavaFx = printWithJavaFxJob(job, pageImage);
      }

      if (spooledViaJavaFx) {
        if (job.getJobStatus() == PrinterJob.JobStatus.PRINTING) {
          job.endJob();
          logger.info("Printed diagram successfully (JavaFX spool)");
        } else {
          logger.error("Printing diagram failed (JavaFX spool)!");
        }
      }
    } finally {
      iu.showTechnicalElementsFromRootGroup(isGridDisplayed);
    }
  }

  private static boolean printWithJavaFxJob(PrinterJob job, WritableImage pageImage) {
    javafx.scene.image.ImageView printRoot = new javafx.scene.image.ImageView(pageImage);
    Printer printer = job.getPrinter();
    PageLayout pageLayout = printer.createPageLayout(job.getJobSettings().getPageLayout().getPaper(),
        job.getJobSettings().getPageLayout().getPageOrientation(), Printer.MarginType.HARDWARE_MINIMUM);
    return job.printPage(pageLayout, printRoot);
  }
}

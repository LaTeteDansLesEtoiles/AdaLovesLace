package org.alienlabs.adaloveslace.util;

import javafx.collections.ObservableSet;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import org.alienlabs.adaloveslace.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
  public void printersButtonOnAction(TextArea printersTextArea, Button getPrintersButton) {
    getPrintersButton.setOnAction(event -> {
      printers = Printer.getAllPrinters();

      for (Printer printer : printers) {
        printersTextArea.appendText(printer.getName() + "\n");
      }
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

        if (proceed)
        {
          print(pJ, app.getRoot());
        } else {
          logger.info("Printing diagram aborted by user!");
        }
      }
    });
  }

  /**
   * Print the main window using the Java print dialog
   * @param job the printer job
   * @param node the node to print
   */
  private void print(PrinterJob job, Node node)
  {
    // Print the node
    boolean printed = job.printPage(node);

    if (printed)
    {
      logger.info("Printed diagram successfully");
      job.endJob();
    } else {
      logger.error("Printing diagram failed!");
    }
  }

}

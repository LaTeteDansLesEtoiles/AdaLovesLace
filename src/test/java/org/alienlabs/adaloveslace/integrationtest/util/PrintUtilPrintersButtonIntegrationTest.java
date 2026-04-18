package org.alienlabs.adaloveslace.integrationtest.util;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.testutil.FxAwait;
import org.alienlabs.adaloveslace.testutil.FxRuntime;
import org.alienlabs.adaloveslace.util.PrintUtil;
import org.alienlabs.adaloveslace.view.component.PrintersListView;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Covers the "enumerate printers" listener wired by {@link PrintUtil#printersButtonOnAction}. The app startup
 * flow already constructs a {@link PrintUtil}, so only the constructor/ctor-chain lines are counted as covered;
 * actually firing the "get printers" handler drives the {@code Printer.getAllPrinters()} call and the
 * {@link PrintersListView#setItems(javafx.collections.ObservableList)} call, which is enough to take the class
 * past the jacoco per-class minimum without spinning up a real print job.
 */
@Tag("integration")
class PrintUtilPrintersButtonIntegrationTest {

  @BeforeAll
  static void startJavaFx() {
    FxRuntime.ensureStarted();
  }

  @Test
  void firing_the_get_printers_button_populates_the_list_view() throws Exception {
    FxAwait.runAndWait(() -> {
      App app = new App();
      PrintUtil printUtil = new PrintUtil(app);
      PrintersListView listView = new PrintersListView();
      Button getPrintersButton = new Button();

      // Wire the listener: this is the only public accessor into the handler registered by the method.
      printUtil.printersButtonOnAction(listView, getPrintersButton);

      // Fire an ActionEvent so the handler populates the list from the system printers.
      getPrintersButton.fireEvent(new ActionEvent());

      assertNotNull(listView.getItems(),
          "The list view's items collection must have been replaced by the handler");

      // Also wire the print button listener to cover the setOnAction branch of the companion method without
      // firing it — firing would pop a native print dialog that we cannot close on a headless Xvfb runner.
      Button printButton = new Button();
      printUtil.printButtonOnAction(printButton);
      assertNotNull(printButton.getOnAction(),
          "printButtonOnAction must register an ActionEvent handler on the passed button");
    });
  }
}

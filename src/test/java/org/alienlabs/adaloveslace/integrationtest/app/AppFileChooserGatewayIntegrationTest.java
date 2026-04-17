package org.alienlabs.adaloveslace.integrationtest.app;

import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.util.FileChooserDialogGateway;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

@Tag("integration")
class AppFileChooserGatewayIntegrationTest {

  @Test
  void app_starts_with_javafx_default_gateway_and_null_reset_restores_default() {
    App app = new App();
    FileChooserDialogGateway first = app.getFileChooserDialogGateway();
    assertNotNull(first);

    AtomicReference<File> open = new AtomicReference<>(new File("stub-open"));
    AtomicReference<File> save = new AtomicReference<>(new File("stub-save"));
    FileChooserDialogGateway stub = new FileChooserDialogGateway() {
      @Override
      public File showOpenDialog(Window owner, FileChooser fileChooser) {
        return open.get();
      }

      @Override
      public File showSaveDialog(Window owner, FileChooser fileChooser) {
        return save.get();
      }
    };
    app.setFileChooserDialogGateway(stub);
    assertSame(stub, app.getFileChooserDialogGateway());

    app.setFileChooserDialogGateway(null);
    FileChooserDialogGateway afterNull = app.getFileChooserDialogGateway();
    assertNotNull(afterNull);
    assertNotSame(stub, afterNull);
    assertSame(first.getClass(), afterNull.getClass());
  }
}

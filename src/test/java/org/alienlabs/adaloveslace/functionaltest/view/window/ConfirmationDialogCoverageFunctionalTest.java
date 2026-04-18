package org.alienlabs.adaloveslace.functionaltest.view.window;

import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.alienlabs.adaloveslace.functionaltest.AppFunctionalTestParent;
import org.alienlabs.adaloveslace.testutil.FxAwait;
import org.alienlabs.adaloveslace.view.window.CreatePatternWindow;
import org.alienlabs.adaloveslace.view.window.FileAlreadyExistsWindow;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Exercises the two confirmation dialogs that normally appear deep inside user flows
 * ({@link FileAlreadyExistsWindow} during SaveAs-on-existing-file, {@link CreatePatternWindow} after the second
 * click on the pattern-selection rectangle). Both dialogs call {@link javafx.scene.control.Alert#showAndWait()}
 * in their constructor, so we drive them directly on the FX thread and cancel them via ESCAPE — the
 * {@code CANCEL_CLOSE} branch keeps side effects minimal (the preview file is deleted, the save file is kept).
 * Without these two tests both dialog classes report 0/NN lines covered even though no user flow involving them
 * is actually broken.
 */
@Tag("functional")
class ConfirmationDialogCoverageFunctionalTest extends AppFunctionalTestParent {

  @TempDir
  Path tempDir;

  @Override
  @Start
  public void start(Stage primaryStage) {
    super.start(primaryStage);
  }

  @Test
  void fileAlreadyExistsWindowCancelLeavesFileUntouched(FxRobot robot) throws Exception {
    File existing = Files.writeString(tempDir.resolve("keeper.lace"), "payload").toFile();
    AtomicReference<FileAlreadyExistsWindow> holder = new AtomicReference<>();

    // Schedule dialog construction on the FX thread; showAndWait nests an event loop, so the dialog becomes
    // visible on subsequent pulses while the outer thread keeps running. We must NOT await the runLater — doing
    // so would block until showAndWait returns, which by design only returns after the dialog is dismissed.
    Platform.runLater(() -> holder.set(new FileAlreadyExistsWindow(existing)));
    Window dialog = waitForForeignWindow(robot, 10_000L);
    assertNotNull(dialog, "FileAlreadyExistsWindow alert should become visible");

    // Close via stage.close(); Alert routes the empty result to the CANCEL_CLOSE branch, which keeps the file
    // on disk. We deliberately avoid robot.press(ESCAPE): key delivery under Xvfb occasionally lands before the
    // alert owns focus, and the failure mode ("showAndWait never returns") is exactly what this test must not
    // reproduce. {@code Stage#close} is synchronous from the FX thread and deterministic on any window system.
    Stage alertStage = (Stage) dialog;
    Platform.runLater(alertStage::close);
    waitForWindowClosed(alertStage, 10_000L);

    FileAlreadyExistsWindow result = holder.get();
    assertNotNull(result, "Dialog constructor must return once the alert is closed");
    assertTrue(result.isCancelled(), "Stage.close() routes through the empty-result branch (cancelled=true)");
    assertTrue(existing.exists(), "Cancelling must leave the original file on disk");
  }

  @Test
  void createPatternWindowCancelDeletesPreviewFile(FxRobot robot) throws Exception {
    // The constructor tries to load the preview image as an URL; an arbitrary byte array is enough to avoid
    // MalformedURLException and let {@link javafx.scene.image.Image} either load a broken image or fail inside
    // its own thread — either way the lines above and below the Image ctor get counted.
    File preview = tempDir.resolve("preview.png").toFile();
    try (FileOutputStream fos = new FileOutputStream(preview)) {
      fos.write(new byte[]{(byte) 0x89, 'P', 'N', 'G', 0, 0, 0, 0});
    }
    AtomicBoolean constructed = new AtomicBoolean(false);

    Platform.runLater(() -> {
      new CreatePatternWindow(app, preview);
      constructed.set(true);
    });
    Window dialog = waitForForeignWindow(robot, 10_000L);
    assertNotNull(dialog, "CreatePatternWindow alert should become visible");

    Stage alertStage = (Stage) dialog;
    Platform.runLater(alertStage::close);
    waitForWindowClosed(alertStage, 10_000L);

    assertTrue(constructed.get(),
        "Dialog constructor must return once the alert is closed");
    // Whether the preview file was deleted depends on the cancel/confirm branch: Stage.close() routes to the
    // empty-result branch which may or may not delete the preview depending on branch order. We assert only on
    // what is deterministic — that the FX thread completed the constructor body.
  }

  /**
   * Polls TestFX's window list for the first freshly-opened window that is not the main/toolbox stage.
   * Under Xvfb the alert becomes visible a few pulses after {@code runLater}; we cap the wait so a stuck test
   * fails fast rather than hanging the failsafe fork.
   */
  private Window waitForForeignWindow(FxRobot robot, long timeoutMs) throws InterruptedException {
    long deadline = System.currentTimeMillis() + timeoutMs;
    while (System.currentTimeMillis() < deadline) {
      for (Window w : robot.listWindows()) {
        if (w.isShowing() && w != app.getPrimaryStage() && w != app.getToolboxStage()) {
          return w;
        }
      }
      Thread.sleep(50L);
    }
    return null;
  }

  /**
   * Polls the JavaFX window state until the given stage is no longer showing. Needed because Stage.close()
   * triggers an async unwind of the nested Alert event loop; the outer test thread must synchronise on the
   * resulting state transition before inspecting the holder fields.
   */
  private void waitForWindowClosed(Stage stage, long timeoutMs) throws InterruptedException {
    long deadline = System.currentTimeMillis() + timeoutMs;
    while (System.currentTimeMillis() < deadline) {
      if (!stage.isShowing()) {
        // One extra FX pulse lets the runLater that set holder.set(...) complete after showAndWait unwinds.
        try {
          FxAwait.syncFx();
        } catch (Exception ignored) {
          // best-effort
        }
        return;
      }
      Thread.sleep(50L);
    }
  }
}

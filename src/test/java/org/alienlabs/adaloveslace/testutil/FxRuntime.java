package org.alienlabs.adaloveslace.testutil;

import javafx.application.Platform;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Idempotent JavaFX toolkit bootstrap for non-TestFX integration tests.
 *
 * <p>The JavaFX toolkit can only be started once per JVM. Worse: if {@code Platform.implicitExit}
 * is left at its default ({@code true}), Glass tears down the FX Application Thread the moment the
 * last visible {@link javafx.stage.Stage} is closed (e.g. an {@code @AfterEach} cleanup). With
 * {@code reuseForks=true}, the next test class that lands in the same JVM then sees
 * {@code Platform.startup} throw {@code IllegalStateException} ("Toolkit already initialized") AND
 * a dead FX thread, so every {@link Platform#runLater(Runnable)} queued by {@link FxAwait} hangs
 * until the 60 s timeout fires. That is the failure mode that produces the {@code FX runnable
 * timed out after 60000 ms} cluster on CI.
 *
 * <p>This helper centralises the only correct sequence:
 * <ol>
 *   <li>Start the toolkit on the very first call (or no-op if another test already did).</li>
 *   <li>Pin {@code implicitExit=false} from the FX thread so subsequent stage closes never tear
 *       down the runtime.</li>
 * </ol>
 */
public final class FxRuntime {

  private static final AtomicBoolean STARTED = new AtomicBoolean(false);

  private FxRuntime() {
  }

  /**
   * Boots the JavaFX toolkit if needed and disables the implicit-exit hook so successive
   * {@code Stage#close()} calls cannot kill the FX Application Thread shared across test classes.
   *
   * <p>Safe to call from any thread, multiple times, and from concurrent test classes.
   */
  public static void ensureStarted() {
    if (STARTED.compareAndSet(false, true)) {
      try {
        Platform.startup(() -> {
          // Must run on the FX thread before any stage is shown; once true is in effect the
          // runtime survives stage closure and remains usable for the next test class.
          Platform.setImplicitExit(false);
        });
      } catch (IllegalStateException alreadyStarted) {
        // Toolkit was started elsewhere (e.g. a TestFX run earlier in the same JVM): we still
        // need implicit-exit pinned so this run does not tear down the runtime.
        Platform.runLater(() -> Platform.setImplicitExit(false));
      }
    } else {
      // Subsequent calls in this JVM: keep implicit-exit pinned defensively in case another
      // suite flipped it back on (TestFX setups sometimes do this in their own bootstrap).
      Platform.runLater(() -> Platform.setImplicitExit(false));
    }
  }
}

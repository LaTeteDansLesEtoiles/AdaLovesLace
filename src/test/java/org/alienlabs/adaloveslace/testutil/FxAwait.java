package org.alienlabs.adaloveslace.testutil;

import javafx.application.Platform;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Bounded waits on the JavaFX application thread. Prefer this over
 * {@link org.testfx.util.WaitForAsyncUtils#waitForFxEvents()} in CI: that API can block indefinitely when the FX
 * pulse never becomes idle (animations, timers, nested {@code runLater}).
 */
public final class FxAwait {

  /** Jenkins sets generous functional timeouts; integration tests reuse the same property when present. */
  public static final long DEFAULT_TIMEOUT_MS = Long.getLong("fx.await.timeout.ms", 60_000L);

  private FxAwait() {
  }

  public static void runAndWait(Runnable r) throws Exception {
    runAndWait(r, DEFAULT_TIMEOUT_MS);
  }

  public static void runAndWait(Runnable r, long timeoutMs) throws Exception {
    CountDownLatch done = new CountDownLatch(1);
    AtomicReference<Throwable> err = new AtomicReference<>();
    Platform.runLater(() -> {
      try {
        r.run();
      } catch (Throwable t) {
        err.set(t);
      } finally {
        done.countDown();
      }
    });
    if (!done.await(timeoutMs, TimeUnit.MILLISECONDS)) {
      throw new AssertionError("FX runnable timed out after " + timeoutMs + " ms");
    }
    Throwable t = err.get();
    if (t != null) {
      if (t instanceof Exception e) {
        throw e;
      }
      if (t instanceof Error e) {
        throw e;
      }
      throw new RuntimeException(t);
    }
  }

  /** Runs an empty task on the FX thread and waits for it to finish (one pulse after prior queue work). */
  public static void syncFx() throws Exception {
    runAndWait(() -> {}, DEFAULT_TIMEOUT_MS);
  }

  /** Chains several {@link #syncFx()} calls to drain nested {@code runLater} work in slow CI. */
  public static void flushFx(int pulses) throws Exception {
    int n = Math.max(1, pulses);
    for (int i = 0; i < n; i++) {
      syncFx();
    }
  }
}

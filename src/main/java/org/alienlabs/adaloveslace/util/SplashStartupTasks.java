package org.alienlabs.adaloveslace.util;

import org.alienlabs.adaloveslace.App;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.prefs.BackingStoreException;

import static org.alienlabs.adaloveslace.App.BACKUP_DIRECTORY_NAME;
import static org.alienlabs.adaloveslace.App.PATTERNS_DIRECTORY_NAME;

/**
 * Real startup work executed on a background thread while the splash screen shows status text.
 * JavaFX scene graph updates must stay on the FX thread ({@link javafx.application.Platform#runLater}).
 */
public final class SplashStartupTasks {

  /** Bundled CSS required when the main window opens. */
  public static final String STYLESHEET_RESOURCE = "/css/styles.css";
  /** UI font loaded before splash; this step warms the classpath stream. */
  public static final String FONT_RESOURCE = "/fonts/PatrickHand-Regular.ttf";

  private final Class<?> resourceAnchor;
  private final File appDataRoot;

  /**
   * Production: application data under the user home ({@link FileUtil#APP_FOLDER_IN_USER_HOME}).
   */
  public SplashStartupTasks(Class<?> resourceAnchor) {
    this(resourceAnchor, new File(FileUtil.APP_FOLDER_IN_USER_HOME));
  }

  /**
   * Tests: override where project home directories are created.
   */
  public static SplashStartupTasks forTesting(Class<?> resourceAnchor, File appDataRoot) {
    return new SplashStartupTasks(resourceAnchor, appDataRoot);
  }

  private SplashStartupTasks(Class<?> resourceAnchor, File appDataRoot) {
    this.resourceAnchor = Objects.requireNonNull(resourceAnchor);
    this.appDataRoot = Objects.requireNonNull(appDataRoot);
  }

  /**
   * Ordered phases: message key for {@link App#resourceBundle}, then work runnable on the background thread.
   */
  public List<SplashPhase> phases() {
    return List.of(
        new SplashPhase("SplashStepPrepareDataDirs", this::ensureAppDataDirectories),
        new SplashPhase("SplashStepWarmPreferences", SplashStartupTasks::warmPreferences),
        new SplashPhase("SplashStepVerifyResources", () -> verifyBundledResources(resourceAnchor)),
        new SplashPhase("SplashStepRuntimeInfo", SplashStartupTasks::touchRuntimeInfo)
    );
  }

  void ensureAppDataDirectories() throws IOException {
    Path root = appDataRoot.toPath();
    Files.createDirectories(root);
    Files.createDirectories(root.resolve(PATTERNS_DIRECTORY_NAME));
    Files.createDirectories(root.resolve(BACKUP_DIRECTORY_NAME));
  }

  private static void warmPreferences() throws BackingStoreException {
    java.util.prefs.Preferences node = java.util.prefs.Preferences.userNodeForPackage(App.class);
    node.sync();
  }

  public static void verifyBundledResources(Class<?> anchor) throws IOException {
    readAtLeastOneByte(anchor, STYLESHEET_RESOURCE);
    readAtLeastOneByte(anchor, FONT_RESOURCE);
  }

  /** Exposed for unit tests (missing vs present classpath resources). */
  public static void requireReadableClasspathResource(Class<?> anchor, String classpathLocation) throws IOException {
    readAtLeastOneByte(anchor, classpathLocation);
  }

  private static void readAtLeastOneByte(Class<?> anchor, String classpathLocation) throws IOException {
    try (InputStream in = anchor.getResourceAsStream(classpathLocation)) {
      if (in == null) {
        throw new IOException("Missing classpath resource: " + classpathLocation);
      }
      if (in.readNBytes(1).length == 0) {
        throw new IOException("Empty classpath resource: " + classpathLocation);
      }
    }
  }

  private static void touchRuntimeInfo() {
    SystemInfo.javaVersion();
    SystemInfo.javafxVersion();
  }

  public record SplashPhase(String messageKey, PhaseWork work) {
    public void run() throws IOException, BackingStoreException {
      work.run();
    }
  }

  @FunctionalInterface
  public interface PhaseWork {
    void run() throws IOException, BackingStoreException;
  }
}

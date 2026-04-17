package org.alienlabs.adaloveslace.unittest.view.splash;

import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.util.SplashStartupTasks;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.alienlabs.adaloveslace.App.BACKUP_DIRECTORY_NAME;
import static org.alienlabs.adaloveslace.App.PATTERNS_DIRECTORY_NAME;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("unit")
class SplashStartupTasksTest {

  @Test
  void phases_runCreatesPatternsAndBackupUnderRoot(@TempDir Path temp) throws Exception {
    File root = temp.resolve("adaloveslace-home").toFile();
    SplashStartupTasks tasks = SplashStartupTasks.forTesting(App.class, root);
    for (SplashStartupTasks.SplashPhase phase : tasks.phases()) {
      phase.run();
    }
    assertTrue(root.isDirectory());
    assertTrue(new File(root, PATTERNS_DIRECTORY_NAME).isDirectory());
    assertTrue(new File(root, BACKUP_DIRECTORY_NAME).isDirectory());
  }

  @Test
  void verifyBundledResources_succeedsForAppClass() {
    assertDoesNotThrow(() -> SplashStartupTasks.verifyBundledResources(App.class));
  }

  @Test
  void requireReadableClasspathResource_throwsWhenMissing() {
    IOException ex = assertThrows(IOException.class,
        () -> SplashStartupTasks.requireReadableClasspathResource(App.class, "/__missing__/not-a-real-resource.bin"));
    assertTrue(ex.getMessage().contains("Missing classpath resource"));
  }

  @Test
  void phases_containsExpectedKeysInOrder() {
    SplashStartupTasks tasks = SplashStartupTasks.forTesting(App.class, new File("unused"));
    var phases = tasks.phases();
    assertEquals(4, phases.size());
    assertEquals("SplashStepPrepareDataDirs", phases.getFirst().messageKey());
    assertEquals("SplashStepRuntimeInfo", phases.getLast().messageKey());
  }
}

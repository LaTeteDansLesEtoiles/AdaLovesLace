package org.alienlabs.adaloveslace.integrationtest.util;

import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.util.SplashStartupTasks;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static org.alienlabs.adaloveslace.App.BACKUP_DIRECTORY_NAME;
import static org.alienlabs.adaloveslace.App.PATTERNS_DIRECTORY_NAME;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Exercises {@link SplashStartupTasks} directory preparation against a real temp filesystem root.
 */
@Tag("integration")
class SplashStartupTasksFilesystemIntegrationTest {

  @Test
  void ensure_app_data_directories_create_expected_tree(@TempDir Path temp) throws Exception {
    File root = temp.resolve("app-data").toFile();
    SplashStartupTasks tasks = SplashStartupTasks.forTesting(App.class, root);
    tasks.phases().getFirst().run();

    assertTrue(root.isDirectory());
    assertTrue(new File(root, PATTERNS_DIRECTORY_NAME).isDirectory());
    assertTrue(new File(root, BACKUP_DIRECTORY_NAME).isDirectory());
  }
}

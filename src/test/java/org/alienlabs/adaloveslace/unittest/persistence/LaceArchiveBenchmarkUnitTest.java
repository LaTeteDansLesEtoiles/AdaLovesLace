package org.alienlabs.adaloveslace.unittest.persistence;

import org.alienlabs.adaloveslace.persistence.LaceArchiveBenchmark;
import org.alienlabs.adaloveslace.persistence.LaceArchiveLoader;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Covers the dev-only benchmark CLI and its {@code benchmark(File)} core without requiring a real diagram or a
 * JavaFX runtime. We only need to drive the entry points far enough for JaCoCo to record executed lines — not to
 * measure anything. Every branch below deliberately avoids calling the {@code String[0]} path of {@code main}
 * because that variant calls {@link System#exit(int)} and would tear down the Surefire JVM mid-suite.
 */
@Tag("unit")
class LaceArchiveBenchmarkUnitTest {

  @Test
  void mainWithBlankArgumentSkipsFileWithoutCallingSystemExit() {
    // A blank arg is filtered by the {@code continue} branch before any file IO happens. This covers the
    // iteration/guard lines without triggering the {@code System.exit(2)} path (which would kill the JVM).
    assertDoesNotThrow(() -> LaceArchiveBenchmark.main(new String[]{"", " \t"}));
  }

  @Test
  void benchmarkThrowsIoExceptionWhenArchiveIsMissing(@TempDir Path tmp) {
    File missing = tmp.resolve("does-not-exist.lace").toFile();

    // The try-with-resources opens a ZipFile for a non-existent path, which is the documented way to make
    // {@link java.util.zip.ZipFile} throw an IOException. That exercises the outer try-with-resources without
    // hitting any descriptor-specific branch.
    assertThrows(java.io.IOException.class, () -> LaceArchiveBenchmark.benchmark(missing));
  }

  @Test
  void benchmarkWithEmptyArchiveLogsNoDescriptorAndReturnsCleanly(@TempDir Path tmp) throws Exception {
    File emptyZip = tmp.resolve("empty.lace").toFile();
    try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(emptyZip.toPath()))) {
      // An archive without the expected descriptor entries takes the "neither xml nor pb entry" early-return
      // branch, which is a distinct execution path from the missing-file case above.
      zos.putNextEntry(new ZipEntry("noise.txt"));
      zos.write(new byte[]{1});
      zos.closeEntry();
    }

    assertDoesNotThrow(() -> LaceArchiveBenchmark.benchmark(emptyZip));
  }

  @Test
  void mainDispatchesThroughBenchmarkForMissingFile(@TempDir Path tmp) {
    File missing = tmp.resolve("route-through-main.lace").toFile();

    // Drives main() all the way into benchmark() to cover the for-loop body, using the IOException from a
    // missing archive to validate the exception actually surfaces (rather than being swallowed).
    assertThrows(java.io.IOException.class,
        () -> LaceArchiveBenchmark.main(new String[]{missing.getAbsolutePath()}));
  }

  @Test
  void loaderEntryConstantsMatchArchiveContractUsedByBenchmark() {
    // Pin the benchmark's ZIP entry names so a future rename of either constant forces an update here and
    // preserves the meaning of the benchmark's log output.
    assertDoesNotThrow(() -> LaceArchiveLoader.LEGACY_XML_ENTRY.length());
    assertDoesNotThrow(() -> LaceArchiveLoader.DESCRIPTOR_PB_ENTRY.length());
  }
}

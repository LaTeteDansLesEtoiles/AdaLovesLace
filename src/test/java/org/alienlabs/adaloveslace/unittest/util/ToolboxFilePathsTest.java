package org.alienlabs.adaloveslace.unittest.util;

import org.alienlabs.adaloveslace.util.ToolboxFilePaths;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("unit")
class ToolboxFilePathsTest {

  @TempDir
  private File tempDir;

  @ParameterizedTest(name = "ensureFileEndsWithExtension({0})")
  @CsvSource({
      "bare-name,bare-name.lace,.lace",
      "already.lace,already.lace,.lace",
      "multi.part.name,multi.part.name.lace,.lace"
  })
  void ensureFileEndsWithExtension(String baseName, String expectedName, String ext) {
    File input = new File(tempDir, baseName);
    File out = ToolboxFilePaths.ensureFileEndsWithExtension(input, ext);
    assertEquals(new File(tempDir, expectedName).getAbsolutePath(), out.getAbsolutePath());
    assertTrue(out.getAbsolutePath().endsWith(ext));
  }

  @Test
  void ensurePathEndsWithSuffix_appendsPngWhenMissing() {
    assertEquals("/tmp/out.png",
        ToolboxFilePaths.ensurePathEndsWithSuffix("/tmp/out", ".png"));
  }

  @Test
  void ensurePathEndsWithSuffix_leavesPdfWhenPresent() {
    assertEquals("/a/b/out.pdf",
        ToolboxFilePaths.ensurePathEndsWithSuffix("/a/b/out.pdf", ".pdf"));
  }
}

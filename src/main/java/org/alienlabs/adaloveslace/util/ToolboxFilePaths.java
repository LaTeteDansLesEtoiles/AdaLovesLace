package org.alienlabs.adaloveslace.util;

import java.io.File;

/**
 * Filename helpers for toolbox save/export actions (no JavaFX controls, safe for unit tests).
 */
public final class ToolboxFilePaths {

  private ToolboxFilePaths() {
  }

  public static File ensureFileEndsWithExtension(File file, String extension) {
    if (file.getName().endsWith(extension)) {
      return file;
    }
    return new File(file.getAbsolutePath() + extension);
  }

  public static String ensurePathEndsWithSuffix(String absolutePath, String suffix) {
    return absolutePath.endsWith(suffix) ? absolutePath : absolutePath + suffix;
  }
}

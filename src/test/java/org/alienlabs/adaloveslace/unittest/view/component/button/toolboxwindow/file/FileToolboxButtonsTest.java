package org.alienlabs.adaloveslace.unittest.view.component.button.toolboxwindow.file;

import org.alienlabs.adaloveslace.util.ToolboxFilePaths;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.file.ExportImageButton;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.file.ExportPdfButton;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.file.LoadButton;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.file.SaveAsButton;
import org.alienlabs.adaloveslace.view.component.button.toolboxwindow.file.SaveButton;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.alienlabs.adaloveslace.App.EXPORT_IMAGE_FILE_TYPE;
import static org.alienlabs.adaloveslace.App.EXPORT_PDF_FILE_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("unit")
class FileToolboxButtonsTest {

  @Test
  void diagramLaceFilter_constants_matchAcrossSaveLoadSaveAs() {
    assertEquals(SaveButton.DIAGRAM_FILES, LoadButton.DIAGRAM_FILES);
    assertEquals(SaveButton.DIAGRAM_FILE_FILTER, LoadButton.DIAGRAM_FILE_FILTER);
    assertEquals(SaveButton.DIAGRAM_FILES, SaveAsButton.DIAGRAM_FILES);
    assertEquals(SaveButton.DIAGRAM_FILE_FILTER, SaveAsButton.DIAGRAM_FILE_FILTER);
  }

  @Test
  void exportPdfButton_temporaryImageName_isPng() {
    assertTrue(ExportPdfButton.OUTPUT_TEMPORARY_IMAGE.endsWith(".png"));
  }

  @Test
  void exportPaths_matchButtonExtensions() {
    assertEquals("/tmp/out" + EXPORT_IMAGE_FILE_TYPE,
        ToolboxFilePaths.ensurePathEndsWithSuffix("/tmp/out", EXPORT_IMAGE_FILE_TYPE));
    assertEquals("/diagram/export" + EXPORT_PDF_FILE_TYPE,
        ToolboxFilePaths.ensurePathEndsWithSuffix("/diagram/export", EXPORT_PDF_FILE_TYPE));
  }

  @Test
  void dialogTitleKeys_areStableStrings() {
    assertEquals("Save", SaveButton.SAVE_FILE_DIALOG_TITLE);
    assertEquals("SaveAs", SaveAsButton.SAVE_FILE_AS_DIALOG_TITLE);
    assertEquals("Load", LoadButton.LOAD_FILE_DIALOG_TITLE);
    assertEquals("Export a diagram image", ExportImageButton.EXPORT_IMAGE_DIALOG_TITLE);
    assertEquals("Export a diagram as PDF", ExportPdfButton.EXPORT_PDF_DIALOG_TITLE);
  }
}

package org.alienlabs.adaloveslace.functionaltest.view.component.button.toolboxwindow.file;

import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.Diagram;
import org.alienlabs.adaloveslace.domain.Knot;
import org.alienlabs.adaloveslace.domain.Pattern;
import org.alienlabs.adaloveslace.domain.Step;
import org.alienlabs.adaloveslace.domain.enumeration.GridType;
import org.alienlabs.adaloveslace.functionaltest.AppFunctionalTestParent;
import org.alienlabs.adaloveslace.persistence.LaceArchiveLoader;
import org.alienlabs.adaloveslace.persistence.LaceArchiveSaver;
import org.alienlabs.adaloveslace.persistence.LaceArchiveSaver.SaveOptions;
import org.alienlabs.adaloveslace.util.FileChooserDialogGateway;
import org.alienlabs.adaloveslace.util.Preferences;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;
import org.alienlabs.adaloveslace.testutil.FxAwait;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipFile;

import static org.alienlabs.adaloveslace.App.resourceBundle;
import static org.alienlabs.adaloveslace.util.Preferences.LACE_FILE_FOLDER_SAVE_PATH;
import static org.alienlabs.adaloveslace.util.Preferences.SAVED_LACE_FILE;
import static org.alienlabs.adaloveslace.view.window.MainWindow.EXPORT_IMAGE;
import static org.alienlabs.adaloveslace.view.window.MainWindow.LOAD_FILE;
import static org.alienlabs.adaloveslace.view.window.MainWindow.SAVE_FILE;
import static org.alienlabs.adaloveslace.view.window.MainWindow.SAVE_FILE_AS;
import static org.alienlabs.adaloveslace.view.component.button.toolboxwindow.file.ExportPdfButton.EXPORT_PDF_BUTTON_NAME;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Exercises toolbox file actions with a {@link FileChooserDialogGateway} stub so CI never opens native choosers.
 */
@Tag("functional")
class FileToolboxFileDialogsFunctionalTest extends AppFunctionalTestParent {

  @TempDir
  Path tempDir;

  private final FixedFileChooserDialogGateway stubGateway = new FixedFileChooserDialogGateway();

  @Override
  @Start
  public void start(Stage primaryStage) {
    super.start(primaryStage);
  }

  @BeforeEach
  void resetLacePreferencesAndGateway(FxRobot robot) {
    robot.interact(() -> {
      Preferences p = new Preferences();
      p.setPathWithFileValue(null, LACE_FILE_FOLDER_SAVE_PATH);
      p.setPathWithFileValue(null, SAVED_LACE_FILE);
      app.setFileChooserDialogGateway(FileChooserDialogGateway.javaFxDefault());
    });
  }

  @Test
  void shouldLoadLaceArchiveWhenLoadChoosesFile(FxRobot robot) throws Exception {
    File lace = writeSingleSnowflakeLace(tempDir.resolve("to-load.lace"));

    robot.interact(() -> {
      stubGateway.setOpenResult(lace);
      stubGateway.setSaveResult(null);
      app.setFileChooserDialogGateway(stubGateway);
    });

    robot.clickOn(resourceBundle.getString(LOAD_FILE));
    FxAwait.flushFx(4);

    assertCondition(
        () -> app.getOptionalDotGrid().getDiagram().getCurrentStep().getAllVisibleKnots().size() >= 1,
        "Loaded diagram should expose at least one visible knot");
  }

  @Test
  void shouldPersistLaceWhenSaveChoosesTargetAndNoSavedPathExists(FxRobot robot) throws Exception {
    File target = tempDir.resolve("stub-save.lace").toFile();

    robot.interact(() -> {
      stubGateway.setOpenResult(null);
      stubGateway.setSaveResult(target);
      app.setFileChooserDialogGateway(stubGateway);
    });

    enterDrawingMode(robot);
    selectAndClickOnSnowflakePatternButton(robot);
    drawASnowflake(robot);
    FxAwait.flushFx(4);

    robot.clickOn(resourceBundle.getString(SAVE_FILE));
    FxAwait.flushFx(4);

    assertCondition(
        () -> target.isFile() && target.length() > 50L,
        "Save should create a non-empty .lace file");

    try (ZipFile zf = new ZipFile(target)) {
      assertNotNull(zf.getEntry(LaceArchiveLoader.DESCRIPTOR_PB_ENTRY), "Saved archive must contain descriptor.pb");
    }
  }

  @Test
  void shouldPersistLaceWhenSaveAsChoosesNewFile(FxRobot robot) throws Exception {
    File target = tempDir.resolve("save-as-out.lace").toFile();

    robot.interact(() -> {
      stubGateway.setOpenResult(null);
      stubGateway.setSaveResult(target);
      app.setFileChooserDialogGateway(stubGateway);
    });

    enterDrawingMode(robot);
    selectAndClickOnSnowflakePatternButton(robot);
    drawASnowflake(robot);
    FxAwait.flushFx(4);

    robot.clickOn(resourceBundle.getString(SAVE_FILE_AS));
    FxAwait.flushFx(4);

    assertCondition(
        () -> target.isFile() && target.length() > 50L,
        "Save As should create a non-empty .lace file");

    try (ZipFile zf = new ZipFile(target)) {
      assertNotNull(zf.getEntry(LaceArchiveLoader.DESCRIPTOR_PB_ENTRY), "Save-as archive must contain descriptor.pb");
    }
  }

  @Test
  void shouldWritePngWhenExportImageChoosesFile(FxRobot robot) throws Exception {
    File png = tempDir.resolve("export-ui.png").toFile();

    robot.interact(() -> {
      stubGateway.setOpenResult(null);
      stubGateway.setSaveResult(png);
      app.setFileChooserDialogGateway(stubGateway);
    });

    robot.clickOn(resourceBundle.getString(EXPORT_IMAGE));
    FxAwait.flushFx(4);

    assertCondition(
        () -> png.isFile() && png.length() > 200L,
        "Export image should write a PNG with non-trivial size");
  }

  @Test
  void shouldWritePdfWhenExportPdfChoosesFile(FxRobot robot) throws Exception {
    File pdf = tempDir.resolve("export-ui.pdf").toFile();

    robot.interact(() -> {
      stubGateway.setOpenResult(null);
      stubGateway.setSaveResult(pdf);
      app.setFileChooserDialogGateway(stubGateway);
    });

    robot.clickOn(resourceBundle.getString(EXPORT_PDF_BUTTON_NAME));
    FxAwait.flushFx(4);

    assertCondition(
        () -> pdf.isFile() && pdf.length() > 500L,
        "Export PDF should write a non-trivial PDF file");

    byte[] head = Files.readAllBytes(pdf.toPath());
    assertTrue(head.length >= 4 && head[0] == '%' && head[1] == 'P' && head[2] == 'D' && head[3] == 'F',
        "Output should start with %PDF");
  }

  private static File writeSingleSnowflakeLace(Path path) throws Exception {
    App bare = new App();
    bare.setMovablePane(new Pane());
    Diagram diagram = new Diagram(bare);
    diagram.setName("functional-stub-load");
    diagram.setCurrentGridType(GridType.CRISS_CROSS);

    Pattern pattern = new Pattern();
    pattern.setFilename("snowflake_small.jpg");
    pattern.setAbsoluteFilename(resolveClasspathFile("org/alienlabs/adaloveslace/unittest/util/snowflake_small.jpg"));
    pattern.setCenterX(1.0d);
    pattern.setCenterY(2.0d);
    pattern.setWidth(10.0d);
    pattern.setHeight(12.0d);

    Knot knot = new Knot(
        50d,
        60d,
        Optional.of(pattern),
        Optional.empty(),
        Optional.empty(),
        null);
    knot.setTypedText(new StringBuilder(" "));

    Step step = new Step();
    step.setStepIndex(1);
    step.setDisplayedKnots(List.of(knot));
    step.setSelectedKnots(List.of());

    diagram.setAllSteps(List.of(step));
    diagram.setCurrentStepIndex(1);
    diagram.getPatterns().clear();
    diagram.addPattern(pattern);

    File lace = path.toFile();
    LaceArchiveSaver.save(lace, diagram, SaveOptions.defaultOptions());
    return lace;
  }

  private static String resolveClasspathFile(String resourcePath) throws Exception {
    return Paths.get(
            FileToolboxFileDialogsFunctionalTest.class.getClassLoader().getResource(resourcePath).toURI())
        .toString();
  }

  private static final class FixedFileChooserDialogGateway implements FileChooserDialogGateway {
    private File openResult;
    private File saveResult;

    void setOpenResult(File openResult) {
      this.openResult = openResult;
    }

    void setSaveResult(File saveResult) {
      this.saveResult = saveResult;
    }

    @Override
    public File showOpenDialog(javafx.stage.Window owner, javafx.stage.FileChooser fileChooser) {
      return openResult;
    }

    @Override
    public File showSaveDialog(javafx.stage.Window owner, javafx.stage.FileChooser fileChooser) {
      return saveResult;
    }
  }
}

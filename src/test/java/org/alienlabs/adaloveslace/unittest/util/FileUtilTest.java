package org.alienlabs.adaloveslace.unittest.util;
import javafx.scene.layout.Pane;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.Diagram;
import org.alienlabs.adaloveslace.domain.Knot;
import org.alienlabs.adaloveslace.domain.Pattern;
import org.alienlabs.adaloveslace.domain.Step;
import org.alienlabs.adaloveslace.util.FileUtil;
import org.alienlabs.adaloveslace.view.component.grid.OptionalDotGrid;
import org.alienlabs.adaloveslace.view.window.MainWindow;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.alienlabs.adaloveslace.App.*;
import static org.alienlabs.adaloveslace.functionaltest.AppFunctionalTestParent.SNOWFLAKE_IMAGE;
import static org.alienlabs.adaloveslace.util.FileUtil.APP_FOLDER_IN_USER_HOME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class FileUtilTest {

    public static final int NUMBER_OF_STEPS     = 10;
    public static final int NUMBER_OF_PATTERNS  = 24;
    public static final int LAST_STEP_INDEX     = 9;

    private File dotLaceFile;
    private FileUtil fileUtil;
    private App app;
    private Diagram diagramToSave;

    private static final Logger logger        = LoggerFactory.getLogger(FileUtilTest.class);

    @BeforeEach
    void beforeEach() {
        app = new App();
        app.setMainWindow(new MainWindow());
        app.setMovablePane(new Pane());
        diagramToSave = new Diagram();
        app.setOptionalDotGrid(new OptionalDotGrid(app, diagramToSave, new Pane()));
        this.app.setDiagram(diagramToSave);
        fileUtil = new FileUtil(app);

        List<Step> allSteps = new ArrayList<>();
        List<Knot> displayedKnots = new ArrayList<>();
        List<Knot> selectedKnots = new ArrayList<>();
        allSteps.add(new Step(app, diagramToSave, displayedKnots, selectedKnots, false));
        allSteps.add(new Step(app, diagramToSave, displayedKnots, selectedKnots, false));
        diagramToSave.setAllSteps(allSteps);
        diagramToSave.setCurrentStepIndex(2);

        Pattern pattern = new Pattern();
        pattern.setAbsoluteFilename(
            new File(this.getClass().getResource(SNOWFLAKE_IMAGE).toString()
                .replace("file:", "")).getAbsolutePath());
        pattern.setFilename(SNOWFLAKE_IMAGE);

        diagramToSave.addPattern(pattern);
        app.setDiagram(diagramToSave);

        dotLaceFile = new File(APP_FOLDER_IN_USER_HOME + PATTERNS_DIRECTORY_NAME + File.separator + "test.lace");
    }

    @AfterEach
    void deleteDotLaceFile() {
        try {
            Files.deleteIfExists(dotLaceFile.toPath());
        } catch (IOException e) {
            logger.error("Error deleting temp file {}", dotLaceFile.getAbsolutePath(), e);
        }
    }

    @Test
    void saved_dot_lace_file_should_contain_a_pattern_file_and_descriptor_pb() {
        // When
        File fileToCheck = fileUtil.saveFile(
                new File(APP_FOLDER_IN_USER_HOME, "1.lace"),
                diagramToSave,
                false
        );

        // Then
        try (ZipFile zf = new ZipFile(fileToCheck)){
            final Enumeration<? extends ZipEntry> e = zf.entries();

            assertEquals(3, zf.size()); // meta.properties, descriptor.pb, pattern
            ZipEntry first = e.nextElement();
            ZipEntry second = e.nextElement();
            ZipEntry third = e.nextElement();
            // Order is not guaranteed, so just assert presence
            java.util.Set<String> names = java.util.Set.of(first.getName(), second.getName(), third.getName());
            org.junit.jupiter.api.Assertions.assertTrue(names.contains(SNOWFLAKE_IMAGE));
            org.junit.jupiter.api.Assertions.assertTrue(names.contains(org.alienlabs.adaloveslace.persistence.LaceArchiveLoader.DESCRIPTOR_PB_ENTRY));
        } catch(final IOException e) {
            logger.error("Error reading .lace file!", e);
            fail();
        }
    }

    @Test
    void check_for_a_loaded_dot_lace_file() {
        // Given
        Locale locale = new Locale(DEFAULT_LOCALE_LANGUAGE, DEFAULT_LOCALE_COUNTRY);
        resourceBundle = ResourceBundle.getBundle(ADA_LOVES_LACE, locale);
        App.setResourceBundle(resourceBundle);

        OptionalDotGrid grid = new OptionalDotGrid(app, diagramToSave, new Pane());
        MainWindow mainWindow = new MainWindow();
        mainWindow.setOptionalDotGrid(grid);
        app.setMainWindow(mainWindow);


        // When
        Diagram diagramToCheck = fileUtil.loadFromLaceFile(app, getTestDotLaceFile());

        // Then
        assertEquals(NUMBER_OF_STEPS, diagramToCheck.getCurrentStepIndex());
        assertEquals(NUMBER_OF_PATTERNS, diagramToCheck.getPatterns().size());
        assertEquals(SNOWFLAKE_IMAGE, diagramToCheck.getPatterns().stream().filter(pattern -> pattern.getFilename().equals(SNOWFLAKE_IMAGE)).findFirst().get().getFilename());

        assertEquals(NUMBER_OF_STEPS, diagramToCheck.getAllSteps().size());
        assertEquals(4, diagramToCheck.getAllSteps().get(LAST_STEP_INDEX).getDisplayedKnots().stream().toList().size());
        assertEquals(SNOWFLAKE_IMAGE, diagramToCheck.getAllSteps().get(LAST_STEP_INDEX).getDisplayedKnots().stream().toList().get(0).getPattern().get().getFilename());
        assertEquals(SNOWFLAKE_IMAGE, diagramToCheck.getAllSteps().get(LAST_STEP_INDEX).getDisplayedKnots().stream().toList().get(1).getPattern().get().getFilename());
        assertEquals(SNOWFLAKE_IMAGE, diagramToCheck.getAllSteps().get(LAST_STEP_INDEX).getDisplayedKnots().stream().toList().get(2).getPattern().get().getFilename());

        assertEquals(2, diagramToCheck.getAllSteps().get(LAST_STEP_INDEX).getDisplayedKnots().stream().toList().get(1).getZoomFactor());
        assertEquals(70, diagramToCheck.getAllSteps().get(LAST_STEP_INDEX).getDisplayedKnots().stream().toList().get(2).getRotationAngle());
    }

    private File getTestDotLaceFile() {
        return new File(
            this.getClass().getResource("test.lace").toString().replace("file:", ""));
    }

}

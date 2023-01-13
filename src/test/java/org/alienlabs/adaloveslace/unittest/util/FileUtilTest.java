package org.alienlabs.adaloveslace.unittest.util;

import jakarta.xml.bind.JAXBException;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Diagram;
import org.alienlabs.adaloveslace.business.model.Pattern;
import org.alienlabs.adaloveslace.business.model.Step;
import org.alienlabs.adaloveslace.util.FileUtil;
import org.alienlabs.adaloveslace.view.component.OptionalDotGrid;
import org.alienlabs.adaloveslace.view.window.MainWindow;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.alienlabs.adaloveslace.App.*;
import static org.alienlabs.adaloveslace.functionaltest.AppFunctionalTestParent.SNOWFLAKE_IMAGE;
import static org.alienlabs.adaloveslace.util.FileUtil.XML_FILE_TO_SAVE_IN_LACE_FILE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class FileUtilTest {

    public static final int NUMBER_OF_STEPS = 23;
    public static final int NUMBER_OF_PATTERNS = 22;
    public static final int LAST_PATTERN_INDEX = 21;
    public static final int LAST_STEP_INDEX = 22;
    @TempDir
    File dotLaceFileTempDir;
    private File dotLaceFile;
    private FileUtil fileUtil;
    private App app;
    private Diagram diagramToSave;

    private static final Logger logger        = LoggerFactory.getLogger(FileUtilTest.class);

    @BeforeEach
    void beforeEach() {
        app = new App();
        fileUtil = new FileUtil();
        diagramToSave = new Diagram();
        Step step = new Step();
        diagramToSave.getAllSteps().add(step);

        Pattern pattern = new Pattern();
        pattern.setAbsoluteFilename(
            new File(this.getClass().getResource(SNOWFLAKE_IMAGE).toString()
                .replace("file:", "")).getAbsolutePath());
        pattern.setFilename(SNOWFLAKE_IMAGE);

        diagramToSave.addPattern(pattern);
        app.setDiagram(diagramToSave);

        dotLaceFile = new File(dotLaceFileTempDir, "1.lace");
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
    void saved_dot_lace_file_should_contain_a_pattern_file() {
        // When
        File fileTocheck = fileUtil.saveFile(dotLaceFile, diagramToSave);

        // Then
        try (ZipFile zf = new ZipFile(fileTocheck)){
            final Enumeration<? extends ZipEntry> e = zf.entries();

            assertEquals(2, zf.size());
            assertEquals(SNOWFLAKE_IMAGE, e.nextElement().getName());
        } catch(final IOException e) {
            logger.error("Error reading .jar file!", e);
            fail();
        }
    }

    @Test
    void saved_dot_lace_file_should_contain_an_xml_file() {
        // When
        File fileTocheck = fileUtil.saveFile(dotLaceFile, diagramToSave);

        // Then
        try (ZipFile zf  = new ZipFile(fileTocheck)) {

        assertEquals(2, zf.size());
        Iterator<? extends ZipEntry> iterator = zf.entries().asIterator();
        iterator.next();
        ZipEntry file = iterator.next();

        assertEquals(XML_FILE_TO_SAVE_IN_LACE_FILE, file.getName());
        } catch(final IOException e) {
            logger.error("Error reading .jar file!", e);
            fail();
        }

    }

    @Test
    void saved_xml_file_should_contain_a_pattern_and_a_current_index() {
        // When
        File fileTocheck = fileUtil.saveFile(dotLaceFile, diagramToSave);

        // Then
        ZipFile zf = null;

        try {
            zf = new ZipFile(fileTocheck);
        } catch(final IOException e) {
            logger.error("Error reading .jar file!", e);
            fail();
        }
        final Enumeration<? extends ZipEntry> e = zf.entries();

        assertEquals(2, zf.size());
        e.nextElement();
        ZipEntry xmlFile = e.nextElement();

        Diagram diagramToCheck = null;

        try {
            diagramToCheck = fileUtil.unmarshallXmlFile(zf, xmlFile);
        } catch (JAXBException | IOException ex) {
            logger.error("Error unmarshalling .jar file!", e);
        }

        assertEquals(0, diagramToCheck.getCurrentStepIndex());
        assertEquals(1, diagramToCheck.getPatterns().size());
        assertEquals(SNOWFLAKE_IMAGE, diagramToCheck.getPatterns().get(0).getFilename());
    }

    @Test
    void check_for_a_loaded_dot_lace_file() {
        // Given
        Locale locale = new Locale(DEFAULT_LOCALE_LANGUAGE, DEFAULT_LOCALE_COUNTRY);
        resourceBundle = ResourceBundle.getBundle(ADA_LOVES_LACE, locale);
        App.setResourceBundle(resourceBundle);

        OptionalDotGrid grid = new OptionalDotGrid(diagramToSave, null);
        MainWindow mainWindow = new MainWindow();
        mainWindow.setOptionalDotGrid(grid);
        app.setMainWindow(mainWindow);


        // When
        Diagram diagramToCheck = fileUtil.loadFromLaceFile(app, getTestDotLaceFile());

        // Then
        assertEquals(NUMBER_OF_STEPS, diagramToCheck.getCurrentStepIndex());
        assertEquals(NUMBER_OF_PATTERNS, diagramToCheck.getPatterns().size());
        assertEquals(SNOWFLAKE_IMAGE, diagramToCheck.getPatterns().get(LAST_PATTERN_INDEX).getFilename());

        assertEquals(NUMBER_OF_STEPS, diagramToCheck.getAllSteps().size());
        assertEquals(3, diagramToCheck.getAllSteps().get(LAST_STEP_INDEX).getDisplayedKnots().stream().toList().size());
        assertEquals(SNOWFLAKE_IMAGE, diagramToCheck.getAllSteps().get(LAST_STEP_INDEX).getDisplayedKnots().stream().toList().get(0).getPattern().getFilename());
        assertEquals(SNOWFLAKE_IMAGE, diagramToCheck.getAllSteps().get(LAST_STEP_INDEX).getDisplayedKnots().stream().toList().get(1).getPattern().getFilename());
        assertEquals(SNOWFLAKE_IMAGE, diagramToCheck.getAllSteps().get(LAST_STEP_INDEX).getDisplayedKnots().stream().toList().get(2).getPattern().getFilename());

        assertEquals(2, diagramToCheck.getAllSteps().get(LAST_STEP_INDEX).getDisplayedKnots().stream().toList().get(1).getZoomFactor());
        assertEquals(70, diagramToCheck.getAllSteps().get(LAST_STEP_INDEX).getDisplayedKnots().stream().toList().get(2).getRotationAngle());
    }

    private File getTestDotLaceFile() {
        return new File(
            this.getClass().getResource("test.lace").toString().replace("file:", ""));
    }

}

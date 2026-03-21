package org.alienlabs.adaloveslace.unittest.persistence;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.Diagram;
import org.alienlabs.adaloveslace.domain.Knot;
import org.alienlabs.adaloveslace.domain.Pattern;
import org.alienlabs.adaloveslace.domain.Step;
import org.alienlabs.adaloveslace.persistence.LaceArchiveLoader;
import org.alienlabs.adaloveslace.persistence.LaceArchiveSaver;
import org.alienlabs.adaloveslace.persistence.LaceArchiveSaver.SaveOptions;
import org.alienlabs.adaloveslace.util.FileUtil;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unit")
class LegacyXmlFallbackTest {

    private static final String LEGACY_TEST_LACE_RESOURCE =
            "org/alienlabs/adaloveslace/unittest/util/test.lace";

    @TempDir
    File tempDir;

    @Test
    void legacy_xml_should_load_then_resave_as_protobuf_only() throws Exception {
        App app = new App();
        app.setMovablePane(new Pane());

        File legacy = copyClasspathResourceToTemp(LEGACY_TEST_LACE_RESOURCE, "legacy.xml-only.lace");
        Diagram loadedFromXml = LaceArchiveLoader.load(legacy, new Diagram(app));

        // Ensure referenced pattern files exist on disk before re-saving.
        // The protobuf saver reads pattern bytes from `Pattern.absoluteFilename`, which the
        // legacy JAXB loader rebuilds into the on-disk `knots/` cache.
        ensurePatternsExtractedForSave(legacy, loadedFromXml);

        File reSaved = new File(tempDir, "resaved.protobuf-only.lace");
        LaceArchiveSaver.save(reSaved, loadedFromXml, SaveOptions.defaultOptions());

        try (ZipFile zf = new ZipFile(reSaved)) {
            assertNotNull(zf.getEntry(LaceArchiveLoader.DESCRIPTOR_PB_ENTRY), "descriptor.pb missing after re-save");
            assertNull(zf.getEntry(LaceArchiveLoader.LEGACY_XML_ENTRY), "save.xml must not be written by default re-save");
            assertNull(zf.getEntry(LaceArchiveLoader.DESCRIPTOR_JSON_ENTRY), "descriptor.json must not be written by default re-save");
        }

        Diagram loadedFromProtobuf = LaceArchiveLoader.load(reSaved, new Diagram(app));
        DiagramSemanticComparator.assertSemanticallyEquivalent(loadedFromXml, loadedFromProtobuf);
    }

    @Test
    void dumpDescriptorJson_should_work_without_descriptor_json_entry() throws Exception {
        App app = new App();
        app.setMovablePane(new Pane());

        Diagram diagram = buildSmallDiagram(app);
        File lace = new File(tempDir, "debug-dump-no-entry.lace");
        LaceArchiveSaver.save(lace, diagram, SaveOptions.defaultOptions()); // descriptor.json must be absent

        try (ZipFile zf = new ZipFile(lace)) {
            assertNull(zf.getEntry(LaceArchiveLoader.DESCRIPTOR_JSON_ENTRY), "descriptor.json should be absent for default save");
        }

        String json = LaceArchiveLoader.dumpDescriptorJson(lace);
        assertNotNull(json);
        assertFalse(json.isBlank());

        // Default JsonFormat uses lowerCamelCase JSON names (schemaVersion, currentStepIndex, gridType...)
        assertTrue(json.contains("schemaVersion"), "Expected schemaVersion field in JSON, got: " + json);
        assertTrue(json.contains("currentStepIndex"), "Expected currentStepIndex field in JSON, got: " + json);
    }

    private File copyClasspathResourceToTemp(String resourcePath, String outputName) throws Exception {
        URL url = LegacyXmlFallbackTest.class.getClassLoader().getResource(resourcePath);
        assertNotNull(url, "Missing classpath resource: " + resourcePath);
        File out = new File(tempDir, outputName);

        try (InputStream in = url.openStream()) {
            Files.copy(in, out.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        return out;
    }

    private void ensurePatternsExtractedForSave(File laceFile, Diagram diagram) throws Exception {
        File patternsDir = new File(FileUtil.APP_FOLDER_IN_USER_HOME + App.PATTERNS_DIRECTORY_NAME);
        // Ensure trailing separator doesn't matter: we create the directory and copy into it.
        if (!patternsDir.exists()) {
            patternsDir.mkdirs();
        }

        try (ZipFile zip = new ZipFile(laceFile)) {
            for (Pattern p : diagram.getPatterns()) {
                if (p == null || p.getFilename() == null || p.getFilename().isBlank()) {
                    continue;
                }
                ZipEntry entry = zip.getEntry(p.getFilename());
                if (entry == null) {
                    throw new AssertionError("Legacy archive is missing pattern entry: " + p.getFilename());
                }
                File target = new File(patternsDir, p.getFilename());
                if (target.exists()) {
                    continue;
                }
                try (InputStream in = zip.getInputStream(entry)) {
                    Files.copy(in, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }

    private static Diagram buildSmallDiagram(App app) {
        Diagram diagram = new Diagram(app);
        diagram.setCurrentGridType(org.alienlabs.adaloveslace.domain.enumeration.GridType.CRISS_CROSS);

        Pattern pattern = new Pattern();
        pattern.setFilename("snowflake_small.jpg");
        pattern.setAbsoluteFilename(resolveClasspathResourceAsFile(
                "org/alienlabs/adaloveslace/unittest/util/snowflake_small.jpg").getAbsolutePath());
        pattern.setCenterX(1d);
        pattern.setCenterY(2d);
        pattern.setWidth(10d);
        pattern.setHeight(12d);

        Knot patternKnot = new Knot(
                10d,
                20d,
                Optional.of(pattern),
                Optional.empty(),
                Optional.of(Color.BLACK),
                null
        );
        patternKnot.setRotationAngle(5);
        patternKnot.setZoomFactor(2);

        Step step1 = new Step();
        step1.setStepIndex(1);
        step1.setDisplayedKnots(List.of(patternKnot));
        step1.setSelectedKnots(List.of());

        diagram.setAllSteps(List.of(step1));
        diagram.setCurrentStepIndex(1);
        diagram.addPattern(pattern);
        return diagram;
    }

    private static File resolveClasspathResourceAsFile(String resourcePath) {
        URL url = LegacyXmlFallbackTest.class.getClassLoader().getResource(resourcePath);
        assertNotNull(url, "Missing classpath resource: " + resourcePath);
        try {
            return new File(url.toURI());
        } catch (Exception e) {
            throw new IllegalStateException("Cannot resolve resource as file: " + resourcePath, e);
        }
    }

}


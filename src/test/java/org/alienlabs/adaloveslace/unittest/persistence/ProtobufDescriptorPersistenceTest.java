package org.alienlabs.adaloveslace.unittest.persistence;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.Diagram;
import org.alienlabs.adaloveslace.domain.Knot;
import org.alienlabs.adaloveslace.domain.Pattern;
import org.alienlabs.adaloveslace.domain.Step;
import org.alienlabs.adaloveslace.persistence.LaceArchiveException;
import org.alienlabs.adaloveslace.persistence.LaceArchiveLoader;
import org.alienlabs.adaloveslace.persistence.LaceArchiveSaver;
import org.alienlabs.adaloveslace.persistence.LaceArchiveSaver.SaveOptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.*;

class ProtobufDescriptorPersistenceTest {

    @TempDir
    File tempDir;

    @Test
    void save_roundtrip_should_use_protobuf_and_not_write_save_xml() throws Exception {
        App app = new App();
        app.setMovablePane(new Pane());

        Diagram diagram = buildRoundtripDiagram(app);

        File lace = new File(tempDir, "roundtrip.lace");
        LaceArchiveSaver.save(lace, diagram, SaveOptions.defaultOptions());

        // Assert archive conventions
        try (java.util.zip.ZipFile zf = new java.util.zip.ZipFile(lace)) {
            assertNotNull(zf.getEntry(LaceArchiveLoader.DESCRIPTOR_PB_ENTRY), "descriptor.pb missing");
            assertNull(zf.getEntry(LaceArchiveLoader.LEGACY_XML_ENTRY), "save.xml must not be written by default");
        }

        Diagram loaded = LaceArchiveLoader.load(lace, new Diagram(app));
        DiagramSemanticComparator.assertSemanticallyEquivalent(diagram, loaded);
    }

    @Test
    void debug_json_export_should_be_optional() throws Exception {
        App app = new App();
        app.setMovablePane(new Pane());
        Diagram diagram = buildRoundtripDiagram(app);

        File lace = new File(tempDir, "debug-export.lace");
        LaceArchiveSaver.save(lace, diagram, new SaveOptions(true));

        try (java.util.zip.ZipFile zf = new java.util.zip.ZipFile(lace)) {
            assertNotNull(zf.getEntry(LaceArchiveLoader.DESCRIPTOR_PB_ENTRY), "descriptor.pb missing");
            assertNotNull(zf.getEntry(LaceArchiveLoader.DESCRIPTOR_JSON_ENTRY), "descriptor.json should be exported only for debug");
        }
    }

    @Test
    void loading_precedence_descriptor_pb_wins_over_save_xml() throws Exception {
        App app = new App();
        app.setMovablePane(new Pane());

        File lace = new File(tempDir, "precedence.lace");
        // Minimal valid protobuf: schema_version=1 only.
        byte[] descriptorPbMinimal = new byte[]{0x08, 0x01};

        try (FileOutputStream fos = new FileOutputStream(lace);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            zos.putNextEntry(new ZipEntry(LaceArchiveLoader.DESCRIPTOR_PB_ENTRY));
            zos.write(descriptorPbMinimal);
            zos.closeEntry();

            zos.putNextEntry(new ZipEntry(LaceArchiveLoader.LEGACY_XML_ENTRY));
            zos.write("<this-is-not-xml>".getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();
        }

        // Should succeed: save.xml must be ignored because descriptor.pb exists.
        Diagram loaded = LaceArchiveLoader.load(lace, new Diagram(app));
        assertNotNull(loaded);
    }

    @Test
    void loading_should_fail_on_unsupported_descriptor_schema_version() throws Exception {
        App app = new App();
        app.setMovablePane(new Pane());

        File lace = new File(tempDir, "unsupported-schema-version.lace");
        // Minimal protobuf: schema_version=2 only.
        byte[] descriptorPbUnsupported = new byte[]{0x08, 0x02};

        try (FileOutputStream fos = new FileOutputStream(lace);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            zos.putNextEntry(new ZipEntry(LaceArchiveLoader.DESCRIPTOR_PB_ENTRY));
            zos.write(descriptorPbUnsupported);
            zos.closeEntry();
        }

        LaceArchiveException ex = assertThrows(LaceArchiveException.class, () -> LaceArchiveLoader.load(lace, new Diagram(app)));
        assertTrue(ex.getMessage().contains("Unsupported descriptor schema version"), "Unexpected error message: " + ex.getMessage());
    }

    @Test
    void loading_should_fail_on_neither_descriptor_pb_nor_save_xml() throws IOException {
        App app = new App();
        app.setMovablePane(new Pane());

        File lace = new File(tempDir, "no-descriptor-and-no-xml.lace");
        try (FileOutputStream fos = new FileOutputStream(lace);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            zos.putNextEntry(new ZipEntry("foo.txt"));
            zos.write("test".getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();
        }

        assertThrows(LaceArchiveException.class, () -> LaceArchiveLoader.load(lace, new Diagram(app)));
    }

    @Test
    void malformed_descriptor_pb_should_not_fall_back_to_save_xml() throws Exception {
        App app = new App();
        app.setMovablePane(new Pane());

        File lace = new File(tempDir, "malformed-descriptor-with-xml.lace");

        try (FileOutputStream fos = new FileOutputStream(lace);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            zos.putNextEntry(new ZipEntry(LaceArchiveLoader.DESCRIPTOR_PB_ENTRY));
            zos.write(new byte[]{0x01, 0x02, 0x03}); // invalid protobuf
            zos.closeEntry();

            zos.putNextEntry(new ZipEntry(LaceArchiveLoader.LEGACY_XML_ENTRY));
            zos.write("<save/>".getBytes(StandardCharsets.UTF_8)); // could be parseable, but must not be used
            zos.closeEntry();
        }

        LaceArchiveException ex = assertThrows(LaceArchiveException.class, () -> LaceArchiveLoader.load(lace, new Diagram(app)));
        assertTrue(ex.getMessage().contains("Malformed protobuf descriptor"), "Unexpected error message: " + ex.getMessage());
    }

    @Test
    void loading_should_fail_when_descriptor_references_missing_pattern_resource() throws Exception {
        App app = new App();
        app.setMovablePane(new Pane());

        File lace = new File(tempDir, "missing-pattern-resource.lace");

        // Build a minimal DiagramDescriptor protobuf:
        // - schema_version = 1
        // - patterns = [ { filename = "missing.png" } ]
        byte[] filenameBytes = "missing.png".getBytes(StandardCharsets.UTF_8);
        int patternDescriptorLen = 2 + filenameBytes.length; // tag + length + payload

        byte[] patternDescriptor = new byte[patternDescriptorLen];
        patternDescriptor[0] = 0x0A; // PatternDescriptor.filename (field 1, string)
        patternDescriptor[1] = (byte) filenameBytes.length;
        System.arraycopy(filenameBytes, 0, patternDescriptor, 2, filenameBytes.length);

        byte[] descriptorPb = new byte[2 /*schema*/ + 2 /*patterns tag/len*/ + patternDescriptorLen];
        int i = 0;
        descriptorPb[i++] = 0x08; // schema_version (field 1, varint)
        descriptorPb[i++] = 0x01; // value 1
        descriptorPb[i++] = 0x32; // patterns (field 6, length-delimited)
        descriptorPb[i++] = (byte) patternDescriptorLen; // length
        System.arraycopy(patternDescriptor, 0, descriptorPb, i, patternDescriptorLen);

        try (FileOutputStream fos = new FileOutputStream(lace);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            zos.putNextEntry(new ZipEntry(LaceArchiveLoader.DESCRIPTOR_PB_ENTRY));
            zos.write(descriptorPb);
            zos.closeEntry();

            // Intentionally omit zip entry for "missing.png" to trigger failure.
        }

        LaceArchiveException ex = assertThrows(LaceArchiveException.class,
                () -> LaceArchiveLoader.load(lace, new Diagram(app)));
        assertTrue(ex.getMessage().contains("Missing required pattern resource in lace archive"),
                "Unexpected error message: " + ex.getMessage());
        assertTrue(ex.getMessage().contains("missing.png"),
                "Expected missing filename in message: " + ex.getMessage());
    }

    private static Diagram buildRoundtripDiagram(App app) {
        Diagram diagram = new Diagram(app);
        diagram.setName("roundtrip");
        diagram.setCurrentGridType(org.alienlabs.adaloveslace.domain.enumeration.GridType.CRISS_CROSS);

        Pattern pattern = new Pattern();
        pattern.setFilename("snowflake_small.jpg");
        pattern.setAbsoluteFilename(resolveTestResourceAsFile("org/alienlabs/adaloveslace/unittest/util/snowflake_small.jpg"));
        pattern.setCenterX(1.0d);
        pattern.setCenterY(2.0d);
        pattern.setWidth(10.0d);
        pattern.setHeight(12.0d);

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
        patternKnot.setTypedText(new StringBuilder(" "));

        Knot textKnot = new Knot(
                30d,
                35d,
                Optional.empty(),
                Optional.of("Hello"),
                Optional.of(Color.BLUE),
                null
        );
        textKnot.setRotationAngle(70);
        textKnot.setZoomFactor(3);
        textKnot.setTypedText(new StringBuilder("Hello"));

        Step step1 = new Step();
        step1.setStepIndex(1);
        step1.setDisplayedKnots(List.of(patternKnot));
        step1.setSelectedKnots(List.of());

        Step step2 = new Step();
        step2.setStepIndex(2);
        step2.setDisplayedKnots(List.of(textKnot));
        step2.setSelectedKnots(List.of());

        diagram.setAllSteps(List.of(step1, step2));
        diagram.setCurrentStepIndex(2);
        diagram.getPatterns().clear(); // Diagram patterns is final Set, clear then add
        diagram.addPattern(pattern);

        return diagram;
    }

    private static String resolveTestResourceAsFile(String resourcePath) {
        try {
            File f = new File(ProtobufDescriptorPersistenceTest.class.getClassLoader().getResource(resourcePath).toURI());
            return f.getAbsolutePath();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot resolve test resource: " + resourcePath, e);
        }
    }
}


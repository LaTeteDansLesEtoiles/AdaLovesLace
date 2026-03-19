package org.alienlabs.adaloveslace.integrationtest.persistence;

import javafx.scene.paint.Color;
import org.alienlabs.adaloveslace.domain.Diagram;
import org.alienlabs.adaloveslace.domain.Knot;
import org.alienlabs.adaloveslace.domain.Pattern;
import org.alienlabs.adaloveslace.domain.Step;
import org.alienlabs.adaloveslace.integrationtest.persistence.support.ArchiveAssertions;
import org.alienlabs.adaloveslace.integrationtest.persistence.support.DiagramSemanticAssertions;
import org.alienlabs.adaloveslace.persistence.LaceArchiveException;
import org.alienlabs.adaloveslace.persistence.LaceArchiveLoader;
import org.alienlabs.adaloveslace.persistence.LaceArchiveSaver.SaveOptions;
import org.alienlabs.adaloveslace.persistence.LacePersistenceUseCase;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
class LacePersistenceIntegrationTest {

    private static final String LEGACY_TEST_LACE_RESOURCE = "org/alienlabs/adaloveslace/unittest/util/test.lace";
    private static final String SNOWFLAKE_RESOURCE = "org/alienlabs/adaloveslace/unittest/util/snowflake_small.jpg";

    @TempDir
    File tempDir;

    @Test
    void load_legacy_xml_only_archive_should_succeed() throws Exception {
        File legacy = copyResourceToTemp(LEGACY_TEST_LACE_RESOURCE, "legacy-only.lace");
        try (ZipFile zf = new ZipFile(legacy)) {
            assertNotNull(zf.getEntry(LaceArchiveLoader.LEGACY_XML_ENTRY), "legacy fixture must contain save.xml");
            assertNull(zf.getEntry(LaceArchiveLoader.DESCRIPTOR_PB_ENTRY), "legacy fixture should not contain descriptor.pb");
        }

        Diagram diagram = LacePersistenceUseCase.load(legacy);

        assertEquals(10, diagram.getAllSteps().size(), "legacy fixture should load known step count");
        assertEquals(10, diagram.getCurrentStepIndex(), "legacy fixture should load known current step");
        assertEquals(24, diagram.getPatterns().size(), "legacy fixture should load known pattern count");
        assertTrue(diagram.getPatterns().stream().anyMatch(p -> "snowflake_small.jpg".equals(p.getFilename())),
                "expected snowflake_small.jpg pattern from fixture");
    }

    @Test
    void save_and_load_protobuf_archive_round_trip_should_preserve_semantics() throws Exception {
        Diagram original = buildFixtureDiagram();
        File lace = new File(tempDir, "roundtrip.lace");

        LacePersistenceUseCase.save(lace, original, SaveOptions.defaultOptions());
        Diagram loaded = LacePersistenceUseCase.load(lace);

        try (ZipFile zf = new ZipFile(lace)) {
            ArchiveAssertions.assertHasEntry(zf, LaceArchiveLoader.DESCRIPTOR_PB_ENTRY);
            ArchiveAssertions.assertMissingEntry(zf, LaceArchiveLoader.LEGACY_XML_ENTRY);
        }
        DiagramSemanticAssertions.assertSemanticallyEquivalent(original, loaded);
    }

    @Test
    void save_should_write_meta_properties_with_archive_version_1() throws Exception {
        Diagram diagram = buildFixtureDiagram();
        File lace = new File(tempDir, "meta-contract.lace");

        LacePersistenceUseCase.save(lace, diagram, SaveOptions.defaultOptions());

        try (ZipFile zf = new ZipFile(lace)) {
            ArchiveAssertions.assertHasEntry(zf, LaceArchiveLoader.META_PROPERTIES_ENTRY);
            Properties props = readMetaProperties(zf);
            assertEquals("1", props.getProperty("archive.version"), "archive.version contract mismatch");
        }
    }

    @Test
    void load_archive_with_both_pb_and_xml_should_prefer_pb() throws Exception {
        Diagram pbDiagram = buildFixtureDiagram();
        File pbArchive = new File(tempDir, "pb.lace");
        LacePersistenceUseCase.save(pbArchive, pbDiagram, SaveOptions.defaultOptions());

        File bothArchive = new File(tempDir, "both.lace");
        try (ZipFile fromPb = new ZipFile(pbArchive);
             ZipFile fromLegacy = new ZipFile(copyResourceToTemp(LEGACY_TEST_LACE_RESOURCE, "legacy-for-both.lace"));
             FileOutputStream fos = new FileOutputStream(bothArchive);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            copyEntry(fromPb, LaceArchiveLoader.META_PROPERTIES_ENTRY, zos);
            copyEntry(fromPb, LaceArchiveLoader.DESCRIPTOR_PB_ENTRY, zos);
            copyEntry(fromPb, "snowflake_small.jpg", zos);
            copyEntry(fromLegacy, LaceArchiveLoader.LEGACY_XML_ENTRY, zos);
        }

        Diagram loaded = LacePersistenceUseCase.load(bothArchive);
        DiagramSemanticAssertions.assertSemanticallyEquivalent(pbDiagram, loaded);
    }

    @Test
    void protobuf_archive_without_meta_properties_should_load() throws IOException {
        File lace = new File(tempDir, "pb-without-meta.lace");
        try (FileOutputStream fos = new FileOutputStream(lace);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            zos.putNextEntry(new ZipEntry(LaceArchiveLoader.DESCRIPTOR_PB_ENTRY));
            zos.write(new byte[]{0x08, 0x01}); // schema_version=1
            zos.closeEntry();
        }

        Diagram loaded = LacePersistenceUseCase.load(lace);
        assertEquals(0, loaded.getCurrentStepIndex(), "minimal descriptor should default currentStepIndex to 0");
        assertEquals(0, loaded.getAllSteps().size(), "minimal descriptor should contain no steps");
    }

    @Test
    void load_archive_with_no_descriptor_should_fail_explicitly() throws IOException {
        File lace = new File(tempDir, "no-descriptor.lace");
        try (FileOutputStream fos = new FileOutputStream(lace);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            zos.putNextEntry(new ZipEntry("foo.txt"));
            zos.write("x".getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();
        }
        LaceArchiveException ex = assertThrows(LaceArchiveException.class, () -> LacePersistenceUseCase.load(lace));
        assertTrue(ex.getMessage().contains("descriptor.pb or save.xml"), ex.getMessage());
    }

    @Test
    void load_invalid_archive_should_fail_explicitly() throws IOException {
        File invalid = new File(tempDir, "invalid.lace");
        Files.writeString(invalid.toPath(), "not-a-zip", StandardCharsets.UTF_8);

        LaceArchiveException ex = assertThrows(LaceArchiveException.class, () -> LacePersistenceUseCase.load(invalid));
        assertTrue(ex.getMessage().contains("I/O error while loading lace archive"), ex.getMessage());
    }

    @Test
    void load_legacy_then_save_should_output_protobuf_by_default() throws Exception {
        File legacy = copyResourceToTemp(LEGACY_TEST_LACE_RESOURCE, "legacy-resave-input.lace");
        Diagram loadedLegacy = LacePersistenceUseCase.load(legacy);
        materializePatternFilesFromArchiveToTempDir(legacy, loadedLegacy, new File(tempDir, "patterns-resave"));

        File out = new File(tempDir, "legacy-resaved.lace");
        LacePersistenceUseCase.save(out, loadedLegacy, SaveOptions.defaultOptions());

        try (ZipFile zf = new ZipFile(out)) {
            ArchiveAssertions.assertHasEntry(zf, LaceArchiveLoader.DESCRIPTOR_PB_ENTRY);
            ArchiveAssertions.assertMissingEntry(zf, LaceArchiveLoader.LEGACY_XML_ENTRY);
            ArchiveAssertions.assertMissingEntry(zf, LaceArchiveLoader.DESCRIPTOR_JSON_ENTRY);
        }
    }

    @Test
    void corrupted_protobuf_descriptor_should_fail_explicitly() throws IOException {
        File lace = new File(tempDir, "corrupt-pb.lace");
        try (FileOutputStream fos = new FileOutputStream(lace);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            zos.putNextEntry(new ZipEntry(LaceArchiveLoader.DESCRIPTOR_PB_ENTRY));
            zos.write(new byte[]{0x01, 0x02, 0x03});
            zos.closeEntry();
        }
        LaceArchiveException ex = assertThrows(LaceArchiveException.class, () -> LacePersistenceUseCase.load(lace));
        assertTrue(ex.getMessage().contains("Malformed protobuf descriptor"), ex.getMessage());
    }

    @Test
    void malformed_protobuf_with_legacy_xml_present_should_fail_and_not_fallback() throws IOException {
        File lace = new File(tempDir, "malformed-pb-with-xml.lace");
        try (FileOutputStream fos = new FileOutputStream(lace);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            zos.putNextEntry(new ZipEntry(LaceArchiveLoader.DESCRIPTOR_PB_ENTRY));
            zos.write(new byte[]{0x01, 0x02, 0x03}); // malformed protobuf
            zos.closeEntry();

            zos.putNextEntry(new ZipEntry(LaceArchiveLoader.LEGACY_XML_ENTRY));
            zos.write("<save/>".getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();
        }

        LaceArchiveException ex = assertThrows(LaceArchiveException.class, () -> LacePersistenceUseCase.load(lace));
        assertTrue(ex.getMessage().contains("Malformed protobuf descriptor"), ex.getMessage());
    }

    @Test
    void empty_protobuf_descriptor_should_fail_explicitly() throws IOException {
        File lace = new File(tempDir, "empty-pb.lace");
        try (FileOutputStream fos = new FileOutputStream(lace);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            zos.putNextEntry(new ZipEntry(LaceArchiveLoader.DESCRIPTOR_PB_ENTRY));
            zos.write(new byte[]{});
            zos.closeEntry();
        }
        LaceArchiveException ex = assertThrows(LaceArchiveException.class, () -> LacePersistenceUseCase.load(lace));
        assertTrue(ex.getMessage().contains("Unsupported descriptor schema version"), ex.getMessage());
    }

    @Test
    void unsupported_archive_version_should_fail_explicitly() throws IOException {
        File lace = new File(tempDir, "unsupported-archive-version.lace");
        try (FileOutputStream fos = new FileOutputStream(lace);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            zos.putNextEntry(new ZipEntry(LaceArchiveLoader.META_PROPERTIES_ENTRY));
            Properties props = new Properties();
            props.setProperty("archive.version", "999");
            props.store(zos, "test");
            zos.closeEntry();

            zos.putNextEntry(new ZipEntry(LaceArchiveLoader.DESCRIPTOR_PB_ENTRY));
            zos.write(new byte[]{0x08, 0x01});
            zos.closeEntry();
        }
        LaceArchiveException ex = assertThrows(LaceArchiveException.class, () -> LacePersistenceUseCase.load(lace));
        assertTrue(ex.getMessage().contains("Unsupported archive version"), ex.getMessage());
    }

    @Test
    void invalid_archive_version_in_meta_properties_should_fail_explicitly() throws IOException {
        File lace = new File(tempDir, "invalid-archive-version-value.lace");
        try (FileOutputStream fos = new FileOutputStream(lace);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            zos.putNextEntry(new ZipEntry(LaceArchiveLoader.META_PROPERTIES_ENTRY));
            Properties props = new Properties();
            props.setProperty("archive.version", "not-a-number");
            props.store(zos, "test");
            zos.closeEntry();

            zos.putNextEntry(new ZipEntry(LaceArchiveLoader.DESCRIPTOR_PB_ENTRY));
            zos.write(new byte[]{0x08, 0x01});
            zos.closeEntry();
        }
        LaceArchiveException ex = assertThrows(LaceArchiveException.class, () -> LacePersistenceUseCase.load(lace));
        assertTrue(ex.getMessage().contains("Invalid archive.version"), ex.getMessage());
    }

    @Test
    void truncated_meta_properties_entry_should_fail_explicitly() throws IOException {
        File lace = new File(tempDir, "truncated-meta-entry.lace");
        try (FileOutputStream fos = new FileOutputStream(lace);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            zos.putNextEntry(new ZipEntry(LaceArchiveLoader.META_PROPERTIES_ENTRY));
            zos.write("archive.ver".getBytes(StandardCharsets.UTF_8)); // missing archive.version key
            zos.closeEntry();

            zos.putNextEntry(new ZipEntry(LaceArchiveLoader.DESCRIPTOR_PB_ENTRY));
            zos.write(new byte[]{0x08, 0x01});
            zos.closeEntry();
        }

        LaceArchiveException ex = assertThrows(LaceArchiveException.class, () -> LacePersistenceUseCase.load(lace));
        assertTrue(ex.getMessage().contains("Missing archive.version"), ex.getMessage());
    }

    @Test
    void unsupported_descriptor_schema_version_should_fail_explicitly() throws IOException {
        File lace = new File(tempDir, "unsupported-descriptor-version.lace");
        try (FileOutputStream fos = new FileOutputStream(lace);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            zos.putNextEntry(new ZipEntry(LaceArchiveLoader.DESCRIPTOR_PB_ENTRY));
            zos.write(new byte[]{0x08, 0x02}); // schema_version=2
            zos.closeEntry();
        }
        LaceArchiveException ex = assertThrows(LaceArchiveException.class, () -> LacePersistenceUseCase.load(lace));
        assertTrue(ex.getMessage().contains("Unsupported descriptor schema version"), ex.getMessage());
    }

    @Test
    void missing_required_pattern_resource_should_fail_explicitly() throws IOException {
        File lace = new File(tempDir, "missing-pattern.lace");

        byte[] filename = "missing.png".getBytes(StandardCharsets.UTF_8);
        byte[] patternMessage = new byte[2 + filename.length];
        patternMessage[0] = 0x0A; // field 1 (filename)
        patternMessage[1] = (byte) filename.length;
        System.arraycopy(filename, 0, patternMessage, 2, filename.length);

        byte[] descriptor = new byte[4 + patternMessage.length];
        int i = 0;
        descriptor[i++] = 0x08; // schema_version
        descriptor[i++] = 0x01; // version 1
        descriptor[i++] = 0x32; // field 6 (patterns)
        descriptor[i++] = (byte) patternMessage.length;
        System.arraycopy(patternMessage, 0, descriptor, i, patternMessage.length);

        try (FileOutputStream fos = new FileOutputStream(lace);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            zos.putNextEntry(new ZipEntry(LaceArchiveLoader.DESCRIPTOR_PB_ENTRY));
            zos.write(descriptor);
            zos.closeEntry();
        }

        LaceArchiveException ex = assertThrows(LaceArchiveException.class, () -> LacePersistenceUseCase.load(lace));
        assertTrue(ex.getMessage().contains("Missing required pattern resource"), ex.getMessage());
        assertTrue(ex.getMessage().contains("missing.png"), ex.getMessage());
    }

    @Test
    void debug_json_descriptor_should_be_optional_and_dumpable() throws Exception {
        Diagram diagram = buildFixtureDiagram();

        File withoutJson = new File(tempDir, "without-debug-json.lace");
        LacePersistenceUseCase.save(withoutJson, diagram, SaveOptions.defaultOptions());
        try (ZipFile zf = new ZipFile(withoutJson)) {
            ArchiveAssertions.assertMissingEntry(zf, LaceArchiveLoader.DESCRIPTOR_JSON_ENTRY);
            ArchiveAssertions.assertHasEntry(zf, LaceArchiveLoader.DESCRIPTOR_PB_ENTRY);
        }
        String dumped = LacePersistenceUseCase.dumpDescriptorJson(withoutJson);
        assertTrue(dumped.contains("schemaVersion"), dumped);
        assertTrue(dumped.contains("currentStepIndex"), dumped);

        File withJson = new File(tempDir, "with-debug-json.lace");
        LacePersistenceUseCase.save(withJson, diagram, new SaveOptions(true));
        try (ZipFile zf = new ZipFile(withJson)) {
            ArchiveAssertions.assertHasEntry(zf, LaceArchiveLoader.DESCRIPTOR_JSON_ENTRY);
            ArchiveAssertions.assertHasEntry(zf, LaceArchiveLoader.DESCRIPTOR_PB_ENTRY);
            ArchiveAssertions.assertMissingEntry(zf, LaceArchiveLoader.LEGACY_XML_ENTRY);
        }
    }

    @Test
    void save_should_embed_pattern_resource_bytes_matching_source_file() throws Exception {
        Diagram diagram = buildFixtureDiagram();
        File lace = new File(tempDir, "pattern-bytes-contract.lace");

        LacePersistenceUseCase.save(lace, diagram, SaveOptions.defaultOptions());

        Pattern pattern = diagram.getPatterns().iterator().next();
        byte[] expectedBytes = Files.readAllBytes(new File(pattern.getAbsoluteFilename()).toPath());
        byte[] archivedBytes;
        try (ZipFile zf = new ZipFile(lace)) {
            ArchiveAssertions.assertHasEntry(zf, pattern.getFilename());
            archivedBytes = readEntryBytes(zf, pattern.getFilename());
        }

        assertTrue(Arrays.equals(expectedBytes, archivedBytes), "pattern bytes in archive must match source bytes");
    }

    @Test
    void duplicate_descriptor_entries_should_fail_explicitly() throws IOException {
        File lace = new File(tempDir, "duplicate-descriptor-entry.lace");
        byte[] firstDescriptor = new byte[]{0x08, 0x01};
        byte[] secondDescriptor = new byte[]{0x08, 0x02};
        writeZipWithDuplicateStoredEntries(
                lace,
                LaceArchiveLoader.DESCRIPTOR_PB_ENTRY,
                firstDescriptor,
                secondDescriptor
        );

        LaceArchiveException ex = assertThrows(LaceArchiveException.class, () -> LacePersistenceUseCase.load(lace));
        assertTrue(ex.getMessage().contains("Duplicate archive entry"), ex.getMessage());
    }

    @Test
    void truncated_central_directory_should_fail_explicitly() throws Exception {
        Diagram diagram = buildFixtureDiagram();
        File valid = new File(tempDir, "valid-for-truncation.lace");
        LacePersistenceUseCase.save(valid, diagram, SaveOptions.defaultOptions());

        byte[] validBytes = Files.readAllBytes(valid.toPath());
        assertTrue(validBytes.length > 64, "Valid fixture archive unexpectedly too small");
        File truncated = new File(tempDir, "truncated-central-directory.lace");
        Files.write(truncated.toPath(), Arrays.copyOf(validBytes, validBytes.length - 32));

        LaceArchiveException ex = assertThrows(LaceArchiveException.class, () -> LacePersistenceUseCase.load(truncated));
        assertTrue(ex.getMessage().contains("I/O error while loading lace archive"), ex.getMessage());
    }

    @Test
    void descriptor_pb_directory_entry_should_fail_explicitly() throws IOException {
        File lace = new File(tempDir, "descriptor-pb-directory-entry.lace");
        try (FileOutputStream fos = new FileOutputStream(lace);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            zos.putNextEntry(new ZipEntry(LaceArchiveLoader.DESCRIPTOR_PB_ENTRY + "/"));
            zos.closeEntry();
        }

        LaceArchiveException ex = assertThrows(LaceArchiveException.class, () -> LacePersistenceUseCase.load(lace));
        assertTrue(ex.getMessage().contains("Unsupported descriptor schema version"), ex.getMessage());
    }

    @Test
    void legacy_xml_and_protobuf_loaded_forms_should_be_semantically_equivalent_after_migration() throws Exception {
        File legacy = copyResourceToTemp(LEGACY_TEST_LACE_RESOURCE, "legacy-semantic.lace");

        Diagram xmlLoaded = LacePersistenceUseCase.load(legacy);
        materializePatternFilesFromArchiveToTempDir(legacy, xmlLoaded, new File(tempDir, "patterns-semantic"));

        File pb = new File(tempDir, "pb-from-legacy.lace");
        LacePersistenceUseCase.save(pb, xmlLoaded, SaveOptions.defaultOptions());
        Diagram pbLoaded = LacePersistenceUseCase.load(pb);

        DiagramSemanticAssertions.assertSemanticallyEquivalent(xmlLoaded, pbLoaded);
    }

    private File copyResourceToTemp(String resourcePath, String outputName) throws IOException {
        URL url = getClass().getClassLoader().getResource(resourcePath);
        assertNotNull(url, "Missing resource: " + resourcePath);
        File out = new File(tempDir, outputName);
        try (InputStream in = url.openStream()) {
            Files.copy(in, out.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        return out;
    }

    private static Diagram buildFixtureDiagram() {
        Diagram diagram = new Diagram();
        diagram.setName("integration-fixture");
        diagram.setCurrentGridType(org.alienlabs.adaloveslace.domain.enumeration.GridType.CRISS_CROSS);

        Pattern pattern = new Pattern();
        pattern.setFilename("snowflake_small.jpg");
        pattern.setAbsoluteFilename(resolveResourceAsFile(SNOWFLAKE_RESOURCE).getAbsolutePath());
        pattern.setCenterX(1d);
        pattern.setCenterY(2d);
        pattern.setWidth(10d);
        pattern.setHeight(12d);

        Knot patternKnot = new Knot(10d, 20d, Optional.of(pattern), Optional.empty(), Optional.of(Color.BLACK), null);
        patternKnot.setRotationAngle(5);
        patternKnot.setZoomFactor(2);

        Knot textKnot = new Knot(30d, 40d, Optional.empty(), Optional.of("Hello"), Optional.of(Color.BLUE), null);
        textKnot.setTypedText(new StringBuilder("Hello"));
        textKnot.setRotationAngle(15);
        textKnot.setZoomFactor(1);

        Step s1 = new Step();
        s1.setStepIndex(1);
        s1.setDisplayedKnots(List.of(patternKnot));
        s1.setSelectedKnots(List.of());

        Step s2 = new Step();
        s2.setStepIndex(2);
        s2.setDisplayedKnots(List.of(textKnot));
        s2.setSelectedKnots(List.of());

        diagram.setAllSteps(List.of(s1, s2));
        diagram.setCurrentStepIndex(2);
        diagram.addPattern(pattern);
        return diagram;
    }

    private static File resolveResourceAsFile(String resourcePath) {
        URL url = LacePersistenceIntegrationTest.class.getClassLoader().getResource(resourcePath);
        assertNotNull(url, "Missing resource: " + resourcePath);
        try {
            return new File(url.toURI());
        } catch (Exception e) {
            throw new IllegalStateException("Cannot resolve resource as file: " + resourcePath, e);
        }
    }

    private static void materializePatternFilesFromArchiveToTempDir(File laceFile, Diagram diagram, File targetDir) throws IOException {
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        try (ZipFile zip = new ZipFile(laceFile)) {
            for (Pattern p : diagram.getPatterns()) {
                if (p == null || p.getFilename() == null || p.getFilename().isBlank()) {
                    continue;
                }
                ZipEntry entry = zip.getEntry(p.getFilename());
                if (entry == null) {
                    continue;
                }
                File target = new File(targetDir, p.getFilename());
                if (target.exists()) {
                    p.setAbsoluteFilename(target.getAbsolutePath());
                    continue;
                }
                try (InputStream in = zip.getInputStream(entry)) {
                    Files.copy(in, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                p.setAbsoluteFilename(target.getAbsolutePath());
            }
        }
    }

    private static void copyEntry(ZipFile source, String entryName, ZipOutputStream target) throws IOException {
        ZipEntry entry = source.getEntry(entryName);
        if (entry == null) {
            return;
        }
        target.putNextEntry(new ZipEntry(entryName));
        try (InputStream in = source.getInputStream(entry)) {
            in.transferTo(target);
        }
        target.closeEntry();
    }

    private static Properties readMetaProperties(ZipFile zipFile) throws IOException {
        ZipEntry metaEntry = zipFile.getEntry(LaceArchiveLoader.META_PROPERTIES_ENTRY);
        assertNotNull(metaEntry, "Missing meta.properties");
        Properties props = new Properties();
        try (InputStream in = zipFile.getInputStream(metaEntry)) {
            props.load(in);
        }
        return props;
    }

    private static byte[] readEntryBytes(ZipFile zipFile, String entryName) throws IOException {
        ZipEntry entry = zipFile.getEntry(entryName);
        assertNotNull(entry, "Missing entry: " + entryName);
        try (InputStream in = zipFile.getInputStream(entry)) {
            return in.readAllBytes();
        }
    }

    private static void writeZipWithDuplicateStoredEntries(File outFile, String entryName, byte[] firstData, byte[] secondData)
            throws IOException {
        try (FileOutputStream out = new FileOutputStream(outFile)) {
            byte[] nameBytes = entryName.getBytes(StandardCharsets.UTF_8);
            long firstOffset = 0L;
            writeLocalHeader(out, nameBytes, firstData);
            out.write(nameBytes);
            out.write(firstData);

            long secondOffset = 30L + nameBytes.length + firstData.length;
            writeLocalHeader(out, nameBytes, secondData);
            out.write(nameBytes);
            out.write(secondData);

            long centralDirOffset = secondOffset + 30L + nameBytes.length + secondData.length;
            long centralStart = centralDirOffset;
            writeCentralDirectoryHeader(out, nameBytes, firstData, firstOffset);
            writeCentralDirectoryHeader(out, nameBytes, secondData, secondOffset);
            long centralDirSize = (30L + 16L + nameBytes.length) * 2L;

            writeEndOfCentralDirectory(out, 2, centralDirSize, centralStart);
        }
    }

    private static void writeLocalHeader(FileOutputStream out, byte[] nameBytes, byte[] data) throws IOException {
        CRC32 crc = new CRC32();
        crc.update(data);
        writeIntLE(out, 0x04034B50);
        writeShortLE(out, 20);
        writeShortLE(out, 0);
        writeShortLE(out, 0); // stored
        writeShortLE(out, 0);
        writeShortLE(out, 0);
        writeIntLE(out, (int) crc.getValue());
        writeIntLE(out, data.length);
        writeIntLE(out, data.length);
        writeShortLE(out, nameBytes.length);
        writeShortLE(out, 0);
    }

    private static void writeCentralDirectoryHeader(FileOutputStream out, byte[] nameBytes, byte[] data, long localOffset)
            throws IOException {
        CRC32 crc = new CRC32();
        crc.update(data);
        writeIntLE(out, 0x02014B50);
        writeShortLE(out, 20);
        writeShortLE(out, 20);
        writeShortLE(out, 0);
        writeShortLE(out, 0);
        writeShortLE(out, 0);
        writeShortLE(out, 0);
        writeIntLE(out, (int) crc.getValue());
        writeIntLE(out, data.length);
        writeIntLE(out, data.length);
        writeShortLE(out, nameBytes.length);
        writeShortLE(out, 0);
        writeShortLE(out, 0);
        writeShortLE(out, 0);
        writeShortLE(out, 0);
        writeIntLE(out, 0);
        writeIntLE(out, (int) localOffset);
        out.write(nameBytes);
    }

    private static void writeEndOfCentralDirectory(FileOutputStream out, int entries, long centralSize, long centralOffset)
            throws IOException {
        writeIntLE(out, 0x06054B50);
        writeShortLE(out, 0);
        writeShortLE(out, 0);
        writeShortLE(out, entries);
        writeShortLE(out, entries);
        writeIntLE(out, (int) centralSize);
        writeIntLE(out, (int) centralOffset);
        writeShortLE(out, 0);
    }

    private static void writeShortLE(FileOutputStream out, int value) throws IOException {
        out.write(value & 0xFF);
        out.write((value >>> 8) & 0xFF);
    }

    private static void writeIntLE(FileOutputStream out, int value) throws IOException {
        out.write(value & 0xFF);
        out.write((value >>> 8) & 0xFF);
        out.write((value >>> 16) & 0xFF);
        out.write((value >>> 24) & 0xFF);
    }
}


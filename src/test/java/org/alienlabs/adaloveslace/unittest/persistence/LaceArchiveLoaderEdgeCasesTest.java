package org.alienlabs.adaloveslace.unittest.persistence;

import javafx.scene.layout.Pane;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.Diagram;
import org.alienlabs.adaloveslace.persistence.LaceArchiveException;
import org.alienlabs.adaloveslace.persistence.LaceArchiveLoader;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unit")
class LaceArchiveLoaderEdgeCasesTest {

    @TempDir
    File tempDir;

    @Test
    void loading_non_zip_file_should_fail_clearly() throws IOException {
        App app = new App();
        app.setMovablePane(new Pane());

        File f = new File(tempDir, "not-a-zip.lace");
        Files.writeString(f.toPath(), "this is not a zip", StandardCharsets.UTF_8);

        LaceArchiveException ex = assertThrows(LaceArchiveException.class, () -> LaceArchiveLoader.load(f, new Diagram(app)));
        assertTrue(ex.getMessage().contains("I/O error while loading lace archive"), ex.getMessage());
    }

    @Test
    void invalid_archive_version_in_meta_properties_should_fail() throws IOException {
        App app = new App();
        app.setMovablePane(new Pane());

        File lace = new File(tempDir, "invalid-meta-version.lace");

        // Minimal valid protobuf: schema_version=1 only.
        byte[] descriptorPbMinimal = new byte[]{0x08, 0x01};

        try (FileOutputStream fos = new FileOutputStream(lace);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            zos.putNextEntry(new ZipEntry(LaceArchiveLoader.META_PROPERTIES_ENTRY));
            Properties props = new Properties();
            props.setProperty("archive.version", "not-a-number");
            props.store(zos, "test");
            zos.closeEntry();

            zos.putNextEntry(new ZipEntry(LaceArchiveLoader.DESCRIPTOR_PB_ENTRY));
            zos.write(descriptorPbMinimal);
            zos.closeEntry();
        }

        LaceArchiveException ex = assertThrows(LaceArchiveException.class, () -> LaceArchiveLoader.load(lace, new Diagram(app)));
        assertTrue(ex.getMessage().contains("Invalid archive.version"), ex.getMessage());
    }

    @Test
    void protobuf_archive_without_meta_properties_should_still_load_as_version_0() throws IOException {
        App app = new App();
        app.setMovablePane(new Pane());

        File lace = new File(tempDir, "pb-without-meta.lace");
        byte[] descriptorPbMinimal = new byte[]{0x08, 0x01};

        try (FileOutputStream fos = new FileOutputStream(lace);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            zos.putNextEntry(new ZipEntry(LaceArchiveLoader.DESCRIPTOR_PB_ENTRY));
            zos.write(descriptorPbMinimal);
            zos.closeEntry();
        }

        Diagram loaded = LaceArchiveLoader.load(lace, new Diagram(app));
        assertNotNull(loaded);
    }

    @Test
    void descriptor_json_entry_should_be_ignored_for_loading() throws IOException {
        App app = new App();
        app.setMovablePane(new Pane());

        File lace = new File(tempDir, "pb-plus-json.lace");
        byte[] descriptorPbMinimal = new byte[]{0x08, 0x01};

        try (FileOutputStream fos = new FileOutputStream(lace);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            zos.putNextEntry(new ZipEntry(LaceArchiveLoader.DESCRIPTOR_PB_ENTRY));
            zos.write(descriptorPbMinimal);
            zos.closeEntry();

            zos.putNextEntry(new ZipEntry(LaceArchiveLoader.DESCRIPTOR_JSON_ENTRY));
            zos.write("not necessarily valid json".getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();
        }

        Diagram loaded = LaceArchiveLoader.load(lace, new Diagram(app));
        assertNotNull(loaded);
    }
}


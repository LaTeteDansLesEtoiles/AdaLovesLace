package org.alienlabs.adaloveslace.unittest.persistence;

import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.Diagram;
import org.alienlabs.adaloveslace.persistence.LaceArchiveException;
import org.alienlabs.adaloveslace.persistence.LaceArchiveLoader;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.assertThrows;

class LaceArchiveLoaderTest {

    @Test
    void loading_archive_without_descriptor_should_fail() throws IOException {
        File tmp = File.createTempFile("no-descriptor", ".lace");
        try (FileOutputStream fos = new FileOutputStream(tmp);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            zos.putNextEntry(new ZipEntry("foo.txt"));
            zos.write("test".getBytes());
            zos.closeEntry();
        }

        Diagram empty = new Diagram(new App());
        assertThrows(LaceArchiveException.class, () -> LaceArchiveLoader.load(tmp, empty));

        // noinspection ResultOfMethodCallIgnored
        tmp.delete();
    }

    @Test
    void loading_archive_with_unsupported_archive_version_should_fail() throws IOException {
        File tmp = File.createTempFile("unsupported-version", ".lace");
        try (FileOutputStream fos = new FileOutputStream(tmp);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            // meta.properties with future archive.version
            zos.putNextEntry(new ZipEntry(LaceArchiveLoader.META_PROPERTIES_ENTRY));
            Properties props = new Properties();
            props.setProperty("archive.version", "999");
            props.store(zos, "test");
            zos.closeEntry();

            // minimal descriptor.pb entry (empty, will not be parsed before version check)
            zos.putNextEntry(new ZipEntry(LaceArchiveLoader.DESCRIPTOR_PB_ENTRY));
            zos.write(new byte[]{});
            zos.closeEntry();
        }

        Diagram empty = new Diagram(new App());
        LaceArchiveException ex = assertThrows(LaceArchiveException.class, () -> LaceArchiveLoader.load(tmp, empty));
        org.junit.jupiter.api.Assertions.assertTrue(ex.getMessage().contains("Unsupported archive version"));

        // noinspection ResultOfMethodCallIgnored
        tmp.delete();
    }

    @Test
    void loading_archive_with_corrupted_protobuf_should_fail() throws IOException {
        File tmp = File.createTempFile("corrupted-descriptor", ".lace");
        try (FileOutputStream fos = new FileOutputStream(tmp);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            zos.putNextEntry(new ZipEntry(LaceArchiveLoader.DESCRIPTOR_PB_ENTRY));
            zos.write(new byte[]{0x01, 0x02, 0x03}); // invalid protobuf
            zos.closeEntry();
        }

        Diagram empty = new Diagram(new App());
        assertThrows(LaceArchiveException.class, () -> LaceArchiveLoader.load(tmp, empty));

        // noinspection ResultOfMethodCallIgnored
        tmp.delete();
    }
}


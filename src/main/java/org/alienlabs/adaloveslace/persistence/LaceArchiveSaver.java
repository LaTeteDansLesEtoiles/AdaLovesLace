package org.alienlabs.adaloveslace.persistence;

import com.google.protobuf.util.JsonFormat;
import org.alienlabs.adaloveslace.domain.Diagram;
import org.alienlabs.adaloveslace.persistence.protobuf.DiagramDescriptor;
import org.alienlabs.adaloveslace.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Centralized saver for .lace archives using protobuf as the primary descriptor format.
 */
public final class LaceArchiveSaver {

    private static final Logger logger = LoggerFactory.getLogger(LaceArchiveSaver.class);

    private LaceArchiveSaver() {
        // Utility
    }

    public static final class SaveOptions {
        private final boolean writeDebugJson;

        public SaveOptions(boolean writeDebugJson) {
            this.writeDebugJson = writeDebugJson;
        }

        public boolean isWriteDebugJson() {
            return writeDebugJson;
        }

        public static SaveOptions defaultOptions() {
            return new SaveOptions(false);
        }
    }

    public static void save(File file, Diagram diagram, SaveOptions options) {
        DiagramDescriptor descriptor = DiagramPersistenceMapper.toProto(diagram);

        try (FileOutputStream fos = new FileOutputStream(file);
             ZipOutputStream zipOut = new ZipOutputStream(fos)) {

            // 1) Write archive metadata
            writeMetaProperties(zipOut);

            // 2) Write descriptor.pb
            zipOut.putNextEntry(new ZipEntry(LaceArchiveLoader.DESCRIPTOR_PB_ENTRY));
            descriptor.writeTo(zipOut);
            zipOut.closeEntry();

            // 3) Optional debug JSON descriptor.json
            if (options != null && options.isWriteDebugJson()) {
                zipOut.putNextEntry(new ZipEntry(LaceArchiveLoader.DESCRIPTOR_JSON_ENTRY));
                String json = JsonFormat.printer().includingDefaultValueFields().print(descriptor);
                zipOut.write(json.getBytes(StandardCharsets.UTF_8));
                zipOut.closeEntry();
            }

            // 4) Patterns / resources: delegate to existing logic by copying files
            writePatternsToArchive(diagram, zipOut);

        } catch (IOException e) {
            throw new LaceArchiveException("Error saving lace archive to " + file.getAbsolutePath(), e);
        }
    }

    private static void writeMetaProperties(ZipOutputStream zipOut) throws IOException {
        Properties props = new Properties();
        props.setProperty("archive.version", "1");
        zipOut.putNextEntry(new ZipEntry(LaceArchiveLoader.META_PROPERTIES_ENTRY));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        props.store(bos, "AdaLovesLace lace archive metadata");
        zipOut.write(bos.toByteArray());
        zipOut.closeEntry();
    }

    private static void writePatternsToArchive(Diagram diagram, ZipOutputStream zipOut) throws IOException {
        // Reuse the existing convention: store each pattern filename as a separate ZIP entry.
        // Sort for stable archive writing order.
        for (org.alienlabs.adaloveslace.domain.Pattern pattern : diagram.getPatterns().stream()
                .sorted(java.util.Comparator.comparing(org.alienlabs.adaloveslace.domain.Pattern::getFilename))
                .toList()) {
            if (pattern.getFilename() == null || pattern.getFilename().isBlank()) {
                continue;
            }

            java.io.File fileToZip;
            if (pattern.getAbsoluteFilename() != null && !pattern.getAbsoluteFilename().isBlank()) {
                fileToZip = new java.io.File(pattern.getAbsoluteFilename());
            } else {
                fileToZip = new java.io.File(
                        FileUtil.APP_FOLDER_IN_USER_HOME +
                                org.alienlabs.adaloveslace.App.PATTERNS_DIRECTORY_NAME +
                                java.io.File.separator +
                                pattern.getFilename());
            }

            if (!fileToZip.exists()) {
                logger.warn("Pattern file not found on disk, skipping: {}", fileToZip.getAbsolutePath());
                continue;
            }
            try {
                zipOut.putNextEntry(new ZipEntry(pattern.getFilename()));
                Files.copy(fileToZip.toPath(), zipOut);
                zipOut.closeEntry();
            } catch (java.util.zip.ZipException e) {
                logger.error("Error saving pattern into lace archive: {}", fileToZip.getAbsolutePath(), e);
            }
        }
    }
}


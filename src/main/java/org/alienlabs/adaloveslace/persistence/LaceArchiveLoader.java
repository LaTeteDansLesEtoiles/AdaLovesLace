package org.alienlabs.adaloveslace.persistence;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import org.alienlabs.adaloveslace.domain.Diagram;
import org.alienlabs.adaloveslace.domain.Pattern;
import org.alienlabs.adaloveslace.persistence.protobuf.DiagramDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Centralized loader for .lace archives. Detects descriptor format and delegates
 * to protobuf or legacy XML loaders as appropriate.
 */
public final class LaceArchiveLoader {

    public static final String DESCRIPTOR_PB_ENTRY = "descriptor.pb";
    public static final String DESCRIPTOR_JSON_ENTRY = "descriptor.json";
    public static final String LEGACY_XML_ENTRY = "save.xml";
    public static final String META_PROPERTIES_ENTRY = "meta.properties";

    private static final Logger logger = LoggerFactory.getLogger(LaceArchiveLoader.class);

    private static final int SUPPORTED_ARCHIVE_VERSION = 1;

    private LaceArchiveLoader() {
        // Utility
    }

    public static Diagram load(File file, Diagram emptyDiagramTemplate) {
        long t0 = System.nanoTime();
        try (ZipFile zipFile = new ZipFile(file)) {
            long tOpen = System.nanoTime();
            logger.debug("Opened lace archive {} in {} ms", file.getAbsolutePath(), elapsedMs(t0, tOpen));
            ensureNoDuplicateEntries(zipFile);

            ZipEntry pbEntry = zipFile.getEntry(DESCRIPTOR_PB_ENTRY);
            ZipEntry xmlEntry = zipFile.getEntry(LEGACY_XML_ENTRY);

            if (pbEntry == null && xmlEntry == null) {
                throw new LaceFormatException("Lace archive does not contain descriptor.pb or save.xml");
            }

            int archiveVersion = readArchiveVersion(zipFile);
            if (archiveVersion > SUPPORTED_ARCHIVE_VERSION) {
                throw new UnsupportedDescriptorVersionException(
                        "Unsupported archive version: " + archiveVersion + ", supported up to " + SUPPORTED_ARCHIVE_VERSION);
            }

            Diagram diagram;
            if (pbEntry != null) {
                if (xmlEntry != null) {
                    logger.info("Both descriptor.pb and save.xml found in archive. Using descriptor.pb and ignoring legacy XML.");
                }
                long tPbStart = System.nanoTime();
                diagram = loadFromProtobuf(zipFile, pbEntry, emptyDiagramTemplate);
                long tPbEnd = System.nanoTime();
                logger.debug("Loaded diagram from protobuf in {} ms", elapsedMs(tPbStart, tPbEnd));
            } else {
                long tXmlStart = System.nanoTime();
                try {
                    diagram = LegacyXmlCompatibilityLoader.loadDiagram(zipFile, xmlEntry);
                } catch (Exception e) {
                    throw new LaceArchiveException("Failed to load legacy XML descriptor from save.xml", e);
                }
                long tXmlEnd = System.nanoTime();
                logger.debug("Loaded diagram via legacy XML in {} ms", elapsedMs(tXmlStart, tXmlEnd));
            }

            verifyReferencedPatternResourcesPresent(zipFile, diagram);

            long tEnd = System.nanoTime();
            logger.debug("Completed lace archive load in {} ms (file: {})", elapsedMs(t0, tEnd), file.getAbsolutePath());
            return diagram;
        } catch (IOException e) {
            throw new LaceArchiveException("I/O error while loading lace archive: " + file.getAbsolutePath(), e);
        }
    }

    private static void verifyReferencedPatternResourcesPresent(ZipFile zipFile, Diagram diagram) {
        if (diagram == null || diagram.getPatterns() == null || diagram.getPatterns().isEmpty()) {
            return;
        }

        Set<String> required = new HashSet<>();
        for (Pattern p : diagram.getPatterns()) {
            if (p == null) {
                continue;
            }
            String filename = p.getFilename();
            if (filename == null || filename.isBlank()) {
                continue;
            }
            required.add(filename);
        }

        for (String filename : required) {
            if (zipFile.getEntry(filename) == null) {
                throw new LaceArchiveException("Missing required pattern resource in lace archive: " + filename);
            }
        }
    }

    private static Diagram loadFromProtobuf(ZipFile zipFile, ZipEntry pbEntry, Diagram emptyDiagramTemplate) {
        try (InputStream in = zipFile.getInputStream(pbEntry)) {
            DiagramDescriptor descriptor = DiagramDescriptor.parseFrom(in);
            return DiagramPersistenceMapper.fromProto(descriptor, emptyDiagramTemplate);
        } catch (InvalidProtocolBufferException e) {
            throw new LaceArchiveException("Malformed protobuf descriptor in descriptor.pb", e);
        } catch (IOException e) {
            throw new LaceArchiveException("I/O error while reading descriptor.pb", e);
        }
    }

    private static int readArchiveVersion(ZipFile zipFile) {
        ZipEntry metaEntry = zipFile.getEntry(META_PROPERTIES_ENTRY);
        if (metaEntry == null) {
            // Legacy archives without metadata are treated as version 0.
            return 0;
        }
        Properties props = new Properties();
        try (InputStream in = zipFile.getInputStream(metaEntry)) {
            props.load(in);
        } catch (IllegalArgumentException e) {
            throw new LaceArchiveException("Malformed meta.properties in lace archive", e);
        } catch (IOException e) {
            throw new LaceArchiveException("Failed to read meta.properties from lace archive", e);
        }
        String versionRaw = props.getProperty("archive.version");
        if (versionRaw == null || versionRaw.isBlank()) {
            throw new LaceArchiveException("Missing archive.version in meta.properties");
        }
        String versionStr = versionRaw.trim();
        try {
            return Integer.parseInt(versionStr);
        } catch (NumberFormatException e) {
            throw new LaceArchiveException("Invalid archive.version in meta.properties: " + versionStr, e);
        }
    }

    private static void ensureNoDuplicateEntries(ZipFile zipFile) {
        Set<String> seen = new HashSet<>();
        zipFile.stream().forEach(entry -> {
            String name = entry.getName();
            if (!seen.add(name)) {
                throw new LaceFormatException("Duplicate archive entry: " + name);
            }
        });
    }

    /**
     * Debug-only helper to dump descriptor.pb to JSON using the same mapping.
     */
    public static String dumpDescriptorJson(File file) {
        try (ZipFile zipFile = new ZipFile(file)) {
            ZipEntry pbEntry = zipFile.getEntry(DESCRIPTOR_PB_ENTRY);
            if (pbEntry == null) {
                throw new LaceFormatException("descriptor.pb not found in archive: " + file.getAbsolutePath());
            }
            try (InputStream in = zipFile.getInputStream(pbEntry)) {
                DiagramDescriptor descriptor = DiagramDescriptor.parseFrom(in);
                return JsonFormat.printer().includingDefaultValueFields().print(descriptor);
            }
        } catch (IOException e) {
            throw new LaceArchiveException("Failed to dump descriptor.json from: " + file.getAbsolutePath(), e);
        }
    }

    private static long elapsedMs(long startNanos, long endNanos) {
        return (endNanos - startNanos) / 1_000_000L;
    }
}


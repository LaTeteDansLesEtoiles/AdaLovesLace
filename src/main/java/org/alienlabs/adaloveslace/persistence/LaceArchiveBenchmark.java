package org.alienlabs.adaloveslace.persistence;

import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.Diagram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Lightweight helper to compare legacy XML and protobuf loading performance.
 * <p>
 * This is intended for development and troubleshooting only.
 */
public final class LaceArchiveBenchmark {

    private static final Logger logger = LoggerFactory.getLogger(LaceArchiveBenchmark.class);

    private LaceArchiveBenchmark() {
        // Utility
    }

    public static void benchmark(File laceFile) throws IOException {
        try (ZipFile zipFile = new ZipFile(laceFile)) {
            ZipEntry xmlEntry = zipFile.getEntry(LaceArchiveLoader.LEGACY_XML_ENTRY);
            ZipEntry pbEntry = zipFile.getEntry(LaceArchiveLoader.DESCRIPTOR_PB_ENTRY);

            if (xmlEntry == null && pbEntry == null) {
                logger.warn("No descriptor found in {}", laceFile.getAbsolutePath());
                return;
            }

            Diagram template = new Diagram(new App());

            if (xmlEntry != null) {
                long t0 = System.nanoTime();
                try {
                    LegacyXmlCompatibilityLoader.loadDiagram(zipFile, xmlEntry);
                    long t1 = System.nanoTime();
                    logger.info("Legacy XML load time for {}: {} ms", laceFile.getName(), (t1 - t0) / 1_000_000L);
                } catch (Exception e) {
                    logger.error("Legacy XML load failed for benchmark", e);
                }
            }

            if (pbEntry != null) {
                long t0 = System.nanoTime();
                LaceArchiveLoader.load(laceFile, template);
                long t1 = System.nanoTime();
                logger.info("Protobuf load time for {}: {} ms", laceFile.getName(), (t1 - t0) / 1_000_000L);
            }
        }
    }

    /**
     * Dev-only CLI entry point for quick manual benchmarking.
     * <p>
     * Usage:
     * {@code mvn -q -DskipTests package && java ... org.alienlabs.adaloveslace.persistence.LaceArchiveBenchmark /path/to/file.lace}
     */
    public static void main(String[] args) throws IOException {
        if (args == null || args.length == 0) {
            System.err.println("Usage: LaceArchiveBenchmark <file1.lace> [file2.lace] ...");
            System.exit(2);
            return;
        }
        for (String arg : args) {
            if (arg == null || arg.isBlank()) {
                continue;
            }
            benchmark(new File(arg));
        }
    }
}


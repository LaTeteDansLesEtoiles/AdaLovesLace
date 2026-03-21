package org.alienlabs.adaloveslace.persistence;

import org.alienlabs.adaloveslace.domain.Diagram;

import java.io.File;

/**
 * Non-UI persistence use-case entry point for .lace archives.
 * <p>
 * This facade intentionally avoids JavaFX/UI wiring and delegates to the
 * archive loader/saver infrastructure.
 */
public final class LacePersistenceUseCase {

    private LacePersistenceUseCase() {
        // Utility
    }

    public static Diagram load(File archiveFile) {
        return LaceArchiveLoader.load(archiveFile, new Diagram());
    }

    public static void save(File archiveFile, Diagram diagram) {
        LaceArchiveSaver.save(archiveFile, diagram, LaceArchiveSaver.SaveOptions.defaultOptions());
    }

    public static void save(File archiveFile, Diagram diagram, LaceArchiveSaver.SaveOptions options) {
        LaceArchiveSaver.save(archiveFile, diagram, options);
    }

    public static String dumpDescriptorJson(File archiveFile) {
        return LaceArchiveLoader.dumpDescriptorJson(archiveFile);
    }
}


package org.alienlabs.adaloveslace.integrationtest.persistence.support;

import java.util.zip.ZipFile;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public final class ArchiveAssertions {

    private ArchiveAssertions() {
    }

    public static void assertHasEntry(ZipFile zipFile, String entryName) {
        assertNotNull(zipFile.getEntry(entryName), "Missing archive entry: " + entryName);
    }

    public static void assertMissingEntry(ZipFile zipFile, String entryName) {
        assertNull(zipFile.getEntry(entryName), "Archive entry should be absent: " + entryName);
    }
}


package org.alienlabs.adaloveslace.persistence;

import java.io.Serial;

/**
 * Base exception for lace archive persistence issues.
 */
public class LaceArchiveException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public LaceArchiveException(String message) {
        super(message);
    }

    public LaceArchiveException(String message, Throwable cause) {
        super(message, cause);
    }
}

/**
 * Thrown when a lace archive structure is invalid or missing required entries.
 */
class LaceFormatException extends LaceArchiveException {

    @Serial
    private static final long serialVersionUID = 1L;

    LaceFormatException(String message) {
        super(message);
    }

    LaceFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}

/**
 * Thrown when the descriptor or archive version is not supported by this runtime.
 */
class UnsupportedDescriptorVersionException extends LaceArchiveException {

    @Serial
    private static final long serialVersionUID = 1L;

    UnsupportedDescriptorVersionException(String message) {
        super(message);
    }
}


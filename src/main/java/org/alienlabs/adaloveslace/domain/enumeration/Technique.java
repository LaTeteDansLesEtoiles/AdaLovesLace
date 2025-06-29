package org.alienlabs.adaloveslace.domain.enumeration;

/**
 * The Technique enumeration.
 */
public enum Technique {
    LACE("dentelle"),
    KNITTING("tricot"),
    CROCHET("crochet"),
    EMBROIDERY("broderie"),
    MISCELLANEOUS("divers");

    private final String value;

    Technique(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

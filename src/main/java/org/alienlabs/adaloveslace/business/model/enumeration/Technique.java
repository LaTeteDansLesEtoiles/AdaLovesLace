package org.alienlabs.adaloveslace.business.model.enumeration;

/**
 * The Technique enumeration.
 */
public enum Technique {
    LACE("dentelle"),
    KNITTING("tricot"),
    CROCHET("crochet");

    private final String value;

    Technique(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

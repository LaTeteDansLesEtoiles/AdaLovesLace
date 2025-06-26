package org.alienlabs.adaloveslace.domain.enumeration;

/**
 * The Language enumeration.
 */
public enum Language {
    FRENCH("français"),
    English("Anglais"),
    NON_RELEVANT("Indifférent");

    private final String value;

    Language(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

package org.alienlabs.adaloveslace.business.model;

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

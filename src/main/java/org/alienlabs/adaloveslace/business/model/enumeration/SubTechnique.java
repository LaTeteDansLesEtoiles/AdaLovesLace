package org.alienlabs.adaloveslace.business.model.enumeration;

/**
 * The SubTechnique enumeration.
 */
public enum SubTechnique {
    HOOK("crochet"),
    TATTING_LACE("dentelle à l&#39;aiguille"),
    SHETTLAND("dentelle de Shettland"),
    GRANNY_SQUARE("granny square");

    private final String value;

    SubTechnique(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

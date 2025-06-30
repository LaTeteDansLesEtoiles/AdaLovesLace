package org.alienlabs.adaloveslace.domain.enumeration;

/**
 * The SubTechnique enumeration.
 */
public enum SubTechnique {
    CROCHET("CROCHET"),
    MISC_CROCHET("MISC_CROCHET"),
    TATTING_LACE("TATTING_LACE"),
    SHETTLAND("SHETTLAND"),
    MISC_LACE("MISC_LACE"),
    GRANNY_SQUARE("GRANNY_SQUARE"),
    CORNER_TO_CORNER("CORNER_TO_CORNER"),
    MOSAIC_CROCHET("MOSAIC_CROCHET"),
    DIAMOND_PAINTING("DIAMOND_PAINTING"),
    PIXEL_ART("PIXEL_ART"),
    CROSS_STITCH("CROSS_STITCH"),
    MISC_EMBROIDERY("MISC_EMBROIDERY"),
    TAPESTRY("TAPESTRY"),
    PEYOTE_WEAVING("PEYOTE_WEAVING"),
    MISC_MISCELLANEOUS("MISC_MISCELLANEOUS"),
    KNITTING("KNITTING"),
    MISC_KNITTING("MISC_KNITTING");

    private final String messageKey;

    SubTechnique(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }
}

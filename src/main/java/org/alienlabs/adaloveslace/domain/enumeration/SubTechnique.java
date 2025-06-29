package org.alienlabs.adaloveslace.domain.enumeration;

/**
 * The SubTechnique enumeration.
 */
public enum SubTechnique {
    CROCHET("crochet"),
    TATTING_LACE("dentelle à l&#39;aiguille"),
    SHETTLAND("dentelle de Shettland"),
    GRANNY_SQUARE("granny square"),
    CORNER_TO_CORNER("corner to corner"),
    MOSAIC_CROCHET("crochet mosa&#239;que"),
    DIAMOND_PAINTING("diamond painting"),
    PIXEL_ART("pixel-art"),
    CROSS_STITCH("point de croix"),
    TAPESTRY("tapisserie"),
    PEYOTE_WEAVING("tissage peyote");

    private final String value;

    SubTechnique(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

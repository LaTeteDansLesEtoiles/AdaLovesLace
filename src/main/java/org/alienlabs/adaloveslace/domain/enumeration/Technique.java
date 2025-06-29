package org.alienlabs.adaloveslace.domain.enumeration;

import java.util.List;

/**
 * The Technique enumeration.
 */
public enum Technique {
    LACE("dentelle",
            SubTechnique.TATTING_LACE,
            SubTechnique.MISC_LACE
    ),
    KNITTING("tricot",
            SubTechnique.KNITTING,
            SubTechnique.SHETTLAND,
            SubTechnique.MISC_KNITTING
    ),
    CROCHET("crochet",
            SubTechnique.CORNER_TO_CORNER,
            SubTechnique.CROCHET,
            SubTechnique.MOSAIC_CROCHET,
            SubTechnique.TAPESTRY,
            SubTechnique.MISC_CROCHET
    ),
    EMBROIDERY("broderie",
            SubTechnique.CROSS_STITCH,
            SubTechnique.MISC_EMBROIDERY
    ),
    MISCELLANEOUS("divers",
            SubTechnique.DIAMOND_PAINTING,
            SubTechnique.PIXEL_ART,
            SubTechnique.PEYOTE_WEAVING,
            SubTechnique.MISC_MISCELLANEOUS
    );

    private final String value;
    private final List<SubTechnique> subTechniques;

    Technique(String value, SubTechnique... subTechniquess) {
        this.value = value;
        this.subTechniques = List.of(subTechniquess);
    }

    public String getValue() {
        return this.value;
    }

    public List<SubTechnique> getSubTechniques() {
        return this.subTechniques;
    }
}

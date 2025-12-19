package org.alienlabs.adaloveslace.domain.enumeration;

import java.util.List;

/**
 * The Technique enumeration.
 */
public enum Technique {
    LACE("LACE",
            SubTechnique.TATTING_LACE,
            SubTechnique.MISC_LACE
    ),
    KNITTING("KNITTING",
            SubTechnique.KNITTING,
            SubTechnique.SHETTLAND,
            SubTechnique.MISC_KNITTING
    ),
    CROCHET("CROCHET",
            SubTechnique.CORNER_TO_CORNER,
            SubTechnique.CROCHET,
            SubTechnique.MOSAIC_CROCHET,
            SubTechnique.TAPESTRY,
            SubTechnique.MISC_CROCHET
    ),
    EMBROIDERY("EMBROIDERY",
            SubTechnique.CROSS_STITCH,
            SubTechnique.MISC_EMBROIDERY
    ),
    MISCELLANEOUS("MISCELLANEOUS",
            SubTechnique.DIAMOND_PAINTING,
            SubTechnique.PIXEL_ART,
            SubTechnique.PEYOTE_WEAVING,
            SubTechnique.MISC_MISCELLANEOUS
    );

    private final String messageKey;
    private final List<SubTechnique> subTechniques;

    Technique(String messageKey, SubTechnique... subTechniquess) {
        this.messageKey = messageKey;
        this.subTechniques = List.of(subTechniquess);
    }

    public String getMessageKey() {
        return this.messageKey;
    }

    public List<SubTechnique> getSubTechniques() {
        return this.subTechniques;
    }
}

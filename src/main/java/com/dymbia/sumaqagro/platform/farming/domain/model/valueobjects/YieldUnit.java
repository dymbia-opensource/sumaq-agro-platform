package com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects;

/**
 * Unit in which the harvest of a campaign is measured.
 *
 * @since 1.1
 */
public enum YieldUnit {
    // Sacks of Andean potato
    SACK,
    // Quintals of specialty coffee
    QUINTAL;

    /**
     * Gets the unit that matches a crop type.
     *
     * @param cropType the crop type of the campaign
     * @return QUINTAL for coffee, SACK for potato
     */
    public static YieldUnit forCropType(CropType cropType) {
        if (cropType == CropType.SPECIALTY_COFFEE) {
            return QUINTAL;
        } else {
            return SACK;
        }
    }
}

package com.dymbia.sumaqagro.platform.farming.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.CropType;

import java.util.Locale;

/**
 * Converts the crop type text sent by the client into the domain enum.
 */
public final class CropTypeFromResourceAssembler {

    private CropTypeFromResourceAssembler() {
    }

    /**
     * Converts a crop type name, such as {@code ANDEAN_POTATO}, into a {@link CropType}.
     *
     * @param cropType the crop type name
     * @return the crop type
     * @throws IllegalArgumentException when the text is blank or is not a crop type
     */
    public static CropType toCropTypeFromResource(String cropType) {
        if (cropType == null || cropType.isBlank()) {
            throw new IllegalArgumentException("crop.campaign.error.cropType.required");
        }
        try {
            return CropType.valueOf(cropType.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("crop.campaign.error.cropType.invalid");
        }
    }
}

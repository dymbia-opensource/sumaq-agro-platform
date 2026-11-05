package com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects;

/**
 * Seed variety used in a crop campaign
 *
 * @param value variety name, not blank and a maximum of 60 characters
 * @since 1.0
 */
public record SeedVariety(String value) {
    private static final int MAX_LENGTH = 60;
    private static final String NOT_BLANK_MESSAGE_KEY = "crop.campaign.error.seedVariety.notBlank";
    private static final String SIZE_MESSAGE_KEY = "crop.campaign.error.seedVariety.size";

    /**
     * Validate value the seed variety
     *
     * @throws IllegalArgumentException if the value is null, blank, or exceeds the maximum length
     */
    public SeedVariety{
        if(value== null||value.isBlank()) {
            throw new IllegalArgumentException(NOT_BLANK_MESSAGE_KEY);
        }
        value =value.trim();
        if(value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(SIZE_MESSAGE_KEY);
        }
    }


}

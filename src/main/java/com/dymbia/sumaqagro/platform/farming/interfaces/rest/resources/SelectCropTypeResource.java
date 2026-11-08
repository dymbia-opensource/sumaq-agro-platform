package com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources;

/**
 * Request body to change the crop of a campaign.
 *
 * @param cropType the crop type ({@code ANDEAN_POTATO} or {@code SPECIALTY_COFFEE})
 */
public record SelectCropTypeResource(String cropType) {
}

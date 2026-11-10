package com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects;

/**
 * Identity of the field plot where a crop campaign takes place.
 * <p>
 * The crop campaign refers to the field plot aggregate only by its identity, never by the object.
 * </p>
 *
 * @param fieldPlotId the field plot id, greater than 0
 * @since 1.0
 */
public record FieldPlotId(Long fieldPlotId) {
    /**
     * Validates the field plot id.
     *
     * @throws IllegalArgumentException when the id is null or not positive
     */
    public FieldPlotId {
        if (fieldPlotId == null || fieldPlotId <= 0) {
            throw new IllegalArgumentException("crop.campaign.error.plotId.invalid");
        }
    }
}

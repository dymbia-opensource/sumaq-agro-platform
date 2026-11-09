package com.dymbia.sumaqagro.platform.farming.domain.model.commands;

/**
 * Command to link a field plot to its polygon in the monitoring provider
 *
 * @param fieldPlotId the field plot id
 * @param polygonId   the polygon id in the provider
 * @since 1.0
 */
public record LinkPlotPolygonCommand(Long fieldPlotId, String polygonId) {

    /**
     * Validates the command parameters
     *
     * @throws IllegalArgumentException when the id or the polygon id is invalid
     */
    public LinkPlotPolygonCommand {
        if (fieldPlotId == null || fieldPlotId <= 0) {
            throw new IllegalArgumentException("field.plot.error.id.invalid");
        }
        if (polygonId == null || polygonId.isBlank()) {
            throw new IllegalArgumentException("field.plot.error.polygonId.notBlank");
        }
    }
}

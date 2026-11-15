package com.dymbia.sumaqagro.platform.farming.domain.model.commands;

import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.SoilBaseline;

/**
 * Command to record the initial soil analysis of a field plot.
 *
 * @param fieldPlotId  the field plot id
 * @param soilBaseline the soil analysis
 * @since 1.0
 */
public record RecordSoilBaselineCommand(Long fieldPlotId, SoilBaseline soilBaseline) {
    /**
     * Validates the command parameters.
     *
     * @throws IllegalArgumentException when the id or the soil analysis is invalid
     */
    public RecordSoilBaselineCommand {
        if (fieldPlotId == null || fieldPlotId <= 0) {
            throw new IllegalArgumentException("field.plot.error.id.invalid");
        }
        if (soilBaseline == null) {
            throw new IllegalArgumentException("field.plot.error.soil.required");
        }
    }
}

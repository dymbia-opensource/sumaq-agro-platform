package com.dymbia.sumaqagro.platform.farming.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.farming.domain.model.commands.RecordSoilBaselineCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.SoilBaseline;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.SoilTexture;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources.SoilBaselineResource;

import java.util.Locale;
import java.util.Objects;

/**
 * Converts the soil analysis request body into its command.
 */
public final class RecordSoilBaselineCommandFromResourceAssembler {

    private RecordSoilBaselineCommandFromResourceAssembler() {
    }

    /**
     * Builds the record soil baseline command.
     *
     * @param fieldPlotId the field plot id from the path
     * @param resource    the request body
     * @return the command
     */
    public static RecordSoilBaselineCommand toCommandFromResource(Long fieldPlotId, SoilBaselineResource resource) {
        Objects.requireNonNull(resource, "Soil baseline request body is required.");
        var soilBaseline = new SoilBaseline(resource.ph(), toSoilTexture(resource.texture()),
                resource.organicMatterPercentage(), resource.recordedAt());
        return new RecordSoilBaselineCommand(fieldPlotId, soilBaseline);
    }

    private static SoilTexture toSoilTexture(String texture) {
        if (texture == null || texture.isBlank()) {
            throw new IllegalArgumentException("field.plot.error.soil.texture.required");
        }
        try {
            return SoilTexture.valueOf(texture.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("field.plot.error.soil.texture.invalid");
        }
    }
}

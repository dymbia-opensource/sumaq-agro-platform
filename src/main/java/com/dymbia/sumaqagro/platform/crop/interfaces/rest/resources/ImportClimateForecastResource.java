package com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Request body to import the weather forecast of a plot from AgroMonitoring.
 *
 * @param plotId the plot; it must be yours and have a monitoring polygon
 * @since 1.1
 */
@Schema(name = "ImportClimateForecastRequest", description = "Plot whose weather forecast is imported from AgroMonitoring")
public record ImportClimateForecastResource(
        @Schema(description = "Field plot with a monitoring polygon", example = "10")
        @NotNull @Positive Long plotId) {
}

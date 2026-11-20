package com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * Request resource used to register a field plot. Field names match the web app.
 *
 * @param ownerUserId         the producer that owns the plot, it must be the authenticated user
 * @param name                the plot name
 * @param region              the region of the plot
 * @param areaHectares        the declared area in hectares
 * @param cooperativeId       the cooperative, only for a plot registered by a cooperative director
 * @param cooperativeMemberId the cooperative member, sent together with the cooperative
 */
@Schema(name = "RegisterFieldPlotRequest", description = "Request payload to register a field plot")
public record RegisterFieldPlotResource(
        @Schema(description = "Producer that owns the plot, it must be the authenticated user", example = "1")
        Long ownerUserId,
        @Schema(description = "Plot name, unique for the producer", example = "Parcela Apurímac", maxLength = 100)
        String name,
        @Schema(description = "Region of the plot", example = "Apurímac", maxLength = 150)
        String region,
        @Schema(description = "Declared area in hectares, zero or positive", example = "2.50")
        BigDecimal areaHectares,
        @Schema(description = "Cooperative, only for a plot registered by a cooperative director", example = "1",
                nullable = true)
        Long cooperativeId,
        @Schema(description = "Cooperative member, sent together with the cooperative", example = "1", nullable = true)
        Long cooperativeMemberId) {
}

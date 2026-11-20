package com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

/**
 * Response body of a field plot.
 *
 * @param id                      the plot id
 * @param ownerUserId             the producer that owns the plot
 * @param name                    the plot name
 * @param region                  the region of the plot
 * @param status                  the status ({@code WITHOUT_POLYGON} or {@code ACTIVE_MONITORING})
 * @param areaHectares            the area in hectares
 * @param agroMonitoringPolygonId the polygon id in AgroMonitoring, or null when it is not linked
 * @param boundary                the vertices of the polygon, empty when it is not delineated
 * @param soilBaseline            the soil analysis, or null when it is not recorded
 * @param cooperativeId           the cooperative of the plot, or null for an independent producer
 * @param cooperativeMemberId     the cooperative member of the plot, or null for an independent producer
 */
@Schema(name = "FieldPlotResponse", description = "Field plot of a producer")
public record FieldPlotResource(
        @Schema(description = "Field plot identifier", example = "1")
        Long id,
        @Schema(description = "Producer that owns the plot", example = "1")
        Long ownerUserId,
        @Schema(description = "Plot name", example = "Parcela Apurímac")
        String name,
        @Schema(description = "Region of the plot", example = "Apurímac")
        String region,
        @Schema(description = "Monitoring status", example = "ACTIVE_MONITORING",
                allowableValues = {"WITHOUT_POLYGON", "ACTIVE_MONITORING"})
        String status,
        @Schema(description = "Area in hectares", example = "2.50")
        BigDecimal areaHectares,
        @Schema(description = "Polygon id in AgroMonitoring, null when it is not linked",
                example = "5aaa8052cbbbb5000b73ff66")
        String agroMonitoringPolygonId,
        @Schema(description = "Vertices of the polygon, empty when it is not delineated")
        List<GeoCoordinateResource> boundary,
        @Schema(description = "Soil analysis, null when it is not recorded")
        SoilBaselineResource soilBaseline,
        @Schema(description = "Cooperative of the plot, null for an independent producer", example = "1")
        Long cooperativeId,
        @Schema(description = "Cooperative member of the plot, null for an independent producer", example = "1")
        Long cooperativeMemberId) {
}

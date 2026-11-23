package com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * A request body to delineate the boundary of a field plot
 *
 * @param coordinates the coordinates of the plot polygon
 */
@Schema(name = "DelineatePlotBoundaryRequest", description = "GPS polygon of a field plot")
public record DelineatePlotBoundaryResource(
        @ArraySchema(arraySchema = @Schema(description = "Vertices of the polygon in order, at least 3 different points"),
                minItems = 3)
        List<GeoCoordinateResource> coordinates) {
}

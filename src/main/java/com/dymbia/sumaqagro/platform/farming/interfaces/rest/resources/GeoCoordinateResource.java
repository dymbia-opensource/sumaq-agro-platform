package com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * A point of the plot polygon
 *
 * @param latitude  the latitude
 * @param longitude the longitude
 */
@Schema(name = "GeoCoordinate", description = "GPS point of the plot polygon")
public record GeoCoordinateResource(
        @Schema(description = "Latitude, from -90 to 90", example = "-13.6339")
        Double latitude,
        @Schema(description = "Longitude, from -180 to 180", example = "-72.8814")
        Double longitude) {
}

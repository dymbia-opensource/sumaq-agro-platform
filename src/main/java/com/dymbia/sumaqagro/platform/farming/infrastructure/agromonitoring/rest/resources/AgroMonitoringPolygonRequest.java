package com.dymbia.sumaqagro.platform.farming.infrastructure.agromonitoring.rest.resources;

import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.PlotBoundary;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * Body of the request that creates a polygon in AgroMonitoring.
 *
 * @param name    the polygon name
 * @param geoJson the polygon in GeoJSON format
 * @since 1.0
 */
public record AgroMonitoringPolygonRequest(String name, @JsonProperty("geo_json") GeoJsonFeature geoJson) {

    public record GeoJsonFeature(String type, Map<String, Object> properties, GeoJsonPolygon geometry) {
    }

    public record GeoJsonPolygon(String type, List<List<List<Double>>> coordinates) {
    }

    /**
     * Builds the request. GeoJSON expects a closed, counterclockwise ring with [longitude, latitude] points.
     *
     * @param name     the polygon name
     * @param boundary the polygon of the plot
     * @return the request body
     */
    public static AgroMonitoringPolygonRequest fromBoundary(String name, PlotBoundary boundary) {
        List<List<Double>> ring =
                boundary.counterClockwise().closedRing().stream().map(vertex -> List.of(vertex.longitude(),
                        vertex.latitude())).toList();
        var polygon = new GeoJsonPolygon("Polygon", List.of(ring));

        return new AgroMonitoringPolygonRequest(name, new GeoJsonFeature("Feature", Map.of(), polygon));
    }
}

package com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources;

/**
 * A point of the plot polygon
 *
 * @param latitude  the latitude
 * @param longitude the longitude
 */
public record GeoCoordinateResource(Double latitude, Double longitude) {
}

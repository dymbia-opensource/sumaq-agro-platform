package com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects;

/**
 * Represents a geographic coordinate with latitude and longitude.
 *
 * @param latitude  the latitude, from -90 to 90
 * @param longitude the longitude, from -180 to 180
 */
public record GeoCoordinate(double latitude, double longitude) {

    /**
     * Validate the coordinate
     *
     * @throws IllegalArgumentException if the coordinate is invalid
     */
    public GeoCoordinate {
        if (Double.isNaN(latitude) || latitude < -90 || latitude > 90 || Double.isNaN(longitude) || longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("field.plot.error.geocoordinate.invalid");
        }
    }
}

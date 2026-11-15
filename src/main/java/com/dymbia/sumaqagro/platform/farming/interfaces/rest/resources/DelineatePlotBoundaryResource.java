package com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources;

import java.util.List;

/**
 * A request body to delineate the boundary of a field plot
 *
 * @param coordinates the coordinates of the plot polygon
 */
public record DelineatePlotBoundaryResource(List<GeoCoordinateResource> coordinates) {
}

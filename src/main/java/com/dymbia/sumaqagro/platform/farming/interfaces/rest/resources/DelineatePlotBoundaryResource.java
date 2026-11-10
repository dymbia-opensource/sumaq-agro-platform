package com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources;

import java.util.List;

/**
 * A request body to delineate the boundary of a field plot
 *
 * @param vertices the vertices of the plot
 */
public record DelineatePlotBoundaryResource(List<GeoCoordinateResource> vertices) {
}

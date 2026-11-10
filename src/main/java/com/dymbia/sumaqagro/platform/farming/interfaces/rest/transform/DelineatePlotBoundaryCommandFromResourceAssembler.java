package com.dymbia.sumaqagro.platform.farming.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.farming.domain.model.commands.DelineatePlotBoundaryCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.GeoCoordinate;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.PlotBoundary;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources.DelineatePlotBoundaryResource;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources.GeoCoordinateResource;

import java.util.Objects;

/**
 * Coverts the delineate plot boundary request body in the command
 */
public final class DelineatePlotBoundaryCommandFromResourceAssembler {
    private DelineatePlotBoundaryCommandFromResourceAssembler() {
    }

    /**
     * Builds to delineate plot boundary command
     *
     * @param fieldPlotId the field plot id from the path
     * @param resource    the request body
     * @return the command
     */
    public static DelineatePlotBoundaryCommand toCommandFromResource(Long fieldPlotId,
                                                                     DelineatePlotBoundaryResource resource) {
        Objects.requireNonNull(resource, "Delineate plot boundary request required");
        if (resource.vertices() == null) {
            throw new IllegalArgumentException("field.plot.error.boundary.required");
        }
        var vertices =
                resource.vertices().stream().map(DelineatePlotBoundaryCommandFromResourceAssembler::toGeoCoordinate).toList();
        return new DelineatePlotBoundaryCommand(fieldPlotId, new PlotBoundary(vertices));
    }

    private static GeoCoordinate toGeoCoordinate(GeoCoordinateResource vertex) {
        if (vertex == null || vertex.latitude() == null || vertex.longitude() == null) {
            throw new IllegalArgumentException("field.plot.error.geocoordinate.invalid");
        }
        return new GeoCoordinate(vertex.latitude(), vertex.longitude());
    }
}

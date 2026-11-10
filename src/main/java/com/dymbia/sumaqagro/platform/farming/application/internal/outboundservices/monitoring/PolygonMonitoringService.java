package com.dymbia.sumaqagro.platform.farming.application.internal.outboundservices.monitoring;

import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.PlotBoundary;

import java.util.Optional;

/**
 * Outbound service that registers the plot polygons in the satellite monitoring provider
 *
 * @since 1.0
 */
public interface PolygonMonitoringService {

    /**
     * Registers the polygon in the provider
     *
     * @param plotName the plot name
     * @param boundary the polygon of the plot
     * @return the polygon id in the provider, or empty if it could not be registered
     */
    Optional<String> registerPolygon(String plotName, PlotBoundary boundary);

    /**
     * Removes a polygon from the provider.
     * A failure is logged and does not stop the caller.
     *
     * @param polygonId the polygon id in the provider
     */
    void removePolygon(String polygonId);
}

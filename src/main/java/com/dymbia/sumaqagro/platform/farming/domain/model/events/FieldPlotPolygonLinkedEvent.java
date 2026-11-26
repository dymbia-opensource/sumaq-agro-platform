package com.dymbia.sumaqagro.platform.farming.domain.model.events;

/**
 * Event recorded when a field plot is linked to its monitoring polygon.
 *
 * @param fieldPlotId the ID of the linked field plot
 * @param polygonId   the ID of the polygon in AgroMonitoring
 * @since 1.1.0
 */
public record FieldPlotPolygonLinkedEvent(Long fieldPlotId, String polygonId) {
}

package com.dymbia.sumaqagro.platform.farming.domain.model.events;

import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.PlotBoundary;

/**
 * Event triggered when the boundary of a field plot is delineated.
 *
 * @param fieldPlotId       the plot that was delineated
 * @param plotName          the name of the plot
 * @param boundary          the new polygon
 * @param previousPolygonId the ID of the previous polygon
 * @since 1.0
 */
public record PlotBoundaryDelineatedEvent(Long fieldPlotId, String plotName, PlotBoundary boundary,
                                          String previousPolygonId) {
}

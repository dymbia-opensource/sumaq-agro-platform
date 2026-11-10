package com.dymbia.sumaqagro.platform.farming.domain.model.commands;

import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.PlotBoundary;

/***
 * Command to draw or redraw the polygon of a field plot
 *
 * @param fieldPlotId   the field plot id
 * @param boundary      the polygon of the plot
 * @since 1.0
 */
public record DelineatePlotBoundaryCommand(Long fieldPlotId, PlotBoundary boundary) {

    /**
     * Validate the command parameters
     *
     * @throws IllegalArgumentException when the id or the polygon is invalid
     */
    public DelineatePlotBoundaryCommand {
        if (fieldPlotId == null||fieldPlotId <= 0) {
            throw new IllegalArgumentException("field.plot.error.id.invalid");
        }
        if (boundary == null) {
            throw new IllegalArgumentException("field.plot.error.boundar.required");
        }
    }
}

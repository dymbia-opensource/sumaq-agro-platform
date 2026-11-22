package com.dymbia.sumaqagro.platform.crop.domain.model.commands;

/**
 * Command to import the weather forecast of a field plot from the monitoring provider.
 *
 * @param plotId            the plot whose polygon is forecast
 * @param requestedByUserId the producer that asks for it; it must own the plot
 * @since 1.1
 */
public record ImportClimateForecastCommand(Long plotId, Long requestedByUserId) {

    public ImportClimateForecastCommand {
        if (plotId == null || plotId <= 0) {
            throw new IllegalArgumentException("Plot id must be greater than zero");
        }
        if (requestedByUserId == null || requestedByUserId <= 0) {
            throw new IllegalArgumentException("Requesting user id must be greater than zero");
        }
    }
}

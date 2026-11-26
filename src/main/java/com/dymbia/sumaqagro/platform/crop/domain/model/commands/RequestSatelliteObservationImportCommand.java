package com.dymbia.sumaqagro.platform.crop.domain.model.commands;

/**
 * Requests the import of the latest available satellite observation for a field plot.
 *
 * @param plotId            the ID of the field plot to monitor
 * @param requestedByUserId the plot owner on whose behalf the import is requested
 * @since 1.1.0
 */
public record RequestSatelliteObservationImportCommand(Long plotId, Long requestedByUserId) {

    public RequestSatelliteObservationImportCommand {
        if (plotId == null || plotId <= 0) {
            throw new IllegalArgumentException("field.plot.error.id.invalid");
        }
        if (requestedByUserId == null || requestedByUserId <= 0) {
            throw new IllegalArgumentException("field.plot.error.ownerUserId.invalid");
        }
    }
}

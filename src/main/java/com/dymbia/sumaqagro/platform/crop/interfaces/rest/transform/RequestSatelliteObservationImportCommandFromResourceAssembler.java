package com.dymbia.sumaqagro.platform.crop.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.crop.domain.model.commands.RequestSatelliteObservationImportCommand;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources.RequestSatelliteObservationImportResource;

/** Combines the import request with the authenticated user's identity. */
public final class RequestSatelliteObservationImportCommandFromResourceAssembler {

    /**
     * Creates the command for searching the satellite archive.
     *
     * @param resource the plot to import
     * @param authenticatedUserId the authenticated requesting user
     * @return the import command
     */
    public static RequestSatelliteObservationImportCommand toCommandFromResource(
            RequestSatelliteObservationImportResource resource, Long authenticatedUserId) {
        return new RequestSatelliteObservationImportCommand(resource.plotId(), authenticatedUserId);
    }
}

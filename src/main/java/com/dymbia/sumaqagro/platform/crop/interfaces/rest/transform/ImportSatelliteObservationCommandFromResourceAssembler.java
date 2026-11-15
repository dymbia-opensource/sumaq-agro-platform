package com.dymbia.sumaqagro.platform.crop.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.crop.domain.model.commands.ImportSatelliteObservationCommand;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources.ImportSatelliteObservationResource;

/**
 * Combines the import request body with the authenticated user's ID.
 */
public final class ImportSatelliteObservationCommandFromResourceAssembler {

    /**
     * Converts an {@link ImportSatelliteObservationResource} and the authenticated user's ID into an {@link ImportSatelliteObservationCommand}.
     *
     * @param resource            The resource containing the import request details.
     * @param authenticatedUserId The ID of the authenticated user making the request.
     * @return The resulting command.
     */
    public static ImportSatelliteObservationCommand toCommandFromResource(ImportSatelliteObservationResource resource, Long authenticatedUserId) {
        return new ImportSatelliteObservationCommand(
                resource.plotId(),
                authenticatedUserId,
                resource.start(),
                resource.end());
    }
}

package com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Requests the latest available satellite observation without specifying dates.
 *
 * @param plotId the ID of the field plot owned by the authenticated user
 * @since 1.1.0
 */
@Schema(name = "RequestSatelliteObservationImportRequest")
public record RequestSatelliteObservationImportResource(
        @NotNull @Positive @Schema(example = "13") Long plotId
) {
}

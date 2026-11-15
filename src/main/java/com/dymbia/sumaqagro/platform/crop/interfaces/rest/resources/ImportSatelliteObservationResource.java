package com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.Instant;

public record ImportSatelliteObservationResource(
        @NotNull @Positive Long plotId,
        @NotNull Instant start,
        @NotNull Instant end
) {
}

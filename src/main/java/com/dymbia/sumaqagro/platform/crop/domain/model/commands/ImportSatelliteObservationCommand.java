package com.dymbia.sumaqagro.platform.crop.domain.model.commands;

import java.time.Instant;

/**
 * Requests a satellite observation for a plot on behalf of the authenticated user.
 */
public record ImportSatelliteObservationCommand(Long plotId, Long requestedByUserId, Instant start, Instant end) {
}

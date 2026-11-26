package com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects;

import java.time.Instant;
import java.util.Set;

/** Immutable capture metadata, assembled on demand and not mapped to a database entity. */
public record SatelliteImageryMetadata(Long observationId, Long plotId, Instant capturedAt,
                                       String source, Set<SatelliteIndex> availableIndices) {
    public SatelliteImageryMetadata {
        if (observationId == null || observationId <= 0 || plotId == null || plotId <= 0) {
            throw new IllegalArgumentException("Positive observation and plot IDs are required");
        }
        if (capturedAt == null || source == null || source.isBlank()) {
            throw new IllegalArgumentException("Capture date and satellite source are required");
        }
        if (availableIndices == null || availableIndices.stream().anyMatch(java.util.Objects::isNull)) {
            throw new IllegalArgumentException("Available indices must not contain null values");
        }
        availableIndices = Set.copyOf(availableIndices);
    }
}

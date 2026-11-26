package com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources;

import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.SatelliteIndex;
import java.time.Instant;
import java.util.Set;

/** Exact capture and supported indices; no remote URLs or credentials. */
public record SatelliteImageryResource(Long observationId, Long plotId, Instant capturedAt,
                                       String source, Set<SatelliteIndex> availableIndices) {

}

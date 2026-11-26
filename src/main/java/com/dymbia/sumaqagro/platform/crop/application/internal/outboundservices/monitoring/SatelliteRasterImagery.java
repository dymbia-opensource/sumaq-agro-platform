package com.dymbia.sumaqagro.platform.crop.application.internal.outboundservices.monitoring;

import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.SatelliteIndex;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;

/** Reads georeferenced raster tiles for an exact date and satellite, without importing measurements. */
public interface SatelliteRasterImagery {
    Set<SatelliteIndex> findAvailableIndices(String polygonId, Instant capturedAt, String source);
    Optional<byte[]> readTile(String polygonId, Instant capturedAt, String source,
                              SatelliteIndex index, int zoom, int x, int y);

    final class Unavailable extends RuntimeException {
        public Unavailable() { super("Satellite raster provider is unavailable"); }
    }
}

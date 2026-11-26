package com.dymbia.sumaqagro.platform.crop.domain.model.queries;

import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.SatelliteIndex;
import java.util.Objects;

/** An authenticated request for one Web Mercator tile of a recorded capture. */
public record GetSatelliteObservationTileQuery(GetSatelliteObservationImageryQuery capture,
                                               SatelliteIndex index, int zoom, int x, int y) {
    public GetSatelliteObservationTileQuery {
        Objects.requireNonNull(capture, "Capture is required");
        Objects.requireNonNull(index, "Index is required");
        if (zoom < 0 || zoom > 22 || x < 0 || y < 0 || x >= (1 << zoom) || y >= (1 << zoom)) {
            throw new IllegalArgumentException("Invalid tile coordinates");
        }
    }
}

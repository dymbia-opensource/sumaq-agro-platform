package com.dymbia.sumaqagro.platform.crop.application.internal.outboundservices.monitoring;

import java.time.Instant;
import java.util.Optional;

/**
 * Reads one available satellite observation from a monitoring provider.
 */
public interface SatelliteImagery {

    Optional<Measurement> findLatest(String polygonId, Instant start, Instant end);

    record Measurement(Instant capturedAt, String source, Double ndviMean, Double ndwiMean,
                       double cloudCoveragePercent) {
    }

    final class Unavailable extends RuntimeException {
        public Unavailable() {
            super("Could not read satellite imagery from the monitoring provider");
        }
    }
}

package com.dymbia.sumaqagro.platform.crop.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.SatelliteObservation;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources.SatelliteObservationResource;

/**
 * Converts a satellite observation aggregate into an API response resource.
 */
public class SatelliteObservationResourceFromEntityAssembler {

    /**
     * Converts a {@link SatelliteObservation} entity into a {@link SatelliteObservationResource}.
     *
     * @param observation The {@link SatelliteObservation} entity to convert.
     * @return The {@link SatelliteObservationResource} that results from the conversion.
     */
    public static SatelliteObservationResource toResourceFromEntity(SatelliteObservation observation) {
        var ndvi = observation.getNdviMean();
        var ndwi = observation.getNdwiMean();

        return new SatelliteObservationResource(
                observation.getId(),
                observation.getPlotId(),
                observation.getCapturedAt(),
                observation.getSource(),
                ndvi != null ? ndvi.value() : null,
                ndwi != null ? ndwi.value() : null,
                observation.getCloudCoveragePercent().value(),
                observation.getSurfaceTempKelvin()
        );
    }
}
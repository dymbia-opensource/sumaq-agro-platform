package com.dymbia.sumaqagro.platform.crop.domain.repositories;

import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.SatelliteObservation;

import java.util.List;

/**
 * Domain repository contract for satellite observations.
 */
public interface SatelliteObservationRepository {

    SatelliteObservation save(SatelliteObservation observation);

    /**
     * Saves an imported satellite observation.
     *
     * @param observation the satellite observation to save
     * @return the saved satellite observation
     */
    SatelliteObservation saveImported(SatelliteObservation observation);

    List<SatelliteObservation> findAllByPlotId(Long plotId);
}
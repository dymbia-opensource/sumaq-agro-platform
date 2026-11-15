package com.dymbia.sumaqagro.platform.crop.application.internal.queryservices;

import com.dymbia.sumaqagro.platform.crop.application.queryservices.SatelliteObservationQueryService;
import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.SatelliteObservation;
import com.dymbia.sumaqagro.platform.crop.domain.model.queries.GetSatelliteObservationsByPlotIdQuery;
import com.dymbia.sumaqagro.platform.crop.domain.repositories.SatelliteObservationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Retrieves satellite observations associated with a plot.
 */
@Service
public class SatelliteObservationQueryServiceImpl implements SatelliteObservationQueryService {

    private final SatelliteObservationRepository observationRepository;

    public SatelliteObservationQueryServiceImpl(SatelliteObservationRepository observationRepository) {
        this.observationRepository = observationRepository;
    }

    @Override
    public List<SatelliteObservation> handle(GetSatelliteObservationsByPlotIdQuery query) {
        return observationRepository.findAllByPlotId(query.plotId());
    }
}

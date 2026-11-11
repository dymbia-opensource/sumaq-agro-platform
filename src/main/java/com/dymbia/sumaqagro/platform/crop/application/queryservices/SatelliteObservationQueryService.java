package com.dymbia.sumaqagro.platform.crop.application.queryservices;

import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.SatelliteObservation;
import com.dymbia.sumaqagro.platform.crop.domain.model.queries.GetSatelliteObservationsByPlotIdQuery;

import java.util.List;

/**
 * Application service contract for satellite observation queries.
 */
public interface SatelliteObservationQueryService {

    List<SatelliteObservation> handle(GetSatelliteObservationsByPlotIdQuery query);
}

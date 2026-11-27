package com.dymbia.sumaqagro.platform.crop.application.queryservices;

import com.dymbia.sumaqagro.platform.crop.domain.model.queries.GetSatelliteObservationImageryQuery;
import com.dymbia.sumaqagro.platform.crop.domain.model.queries.GetSatelliteObservationTileQuery;
import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.SatelliteImageryMetadata;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;

public interface SatelliteImageryQueryService {
    Result<SatelliteImageryMetadata, ApplicationError> handle(GetSatelliteObservationImageryQuery query);
    Result<byte[], ApplicationError> handle(GetSatelliteObservationTileQuery query);
}

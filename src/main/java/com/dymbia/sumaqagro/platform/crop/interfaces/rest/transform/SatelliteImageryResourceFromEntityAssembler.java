package com.dymbia.sumaqagro.platform.crop.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.SatelliteImageryMetadata;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources.SatelliteImageryResource;

public final class SatelliteImageryResourceFromEntityAssembler {
    public static SatelliteImageryResource toResourceFromEntity(SatelliteImageryMetadata item) {
        return new SatelliteImageryResource(item.observationId(), item.plotId(), item.capturedAt(), item.source(), item.availableIndices());
    }
}

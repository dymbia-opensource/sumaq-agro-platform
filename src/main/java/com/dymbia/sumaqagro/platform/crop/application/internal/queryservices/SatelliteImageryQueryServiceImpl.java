package com.dymbia.sumaqagro.platform.crop.application.internal.queryservices;

import com.dymbia.sumaqagro.platform.crop.application.queryservices.SatelliteImageryQueryService;
import com.dymbia.sumaqagro.platform.crop.application.internal.outboundservices.acl.ExternalFarmingService;
import com.dymbia.sumaqagro.platform.crop.application.internal.outboundservices.acl.ExternalProfilesService;
import com.dymbia.sumaqagro.platform.crop.application.internal.outboundservices.monitoring.SatelliteRasterImagery;
import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.SatelliteObservation;
import com.dymbia.sumaqagro.platform.crop.domain.model.queries.GetSatelliteObservationImageryQuery;
import com.dymbia.sumaqagro.platform.crop.domain.model.queries.GetSatelliteObservationTileQuery;
import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.SatelliteImageryMetadata;
import com.dymbia.sumaqagro.platform.crop.domain.repositories.SatelliteObservationRepository;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import java.util.Optional;

/** Authorizes each read, including cache hits, against current plot ownership and collaboration grants. */
@Service
public class SatelliteImageryQueryServiceImpl implements SatelliteImageryQueryService {
    private final SatelliteObservationRepository observations;
    private final ExternalFarmingService farming;
    private final ExternalProfilesService profiles;
    private final SatelliteRasterImagery imagery;

    public SatelliteImageryQueryServiceImpl(SatelliteObservationRepository observations, ExternalFarmingService farming,
                                            ExternalProfilesService profiles, SatelliteRasterImagery imagery) {
        this.observations = observations; this.farming = farming; this.profiles = profiles; this.imagery = imagery;
    }
    private record Capture(SatelliteObservation observation, String polygonId) { }
    private Optional<Capture> resolve(GetSatelliteObservationImageryQuery query) {
        var owner = farming.fetchOwnerUserIdByPlotId(query.plotId());
        if (owner.isEmpty() || (!owner.get().equals(query.requestedByUserId())
                && !profiles.canViewProducerPlot(query.requestedByUserId(), owner.get(), query.plotId()))) return Optional.empty();
        var polygon = farming.fetchMonitoringPolygonIdByPlotId(query.plotId());
        if (polygon.isEmpty()) return Optional.empty();
        return observations.findAllByPlotId(query.plotId()).stream()
                .filter(item -> query.observationId().equals(item.getId()) && query.plotId().equals(item.getPlotId()))
                .findFirst().map(item -> new Capture(item, polygon.get()));
    }
    @Override
    public Result<SatelliteImageryMetadata, ApplicationError> handle(GetSatelliteObservationImageryQuery query) {
        var capture = resolve(query);
        if (capture.isEmpty()) return Result.failure(ApplicationError.notFound("SatelliteImagery", query.observationId().toString()));
        var item = capture.get().observation();
        try {
            var indices = imagery.findAvailableIndices(capture.get().polygonId(), item.getCapturedAt(), item.getSource());
            return Result.success(new SatelliteImageryMetadata(item.getId(), item.getPlotId(), item.getCapturedAt(), item.getSource(), indices));
        } catch (SatelliteRasterImagery.Unavailable exception) {
            return Result.failure(ApplicationError.unexpected("SatelliteImagery", "Satellite image provider is unavailable"));
        }
    }
    @Override
    public Result<byte[], ApplicationError> handle(GetSatelliteObservationTileQuery query) {
        var capture = resolve(query.capture());
        if (capture.isEmpty()) return Result.failure(ApplicationError.notFound("SatelliteImagery", query.capture().observationId().toString()));
        var item = capture.get().observation();
        try {
            return imagery.readTile(capture.get().polygonId(), item.getCapturedAt(), item.getSource(), query.index(), query.zoom(), query.x(), query.y())
                    .<Result<byte[], ApplicationError>>map(Result::success)
                    .orElseGet(() -> Result.failure(ApplicationError.notFound("SatelliteTile", item.getId().toString())));
        } catch (SatelliteRasterImagery.Unavailable exception) {
            return Result.failure(ApplicationError.unexpected("SatelliteImagery", "Satellite image provider is unavailable"));
        }
    }
}

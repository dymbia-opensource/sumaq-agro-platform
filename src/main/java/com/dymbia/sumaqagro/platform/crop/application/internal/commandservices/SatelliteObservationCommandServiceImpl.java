package com.dymbia.sumaqagro.platform.crop.application.internal.commandservices;

import com.dymbia.sumaqagro.platform.crop.application.commandservices.SatelliteObservationCommandService;
import com.dymbia.sumaqagro.platform.crop.application.internal.outboundservices.acl.ExternalFarmingService;
import com.dymbia.sumaqagro.platform.crop.application.internal.outboundservices.monitoring.SatelliteImagery;
import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.SatelliteObservation;
import com.dymbia.sumaqagro.platform.crop.domain.model.commands.ImportSatelliteObservationCommand;
import com.dymbia.sumaqagro.platform.crop.domain.model.commands.RecordSatelliteObservationCommand;
import com.dymbia.sumaqagro.platform.crop.domain.model.commands.RequestSatelliteObservationImportCommand;
import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.NdviIndex;
import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.NdwiIndex;
import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.Percentage;
import com.dymbia.sumaqagro.platform.crop.domain.repositories.SatelliteObservationRepository;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;
import java.time.Duration;
import java.util.Optional;

/**
 * Handles recording and importing satellite observations.
 */
@Service
public class SatelliteObservationCommandServiceImpl implements SatelliteObservationCommandService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SatelliteObservationCommandServiceImpl.class);

    private final SatelliteObservationRepository observationRepository;

    private final ExternalFarmingService externalFarmingService;
    private final SatelliteImagery imagery;
    private Instant archiveStart = Instant.parse("2018-01-01T00:00:00Z");

    /** Configures the earliest date to search when importing automatically. */
    @Value("${crop.satellite.archive-start:2018-01-01T00:00:00Z}")
    public void configureArchiveStart(String value) {
        archiveStart = Instant.parse(value);
    }

    public SatelliteObservationCommandServiceImpl(SatelliteObservationRepository observationRepository,
                                                  ExternalFarmingService externalFarmingService,
                                                  SatelliteImagery imagery) {
        this.observationRepository = observationRepository;
        this.externalFarmingService = externalFarmingService;
        this.imagery = imagery;
    }

    @Override
    public Result<Long, ApplicationError> handle(RecordSatelliteObservationCommand command) {
        try {
            var observation = new SatelliteObservation(command);
            var savedObservation = observationRepository.save(observation);

            return Result.success(savedObservation.getId());
        } catch (RuntimeException e) {
            LOGGER.error("Failed to record satellite observation", e);
            return Result.failure(ApplicationError.unexpected("record-satellite-observation",
                    "Could not record the " + "satellite observation"));
        }
    }

    @Override
    public Result<SatelliteObservation, ApplicationError> handle(ImportSatelliteObservationCommand command) {
        var plotId = command.plotId();
        var start = command.start();
        var end = command.end();

        if (start.getEpochSecond() >= end.getEpochSecond()) {
            return Result.failure(ApplicationError.validationError("SatelliteObservation", "Start must precede end " +
                    "at" + " whole-second precision"));
        }

        // Another producer's plot answers 404, so it does not reveal that the plot exists
        var ownedByRequester =
                externalFarmingService.fetchOwnerUserIdByPlotId(plotId).filter(ownerUserId -> ownerUserId.equals(command.requestedByUserId()));
        if (ownedByRequester.isEmpty()) {
            return Result.failure(ApplicationError.notFound("FieldPlot", plotId.toString()));
        }
        var polygonId = externalFarmingService.fetchMonitoringPolygonIdByPlotId(plotId);
        if (polygonId.isEmpty()) {
            return Result.failure(ApplicationError.conflict("FieldPlot",
                    "The plot must be linked to a monitoring " + "polygon"));
        }

        try {
            var measurement = imagery.findLatest(polygonId.get(), start, end);
            return saveMeasurement(plotId, polygonId.get(), measurement);
        } catch (SatelliteImagery.Unavailable | IllegalArgumentException e) {
            return unavailable();
        }
    }

    @Override
    public Result<SatelliteObservation, ApplicationError> handle(RequestSatelliteObservationImportCommand command) {
        var plotId = command.plotId();
        var owner = externalFarmingService.fetchOwnerUserIdByPlotId(plotId);
        if (owner.isEmpty() || !owner.get().equals(command.requestedByUserId())) {
            return Result.failure(ApplicationError.notFound("FieldPlot", plotId.toString()));
        }
        var polygonId = externalFarmingService.fetchMonitoringPolygonIdByPlotId(plotId);
        if (polygonId.isEmpty()) {
            return Result.failure(ApplicationError.conflict("FieldPlot", "The plot must be linked to a monitoring polygon"));
        }
        try {
            var end = Instant.now();
            SatelliteImagery.Measurement latestPartial = null;
            while (end.isAfter(archiveStart)) {
                var start = end.minus(Duration.ofDays(365));
                if (start.isBefore(archiveStart)) {
                    start = archiveStart;
                }
                var measurement = imagery.findLatest(polygonId.get(), start, end);
                if (measurement.isPresent()) {
                    var capture = measurement.get();
                    if (capture.ndviMean() != null && capture.ndwiMean() != null) {
                        return saveMeasurement(plotId, polygonId.get(), measurement);
                    }
                    if (latestPartial == null) {
                        latestPartial = capture;
                    }
                }
                end = start;
            }
            return saveMeasurement(plotId, polygonId.get(), Optional.ofNullable(latestPartial));
        } catch (SatelliteImagery.Unavailable | IllegalArgumentException e) {
            return unavailable();
        }
    }

    private Result<SatelliteObservation, ApplicationError> saveMeasurement(
            Long plotId, String polygonId, Optional<SatelliteImagery.Measurement> measurement) {
        if (measurement.isEmpty()) {
            return Result.failure(ApplicationError.notFound("SatelliteObservation", plotId.toString()));
        }
        if (externalFarmingService.fetchMonitoringPolygonIdByPlotId(plotId).filter(polygonId::equals).isEmpty()) {
            return Result.failure(ApplicationError.conflict("FieldPlot", "The monitoring polygon changed during import"));
        }
        SatelliteObservation observation;
        try {
            var capture = measurement.get();
            var recordCommand = new RecordSatelliteObservationCommand(plotId, capture.capturedAt(), capture.source(),
                    capture.ndviMean() == null ? null : new NdviIndex(capture.ndviMean()),
                    capture.ndwiMean() == null ? null : new NdwiIndex(capture.ndwiMean()),
                    new Percentage(capture.cloudCoveragePercent()), null);
            observation = new SatelliteObservation(recordCommand);
        } catch (IllegalArgumentException e) {
            return unavailable();
        }
        return Result.success(observationRepository.saveImported(observation));
    }

    private Result<SatelliteObservation, ApplicationError> unavailable() {
        return Result.failure(ApplicationError.unexpected("import-satellite-observation",
                "Could not read usable measurements from AgroMonitoring"));
    }
}

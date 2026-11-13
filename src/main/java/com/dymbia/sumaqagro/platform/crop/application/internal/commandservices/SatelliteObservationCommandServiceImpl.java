package com.dymbia.sumaqagro.platform.crop.application.internal.commandservices;

import com.dymbia.sumaqagro.platform.crop.application.commandservices.SatelliteObservationCommandService;
import com.dymbia.sumaqagro.platform.crop.application.internal.outboundservices.monitoring.SatelliteImagery;
import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.SatelliteObservation;
import com.dymbia.sumaqagro.platform.crop.domain.model.commands.ImportSatelliteObservationCommand;
import com.dymbia.sumaqagro.platform.crop.domain.model.commands.RecordSatelliteObservationCommand;
import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.NdviIndex;
import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.NdwiIndex;
import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.Percentage;
import com.dymbia.sumaqagro.platform.crop.domain.repositories.SatelliteObservationRepository;
import com.dymbia.sumaqagro.platform.farming.application.queryservices.FieldPlotQueryService;
import com.dymbia.sumaqagro.platform.farming.domain.model.queries.GetFieldPlotByIdQuery;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Handles recording and importing satellite observations.
 */
@Service
public class SatelliteObservationCommandServiceImpl implements SatelliteObservationCommandService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SatelliteObservationCommandServiceImpl.class);

    private final SatelliteObservationRepository observationRepository;

    private final FieldPlotQueryService fieldPlots;
    private final SatelliteImagery imagery;

    public SatelliteObservationCommandServiceImpl(SatelliteObservationRepository observationRepository,
                                                  FieldPlotQueryService fieldPlots, SatelliteImagery imagery) {
        this.observationRepository = observationRepository;
        this.fieldPlots = fieldPlots;
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
            return Result.failure(ApplicationError.unexpected(
                    "record-satellite-observation", "Could not record the satellite observation"
            ));
        }
    }

    @Override
    public Result<SatelliteObservation, ApplicationError> handle(ImportSatelliteObservationCommand command) {
        var plotId = command.plotId();
        var start = command.start();
        var end = command.end();

        if (start.getEpochSecond() >= end.getEpochSecond()) {
            return Result.failure(ApplicationError.validationError(
                    "SatelliteObservation", "Start must precede end at whole-second precision"));
        }

        var plot = fieldPlots.handle(new GetFieldPlotByIdQuery(plotId))
                .filter(fieldPlot -> command.requestedByUserId().equals(fieldPlot.getOwnerUserId()));
        if (plot.isEmpty()) {
            return Result.failure(ApplicationError.notFound("FieldPlot", plotId.toString()));
        }
        var polygonId = plot.get().getAgroMonitoringPolygonId();
        if (polygonId == null || polygonId.isBlank()) {
            return Result.failure(ApplicationError.conflict(
                    "FieldPlot", "The plot must be linked to a monitoring polygon"));
        }

        SatelliteObservation observation;
        try {
            var measurement = imagery.findLatest(polygonId, start, end);
            if (measurement.isEmpty()) {
                return Result.failure(ApplicationError.notFound("SatelliteObservation", plotId.toString()));
            }
            var capture = measurement.get();
            var recordCommand = new RecordSatelliteObservationCommand(
                    plotId, capture.capturedAt(), capture.source(),
                    capture.ndviMean() == null ? null : new NdviIndex(capture.ndviMean()),
                    capture.ndwiMean() == null ? null : new NdwiIndex(capture.ndwiMean()),
                    new Percentage(capture.cloudCoveragePercent()), null
            );
            observation = new SatelliteObservation(recordCommand);
        } catch (SatelliteImagery.Unavailable | IllegalArgumentException e) {
            return Result.failure(ApplicationError.unexpected(
                    "import-satellite-observation", "Could not read usable measurements from AgroMonitoring"));
        }
        return Result.success(observationRepository.saveImported(observation));
    }
}

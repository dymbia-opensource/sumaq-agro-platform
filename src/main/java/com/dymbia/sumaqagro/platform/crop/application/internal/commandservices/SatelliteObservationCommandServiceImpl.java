package com.dymbia.sumaqagro.platform.crop.application.internal.commandservices;

import com.dymbia.sumaqagro.platform.crop.application.commandservices.SatelliteObservationCommandService;
import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.SatelliteObservation;
import com.dymbia.sumaqagro.platform.crop.domain.model.commands.RecordSatelliteObservationCommand;
import com.dymbia.sumaqagro.platform.crop.domain.repositories.SatelliteObservationRepository;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Records a satellite observation and returns its persisted identifier.
 */
@Service
public class SatelliteObservationCommandServiceImpl implements SatelliteObservationCommandService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SatelliteObservationCommandServiceImpl.class);

    private final SatelliteObservationRepository observationRepository;

    public SatelliteObservationCommandServiceImpl(SatelliteObservationRepository observationRepository) {
        this.observationRepository = observationRepository;
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
}

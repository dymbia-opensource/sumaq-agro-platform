package com.dymbia.sumaqagro.platform.crop.application.commandservices;

import com.dymbia.sumaqagro.platform.crop.domain.model.commands.RecordSatelliteObservationCommand;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;

/**
 * Application service contract for recording satellite observations.
 */
public interface SatelliteObservationCommandService {

    Result<Long, ApplicationError> handle(RecordSatelliteObservationCommand command);
}

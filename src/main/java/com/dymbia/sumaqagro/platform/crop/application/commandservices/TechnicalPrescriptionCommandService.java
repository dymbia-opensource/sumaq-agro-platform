package com.dymbia.sumaqagro.platform.crop.application.commandservices;

import com.dymbia.sumaqagro.platform.crop.domain.model.commands.IssueTechnicalPrescriptionCommand;
import com.dymbia.sumaqagro.platform.crop.domain.model.commands.RegisterTechnicalPrescriptionApplicationCommand;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;

/**
 * Application service contract for technical prescription commands.
 */
public interface TechnicalPrescriptionCommandService {

    /**
     * Handles the issuance of a new technical prescription.
     *
     * @param command command containing technical prescription data
     * @return created technical prescription identifier or an application error
     */
    Result<Long, ApplicationError> handle(IssueTechnicalPrescriptionCommand command);

    /**
     * Handles the registration of a technical prescription application.
     *
     * @param command command containing technical prescription application data
     * @return updated technical prescription identifier or an application error
     */
    Result<Long, ApplicationError> handle(RegisterTechnicalPrescriptionApplicationCommand command);
}
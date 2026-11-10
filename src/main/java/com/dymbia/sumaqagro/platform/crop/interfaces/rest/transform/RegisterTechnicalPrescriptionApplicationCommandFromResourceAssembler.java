package com.dymbia.sumaqagro.platform.crop.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.crop.domain.model.commands.RegisterTechnicalPrescriptionApplicationCommand;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources.RegisterTechnicalPrescriptionApplicationResource;

/**
 * Converts the prescription identifier into an application confirmation command.
 */
public class RegisterTechnicalPrescriptionApplicationCommandFromResourceAssembler {

    /**
     * Converts a {@link RegisterTechnicalPrescriptionApplicationResource} into a {@link RegisterTechnicalPrescriptionApplicationCommand}.
     *
     * @param resource The resource containing the prescription application details.
     * @return The resulting command.
     */
    public static RegisterTechnicalPrescriptionApplicationCommand toCommandFromResource(RegisterTechnicalPrescriptionApplicationResource resource) {
        return new RegisterTechnicalPrescriptionApplicationCommand(
                resource.technicalPrescriptionId()
        );
    }
}

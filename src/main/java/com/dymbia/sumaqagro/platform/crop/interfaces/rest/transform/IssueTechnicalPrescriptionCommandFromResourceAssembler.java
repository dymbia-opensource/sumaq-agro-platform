package com.dymbia.sumaqagro.platform.crop.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.crop.domain.model.commands.IssueTechnicalPrescriptionCommand;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources.IssueTechnicalPrescriptionResource;

/**
 * Converts an API request into a prescription issuance command.
 */
public class IssueTechnicalPrescriptionCommandFromResourceAssembler {

    /**
     * Converts an {@link IssueTechnicalPrescriptionResource} into an {@link IssueTechnicalPrescriptionCommand}.
     *
     * @param resource The resource containing the prescription issuance details.
     * @return The resulting command.
     */
    public static IssueTechnicalPrescriptionCommand toCommandFromResource(IssueTechnicalPrescriptionResource resource) {
        return new IssueTechnicalPrescriptionCommand(
                resource.pestReportId(),
                resource.agronomistId(),
                resource.product(),
                resource.dosage(),
                resource.instructions(),
                resource.applicationDate()
        );
    }
}

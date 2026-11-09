package com.dymbia.sumaqagro.platform.crop.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.crop.domain.model.commands.CompleteFieldInspectionCommand;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources.CompleteFieldInspectionResource;

/**
 * Converts an inspection identifier and API request into a completion command.
 */
public class CompleteFieldInspectionCommandFromResourceAssembler {

    /**
     * Converts a {@link CompleteFieldInspectionResource} into a {@link CompleteFieldInspectionCommand}.
     *
     * @param fieldInspectionId The identifier of the field inspection to complete.
     * @param resource The resource containing the completion details.
     * @return The resulting command.
     */
    public static CompleteFieldInspectionCommand toCommandFromResource(Long fieldInspectionId, CompleteFieldInspectionResource resource) {
        return new CompleteFieldInspectionCommand(
                fieldInspectionId,
                resource.findings()
        );
    }
}

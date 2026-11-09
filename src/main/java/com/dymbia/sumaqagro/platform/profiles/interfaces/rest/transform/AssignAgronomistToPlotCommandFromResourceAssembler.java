package com.dymbia.sumaqagro.platform.profiles.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.AssignAgronomistToPlotCommand;
import com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources.AssignAgronomistToPlotResource;

import java.util.Objects;

/**
 * Converts an AssignAgronomistToPlotResource into an AssignAgronomistToPlotCommand.
 *
 * @since 1.0
 */
public final class AssignAgronomistToPlotCommandFromResourceAssembler {

    private AssignAgronomistToPlotCommandFromResourceAssembler() {
    }

    public static AssignAgronomistToPlotCommand toCommandFromResource(AssignAgronomistToPlotResource resource) {
        Objects.requireNonNull(resource, "Assign agronomist to plot request body is required.");
        return new AssignAgronomistToPlotCommand(
                resource.cooperativeId(),
                resource.agronomistUserId(),
                resource.plotId()
        );
    }
}

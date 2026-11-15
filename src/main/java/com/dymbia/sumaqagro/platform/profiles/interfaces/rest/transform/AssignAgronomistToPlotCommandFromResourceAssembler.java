package com.dymbia.sumaqagro.platform.profiles.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.AssignAgronomistToPlotCommand;
import com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources.AssignAgronomistToPlotResource;

public final class AssignAgronomistToPlotCommandFromResourceAssembler {

    private AssignAgronomistToPlotCommandFromResourceAssembler() {
    }

    public static AssignAgronomistToPlotCommand toCommandFromResource(AssignAgronomistToPlotResource resource) {
        return new AssignAgronomistToPlotCommand(
                resource.cooperativeId(),
                resource.agronomistUserId(),
                resource.plotId());
    }
}

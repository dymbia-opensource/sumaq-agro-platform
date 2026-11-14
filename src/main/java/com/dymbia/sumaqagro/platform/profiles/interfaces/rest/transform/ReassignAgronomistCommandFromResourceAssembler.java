package com.dymbia.sumaqagro.platform.profiles.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.ReassignAgronomistToPlotCommand;
import com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources.ReassignAgronomistResource;

public final class ReassignAgronomistCommandFromResourceAssembler {

    private ReassignAgronomistCommandFromResourceAssembler() {
    }

    public static ReassignAgronomistToPlotCommand toCommandFromResource(Long assignmentId,
                                                                        ReassignAgronomistResource resource) {
        return new ReassignAgronomistToPlotCommand(assignmentId, resource.agronomistUserId());
    }
}

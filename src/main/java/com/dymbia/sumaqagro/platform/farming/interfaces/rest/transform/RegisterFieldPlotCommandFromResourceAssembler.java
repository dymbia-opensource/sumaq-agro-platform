package com.dymbia.sumaqagro.platform.farming.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.farming.domain.model.commands.RegisterFieldPlotCommand;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources.RegisterFieldPlotResource;

import java.util.Objects;

public final class RegisterFieldPlotCommandFromResourceAssembler {
    private RegisterFieldPlotCommandFromResourceAssembler() {
    }
    public static RegisterFieldPlotCommand toCommandFromResource(RegisterFieldPlotResource resource) {
        Objects.requireNonNull(resource, "Register field plot request body is required.");
        return new RegisterFieldPlotCommand(
                resource.ownerUserId(),
                resource.name(),
                resource.region(),
                resource.areaHectares(),
                resource.cooperativeId(),
                resource.cooperativeMemberId());
    }
}

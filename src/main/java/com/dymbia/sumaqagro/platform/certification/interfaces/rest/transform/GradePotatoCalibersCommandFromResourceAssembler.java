package com.dymbia.sumaqagro.platform.certification.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.certification.domain.model.commands.GradePotatoCalibersCommand;
import com.dymbia.sumaqagro.platform.certification.interfaces.rest.resources.GradePotatoCalibersResource;

import java.util.Objects;

public final class GradePotatoCalibersCommandFromResourceAssembler {

    private GradePotatoCalibersCommandFromResourceAssembler() {
    }

    public static GradePotatoCalibersCommand toCommandFromResource(Long harvestBatchId, GradePotatoCalibersResource resource) {
        Objects.requireNonNull(resource, "Grade potato calibers request body is required.");
        return new GradePotatoCalibersCommand(
                harvestBatchId,
                resource.firstPercentage(),
                resource.secondPercentage(),
                resource.thirdPercentage(),
                resource.pestDamagePercentage());
    }
}

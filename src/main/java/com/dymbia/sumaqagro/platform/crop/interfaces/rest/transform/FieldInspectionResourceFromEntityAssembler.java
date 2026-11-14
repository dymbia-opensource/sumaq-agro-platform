package com.dymbia.sumaqagro.platform.crop.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.FieldInspection;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources.FieldInspectionResource;

/**
 * Converts a field inspection aggregate into an API response resource.
 */
public class FieldInspectionResourceFromEntityAssembler {

    /**
     * Converts a {@link FieldInspection} entity into a {@link FieldInspectionResource}.
     *
     * @param entity The field inspection entity to convert.
     * @return The resulting resource.
     */
    public static FieldInspectionResource toResourceFromEntity(FieldInspection entity) {
        return new FieldInspectionResource(
                entity.getId(),
                entity.getPestReportId(),
                entity.getPlotId(),
                entity.getAgronomistId(),
                entity.getScheduledAt(),
                entity.getStatus(),
                entity.getFindings(),
                entity.getCompletedAt()
        );
    }
}

package com.dymbia.sumaqagro.platform.crop.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.PestReport;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources.PestReportResource;

/**
 * Converts a PestReport aggregate into an API response resource.
 */
public class PestReportResourceFromEntityAssembler {

    /**
     * Converts a PestReport entity to a PestReportResource.
     *
     * @param entity The {@link PestReport} entity to convert.
     * @return The {@link PestReportResource} resource that results from the conversion.
     */
    public static PestReportResource toResourceFromEntity(PestReport entity) {
        return new PestReportResource(
                entity.getId(),
                entity.getPlotId(),
                entity.getReporterId(),
                entity.getDescription(),
                entity.getSeverity(),
                entity.getStatus()
        );
    }
}
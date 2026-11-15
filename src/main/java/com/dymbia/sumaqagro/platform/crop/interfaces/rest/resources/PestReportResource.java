package com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources;

import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.PestReportSeverity;
import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.PestReportStatus;

/**
 * API response containing pest report information.
 */
public record PestReportResource(
        Long id,
        Long plotId,
        Long reporterId,
        String description,
        PestReportSeverity severity,
        PestReportStatus status
) {
}
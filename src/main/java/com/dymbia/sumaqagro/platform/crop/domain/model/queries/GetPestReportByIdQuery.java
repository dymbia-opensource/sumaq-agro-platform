package com.dymbia.sumaqagro.platform.crop.domain.model.queries;

/**
 * Query for retrieving a pest report by its identifier.
 *
 * @param pestReportId pest report identifier
 */

public record GetPestReportByIdQuery(Long pestReportId) {

    public GetPestReportByIdQuery {
        if (pestReportId == null || pestReportId <= 0) {
            throw new IllegalArgumentException("Pest report ID must be greater than zero");
        }
    }
}
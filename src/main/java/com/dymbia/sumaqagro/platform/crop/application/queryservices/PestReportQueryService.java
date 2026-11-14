package com.dymbia.sumaqagro.platform.crop.application.queryservices;

import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.PestReport;
import com.dymbia.sumaqagro.platform.crop.domain.model.queries.GetPestReportByIdQuery;
import com.dymbia.sumaqagro.platform.crop.domain.model.queries.GetAllPestReportsByPlotIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Application service contract for pest report read queries.
 */
public interface PestReportQueryService {

    /**
     * Handles retrieval of a pest report by id.
     *
     * @param query pest-report-id query
     * @return matching pest report, if found
     * @see GetPestReportByIdQuery
     */
    Optional<PestReport> handle(GetPestReportByIdQuery query);

    /**
     * Handles retrieval of pest reports by plot id.
     *
     * @param query plot-id query
     * @return list of pest reports for the given plot
     * @see GetAllPestReportsByPlotIdQuery
     */
    List<PestReport> handle(GetAllPestReportsByPlotIdQuery query);
}
package com.dymbia.sumaqagro.platform.crop.application.queryservices;

import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.FieldInspection;
import com.dymbia.sumaqagro.platform.crop.domain.model.queries.GetFieldInspectionByIdQuery;
import com.dymbia.sumaqagro.platform.crop.domain.model.queries.GetAllFieldInspectionsByPlotIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Application service contract for field inspection read queries.
 */
public interface FieldInspectionQueryService {

    /**
     * Retrieves an inspection by its identifier.
     *
     * @param query inspection identifier
     * @return matching inspection, if found
     */
    Optional<FieldInspection> handle(GetFieldInspectionByIdQuery query);

    /**
     * Retrieves inspections associated with a plot.
     *
     * @param query plot identifier
     * @return inspections for the plot, or an empty list
     */
    List<FieldInspection> handle(GetAllFieldInspectionsByPlotIdQuery query);
}
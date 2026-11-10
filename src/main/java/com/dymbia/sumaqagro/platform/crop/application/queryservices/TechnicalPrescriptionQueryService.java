package com.dymbia.sumaqagro.platform.crop.application.queryservices;

import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.TechnicalPrescription;
import com.dymbia.sumaqagro.platform.crop.domain.model.queries.GetTechnicalPrescriptionByIdQuery;
import com.dymbia.sumaqagro.platform.crop.domain.model.queries.GetTechnicalPrescriptionsByPlotIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Application service contract for technical prescription queries.
 */
public interface TechnicalPrescriptionQueryService {

    /**
     * Handles retrieval of a technical prescription by id.
     *
     * @param query technical-prescription-id query
     * @return matching technical prescription, if found
     * @see GetTechnicalPrescriptionByIdQuery
     */
    Optional<TechnicalPrescription> handle(GetTechnicalPrescriptionByIdQuery query);

    /**
     * Handles retrieval of technical prescriptions by plot id.
     *
     * @param query plot-id query
     * @return list of technical prescriptions for the given plot
     * @see GetTechnicalPrescriptionsByPlotIdQuery
     */
    List<TechnicalPrescription> handle(GetTechnicalPrescriptionsByPlotIdQuery query);
}
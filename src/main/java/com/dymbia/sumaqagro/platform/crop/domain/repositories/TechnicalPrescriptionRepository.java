package com.dymbia.sumaqagro.platform.crop.domain.repositories;

import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.TechnicalPrescription;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository contract for technical prescriptions.
 */
public interface TechnicalPrescriptionRepository {

    TechnicalPrescription save(TechnicalPrescription prescription);

    Optional<TechnicalPrescription> findById(Long technicalPrescriptionId);

    List<TechnicalPrescription> findAllByPlotId(Long plotId);

    boolean existsByPestReportId(Long pestReportId);
}
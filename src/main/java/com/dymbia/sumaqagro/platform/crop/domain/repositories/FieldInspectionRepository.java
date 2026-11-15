package com.dymbia.sumaqagro.platform.crop.domain.repositories;

import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.FieldInspection;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository contract for field inspections.
 */
public interface FieldInspectionRepository {

    FieldInspection save(FieldInspection fieldInspection);

    Optional<FieldInspection> findById(Long fieldInspectionId);

    List<FieldInspection> findAllByPlotId(Long plotId);

    boolean existsByPestReportId(Long pestReportId);
}
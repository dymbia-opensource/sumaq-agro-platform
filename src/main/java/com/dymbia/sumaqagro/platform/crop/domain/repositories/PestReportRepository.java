package com.dymbia.sumaqagro.platform.crop.domain.repositories;

import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.PestReport;

import java.util.List;
import java.util.Optional;

/**
 * Pest report repository port.
 * This interface defines the contract for the pest report repository.
 */
public interface PestReportRepository {

    PestReport save(PestReport pestReport);

    Optional<PestReport> findById(Long pestReportId);

    List<PestReport> findAllByPlotId(Long plotId);

    void deleteById(Long pestReportId);
}
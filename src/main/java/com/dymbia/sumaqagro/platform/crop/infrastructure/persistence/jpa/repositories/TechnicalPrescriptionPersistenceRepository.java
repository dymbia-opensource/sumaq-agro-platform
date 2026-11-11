package com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.repositories;

import com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.entities.TechnicalPrescriptionPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for technical prescription entities.
 */
public interface TechnicalPrescriptionPersistenceRepository extends JpaRepository<TechnicalPrescriptionPersistenceEntity, Long> {

    /**
     * Finds all technical prescription persistence entities by plot identifier.
     *
     * @param plotId the plot identifier
     * @return a list of technical prescription persistence entities associated with the given plot identifier
     */
    List<TechnicalPrescriptionPersistenceEntity> findAllByPlotId(Long plotId);

    /**
     * Checks if a technical prescription exists for a given pest report identifier.
     *
     * @param pestReportId the pest report identifier
     * @return true if a technical prescription exists for the given pest report identifier, false otherwise
     */
    boolean existsByPestReportId(Long pestReportId);
}
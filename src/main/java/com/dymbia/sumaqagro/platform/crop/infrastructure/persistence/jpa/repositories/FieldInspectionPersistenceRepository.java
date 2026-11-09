package com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.repositories;

import com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.entities.FieldInspectionPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for field inspection persistence entities.
 */
public interface FieldInspectionPersistenceRepository extends JpaRepository<FieldInspectionPersistenceEntity, Long> {

    /**
     * Finds all field inspections associated with a specific plot ID.
     *
     * @param plotId The ID of the plot for which to retrieve field inspections.
     * @return A list of {@link FieldInspectionPersistenceEntity} instances associated with the given plot ID.
     */
    List<FieldInspectionPersistenceEntity> findAllByPlotId(Long plotId);

    /**
     * Checks if a field inspection exists for a specific pest report ID.
     *
     * @param pestReportId The ID of the pest report to check for associated field inspections.
     * @return {@code true} if a field inspection exists for the given pest report ID, {@code false} otherwise.
     */
    boolean existsByPestReportId(Long pestReportId);
}
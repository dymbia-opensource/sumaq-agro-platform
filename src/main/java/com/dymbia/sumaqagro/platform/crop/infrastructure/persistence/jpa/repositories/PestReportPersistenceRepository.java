package com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.repositories;

import com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.entities.PestReportPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for pest report persistence entities.
 */
public interface PestReportPersistenceRepository extends JpaRepository<PestReportPersistenceEntity, Long> {

    /**
     * Finds all pest report persistence entities by plot identifier.
     *
     * @param plotId the plot identifier
     * @return a list of pest report persistence entities associated with the given plot identifier
     */
    List<PestReportPersistenceEntity> findAllByPlotId(Long plotId);
}
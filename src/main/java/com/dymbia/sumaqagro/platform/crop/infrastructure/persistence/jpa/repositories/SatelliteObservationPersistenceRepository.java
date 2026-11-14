package com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.repositories;

import com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.entities.SatelliteObservationPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for satellite observations.
 */
public interface SatelliteObservationPersistenceRepository extends JpaRepository<SatelliteObservationPersistenceEntity, Long> {

    /**
     * Finds all satellite observations for a given plot ID, ordered by capture time and ID in descending order.
     *
     * @param plotId the ID of the plot
     * @return a list of satellite observation persistence entities
     */
    List<SatelliteObservationPersistenceEntity> findAllByPlotIdOrderByCapturedAtDescIdDesc(Long plotId);
}

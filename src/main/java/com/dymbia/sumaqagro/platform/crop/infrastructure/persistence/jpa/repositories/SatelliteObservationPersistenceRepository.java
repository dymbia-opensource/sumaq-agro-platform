package com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.repositories;

import com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.entities.SatelliteObservationPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

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

    /**
     * Finds a satellite observation by its import key.
     *
     * @param importKey the import key of the satellite observation
     * @return an optional containing the satellite observation persistence entity if found, or empty if not found
     */
    Optional<SatelliteObservationPersistenceEntity> findByImportKey(String importKey);

    /**
     * Finds the first satellite observation for a given plot ID, capture time, and source, ordered by ID in ascending order.
     *
     * @param plotId     the ID of the plot
     * @param capturedAt the capture time of the satellite observation
     * @param source     the source of the satellite observation
     * @return an optional containing the satellite observation persistence entity if found, or empty if not found
     */
    Optional<SatelliteObservationPersistenceEntity> findFirstByPlotIdAndCapturedAtAndSourceOrderByIdAsc(
            Long plotId, Instant capturedAt, String source
    );
}

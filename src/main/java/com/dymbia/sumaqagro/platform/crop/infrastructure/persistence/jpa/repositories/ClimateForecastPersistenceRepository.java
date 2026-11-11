package com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.repositories;

import com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.entities.ClimateForecastPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for climate forecasts.
 */
public interface ClimateForecastPersistenceRepository extends JpaRepository<ClimateForecastPersistenceEntity, Long> {

    List<ClimateForecastPersistenceEntity> findAllByRegionOrderByForecastDateDescIdDesc(String region);
}

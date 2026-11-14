package com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.repositories;

import com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.entities.AgroclimaticAlertPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for agroclimatic alerts.
 */
public interface AgroclimaticAlertPersistenceRepository extends JpaRepository<AgroclimaticAlertPersistenceEntity, Long> {

    List<AgroclimaticAlertPersistenceEntity> findAllByRegionOrderByIssuedAtDescIdDesc(String region);
}

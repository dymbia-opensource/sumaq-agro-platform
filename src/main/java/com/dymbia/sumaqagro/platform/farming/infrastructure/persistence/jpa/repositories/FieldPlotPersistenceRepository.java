package com.dymbia.sumaqagro.platform.farming.infrastructure.persistence.jpa.repositories;

import com.dymbia.sumaqagro.platform.farming.infrastructure.persistence.jpa.entities.FieldPlotPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FieldPlotPersistenceRepository extends JpaRepository<FieldPlotPersistenceEntity, Long> {

    List<FieldPlotPersistenceEntity> findAllByOwnerUserId(Long ownerUserId);

    long countByCooperativeId(Long cooperativeId);

    long countByOwnerUserIdAndCooperativeIdIsNull(Long ownerUserId);

    boolean existsByOwnerUserIdAndName(Long ownerUserId, String name);
}

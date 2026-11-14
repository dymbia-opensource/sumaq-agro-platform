package com.dymbia.sumaqagro.platform.subscriptions.infrastructure.persistence.jpa.repositories;

import com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects.PlanCode;
import com.dymbia.sumaqagro.platform.subscriptions.infrastructure.persistence.jpa.entities.PlanPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/** Spring Data repository for subscription plan records. */
public interface PlanPersistenceRepository extends JpaRepository<PlanPersistenceEntity, Long> {

    /** Checks for an existing plan by its unique commercial code. */
    boolean existsByCode(PlanCode code);

    /**
     * Finds a plan by its unique commercial code.
     */
    Optional<PlanPersistenceEntity> findByCode(PlanCode code);
}

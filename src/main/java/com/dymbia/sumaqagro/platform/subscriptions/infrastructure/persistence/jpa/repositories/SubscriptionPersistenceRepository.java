package com.dymbia.sumaqagro.platform.subscriptions.infrastructure.persistence.jpa.repositories;

import com.dymbia.sumaqagro.platform.subscriptions.infrastructure.persistence.jpa.entities.SubscriptionPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data repository for subscription records.
 */
public interface SubscriptionPersistenceRepository
        extends JpaRepository<SubscriptionPersistenceEntity, Long> {

    /**
     * Finds the unique subscription record belonging to an account
     */
    Optional<SubscriptionPersistenceEntity> findByUserId(long userId);
}
package com.dymbia.sumaqagro.platform.subscriptions.infrastructure.persistence.jpa.repositories;

import com.dymbia.sumaqagro.platform.subscriptions.infrastructure.persistence.jpa.entities.SubscriptionPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Spring Data repository for subscription records.
 */
public interface SubscriptionPersistenceRepository extends JpaRepository<SubscriptionPersistenceEntity, Long> {

    /**
     * Finds the unique subscription record belonging to an account
     */
    Optional<SubscriptionPersistenceEntity> findByUserId(long userId);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from SubscriptionPersistenceEntity s where s.userId = :userId")
    Optional<SubscriptionPersistenceEntity> findByUserIdForUpdate(@Param("userId") long userId);
}
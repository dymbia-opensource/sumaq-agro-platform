package com.dymbia.sumaqagro.platform.subscriptions.infrastructure.persistence.jpa.repositories;

import com.dymbia.sumaqagro.platform.subscriptions.infrastructure.persistence.jpa.entities.PaymentPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Spring Data repository for checkout attempts. */
public interface PaymentPersistenceRepository extends JpaRepository<PaymentPersistenceEntity, Long> {

    /**
     * Finds payment attempts by owner, with the ID breaking date ties.
     */
    List<PaymentPersistenceEntity> findByUserIdOrderByAttemptedAtDescIdDesc(long userId);
}

package com.dymbia.sumaqagro.platform.subscriptions.infrastructure.persistence.jpa.repositories;

import com.dymbia.sumaqagro.platform.subscriptions.infrastructure.persistence.jpa.entities.PaymentPersistenceEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentPersistenceRepository extends JpaRepository<PaymentPersistenceEntity, Long> {

    List<PaymentPersistenceEntity> findByUserIdOrderByAttemptedAtDescIdDesc(long userId);

    Optional<PaymentPersistenceEntity> findByReference(String reference);

    // The browser confirmation and the provider notification can arrive at the same time
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select payment from PaymentPersistenceEntity payment where payment.reference = :reference")
    Optional<PaymentPersistenceEntity> findByReferenceForUpdate(@Param("reference") String reference);
}

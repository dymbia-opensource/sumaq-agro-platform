package com.dymbia.sumaqagro.platform.subscriptions.infrastructure.persistence.jpa.adapters;

import com.dymbia.sumaqagro.platform.subscriptions.domain.model.entities.Payment;
import com.dymbia.sumaqagro.platform.subscriptions.domain.repositories.PaymentRepository;
import com.dymbia.sumaqagro.platform.subscriptions.infrastructure.persistence.jpa.assemblers.PaymentPersistenceAssembler;
import com.dymbia.sumaqagro.platform.subscriptions.infrastructure.persistence.jpa.repositories.PaymentPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/** Persists checkout attempts using Spring Data JPA. */
@Repository
public class PaymentRepositoryImpl implements PaymentRepository {
    private final PaymentPersistenceRepository persistenceRepository;

    public PaymentRepositoryImpl(PaymentPersistenceRepository persistenceRepository) {
        this.persistenceRepository = persistenceRepository;
    }

    @Override
    public void insert(Payment payment) {
        persistenceRepository.save(PaymentPersistenceAssembler.toPersistenceFromDomain(payment));
    }

    @Override
    public List<Payment> findByUserId(long userId) {
        return persistenceRepository.findByUserIdOrderByAttemptedAtDescIdDesc(userId)
                .stream()
                .map(PaymentPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }
}

package com.dymbia.sumaqagro.platform.subscriptions.infrastructure.persistence.jpa.adapters;

import com.dymbia.sumaqagro.platform.subscriptions.domain.model.entities.Payment;
import com.dymbia.sumaqagro.platform.subscriptions.domain.repositories.PaymentRepository;
import com.dymbia.sumaqagro.platform.subscriptions.infrastructure.persistence.jpa.assemblers.PaymentPersistenceAssembler;
import com.dymbia.sumaqagro.platform.subscriptions.infrastructure.persistence.jpa.repositories.PaymentPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    @Override
    public Optional<Payment> findByReferenceForUpdate(String reference) {
        return persistenceRepository.findByReferenceForUpdate(reference)
                .map(PaymentPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public void update(Payment payment) {
        var entity = persistenceRepository.findByReference(payment.reference())
                .orElseThrow(() -> new IllegalStateException("Payment not found: " + payment.reference()));
        entity.applyOutcome(payment.status(), payment.subscriptionId(), payment.providerPaymentId());
        persistenceRepository.save(entity);
    }
}

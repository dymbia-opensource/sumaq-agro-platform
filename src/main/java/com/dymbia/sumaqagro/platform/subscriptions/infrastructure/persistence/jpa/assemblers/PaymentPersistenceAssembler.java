package com.dymbia.sumaqagro.platform.subscriptions.infrastructure.persistence.jpa.assemblers;

import com.dymbia.sumaqagro.platform.subscriptions.domain.model.entities.Payment;
import com.dymbia.sumaqagro.platform.subscriptions.infrastructure.persistence.jpa.entities.PaymentPersistenceEntity;

/** Maps checkout attempts to JPA records. */
public final class PaymentPersistenceAssembler {
    private PaymentPersistenceAssembler() {
    }

    public static PaymentPersistenceEntity toPersistenceFromDomain(Payment payment) {
        return new PaymentPersistenceEntity(
                payment.reference(), payment.userId(), payment.subscriptionId(),
                payment.planCode(), payment.billingCycle(), payment.amount(),
                payment.status(), payment.attemptedAt()
        );
    }

    /**
     * Restores a payment attempt using the amount recorded at checkout.
     */
    public static Payment toDomainFromPersistence(PaymentPersistenceEntity entity) {
        return new Payment(
                entity.getReference(),
                entity.getUserId(),
                entity.getSubscriptionId(),
                entity.getPlanCode(),
                entity.getBillingCycle(),
                entity.getAmount(),
                entity.getStatus(),
                entity.getAttemptedAt()
        );
    }


}

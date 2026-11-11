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
}

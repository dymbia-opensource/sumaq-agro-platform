package com.dymbia.sumaqagro.platform.subscriptions.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.subscriptions.domain.model.entities.Payment;
import com.dymbia.sumaqagro.platform.subscriptions.interfaces.rest.resources.PaymentResource;

/** Maps a checkout attempt to its API response. */
public final class PaymentResourceFromEntityAssembler {
    private PaymentResourceFromEntityAssembler() {
    }

    public static PaymentResource toResourceFromEntity(Payment payment) {
        return new PaymentResource(
                payment.reference(), payment.subscriptionId(), payment.planCode().name(),
                payment.billingCycle().name(), payment.amount(), "PEN",
                payment.status().name(), payment.attemptedAt()
        );
    }
}

package com.dymbia.sumaqagro.platform.subscriptions.domain.model.entities;

import com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects.BillingCycle;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects.PaymentStatus;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects.PlanCode;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Records a simulated payment attempt and the price applied to it.
 * The subscription ID is absent when the first payment is rejected.
 */
public record Payment(
        String reference,
        long userId,
        Long subscriptionId,
        PlanCode planCode,
        BillingCycle billingCycle,
        BigDecimal amount,
        PaymentStatus status,
        Instant attemptedAt
) {
}

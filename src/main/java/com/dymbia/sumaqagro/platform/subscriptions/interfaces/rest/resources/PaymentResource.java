package com.dymbia.sumaqagro.platform.subscriptions.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.Instant;

/** Checkout result returned to the authenticated account. */
public record PaymentResource(
        String reference,
        Long subscriptionId,
        String planCode,
        String billingCycle,
        BigDecimal amount,
        String currency,
        String status,
        Instant attemptedAt
) {
}

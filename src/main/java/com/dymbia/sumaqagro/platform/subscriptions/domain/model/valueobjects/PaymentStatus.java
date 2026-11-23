package com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects;

/**
 * Outcome of a payment attempt.
 * <p>
 * {@code PENDING} is only used by the payment provider while the producer is paying or the provider
 * is still processing; the demo simulator always gives a final outcome.
 * </p>
 */
public enum PaymentStatus {
    PENDING,
    COMPLETED,
    REJECTED
}

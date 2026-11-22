package com.dymbia.sumaqagro.platform.subscriptions.domain.model.entities;

import com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects.BillingCycle;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects.PaymentStatus;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects.PlanCode;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * Records a payment attempt and the price applied to it.
 * The subscription ID is absent while the payment is pending or when the first payment is rejected.
 * The provider payment ID is the id given by the payment provider (Mercado Pago); it is absent for demo payments.
 */
public record Payment(
        String reference,
        long userId,
        Long subscriptionId,
        PlanCode planCode,
        BillingCycle billingCycle,
        BigDecimal amount,
        PaymentStatus status,
        Instant attemptedAt,
        String providerPaymentId
) {

    /**
     * Starts a payment that the producer still has to pay in the provider checkout.
     *
     * @return a pending payment, without subscription
     * @since 1.1
     */
    public static Payment pending(String reference, long userId, PlanCode planCode, BillingCycle billingCycle,
                                  BigDecimal amount, Instant attemptedAt) {
        return new Payment(reference, userId, null, planCode, billingCycle, amount, PaymentStatus.PENDING,
                attemptedAt, null);
    }

    /**
     * Tells whether the payment still waits for the provider outcome.
     *
     * @return {@code true} while the payment is pending
     * @since 1.1
     */
    public boolean isPending() {
        return status == PaymentStatus.PENDING;
    }

    /**
     * Completes a pending payment once the provider approved it.
     *
     * @param subscriptionId    the subscription activated or renewed by the payment
     * @param providerPaymentId the payment id in the provider
     * @return the completed payment
     * @throws IllegalStateException when the payment is not pending
     * @since 1.1
     */
    public Payment complete(Long subscriptionId, String providerPaymentId) {
        ensurePending();
        return new Payment(reference, userId, Objects.requireNonNull(subscriptionId, "Subscription ID is required"),
                planCode, billingCycle, amount, PaymentStatus.COMPLETED, attemptedAt, providerPaymentId);
    }

    /**
     * Rejects a pending payment once the provider rejected or cancelled it.
     *
     * @param providerPaymentId the payment id in the provider
     * @return the rejected payment
     * @throws IllegalStateException when the payment is not pending
     * @since 1.1
     */
    public Payment reject(String providerPaymentId) {
        ensurePending();
        return new Payment(reference, userId, subscriptionId, planCode, billingCycle, amount, PaymentStatus.REJECTED,
                attemptedAt, providerPaymentId);
    }

    private void ensurePending() {
        if (!isPending()) {
            throw new IllegalStateException("Only a pending payment can change its outcome");
        }
    }
}

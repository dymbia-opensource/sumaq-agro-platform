package com.dymbia.sumaqagro.platform.subscriptions.domain.repositories;

import com.dymbia.sumaqagro.platform.subscriptions.domain.model.entities.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository {
    void insert(Payment payment);

    List<Payment> findByUserId(long userId);

    /**
     * Finds a payment by its reference and locks it until the transaction ends, so the same
     * payment is never applied twice.
     *
     * @param reference the payment reference
     * @return the payment, or empty when it does not exist
     * @since 1.1
     */
    Optional<Payment> findByReferenceForUpdate(String reference);

    /**
     * Saves the outcome of an existing payment.
     *
     * @param payment the payment with its new status
     * @since 1.1
     */
    void update(Payment payment);
}

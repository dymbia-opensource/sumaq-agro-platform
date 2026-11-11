package com.dymbia.sumaqagro.platform.subscriptions.domain.repositories;

import com.dymbia.sumaqagro.platform.subscriptions.domain.model.entities.Payment;

import java.util.List;

/**
 * Persistence port for checkout attempts.
 */
public interface PaymentRepository {
    /**
     * Inserts a new payment into the repository.
     */
    void insert(Payment payment);

    /**
     * Returns the account's payment attempts, newest first.
     */
    List<Payment> findByUserId(long userId);
}

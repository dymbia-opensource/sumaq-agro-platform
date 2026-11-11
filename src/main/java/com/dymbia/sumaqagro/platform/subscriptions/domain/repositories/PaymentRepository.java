package com.dymbia.sumaqagro.platform.subscriptions.domain.repositories;

import com.dymbia.sumaqagro.platform.subscriptions.domain.model.entities.Payment;

/** Persistence port for checkout attempts. */
public interface PaymentRepository {
    void insert(Payment payment);
}

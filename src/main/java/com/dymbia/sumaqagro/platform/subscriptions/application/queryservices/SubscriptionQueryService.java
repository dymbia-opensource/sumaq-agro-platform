package com.dymbia.sumaqagro.platform.subscriptions.application.queryservices;

import com.dymbia.sumaqagro.platform.subscriptions.domain.model.aggregates.Subscription;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.entities.Payment;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.queries.GetPaymentsByUserIdQuery;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.queries.GetSubscriptionByUserIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Handles read operations for subscriptions.
 */
public interface SubscriptionQueryService {

    /**
     * Returns the account's subscription if it has been created.
     */
    Optional<Subscription> handle(GetSubscriptionByUserIdQuery query);

    /** Returns recorded checkout attempts for the requested account. */
    List<Payment> handle(GetPaymentsByUserIdQuery query);
}

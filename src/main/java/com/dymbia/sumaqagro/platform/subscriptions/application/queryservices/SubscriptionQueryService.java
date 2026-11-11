package com.dymbia.sumaqagro.platform.subscriptions.application.queryservices;

import com.dymbia.sumaqagro.platform.subscriptions.domain.model.aggregates.Subscription;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.queries.GetSubscriptionByUserIdQuery;

import java.util.Optional;

/**
 * Handles read operations for subscriptions.
 */
public interface SubscriptionQueryService {

    /**
     * Returns the account's subscription if it has been created.
     */
    Optional<Subscription> handle(GetSubscriptionByUserIdQuery query);
}